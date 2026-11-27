package main.maquina_estado.juego;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Orden de acción de una ronda: los héroes que se activan (de los dos equipos) actúan de uno en uno por prioridad
 * (Asesino, Sacerdote, Ingeniero, Mago, Guerrero, Arquero). Entre iguales, el primer equipo (jugador) va antes.
 * Cuando se vacía la cola se vuelve a mirar: quien quede activado por una ayuda (Sacerdote) actúa después de todos.
 * Un héroe retrasado por un Asesino antes de su turno pierde la acción.
 */
class ColaAcciones {

    private static final int MAX_ACCIONES_POR_RONDA = 24; // Tope de seguridad: nunca hay bucles infinitos

    private final EsqueletoPlayer primero;
    private final EsqueletoPlayer segundo;
    private final Deque<Figura> pendientes = new ArrayDeque<>();
    private boolean activa = false;
    private int acciones;

    ColaAcciones(final EsqueletoPlayer primero, final EsqueletoPlayer segundo) {
        this.primero = primero;
        this.segundo = segundo;
    }

    /** Empieza una ronda: reúne los héroes activados de los dos equipos. */
    void iniciarRonda() {
        primero.terminadoDeProcesarAtaques = false;
        segundo.terminadoDeProcesarAtaques = false;
        acciones = 0;
        activa = true;
        reunir();
    }

    boolean estaActiva() {
        return activa;
    }

    /**
     * Se llama en cada actualización: lanza la siguiente acción cuando no hay ninguna en marcha.
     * Devuelve false cuando la ronda ha terminado.
     */
    boolean avanzar() {
        if (!activa) {
            return false;
        }
        if (primero.figurasAtacando() || segundo.figurasAtacando()) {
            return true; // Una acción sigue animándose
        }
        while (acciones < MAX_ACCIONES_POR_RONDA) {
            Figura siguiente = pendientes.poll();
            if (siguiente == null) {
                reunir(); // ¿Alguien quedó activado por una ayuda?
                if (pendientes.isEmpty()) {
                    break;
                }
                continue;
            }
            if (siguiente.getActivador().getAltura() == 0 && !siguiente.estaAtacando) { // Si lo retrasaron, no actúa
                acciones++;
                siguiente.atacarNormal();
                if (siguiente.estaAtacando) {
                    return true;
                }
            }
        }
        terminar();
        return false;
    }

    private void terminar() {
        pendientes.clear();
        activa = false;
        primero.terminadoDeProcesarAtaques = true;
        segundo.terminadoDeProcesarAtaques = true;
    }

    // Añade los héroes activados, ordenados por prioridad (estable: en empate van primero los del jugador)
    private void reunir() {
        List<Figura> activados = new ArrayList<>();
        anadirSiActivada(activados, primero.getFiguraIzquierda());
        anadirSiActivada(activados, primero.getFiguraDerecha());
        anadirSiActivada(activados, segundo.getFiguraIzquierda());
        anadirSiActivada(activados, segundo.getFiguraDerecha());
        activados.sort((a, b) -> Integer.compare(a.getPrioridad(), b.getPrioridad()));
        pendientes.addAll(activados);
    }

    private void anadirSiActivada(final List<Figura> lista, final Figura figura) {
        if (figura != null && figura.getActivador().getAltura() == 0 && !figura.estaAtacando && !pendientes.contains(figura)) {
            lista.add(figura);
        }
    }
}
