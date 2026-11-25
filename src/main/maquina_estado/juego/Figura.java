package main.maquina_estado.juego;

import main.herramientas.Depurar;
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
            if (esCuadrado) {
                Dibujador.IMAGEN.dibujar(graficos, imagenFigura, Constantes.COORDENADAS.FIGURA[i]); // Dibujar la figura
            } else {
                Dibujador.IMAGEN.dibujarRotado(graficos, imagenFigura, Constantes.COORDENADAS.FIGURA[i]);
            }
            this.getActivador().dibujarActivador(graficos, Constantes.COORDENADAS.ACTIVADOR[i], esCuadrado); // Dibujar el activador
            this.dibujarStats(graficos, Constantes.COORDENADAS.STATS[i]); // Dibujar los stats
        }

        Ataque actual = colaAtaques.peek();
        if (estaAtacando && actual != null) {
            actual.dibujar(graficos);
        }
    }

    /** Cuántos ataques hace al activarse (el Mago hace 2). */
    public int getNumeroAtaques() {
        return 1;
    }

    public int getAtaquesLanzados() {
        return ataquesLanzados;
    }

    private Point[] coordenadasAtaque() {
        if (posicion < 1 || posicion > 4) {
            return null; // Figura sin posición asignada
        }
        return Constantes.COORDENADAS.ATAQUE[posicion - 1];
    }

    // Al activarse crea sus ataques (getNumeroAtaques) y los encola. Los siguientes al primero ignoran el bastión.
    public void atacarNormal() {
        Point[] camino = coordenadasAtaque();
        if (camino == null) {
            return; // Figura sin posición asignada
        }

        this.estaAtacando = true;
        this.activacionPendiente = true;
        for (int i = 0; i < getNumeroAtaques(); i++) {
            Ataque nuevo = new Ataque(this, camino, ataqueImagen);
            nuevo.setIgnoraBastion(i > 0);
            colaAtaques.add(nuevo);
            ataquesLanzados++;
        }
        colaAtaques.peek().iniciar(); // Solo corre el primero; el resto empieza al llegarle el turno

        if (Depurar.ACTIVO) { // Solo se construye el texto si hace falta
            Depurar.log("Ataque de " + this.getClass().getSimpleName() + getDuenio().getClass().getSimpleName() + " a " + getEnemigo().getClass().getSimpleName());
        }
    }


    public void atacarEspecial() { // Hace dos de daño independientemente e ignora bastiones
        Point[] camino = coordenadasAtaque();
        if (camino == null) {
            return;
        }

        Ataque bomba = new Ataque(this, camino, ataqueEspecialImagen);
        bomba.setEspecial(true);
        colaAtaques.add(bomba);
        if (colaAtaques.size() == 1) {
            bomba.iniciar();
        }
        this.estaAtacando = true;
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

    // Efecto de un ataque cuando termina su animación
    public final void resolverAtaque(Ataque ataque, EsqueletoPlayer enemigo) {
        if (ataque.esEspecial()) { // Bomba: daño directo, ignora el bastión
            enemigo.recibirAtaque(DANIO_BOMBA);
            return;
        }
        aplicarEfecto(ataque, enemigo);
    }

    // Cada héroe define cómo golpea (el enemigo es el objetivo del ataque)
    protected abstract void aplicarEfecto(Ataque ataque, EsqueletoPlayer enemigo);

    // Golpe "normal": si el enemigo tiene bastión lo frena (baja stat2); si no, daña la corona (stat1)
    protected void golpeNormal(EsqueletoPlayer enemigo) {
        if (enemigo.tieneBation()) {
            enemigo.getBastion().disminuirAltura(getStat2());
        } else {
            enemigo.recibirAtaque(getStat1());
        }
    }

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
