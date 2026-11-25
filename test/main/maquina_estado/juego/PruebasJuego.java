package main.maquina_estado.juego;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

/**
 * Pruebas de las clases del juego. Está en el mismo paquete que ellas para poder
 * usar los miembros protegidos/de paquete (actualizarEstados, estaAtacando, ...).
 */
public class PruebasJuego {

    // Pone las dos figuras con el activador a 0 y deja que el jugador lance sus ataques
    private static Player jugadorConFiguras(final Figura izquierda, final Figura derecha) {
        Player p = new Player();
        p.setFiguraIzquierda(izquierda);
        p.setFiguraDerecha(derecha);
        izquierda.getActivador().setAltura(0);
        derecha.getActivador().setAltura(0);
        return p;
    }

    public static void ejecutar() {
        prueba("Mago a la derecha ataca 2 veces y la izquierda (Arquero) 1", () -> {
            Player p0 = new Player();
            Figura arquero = new main.maquina_estado.juego.figuras.Arquero(p0);
            Figura mago = new main.maquina_estado.juego.figuras.Mago(p0);
            Player p = jugadorConFiguras(arquero, mago);
            p.actualizarEstados();
            verificar(arquero.getAtaquesLanzados() == 1, "Arquero: " + arquero.getAtaquesLanzados());
            verificar(mago.getAtaquesLanzados() == 2, "Mago: " + mago.getAtaquesLanzados());
        });

        prueba("Arquero a la derecha con Mago a la izquierda ataca 1 sola vez", () -> {
            Player p0 = new Player();
            Figura mago = new main.maquina_estado.juego.figuras.Mago(p0);
            Figura arquero = new main.maquina_estado.juego.figuras.Arquero(p0);
            Player p = jugadorConFiguras(mago, arquero);
            p.actualizarEstados();
            verificar(mago.getAtaquesLanzados() == 2, "Mago: " + mago.getAtaquesLanzados());
            verificar(arquero.getAtaquesLanzados() == 1, "Arquero: " + arquero.getAtaquesLanzados());
        });


        prueba("el jugador y la IA tienen rodillos distintos", () -> {
            Player player = new Player();
            IA ia = new IA();
            verificar(player.getRodillo() != null && ia.getRodillo() != null, "rodillo null");
            verificar(player.getRodillo() != ia.getRodillo(), "comparten el mismo rodillo");
        });
    }
}
