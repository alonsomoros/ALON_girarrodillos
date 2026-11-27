package main.maquina_estado.juego;

import main.AudioPlayer;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.motor.Animacion;
import main.motor.Interpolador;
import main.motor.Tiempo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Pantalla de fin de partida: oscurece el tablero y el título cae con rebote. En victoria llueven estrellas y confeti; en derrota
 * se cierra una viñeta oscura con humo; el empate es sobrio. Después las monedas ganadas suben de una en una con su sonido.
 * La lluvia y el humo usan valores fijos por pieza (nada aleatorio ni objetos nuevos por frame).
 */
class PantallaFinal {

    static final String RUTA_CONFETI = "rec/sprites/efectos/confeti_4x.png";
    private static final int TICKS_BANNER = 40;
    private static final int INICIO_MONEDAS = 70;
    private static final int TICKS_POR_MONEDA = 10;
    private static final int ESTRELLAS = 34;
    private static final int PIEZAS_CONFETI = 46;
    private static final Color[] COLORES_CONFETI = {new Color(0xE0503C), new Color(0xF4C94A), new Color(0x6FCB6F), new Color(0x6FB7E0)};

    private ResultadoPartida resultado = ResultadoPartida.JUGANDO;
    private int monedas;
    private int monedasSonadas;
    private long inicio;
    private BufferedImage[] confeti;

    void iniciar(final ResultadoPartida resultado, final int monedas) {
        this.resultado = resultado;
        this.monedas = monedas;
        this.monedasSonadas = 0;
        this.inicio = Tiempo.ahora();
    }

    /** Monedas que se muestran a esta edad (sube de una en una, cada 10 actualizaciones, tras el título). */
    static int monedasMostradas(final long edad, final int total) {
        if (edad < INICIO_MONEDAS) {
            return 0;
        }
        return (int) Math.min(total, (edad - INICIO_MONEDAS) / TICKS_POR_MONEDA + 1);
    }

    /** Se llama en cada actualización: hace sonar cada moneda que se suma. */
    void actualizar() {
        int mostradas = monedasMostradas(Tiempo.ahora() - inicio, monedas);
        while (monedasSonadas < mostradas) {
            monedasSonadas++;
            AudioPlayer.getInstance().playSoundOnce(Constantes.RUTAS.EFECTOS.MONEDAS);
        }
    }

    void dibujar(final Graphics graficos, final int totalMonedas) {
        Graphics2D g = (Graphics2D) graficos;
        long edad = Tiempo.ahora() - inicio;
        int ancho = Constantes.ANCHO_JUEGO;
        int alto = Constantes.ALTO_JUEGO;

        Dibujador.RECTANGULO_RELLENO.dibujar(g, new java.awt.Rectangle(0, 0, ancho, alto), new Color(0, 0, 0, resultado == ResultadoPartida.DERROTA ? 110 : 170));
        if (resultado == ResultadoPartida.VICTORIA) {
            dibujarLluvia(g, edad);
        } else if (resultado == ResultadoPartida.DERROTA) {
            dibujarVinetaYHumo(g, edad, ancho, alto);
        }

        String titulo;
        Color color;
        switch (resultado) {
            case VICTORIA:
                titulo = "VICTORIA";
                color = new Color(0xE4DEB0);
                break;
            case DERROTA:
                titulo = "DERROTA";
                color = new Color(0xD66A5E);
                break;
            default:
                titulo = "EMPATE";
                color = new Color(0xD6BFAD);
                break;
        }

        // El título cae desde arriba y rebota hasta el centro
        double caida = Interpolador.RETROCESO.aplicar(Interpolador.limitar(edad / (double) TICKS_BANNER));
        int y = (int) (-90 + (Constantes.MITAD_VENTANA_ALTO + 90) * caida);
        Dibujador.TEXTO.dibujarConSombra(g, titulo, Constantes.MITAD_VENTANA_ANCHO, y, 90, color);

        if (edad >= INICIO_MONEDAS - 10) {
            int mostradas = monedasMostradas(edad, monedas);
            int total = totalMonedas - monedas + mostradas;
            Dibujador.TEXTO.dibujar(g, "+" + mostradas + (mostradas == 1 ? " moneda" : " monedas") + "  (total: " + total + ")",
                    Constantes.MITAD_VENTANA_ANCHO, Constantes.MITAD_VENTANA_ALTO + 40, 24, new Color(0xE4DEB0));
            Dibujador.TEXTO.dibujar(g, "Enter: volver al menu", Constantes.MITAD_VENTANA_ANCHO, Constantes.MITAD_VENTANA_ALTO + 80, 24, Color.WHITE);
        }
    }

    // Estrellas y confeti que caen sin parar; cada pieza tiene su columna, velocidad y fase fijas
    private void dibujarLluvia(final Graphics2D g, final long edad) {
        if (edad < 8) {
            return;
        }
        long t = edad - 8;
        int alto = Constantes.ALTO_JUEGO;
        for (int i = 0; i < ESTRELLAS; i++) {
            double velocidad = 1.1 + (i % 5) * 0.45;
            double y = ((t * velocidad + (i * 131) % 700) % (alto + 80)) - 40;
            double x = (i * 83 + 40) % Constantes.ANCHO_JUEGO + Math.sin((t + i * 20) * 0.04) * 14;
            Estrella.dibujar(g, i % 3, x, y, t * 0.03 * (i % 2 == 0 ? 1 : -1), 0.95f);
        }
        BufferedImage[] c = confeti();
        for (int i = 0; i < PIEZAS_CONFETI; i++) {
            double velocidad = 1.6 + (i % 4) * 0.5;
            double y = ((t * velocidad + (i * 97) % 760) % (alto + 60)) - 30;
            double x = (i * 59 + 15) % Constantes.ANCHO_JUEGO + Math.sin((t + i * 13) * 0.07) * 22;
            double giro = (t + i * 9) * 0.1;
            AffineTransform original = g.getTransform();
            g.translate(x, y);
            g.rotate(giro * 0.4);
            g.scale(Math.cos(giro), 1); // Se voltea al caer
            BufferedImage pieza = c[i % c.length];
            g.drawImage(pieza, -pieza.getWidth() / 2, -pieza.getHeight() / 2, null);
            g.setTransform(original);
        }
    }

    private BufferedImage[] confeti() {
        if (confeti == null) {
            if (new File(RUTA_CONFETI).exists()) {
                confeti = Animacion.cortarTira(CargadorRecursos.cargarImagenCompatibleTranslucida(RUTA_CONFETI), Animacion.framesEnNombre(RUTA_CONFETI));
            } else {
                confeti = new BufferedImage[COLORES_CONFETI.length];
                for (int k = 0; k < confeti.length; k++) {
                    confeti[k] = new BufferedImage(8, 12, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D gc = confeti[k].createGraphics();
                    gc.setColor(COLORES_CONFETI[k]);
                    gc.fillRect(0, 0, 8, 12);
                    gc.dispose();
                }
            }
        }
        return confeti;
    }

    // Viñeta que se cierra desde los bordes y humo gris que sube
    private void dibujarVinetaYHumo(final Graphics2D g, final long edad, final int ancho, final int alto) {
        double cierre = Interpolador.SALIDA.aplicar(Interpolador.limitar(edad / 60.0));
        float radio = (float) (ancho * (1.0 - 0.45 * cierre));
        g.setPaint(new RadialGradientPaint(ancho / 2f, alto / 2f, radio, new float[]{0.35f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 230)}));
        g.fillRect(0, 0, ancho, alto);

        java.awt.Composite original = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.16f));
        g.setColor(new Color(0xA9A29B));
        for (int i = 0; i < 14; i++) {
            double y = alto + 40 - ((edad * 0.9 + i * 57) % (alto + 80));
            double x = (i * 97 + 30) % ancho + Math.sin((edad + i * 30) * 0.03) * 30;
            int r = 34 + (i % 4) * 12 + (int) (edad % 400 * 0.02);
            g.fillOval((int) x - r, (int) y - r, r * 2, r * 2);
        }
        g.setComposite(original);
        g.setPaint(null);
    }
}
