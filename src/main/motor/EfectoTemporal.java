package main.motor;

/**
 * Base para efectos que duran un tiempo fijo: lleva la cuenta de su edad y de su progreso (0 a 1),
 * que se puede pasar a un {@link Interpolador.Easing} para animar posición, tamaño o transparencia.
 */
public abstract class EfectoTemporal implements Efecto {

    private final long inicio = Tiempo.ahora();
    private final int duracionTicks;

    protected EfectoTemporal(final int duracionMs) {
        this.duracionTicks = Math.max(1, Tiempo.msATicks(duracionMs));
    }

    /** Ticks transcurridos desde que se creó. */
    protected long edad() {
        return Tiempo.ahora() - inicio;
    }

    /** Avance de 0 (recién creado) a 1 (fin). */
    protected double progreso() {
        return Interpolador.limitar(edad() / (double) duracionTicks);
    }

    @Override
    public void actualizar() {
        // Por defecto el efecto solo depende de su progreso al dibujarse
    }

    @Override
    public boolean terminado() {
        return edad() >= duracionTicks;
    }
}
