package main.control;

public class Tecla {

    private volatile boolean pulsada = false;
    private volatile boolean procesada = false;
    private volatile long ultimaPulsacion = System.nanoTime();

    public void teclaPulsada() {
        pulsada = true;
        ultimaPulsacion = System.nanoTime();
    }

    public void teclaLiberada() {
        pulsada = false;
    }

    public boolean estaPulsada() {
        return pulsada;
    }

    public boolean estaProcesada() {
        return procesada;
    }

    public void setProcesada(boolean procesada) {
        this.procesada = procesada;
    }

    public long obtenerUltimaPulsacion() {
        return ultimaPulsacion;
    }
}
