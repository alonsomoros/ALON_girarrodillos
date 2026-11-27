package main.maquina_estado.juego;

import main.AudioPlayer;
import main.Constantes;
import main.motor.Interpolador;
import main.motor.Sacudida;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Una animación de ataque: un objeto sale por una trampilla, viaja, golpea y se esconde. Es una línea de tiempo de pasos
 * (salir, viajar, golpe, efecto, esconder) cuyas duraciones se reparten según su peso. Los pasos se calculan al empezar
 * ({@link #iniciar()}), con el estado del tablero en ese momento, y el efecto del juego (daño, curación...) se aplica
 * en el instante del golpe, no al final.
 */
public class Ataque {
    /** Duración en ms de un recorrido completo (salir, recorrido, golpe y esconder). Las pruebas la acortan. */
    static long duracionPorDefecto = 2000;

    private static final double PESO_RECORRIDO_COMPLETO = 5.0;

    /** Aspecto propio de cada héroe: estela del proyectil, tajo al golpear o destello al aparecer. */
    public enum Estilo {NINGUNO, ESTELA_LARGA, ESTELA_CORTA, FUEGO, TAJO, DESTELLO}

    enum Tipo {SALIR, VIAJAR, GOLPE, ESPERAR, ESCONDER, EFECTO}

    /** Un paso de la línea de tiempo. Se crean con las funciones estáticas. */
    public static final class Paso {
        final Tipo tipo;
        final Point desde;
        final Point hasta;
        final Point[] ruta; // Solo VIAJAR: puntos por los que pasa (se recorren de seguido, sin pausas)
        final double peso;
        final Runnable accion;
        long inicioMs;
        long finMs;

        private Paso(final Tipo tipo, final Point desde, final Point hasta, final double peso, final Runnable accion) {
            this(tipo, desde, hasta, null, peso, accion);
        }

        private Paso(final Tipo tipo, final Point desde, final Point hasta, final Point[] ruta, final double peso, final Runnable accion) {
            this.tipo = tipo;
            this.desde = desde;
            this.hasta = hasta;
            this.ruta = ruta;
            this.peso = peso;
            this.accion = accion;
        }

        /** Sube por la trampilla. */
        public static Paso salir(final Point trampilla) {
            return new Paso(Tipo.SALIR, trampilla, trampilla, 1.0, null);
        }

        /** Va de una trampilla a otra, acelerando hasta chocar (como cayendo). */
        public static Paso viajar(final Point desde, final Point hasta) {
            return recorrer(desde, hasta);
        }

        /** Recorre varios puntos de seguido en una sola carrera que acelera hasta el último, sin pararse en los intermedios. */
        public static Paso recorrer(final Point... puntos) {
            return new Paso(Tipo.VIAJAR, puntos[0], puntos[puntos.length - 1], puntos, 2.4, null);
        }

        /** Impacto en la trampilla: pulso, destello y sacudida pequeña. */
        public static Paso golpe(final Point trampilla) {
            return new Paso(Tipo.GOLPE, trampilla, trampilla, 0.8, null);
        }

        /** Se queda quieto en la trampilla (null: no se ve nada, solo pasa el tiempo). */
        public static Paso esperar(final Point trampilla, final double peso) {
            return new Paso(Tipo.ESPERAR, trampilla, trampilla, peso, null);
        }

        /** Baja por la trampilla. */
        public static Paso esconder(final Point trampilla) {
            return new Paso(Tipo.ESCONDER, trampilla, trampilla, 0.8, null);
        }

        /** Algo que ocurre en un instante (no dura): daño, curación, sacudida... */
        public static Paso efecto(final Runnable accion) {
            return new Paso(Tipo.EFECTO, null, null, 0, accion);
        }
    }

    private final Figura figuraAtacante;
    private final Image imagen;
    private final Supplier<List<Paso>> planificador;

    private List<Paso> pasos = new ArrayList<>();
    private int cursor;          // Siguiente paso cuyo inicio aún no se procesó
    private long startTime;
    private long duracion;
    private boolean activo = true;
    private boolean terminadaAnimacion = false;
    private boolean iniciado = false;

    public Ataque(final Figura figuraAtacante, final Image imagen, final Supplier<List<Paso>> planificador) {
        this.figuraAtacante = figuraAtacante;
        this.imagen = imagen;
        this.planificador = planificador;
    }

    /** Calcula los pasos con el estado actual del tablero y arranca el cronómetro. */
    public void iniciar() {
        pasos = new ArrayList<>();
        for (Paso p : planificador.get()) {
            if (p.tipo == Tipo.GOLPE) { // Cada golpe sacude un poco la pantalla
                pasos.add(Paso.efecto(() -> {
                    Sacudida.iniciar(3, 180);
                    AudioPlayer.getInstance().playSoundOnce(Constantes.RUTAS.EFECTOS.IMPACTO);
                }));
            }
            pasos.add(p);
        }

        double unidadMs = duracionPorDefecto / PESO_RECORRIDO_COMPLETO;
        double acumulado = 0;
        for (Paso p : pasos) {
            p.inicioMs = (long) (acumulado * unidadMs);
            acumulado += p.peso;
            p.finMs = (long) (acumulado * unidadMs);
        }
        duracion = (long) (acumulado * unidadMs);
        cursor = 0;
        iniciado = true;
        startTime = System.currentTimeMillis();
        AudioPlayer.getInstance().playSoundOnce(figuraAtacante.getSonidoAtaque());
    }

    public void actualizar() {
        if (!activo) {
            return;
        }
        if (!iniciado) {
            iniciar();
        }

        long t = System.currentTimeMillis() - startTime;
        boolean fin = t >= duracion;
        while (cursor < pasos.size() && (fin || pasos.get(cursor).inicioMs <= t)) {
            Paso p = pasos.get(cursor++);
            if (p.accion != null) {
                p.accion.run(); // Los efectos siempre se aplican, aunque el equipo vaya con lag
            }
        }

        if (fin) {
            activo = false;
            terminadaAnimacion = true;
        }
    }

    public void dibujar(final Graphics g) {
        if (!activo || !iniciado) {
            return;
        }
        long t = System.currentTimeMillis() - startTime;
        for (Paso p : pasos) {
            if (p.tipo != Tipo.EFECTO && t >= p.inicioMs && t < p.finMs) {
                double u = (double) (t - p.inicioMs) / Math.max(1, p.finMs - p.inicioMs);
                dibujarPaso((Graphics2D) g, p, u);
                return;
            }
        }
    }

    private void dibujarPaso(final Graphics2D g, final Paso p, final double u) {
        double x = p.desde == null ? 0 : p.desde.x;
        double y = p.desde == null ? 0 : p.desde.y;
        double escala = 1;
        double elevacion = 0; // Sube un poco al salir de la trampilla

        switch (p.tipo) {
            case SALIR:
                escala = Math.max(0, Interpolador.RETROCESO.aplicar(u));
                elevacion = 12 * Interpolador.SALIDA.aplicar(u);
                break;
            case VIAJAR:
                // Una sola carrera que arranca despacio y acelera hasta el objetivo; los puntos intermedios solo curvan el camino
                double[] punto = puntoDelRecorrido(p.ruta, Interpolador.ENTRADA.aplicar(u));
                x = punto[0];
                y = punto[1];
                elevacion = 12;
                break;
            case GOLPE:
                escala = 1 + 0.3 * Math.sin(Math.PI * u);
                elevacion = 12 + 16 * (1 - Interpolador.REBOTE.aplicar(u)); // Rebota al chocar y se asienta
                break;
            case ESPERAR:
                if (p.desde == null) {
                    return;
                }
                elevacion = 12;
                break;
            case ESCONDER:
                escala = 1 - Interpolador.ENTRADA.aplicar(u);
                elevacion = 12 * (1 - Interpolador.ENTRADA.aplicar(u));
                break;
            default:
                return;
        }

        if (p.tipo == Tipo.VIAJAR) {
            dibujarEstela(g, p, u, elevacion);
        }
        int ancho = (int) Math.round(imagen.getWidth(null) * escala);
        int alto = (int) Math.round(imagen.getHeight(null) * escala);
        if (ancho > 0 && alto > 0) {
            g.drawImage(imagen, (int) Math.round(x - ancho / 2.0), (int) Math.round(y - elevacion - alto / 2.0), ancho, alto, null);
        }

        Estilo estilo = figuraAtacante.getEstiloAtaque();
        if (estilo == Estilo.TAJO && p.tipo == Tipo.GOLPE) { // Tajo: un arco blanco que barre el punto de impacto
            dibujarTajo(g, x, y - elevacion, u);
        }
        if (estilo == Estilo.DESTELLO && (p.tipo == Tipo.SALIR || p.tipo == Tipo.ESPERAR)) { // Halo dorado del Sacerdote
            int radio = (int) (20 + 10 * Math.sin(Math.PI * Math.min(1, u * 1.5)));
            java.awt.Composite original = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.5 * (p.tipo == Tipo.SALIR ? u : 1 - u * 0.5))));
            g.setColor(new Color(0xFFE08A));
            g.fillOval((int) x - radio, (int) (y - elevacion) - radio, radio * 2, radio * 2);
            g.setComposite(original);
        }
        if (p.tipo == Tipo.GOLPE) { // Destello que crece y se desvanece
            int radio = (int) (14 + 30 * u);
            java.awt.Composite original = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.35 * (1 - u))));
            g.setColor(Color.WHITE);
            g.fillOval((int) x - radio, (int) (y - elevacion) - radio, radio * 2, radio * 2);
            g.setComposite(original);
            Impacto.dibujar(g, x, y - elevacion, u); // Animación de 5 frames sobre el destello
        }
    }

    // Copias del sprite detrás del proyectil (y chispas en el fuego del Mago), tanto más largas cuanto más rápido va
    private void dibujarEstela(final Graphics2D g, final Paso p, final double u, final double elevacion) {
        Estilo estilo = figuraAtacante.getEstiloAtaque();
        int copias = estilo == Estilo.ESTELA_LARGA ? 4 : (estilo == Estilo.FUEGO ? 5 : (estilo == Estilo.ESTELA_CORTA ? 2 : 0));
        if (copias == 0) {
            return;
        }
        java.awt.Composite original = g.getComposite();
        double fraccion = Interpolador.ENTRADA.aplicar(u);
        for (int k = copias; k >= 1; k--) {
            double antes = Math.max(0, Interpolador.ENTRADA.aplicar(Math.max(0, u - k * 0.045)));
            double[] q = puntoDelRecorrido(p.ruta, antes);
            float alfa = 0.45f * (1 - k / (float) (copias + 1));
            double e = 1 - 0.07 * k;
            int ancho = (int) (imagen.getWidth(null) * e);
            int alto = (int) (imagen.getHeight(null) * e);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
            g.drawImage(imagen, (int) (q[0] - ancho / 2.0), (int) (q[1] - elevacion - alto / 2.0), ancho, alto, null);
            if (estilo == Estilo.FUEGO && fraccion > 0.05) { // Brasas que se quedan atrás
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa + 0.15f));
                g.setColor(new Color(0xFF9A2E));
                int r = 5 - k / 2;
                g.fillOval((int) q[0] - r + (k % 2 == 0 ? 6 : -6), (int) (q[1] - elevacion) - r + (k % 3 - 1) * 5, r * 2, r * 2);
            }
        }
        g.setComposite(original);
    }

    private static void dibujarTajo(final Graphics2D g, final double cx, final double cy, final double u) {
        java.awt.Composite original = g.getComposite();
        java.awt.Stroke trazo = g.getStroke();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.max(0, 1 - u)));
        g.setColor(Color.WHITE);
        g.setStroke(new java.awt.BasicStroke(5f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
        int radio = 30;
        g.drawArc((int) cx - radio, (int) cy - radio, radio * 2, radio * 2, 135, (int) (-120 * Math.min(1, u * 2.5)));
        g.setStroke(trazo);
        g.setComposite(original);
    }

    // Punto a la fracción f (0 a 1) de la longitud total de la ruta
    private static double[] puntoDelRecorrido(final Point[] ruta, final double f) {
        double total = 0;
        for (int i = 1; i < ruta.length; i++) {
            total += ruta[i - 1].distance(ruta[i]);
        }
        double restante = f * total;
        for (int i = 1; i < ruta.length; i++) {
            double tramo = ruta[i - 1].distance(ruta[i]);
            if (restante <= tramo || i == ruta.length - 1) {
                double k = tramo == 0 ? 1 : Math.min(1, restante / tramo);
                return new double[]{ruta[i - 1].x + (ruta[i].x - ruta[i - 1].x) * k, ruta[i - 1].y + (ruta[i].y - ruta[i - 1].y) * k};
            }
            restante -= tramo;
        }
        return new double[]{ruta[0].x, ruta[0].y};
    }

    public Figura getFiguraAtacante() {
        return figuraAtacante;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean terminadaAnimacion() {
        return terminadaAnimacion;
    }
}
