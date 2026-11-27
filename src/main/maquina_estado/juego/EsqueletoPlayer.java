package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.Dibujador;
import main.motor.Interpolador;
import main.motor.Tiempo;
import main.maquina_estado.juego.figuras.Asesino;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EsqueletoPlayer {
    protected int vida;
    protected ArrayList<Figura> figurasDisponibles;
    protected Figura figuraIzquierda, figuraDerecha;
    protected Figura figuraIndex;
    protected Rodillo rodillo;
    protected Bastion bastion;
    protected int seleccionandoIzquierda = 0;
    protected boolean terminadoDeProcesarAtaques;
    protected boolean elegidas = false;

    protected Color color = new Color(0xFF8A7D72, true);
    private EsqueletoPlayer enemigo;

    // Animación de la corona
    private static final int TICKS_EFECTO_VIDA = 24;
    private long tickCambioVida = -1000;
    private int vidaAntesDelCambio;
    private boolean ultimoCambioFueDanio;
    private final GemasVida gemas = new GemasVida();


    public EsqueletoPlayer() {
        this.vida = Constantes.VIDA;
        this.bastion = new Bastion(this instanceof Player ? PUNTO_BASTION_JUGADOR : PUNTO_BASTION_IA);

        figuraIzquierda = null;
        figuraDerecha = null;
        terminadoDeProcesarAtaques = false;
    }

    public void setFiguraIzquierda(final Figura figura) {
        this.figuraIzquierda = figura;
    }

    public void setFiguraDerecha(final Figura figura) {
        this.figuraDerecha = figura;
    }

    public int getVida() {
        return this.vida;
    }

    public String getVidaString() {
        if (vida < 10) {
            return 0 + "   " + vida;
        } else {
            return vida / 10 + "   " + vida % 10;
        }
    }

    public void setVida(int vidaActualizada) {
        if (vidaActualizada < 0) {
            this.vida = 0;
        } else this.vida = Math.min(vidaActualizada, Constantes.VIDA_MAXIMA);
    }

    // Centros (en píxeles) donde aparecen los números flotantes: la corona de cada equipo y su bastión
    private static final Point PUNTO_CORONA_JUGADOR = new Point(640, 440);
    private static final Point PUNTO_CORONA_IA = new Point(640, 150);
    private static final Point PUNTO_BASTION_JUGADOR = new Point(617, 395);
    private static final Point PUNTO_BASTION_IA = new Point(617, 265);

    public void recibirAtaque(int danio) {
        if (danio <= 0) {
            return;
        }
        registrarCambioDeVida(true);
        this.setVida(this.getVida() - danio);
        mostrarEnCorona("-" + danio, new Color(0xE0503C));
    }

    public void recibirCuracion(int curacion) {
        if (curacion <= 0) {
            return;
        }
        registrarCambioDeVida(false);
        this.setVida(this.getVida() + curacion);
        mostrarEnCorona("+" + curacion, new Color(0x6FCB6F)); // Se ve siempre, aunque la vida ya estuviera al máximo
    }

    // Guarda la vida y el instante del cambio para animar la corona y las gemas
    private void registrarCambioDeVida(final boolean danio) {
        vidaAntesDelCambio = vida;
        tickCambioVida = Tiempo.ahora();
        ultimoCambioFueDanio = danio;
    }

    /**
     * Dibuja la vida de la corona: dígitos que tiemblan (daño) o se tiñen (curación), brillo rojo sobre la corona al recibir
     * daño, grietas si queda poca vida y la barra de gemas.
     */
    protected void dibujarCorona(final Graphics graficos, final Point textoVida, final Point centroCorona, final int yGemas) {
        Graphics2D g = (Graphics2D) graficos;
        long edad = Tiempo.ahora() - tickCambioVida;
        boolean animando = edad < TICKS_EFECTO_VIDA;
        double t = Interpolador.limitar(edad / (double) TICKS_EFECTO_VIDA);

        int dx = 0;
        Color colorDigitos = color;
        if (animando && ultimoCambioFueDanio) {
            dx = (int) Math.round(Math.sin(edad * 1.9) * 5 * (1 - t));
            colorDigitos = mezclar(new Color(0xFF4A3A), color, t);
            java.awt.Composite original = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.5 * (1 - t))));
            g.setColor(new Color(0xFF3B2F));
            g.fillOval(centroCorona.x - 54, centroCorona.y - 40, 108, 80);
            g.setComposite(original);
        } else if (animando) {
            colorDigitos = mezclar(new Color(0x8BE08B), color, t);
        }

        if (vida > 0 && vida <= 3) { // Corona agrietada
            g.setColor(new Color(20, 12, 8, 190));
            java.awt.Stroke trazo = g.getStroke();
            g.setStroke(new java.awt.BasicStroke(2f));
            int cx = centroCorona.x;
            int cy = centroCorona.y;
            g.drawPolyline(new int[]{cx - 14, cx - 6, cx - 16, cx - 4}, new int[]{cy - 26, cy - 10, cy + 2, cy + 16}, 4);
            g.drawPolyline(new int[]{cx + 12, cx + 5, cx + 16}, new int[]{cy - 24, cy - 6, cy + 8}, 3);
            g.setStroke(trazo);
        }

        Dibujador.TEXTO.dibujar(g, getVidaString(), textoVida.x + dx, textoVida.y, colorDigitos);
        gemas.dibujar(g, centroCorona.x, yGemas, vida, vidaAntesDelCambio, edad);
    }

    private static Color mezclar(final Color a, final Color b, final double t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t), (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t), (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
    }

    private void mostrarEnCorona(final String texto, final Color color) {
        Point p = this instanceof Player ? PUNTO_CORONA_JUGADOR : PUNTO_CORONA_IA;
        main.motor.TextoFlotante.mostrar(texto, p.x, p.y, color);
    }

    public Figura getFiguraIzquierda() {
        return this.figuraIzquierda;
    }

    public Figura getFiguraDerecha() {
        return this.figuraDerecha;
    }

    // El héroe al que le queda menos energía para actuar; si empatan, el que está enfrente del atacante
    public Figura getFiguraMasCercaDeActuar(Figura atacante) {
        // Los asesinos no se atacan entre sí: actúan a la vez, así que el rival que se activa en esta ronda no es objetivo
        boolean izquierdaValida = !esAsesinoActivadoContra(atacante, figuraIzquierda);
        boolean derechaValida = !esAsesinoActivadoContra(atacante, figuraDerecha);
        if (!izquierdaValida && !derechaValida) {
            return null;
        } else if (!izquierdaValida) {
            return figuraDerecha;
        } else if (!derechaValida) {
            return figuraIzquierda;
        }

        int alturaIzquierda = figuraIzquierda.getActivador().getAltura();
        int alturaDerecha = figuraDerecha.getActivador().getAltura();
        if (alturaIzquierda < alturaDerecha) {
            return figuraIzquierda;
        } else if (alturaDerecha < alturaIzquierda) {
            return figuraDerecha;
        }
        return getFiguraEnfrente(atacante);
    }

    private static boolean esAsesinoActivadoContra(final Figura atacante, final Figura candidata) {
        return atacante instanceof Asesino && candidata instanceof Asesino && candidata.getActivador().getAltura() == 0;
    }

    public Figura getFiguraEnfrente(Figura figura) {

        if (figura.getPosicion() % 2 == 0) {
            return this.figuraDerecha;
        } else {
            return this.figuraIzquierda;
        }
    }

    public Rodillo getRodillo() {
        return rodillo;
    }

    public Bastion getBastion() {
        return this.bastion;
    }

    public boolean tieneBastion() {
        return bastion.getAltura() > 0;
    }

    public boolean tieneFigurasElegidas() {
        return figuraIzquierda != null && figuraDerecha != null && elegidas;
    }

    public boolean seleccionandoFiguras() {
        return seleccionandoIzquierda == 0 || seleccionandoIzquierda == 1;
    }

    protected boolean figurasAtacando() {
        return figuraDerecha.estaAtacando || figuraIzquierda.estaAtacando;
    }

    @SuppressWarnings("unchecked")
    public void establecerRodillo(HashMap<Constantes.RODILLOS.Elementos, Double> p) {
        HashMap<Constantes.RODILLOS.Elementos, Double>[] rodilloPlayer = new HashMap[5];
        for (int i = 0; i < 5; i++) {
            rodilloPlayer[i] = p;
        }
        rodillo = new Rodillo(rodilloPlayer);
    }

    @SuppressWarnings("unchecked")
    public void establecerRodillo(HashMap<Constantes.RODILLOS.Elementos, Double> p1, HashMap<Constantes.RODILLOS.Elementos, Double> p2, HashMap<Constantes.RODILLOS.Elementos, Double> p3, HashMap<Constantes.RODILLOS.Elementos, Double> p4, HashMap<Constantes.RODILLOS.Elementos, Double> p5) {
        HashMap<Constantes.RODILLOS.Elementos, Double>[] rodilloPlayer = new HashMap[5];

        rodilloPlayer[0] = p1;
        rodilloPlayer[1] = p2;
        rodilloPlayer[2] = p3;
        rodilloPlayer[3] = p4;
        rodilloPlayer[4] = p5;

        rodillo = new Rodillo(rodilloPlayer);
    }

    public void setEnemigo(EsqueletoPlayer enemigo) {
        this.enemigo = enemigo;
    }

    public EsqueletoPlayer getEnemigo() {
        return enemigo;
    }

    protected void calcularSalidaRodillo(Constantes.RODILLOS.Elementos[] rodillos) {
        int ataqueC = 0, ataqueR = 0, experienciaC = 0, experienciaR = 0, muralla = 0;

        for (Constantes.RODILLOS.Elementos elemento : rodillos) {
            switch (elemento.tipo) {
                case CUADRADO:
                    ataqueC += elemento.valor;
                    if (elemento.daExperiencia) {
                        experienciaC += elemento.valor;
                    }
                    break;
                case ROMBO:
                    ataqueR += elemento.valor;
                    if (elemento.daExperiencia) {
                        experienciaR += elemento.valor;
                    }
                    break;
                case MARTILLO:
                    muralla += elemento.valor;
                    break;
                default: // roto: no aporta nada
                    break;
            }
        }

        if (ataqueC >= 3) {
            figuraIzquierda.getActivador().disminuirAltura(ataqueC - 2);
        }
        if (ataqueR >= 3) {
            figuraDerecha.getActivador().disminuirAltura(ataqueR - 2);
        }
        if (experienciaC >= 1) {
            figuraIzquierda.aumentarExperiencia(experienciaC);
        }
        if (experienciaR >= 1) {
            figuraDerecha.aumentarExperiencia(experienciaR);
        }
        if (muralla >= 3) {
            bastion.aumentarAltura(muralla - 2);
        }

    }

    // Si el activador de la figura llegó a 0 y no está ya atacando, lanza todos sus ataques
    protected void lanzarAtaque(final Figura figura) {
        if (figura.getActivador().getAltura() == 0 && !figura.estaAtacando) {
            figura.atacarNormal(); // Crea y encola todos sus ataques (el Mago, 2)

        }
    }

    protected void actualizarEstados() {
        terminadoDeProcesarAtaques = false; // Se decide de nuevo en cada ronda


        lanzarAtaque(figuraIzquierda);
        lanzarAtaque(figuraDerecha);

        if (!figurasAtacando()) {
            terminadoDeProcesarAtaques = true;
        }
    }

}
