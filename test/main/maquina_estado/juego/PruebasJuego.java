package main.maquina_estado.juego;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

/**
 * Pruebas de las clases del juego. Está en el mismo paquete que ellas para poder
 * usar los miembros protegidos/de paquete (actualizarEstados, estaAtacando, ...).
 */
public class PruebasJuego {

    public static void ejecutar() {
        prueba("el jugador y la IA tienen rodillos distintos", () -> {
            Player player = new Player();
            IA ia = new IA();
            verificar(player.getRodillo() != null && ia.getRodillo() != null, "rodillo null");
            verificar(player.getRodillo() != ia.getRodillo(), "comparten el mismo rodillo");
        });
    }
}
