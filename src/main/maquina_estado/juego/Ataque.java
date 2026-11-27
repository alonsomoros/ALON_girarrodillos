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
    /** Duración en ms de un recorrido completo (salir, dos viajes, golpe y esconder). Las pruebas la acortan. */
    static long duracionPorDefecto = 2000;

    private static final double PESO_RECORRIDO_COMPLETO = 5.8;

    enum Tipo {SALIR, VIAJAR, GOLPE, ESPERAR, ESCONDER, EFECTO}

    /** Un paso de la línea de tiempo. Se crean con las funciones estáticas. */
    public static final class Paso {
        final Tipo tipo;
        final Point desde;
        final Point hasta;
        final double peso;
        final Runnable accion;
        long inicioMs;
        long finMs;

        private Paso(final Tipo tipo, final Point desde, final Point hasta, final double peso, final Runnable accion) {
            this.tipo = tipo;
            this.desde = desde;
            this.hasta = hasta;
            this.peso = peso;
            this.accion = accion;
        }

        /** Sube por la trampilla. */
        public static Paso salir(final Point trampilla) {
            return new Paso(Tipo.SALIR, trampilla, trampilla, 1.0, null);
        }

        /** Se desliza de una trampilla a otra. */
        public static Paso viajar(final Point desde, final Point hasta) {
            return new Paso(Tipo.VIAJAR, desde, hasta, 1.6, null);
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
                pasos.add(Paso.efecto(() -> Sacudida.iniciar(3, 180)));
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
        AudioPlayer.getInstance().playSoundOnce(Constantes.RUTAS.EFECTOS.ATAQUE);
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
                double e = Interpolador.ENTRADA_SALIDA.aplicar(u);
                x = p.desde.x + (p.hasta.x - p.desde.x) * e;
                y = p.desde.y + (p.hasta.y - p.desde.y) * e;
                elevacion = 12;
                break;
            case GOLPE:
                escala = 1 + 0.3 * Math.sin(Math.PI * u);
                elevacion = 12;
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

        int ancho = (int) Math.round(imagen.getWidth(null) * escala);
        int alto = (int) Math.round(imagen.getHeight(null) * escala);
        if (ancho > 0 && alto > 0) {
            g.drawImage(imagen, (int) Math.round(x - ancho / 2.0), (int) Math.round(y - elevacion - alto / 2.0), ancho, alto, null);
        }

        if (p.tipo == Tipo.GOLPE) { // Destello que crece y se desvanece
            int radio = (int) (14 + 30 * u);
            java.awt.Composite original = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (0.7 * (1 - u))));
            g.setColor(Color.WHITE);
            g.fillOval((int) x - radio, (int) (y - elevacion) - radio, radio * 2, radio * 2);
            g.setComposite(original);
        }
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
