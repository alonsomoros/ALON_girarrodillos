package main.control;

import main.GestorPrincipal;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Teclado implements KeyListener {
    public Tecla arriba = new Tecla();
    public Tecla abajo = new Tecla();
    public Tecla izquierda = new Tecla();
    public Tecla derecha = new Tecla();
    public Tecla espacio = new Tecla();
    public Tecla teclaE = new Tecla();
    public Tecla X = new Tecla();
    public Tecla f2 = new Tecla();


    public final long INTERVALO_PULSACION = 1;

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                arriba.teclaPulsada();
                break;
            case KeyEvent.VK_E:
                this.teclaE.teclaPulsada();
                break;
            case KeyEvent.VK_S:
                abajo.teclaPulsada();
                break;
            case KeyEvent.VK_A:
                izquierda.teclaPulsada();
                break;
            case KeyEvent.VK_D:
                derecha.teclaPulsada();
                break;
            case KeyEvent.VK_SPACE:
                espacio.teclaPulsada();
                break;
            case KeyEvent.VK_ESCAPE:
                GestorPrincipal.solicitarCierre(); // El bucle principal cierra todo de forma ordenada
                break;

            case KeyEvent.VK_ENTER:
                X.teclaPulsada();
                break;
            case KeyEvent.VK_F2:
                f2.teclaPulsada();
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                arriba.teclaLiberada();
                break;
            case KeyEvent.VK_E:
                this.teclaE.teclaLiberada();
                break;
            case KeyEvent.VK_S:
                abajo.teclaLiberada();
                break;
            case KeyEvent.VK_A:
                izquierda.teclaLiberada();
                break;
            case KeyEvent.VK_D:
                derecha.teclaLiberada();
                break;
            case KeyEvent.VK_SPACE:
                espacio.teclaLiberada();
                break;
            case KeyEvent.VK_ENTER:
                X.teclaLiberada();
                break;
            case KeyEvent.VK_F2:
                f2.teclaLiberada();
                break;

        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void procesarTecla(Tecla tecla, Runnable accion, long tiempoActual) {
        if (tecla.estaPulsada()) {
            if (!tecla.estaProcesada() &&
                    tiempoActual - tecla.obtenerUltimaPulsacion() > this.INTERVALO_PULSACION) {

                accion.run();
                tecla.setProcesada(true);
                tecla.teclaPulsada(); // Actualiza la última pulsación
            }
        } else {
            tecla.setProcesada(false);
        }
    }

}

