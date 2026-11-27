package main.maquina_estado.juego;

import main.AudioPlayer;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.motor.Interpolador;
import main.motor.Tiempo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * El bastión (muro) de un equipo. Su altura va de 0 a 5. Al subir, el nuevo tramo cae con rebote; al bajar, el tramo
 * perdido se sacude, se desvanece y suelta escombros y polvo. Cada cambio muestra un número flotante y suena.
 */
public class Bastion {
    private static final int TICKS_ANIMACION = 30;
    private static final int ANCHO_SPRITE = 355;
    private static final int ALTO_SPRITE = 160;

    // Sprites propios de la IA (bastion_ia_1.png a bastion_ia_5.png, mismo tamaño que los del jugador): si no existen,
    // usa los del jugador volteados en vertical. Se comprueba una sola vez por altura.
    private static final String RUTA_IA = "rec/sprites/bastion/bastion_ia_";
    private static final Boolean[] TIENE_PROPIO_IA = new Boolean[6];

    // Escombros: desplazamiento inicial x, velocidad x, velocidad y y tamaño de cada trozo (valores fijos: nada aleatorio por frame)
    private static final int[] ESC_X = {-120, -75, -35, 5, 40, 85, 125};
    private static final double[] ESC_VX = {-1.6, -1.0, -0.4, 0.3, 0.9, 1.4, 1.8};
    private static final double[] ESC_VY = {-2.2, -3.0, -1.6, -3.4, -2.0, -2.8, -1.8};
    private static final int[] ESC_TAM = {8, 6, 9, 7, 6, 8, 7};

    private int altura;
    private int alturaAnterior;
    private long tickCambio = -1000;
    private final Point centroTexto; // Dónde salen los números flotantes (null: no salen ni suena)

    public Bastion() {
        this(null);
    }

    public Bastion(final Point centroTexto) {
        altura = 0;
        alturaAnterior = 0;
        this.centroTexto = centroTexto;
    }

    private static boolean existeSpriteIA(final int altura) {
        if (TIENE_PROPIO_IA[altura] == null) {
            TIENE_PROPIO_IA[altura] = new java.io.File(RUTA_IA + altura + ".png").exists();
        }
        return TIENE_PROPIO_IA[altura];
    }

    /** true mientras dura la animación de un cambio de altura (el muro se sigue dibujando aunque ya esté a 0). */
    public boolean estaAnimando() {
        return Tiempo.ahora() - tickCambio < TICKS_ANIMACION;
    }

    public void dibujarBastion(final Graphics graficos, final EsqueletoPlayer duenio, final Point coordenadas) {
        Graphics2D g = (Graphics2D) graficos;
        boolean animando = estaAnimando();
        long edad = Tiempo.ahora() - tickCambio;
        double t = Interpolador.limitar(edad / (double) TICKS_ANIMACION);

        if (!animando || alturaAnterior == altura) {
            dibujarAltura(g, duenio, altura, coordenadas, 0, 0, 1f);
            return;
        }

        if (altura > alturaAnterior) { // Sube: lo que ya estaba se queda y el tramo nuevo cae con rebote
            dibujarAltura(g, duenio, alturaAnterior, coordenadas, 0, 0, 1f);
            double caida = 28 * (1 - Interpolador.RETROCESO.aplicar(Interpolador.limitar(t * 1.6)));
            dibujarAltura(g, duenio, altura, coordenadas, 0, (int) -caida, (float) Math.min(1, t * 5));
        } else { // Baja: lo que queda se ve entero y el tramo perdido tiembla y se desvanece
            dibujarAltura(g, duenio, altura, coordenadas, 0, 0, 1f);
            int temblor = (int) Math.round(Math.sin(edad * 2.2) * 4 * (1 - t));
            dibujarAltura(g, duenio, alturaAnterior, coordenadas, temblor, 0, (float) (1 - t));
            dibujarEscombrosYPolvo(g, coordenadas, edad, t);
        }
    }

    private void dibujarAltura(final Graphics2D g, final EsqueletoPlayer duenio, final int h, final Point coordenadas,
                               final int dx, final int dy, final float alfa) {
        String[] rutas = Constantes.RUTAS.BASTION.PLAYER;
        if (h < 1 || h >= rutas.length || alfa <= 0) {
            return;
        }
        java.awt.Composite original = g.getComposite();
        if (alfa < 1) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
        }
        Point punto = new Point(coordenadas.x + dx, coordenadas.y + dy);
        if (duenio instanceof Player) {
            Dibujador.IMAGEN.dibujar(g, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[h]), punto);
        } else if (existeSpriteIA(h)) {
            Dibujador.IMAGEN.dibujar(g, CargadorRecursos.cargarImagenCompatibleTranslucida(RUTA_IA + h + ".png"), punto);
        } else { // Sin sprite propio: el del jugador boca abajo
            Dibujador.IMAGEN.dibujarVolteadoVertical(g, CargadorRecursos.cargarImagenCompatibleTranslucida(rutas[h]), punto);
        }
        g.setComposite(original);
    }

    // Trozos de piedra que saltan y caen, y nubes de polvo que crecen y se desvanecen
    private void dibujarEscombrosYPolvo(final Graphics2D g, final Point coordenadas, final long edad, final double t) {
        int cx = coordenadas.x + ANCHO_SPRITE / 2;
        int cy = coordenadas.y + ALTO_SPRITE / 2;
        java.awt.Composite original = g.getComposite();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.max(0, 0.45 * (1 - t))));
        g.setColor(new Color(0xB5ADA0));
        for (int i = 0; i < ESC_X.length; i++) {
            int radio = (int) (9 + edad * 0.7);
            g.fillOval(cx + ESC_X[i] / 2 - radio, cy + 18 - (int) (edad * 0.25) - radio, radio * 2, radio * 2);
        }

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.max(0, 1 - t * t)));
        g.setColor(new Color(0x8D857A));
        for (int i = 0; i < ESC_X.length; i++) {
            double x = cx + ESC_X[i] + ESC_VX[i] * edad;
            double y = cy + ESC_VY[i] * edad + 0.16 * edad * edad;
            g.fillRect((int) x, (int) y, ESC_TAM[i], ESC_TAM[i]);
        }
        g.setComposite(original);
    }

    public void aumentarAltura(int altura) {
        int antes = this.altura;
        this.altura += altura;

        if (this.altura > 5) {
            this.altura = 5;
        }
        registrarCambio(antes);
    }

    public int getAltura() {
        return altura;
    }

    public void disminuirAltura(int altura) {
        int antes = this.altura;
        this.altura -= altura;

        if (this.altura < 0) {
            this.altura = 0;
        }
        registrarCambio(antes);
    }

    // Arranca la animación, el sonido y el número flotante con lo que cambió de verdad
    private void registrarCambio(final int antes) {
        int cambio = altura - antes;
        if (cambio == 0) {
            return;
        }
        alturaAnterior = antes;
        tickCambio = Tiempo.ahora();
        if (centroTexto != null) {
            main.motor.TextoFlotante.mostrar((cambio > 0 ? "+" : "") + cambio, centroTexto.x, centroTexto.y,
                    cambio > 0 ? new Color(0xE6E0D2) : new Color(0xB5ADA0));
            AudioPlayer.getInstance().playSoundOnce(cambio > 0 ? Constantes.RUTAS.EFECTOS.BASTION_SUBE : Constantes.RUTAS.EFECTOS.BASTION_ROMPE);
        }
    }
}
