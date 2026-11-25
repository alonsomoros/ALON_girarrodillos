package main.control;

public class Tecla {

    private boolean pulsada = false;
    private boolean procesada = false;
    private long ultimaPulsacion = System.nanoTime();

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
