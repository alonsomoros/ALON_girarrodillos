package main.maquina_estado.juego;

import main.herramientas.Depurar;
import main.motor.Animacion;
import main.motor.Interpolador;
import main.motor.Tiempo;
import main.Constantes;

import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class Figura {
    private final EsqueletoPlayer duenio; // Jugador o IA a quien pertenece

    protected Image imagenFigura;
    protected Image ataqueImagen;
    protected Image ataqueEspecialImagen;
    protected Image stats;
    // Ataques de esta activación, en orden: se animan de uno en uno
    private final Deque<Ataque> colaAtaques = new ArrayDeque<>();
    private boolean activacionPendiente; // true si al terminar hay que resetear activador y dar XP
    private String sonidoAtaque;           // Se resuelve la primera vez que suena
    private int energiaPendiente;        // Energía que el Sacerdote da cuando esta figura termine de actuar
    protected int posicion;
    protected int nivel; // Del 1 al 3
    protected int experiencia; // Del 0 al 6
    protected Activador activador;

    protected boolean estaAtacando;
    private int ataquesLanzados; // Solo informativo (depuración y pruebas)


    public Figura(final EsqueletoPlayer duenio) {
        this.duenio = duenio;
        nivel = 1;
        experiencia = 0;
        imagenFigura = getImagen();
        ataqueImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + this.getClass().getSimpleName().toLowerCase() + ".png");
        ataqueEspecialImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + "Especial.png");
        estaAtacando = false;
        posicion = 0;
    }


    public void actualizar() {
        if (!estaAtacando) {
            return;
        }

        Ataque actual = colaAtaques.peek();
        if (actual != null) {
            actual.actualizar(); // Esto hace que se anime
            if (actual.terminadaAnimacion()) {
                colaAtaques.poll();
                Ataque siguiente = colaAtaques.peek();
                if (siguiente != null) {
                    siguiente.iniciar(); // El siguiente ataque empieza ahora
                }
            }
        }

        if (colaAtaques.isEmpty()) {
            finalizarAtaques();
        }
    }

    // Se llama cuando se acabó la cola de ataques
    private void finalizarAtaques() {
        if (activacionPendiente) {
            activacionPendiente = false;
            activador.resetear();
            if (energiaPendiente > 0) { // El Sacerdote le daba energía mientras actuaba: llega justo después
                activador.disminuirAltura(energiaPendiente);
                energiaPendiente = 0;
            }
            // Sumar +2 de nivel etc
            this.aumentarExperiencia(2); // Si llena la barra: sube de nivel o encola la bomba
        }
        estaAtacando = !colaAtaques.isEmpty();
    }

    public void dibujar(Graphics graficos, int posicion) {
        if (posicion >= 1 && posicion <= 4) {
            int i = posicion - 1;
            boolean esCuadrado = posicion % 2 == 1; // 1 y 3: izquierda (cuadrado); 2 y 4: derecha (rombo, volteada)

            dibujarBase(graficos, Constantes.COORDENADAS.BASE[i], esCuadrado); // Dibujar la base
            dibujarFigura(graficos, i, esCuadrado); // La figura, con su reposo y su impulso al atacar
            this.getActivador().dibujarActivador(graficos, Constantes.COORDENADAS.ACTIVADOR[i], esCuadrado); // Dibujar el activador
            this.dibujarStats(graficos, Constantes.COORDENADAS.STATS[i]); // Dibujar los stats
        }

        Ataque actual = colaAtaques.peek();
        if (estaAtacando && actual != null) {
            actual.dibujar(graficos);
        }
    }

    // ------------------- Animación de la figura -------------------

    private static final int TICKS_IMPULSO = 22;
    private static final int PERIODO_RESPIRACION = 120; // 2 s a 60 actualizaciones por segundo
    private static final java.util.Map<Image, java.awt.image.BufferedImage> ILUMINADAS = new java.util.WeakHashMap<>();

    private long tickImpulso = -1000;
    private Animacion reposo;       // Tira propia de reposo (<héroe>_lvlN_Mf.png), si existe
    private int nivelDelReposo = -1;

    /** Lo llama cada ataque al empezar: el héroe retrocede un poco, se ilumina y se lanza hacia delante. */
    public void iniciarImpulso() {
        tickImpulso = Tiempo.ahora();
    }

    // Busca rec/sprites/figuras/<héroe>/<héroe>_lvl<N>_<M>f.png (M frames de reposo); si no hay, el reposo es procedural
    private Animacion reposoPropio() {
        if (nivelDelReposo != nivel) {
            nivelDelReposo = nivel;
            reposo = null;
            String heroe = getClass().getSimpleName().toLowerCase();
            java.io.File carpeta = new java.io.File(Constantes.RUTAS.FIGURAS.RUTA_FIGURAS + heroe);
            String[] nombres = carpeta.list();
            if (nombres != null) {
                java.util.regex.Pattern patron = java.util.regex.Pattern.compile(heroe + "_lvl" + nivel + "_[0-9]+f[.]png");
                for (String n : nombres) {
                    if (patron.matcher(n).matches()) {
                        reposo = Animacion.cargar(Constantes.RUTAS.FIGURAS.RUTA_FIGURAS + heroe + "/" + n, 400, Animacion.Modo.PING_PONG);
                        break;
                    }
                }
            }
        }
        return reposo;
    }

    private void dibujarFigura(final Graphics graficos, final int i, final boolean esCuadrado) {
        Point punto = Constantes.COORDENADAS.FIGURA[i];
        Animacion propio = reposoPropio();
        Image imagen = propio != null ? propio.frame(Tiempo.ahora() + posicion * 17) : imagenFigura;
        int ancho = imagen.getWidth(null);
        int alto = imagen.getHeight(null);

        double dx = 0;
        double dy = 0;
        double escalaY = 1;
        if (propio == null) { // Respira: sube y baja ~1,5 px y se estira apenas (cada figura con su desfase)
            double onda = Math.sin(2 * Math.PI * ((Tiempo.ahora() + posicion * 31) % PERIODO_RESPIRACION) / PERIODO_RESPIRACION);
            dy = 1.5 * onda;
            escalaY = 1 + 0.012 * onda;
        }

        long edad = Tiempo.ahora() - tickImpulso;
        double brillo = 0;
        if (edad >= 0 && edad < TICKS_IMPULSO) {
            double[] dir = direccionDeAtaque();
            double avance;
            if (edad < 5) { // Se echa hacia atrás un poco
                avance = -3 * Interpolador.SALIDA.aplicar(edad / 5.0);
                brillo = edad / 5.0;
            } else if (edad < 10) { // Y se lanza hacia delante
                avance = -3 + 13 * Interpolador.ENTRADA.aplicar((edad - 5) / 5.0);
                brillo = 1;
            } else { // Vuelve a su sitio
                avance = 10 * (1 - Interpolador.SALIDA.aplicar((edad - 10) / 12.0));
                brillo = Math.max(0, 1 - (edad - 10) / 8.0);
            }
            dx += dir[0] * avance;
            dy += dir[1] * avance;
        }

        Graphics2D g = (Graphics2D) graficos.create();
        try {
            g.translate(dx, dy);
            g.translate(punto.x + ancho / 2.0, punto.y + alto); // Escala desde los pies
            g.scale(1, escalaY);
            g.translate(-(punto.x + ancho / 2.0), -(punto.y + alto));
            dibujarImagenFigura(g, imagen, punto, esCuadrado);
            if (brillo > 0) { // Se ilumina: encima, la misma imagen aclarada
                g.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, (float) (0.55 * brillo)));
                dibujarImagenFigura(g, iluminada(imagen), punto, esCuadrado);
            }
        } finally {
            g.dispose();
        }
    }

    private void dibujarImagenFigura(final Graphics g, final Image imagen, final Point punto, final boolean esCuadrado) {
        if (esCuadrado) {
            Dibujador.IMAGEN.dibujar(g, imagen, punto);
        } else {
            Dibujador.IMAGEN.dibujarRotado(g, imagen, punto);
        }
    }

    // Misma imagen más clara (se calcula una vez por imagen)
    private static synchronized Image iluminada(final Image imagen) {
        java.awt.image.BufferedImage clara = ILUMINADAS.get(imagen);
        if (clara == null) {
            java.awt.image.BufferedImage origen = (java.awt.image.BufferedImage) imagen;
            clara = new java.awt.image.BufferedImage(origen.getWidth(), origen.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
            new java.awt.image.RescaleOp(new float[]{1f, 1f, 1f, 1f}, new float[]{80f, 80f, 80f, 0f}, null).filter(origen, clara);
            ILUMINADAS.put(imagen, clara);
        }
        return clara;
    }

    // Vector unitario desde la peana hacia el centro del tablero (hacia donde se lanza el ataque)
    private double[] direccionDeAtaque() {
        Point esquina = Trampillas.esquina(posicion);
        Point centro = Trampillas.centro(posicion);
        double vx = centro.x - esquina.x;
        double vy = centro.y - esquina.y;
        double largo = Math.max(1, Math.hypot(vx, vy));
        return new double[]{vx / largo, vy / largo};
    }

    /** Da energía a esta figura: si ya está activada (energía llena) llega después de que actúe; si no, al momento. */
    public void recibirEnergia(final int energia) {
        if (activador.getAltura() == 0) {
            energiaPendiente += energia;
        } else {
            activador.disminuirAltura(energia);
        }
    }

    public int getAtaquesLanzados() {
        return ataquesLanzados;
    }

    // Al activarse crea sus ataques (cada héroe define los suyos en crearAtaques) y los encola
    public void atacarNormal() {
        if (posicion < 1 || posicion > 4) {
            return; // Figura sin posición asignada
        }

        this.estaAtacando = true;
        this.activacionPendiente = true;
        for (Ataque nuevo : crearAtaques()) {
            colaAtaques.add(nuevo);
            ataquesLanzados++;
        }
        colaAtaques.peek().iniciar(); // Solo corre el primero; el resto empieza al llegarle el turno

        if (Depurar.ACTIVO) { // Solo se construye el texto si hace falta
            Depurar.log("Ataque de " + this.getClass().getSimpleName() + getDuenio().getClass().getSimpleName() + " a " + getEnemigo().getClass().getSimpleName());
        }
    }

    // Bomba (héroe de oro con la barra llena): recorrido completo, sacudida fuerte y 2 de daño directo a la corona
    public void atacarEspecial() {
        if (posicion < 1 || posicion > 4) {
            return;
        }

        Ataque bomba = new Ataque(this, ataqueEspecialImagen, () -> {
            Point e = Trampillas.esquina(posicion);
            Point c = Trampillas.centro(posicion);
            Point k = Trampillas.coronaEnemiga(posicion);
            return java.util.List.of(Ataque.Paso.salir(e), Ataque.Paso.recorrer(e, c, k),
                    Ataque.Paso.efecto(() -> main.motor.Sacudida.iniciar(10, 450)), Ataque.Paso.golpe(k),
                    Ataque.Paso.efecto(() -> getEnemigo().recibirAtaque(DANIO_BOMBA)), Ataque.Paso.esconder(k));
        });
        colaAtaques.add(bomba);
        if (colaAtaques.size() == 1) {
            bomba.iniciar();
        }
        this.estaAtacando = true;
    }

    /**
     * Ataques de esta activación, en orden (se animan de uno en uno). Cada héroe los define; las funciones de ayuda de
     * abajo montan los recorridos por las trampillas. Ver docs/animaciones.md.
     */
    protected abstract java.util.List<Ataque> crearAtaques();

    /** Ataque con trayecto completo: sale de su esquina, pasa por el centro y llega a la corona enemiga (o rebota en el centro si el muro lo frena). */
    protected Ataque ataqueAlEnemigo(final java.util.function.BooleanSupplier muroLoFrena, final Runnable efecto, final double pausaInicial) {
        return new Ataque(this, ataqueImagen, () -> {
            Point e = Trampillas.esquina(posicion);
            Point c = Trampillas.centro(posicion);
            Point k = Trampillas.coronaEnemiga(posicion);
            java.util.List<Ataque.Paso> pasos = new java.util.ArrayList<>();
            if (pausaInicial > 0) {
                pasos.add(Ataque.Paso.esperar(null, pausaInicial));
            }
            pasos.add(Ataque.Paso.salir(e));
            boolean llegaALaCorona = muroLoFrena == null || !muroLoFrena.getAsBoolean();
            Point destino = llegaALaCorona ? k : c;
            pasos.add(llegaALaCorona ? Ataque.Paso.recorrer(e, c, k) : Ataque.Paso.recorrer(e, c));
            pasos.add(Ataque.Paso.golpe(destino));
            pasos.add(Ataque.Paso.efecto(efecto));
            pasos.add(Ataque.Paso.esconder(destino));
            return pasos;
        });
    }

    /** Ataque que aparece directamente en una trampilla (sin viajar): corona del asesino, martillo, curación... */
    protected Ataque ataqueEnPunto(final java.util.function.Supplier<Point> trampilla, final boolean conGolpe, final Runnable efecto) {
        return new Ataque(this, ataqueImagen, () -> {
            Point t = trampilla.get();
            java.util.List<Ataque.Paso> pasos = new java.util.ArrayList<>();
            pasos.add(Ataque.Paso.salir(t));
            pasos.add(conGolpe ? Ataque.Paso.golpe(t) : Ataque.Paso.esperar(t, 0.6));
            pasos.add(Ataque.Paso.efecto(efecto));
            pasos.add(Ataque.Paso.esconder(t));
            return pasos;
        });
    }

    /** Ataque con pasos propios, calculados al empezar (los usa el Asesino para elegir objetivo en ese momento). */
    protected Ataque ataquePersonalizado(final java.util.function.Supplier<java.util.List<Ataque.Paso>> pasos) {
        return new Ataque(this, ataqueImagen, pasos);
    }

    /** El enemigo tiene bastión (false si todavía no hay enemigo asignado, como en algunas pruebas). */
    protected boolean enemigoTieneBastion() {
        return getEnemigo() != null && getEnemigo().tieneBastion();
    }

    protected Point esquinaPropia() {
        return Trampillas.esquina(posicion);
    }

    protected Point coronaPropia() {
        return Trampillas.coronaPropia(posicion);
    }

    protected Point coronaEnemiga() {
        return Trampillas.coronaEnemiga(posicion);
    }

    public void dibujarBase(Graphics graficos, Point coordenadas, boolean esCuadrado) {
        String[] rutas = esCuadrado ? Constantes.RUTAS.BASES.CUADRADO : Constantes.RUTAS.BASES.ROMBO;
        if (experiencia >= 0 && experiencia < rutas.length) {
            Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[experiencia]), coordenadas);
        }
    }

    public void dibujarStats(Graphics graficos, Point coordenadas) {
        Dibujador.IMAGEN.dibujar(graficos, stats, coordenadas);
        Dibujador.TEXTO.dibujar(graficos, this.getStat1() + "              " + this.getStat2(), coordenadas.x + 100, coordenadas.y + 45, new Color(0xFF8A7D72, true));
    }

    public static final int DANIO_BOMBA = 2;

    // Golpe "normal": si el enemigo tiene bastión lo frena (baja stat2); si no, daña la corona (stat1)
    protected void golpeNormal(EsqueletoPlayer enemigo) {
        if (enemigo.tieneBastion()) {
            enemigo.getBastion().disminuirAltura(getStat2());
        } else {
            enemigo.recibirAtaque(getStat1());
        }
    }

    /** Sonido propio del héroe (rec/sonidos/ataque_<héroe>.wav); si el archivo no existe suena el genérico. */
    public String getSonidoAtaque() {
        if (sonidoAtaque == null) {
            String propio = Constantes.RUTAS.EFECTOS.RUTA_ATAQUE_HEROE + getClass().getSimpleName().toLowerCase() + ".wav";
            sonidoAtaque = new java.io.File(propio).exists() ? propio : Constantes.RUTAS.EFECTOS.ATAQUE;
        }
        return sonidoAtaque;
    }

    /** Aspecto del ataque de este héroe (estela, tajo, destello...). Cada héroe lo cambia. */
    public Ataque.Estilo getEstiloAtaque() {
        return Ataque.Estilo.NINGUNO;
    }

    /** Orden de acción cuando varios héroes se activan en la misma ronda (1 = primero). */
    public abstract int getPrioridad();

    public abstract int getStat1();

    public abstract int getStat2();

    public Image getImagenFigura() {
        return imagenFigura;
    }

    public Activador getActivador() {
        return activador;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public int getAlturaDefault() {
        return activador.getAlturaDefault();

    }

    public void subirNivel() {
        nivel++;
        setExperiencia(0);

        if (nivel > 3) {
            nivel = 3;
            atacarEspecial(); // Bomba: 2 de daño directo a la corona enemiga
        }

        imagenFigura = getImagen();
    }

    public void aumentarExperiencia(int experiencia) {
        if (experiencia > 6) {
            experiencia = 6;
        }
        this.experiencia = Math.min(6, this.experiencia + experiencia); // El exceso se pierde

        if (this.experiencia == 6) { // Barra llena: sube de nivel (o bomba si ya es de oro)
            subirNivel();
        }
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getPosicion() {
        return posicion;
    }

    public EsqueletoPlayer getDuenio() {
        return duenio;
    }

    public EsqueletoPlayer getEnemigo() {
        return duenio.getEnemigo();
    }


    public Image getImagen() {
        return CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.RUTA_FIGURAS + this.getClass().getSimpleName().toLowerCase() + "/" + this.getClass().getSimpleName().toLowerCase() + "_lvl" + getNivel() + ".png");
    }

    public Figura getCompaniero() {
        if (this.getPosicion() % 2 == 0) {
            return getDuenio().getFiguraIzquierda();
        } else {
            return getDuenio().getFiguraDerecha();
        }

    }

}
