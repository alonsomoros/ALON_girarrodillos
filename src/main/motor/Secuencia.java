package main.motor;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleConsumer;

/**
 * Línea de tiempo: una lista de pasos que se ejecutan en orden, uno detrás de otro. Sirve para coreografiar cosas
 * ("espera 300 ms, lanza el proyectil durante 500 ms, y al llegar haz el daño y suena el impacto") sin escribir
 * máquinas de estado a mano. Es un {@link Efecto}: se añade a un {@link GestorEfectos} y avanza sola.
 *
 * <pre>
 * Secuencia.nueva()
 *     .esperar(300)
 *     .durante(500, t -&gt; x = Interpolador.interpolar(0, 100, t, Interpolador.SALIDA))
 *     .hacer(() -&gt; recibirDanio())
 *     .esperar(200)
 *     .hacer(() -&gt; sonar());
 * </pre>
 */
public class Secuencia implements Efecto {

    private static class Paso {
        final int duracionTicks;      // 0 = instantáneo
        final DoubleConsumer avance;  // Recibe el progreso 0..1 en cada tick (puede ser null)
        final Runnable accion;        // Se ejecuta una vez (pasos instantáneos)
        int transcurridos = 0;

        Paso(final int duracionTicks, final DoubleConsumer avance, final Runnable accion) {
            this.duracionTicks = duracionTicks;
            this.avance = avance;
            this.accion = accion;
        }
    }

    private final List<Paso> pasos = new ArrayList<>();
    private int indice = 0;
    private boolean cancelada = false;

    private Secuencia() {
    }

    public static Secuencia nueva() {
        return new Secuencia();
    }

    /** Pausa de ms milisegundos. */
    public Secuencia esperar(final long ms) {
        pasos.add(new Paso(Tiempo.msATicks(ms), null, null));
        return this;
    }

    /** Ejecuta algo una vez, al llegar su turno (no consume tiempo). */
    public Secuencia hacer(final Runnable accion) {
        pasos.add(new Paso(0, null, accion));
        return this;
    }

    /** Durante ms milisegundos llama a avance en cada tick con el progreso (0 a 1); el último valor es siempre 1. */
    public Secuencia durante(final long ms, final DoubleConsumer avance) {
        pasos.add(new Paso(Math.max(1, Tiempo.msATicks(ms)), avance, null));
        return this;
    }

    /** Detiene la secuencia: los pasos pendientes no se ejecutan. */
    public void cancelar() {
        cancelada = true;
    }

    @Override
    public void actualizar() {
        if (terminado()) {
            return;
        }
        ejecutarInstantaneos();
        if (indice >= pasos.size()) {
            return;
        }

        Paso paso = pasos.get(indice);
        paso.transcurridos++;
        if (paso.avance != null) {
            paso.avance.accept(Math.min(1.0, paso.transcurridos / (double) paso.duracionTicks));
        }
        if (paso.transcurridos >= paso.duracionTicks) {
            indice++;
            ejecutarInstantaneos(); // Lo que sigue a una espera ocurre en el mismo tick en que esta acaba
        }
    }

    private void ejecutarInstantaneos() {
        while (!cancelada && indice < pasos.size() && pasos.get(indice).duracionTicks == 0) {
            Runnable accion = pasos.get(indice).accion;
            indice++;
            if (accion != null) {
                accion.run();
            }
        }
    }

    @Override
    public void dibujar(final Graphics graficos) {
        // Una secuencia no dibuja nada
    }

    @Override
    public boolean terminado() {
        return cancelada || indice >= pasos.size();
    }
}
