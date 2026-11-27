package main.maquina_estado;

import main.motor.Tiempo;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

public class PruebasEstados {

    private static void pasar(final GestorEstados g, final int ticks) {
        for (int i = 0; i < ticks; i++) {
            Tiempo.avanzar();
            g.getEfectos().actualizar();
        }
    }

    public static void ejecutar() {
        prueba("GestorEstados.irA: cambia de estado a mitad del fundido, ignora otra petición mientras dura y termina", () -> {
            GestorEstados g = GestorEstados.getInstance();
            verificar(g.getEstadoActualId() == Estado.MENU, "empieza en el menú: " + g.getEstadoActualId());
            g.irA(Estado.TIENDA);
            verificar(g.getEstadoActualId() == Estado.MENU && g.estaTransicionando(), "no cambia al instante");
            g.irA(Estado.CREDITOS); // ignorada
            pasar(g, 6);
            verificar(g.getEstadoActualId() == Estado.MENU, "todavía fundiendo a negro");
            pasar(g, 10); // 16 ticks: ya pasó el fundido de salida (13)
            verificar(g.getEstadoActualId() == Estado.TIENDA, "debería estar en la tienda: " + g.getEstadoActualId());
            pasar(g, 30);
            verificar(!g.estaTransicionando(), "la transición debería haber terminado");
            verificar(g.getEfectos().cantidad() == 0, "quedan efectos: " + g.getEfectos().cantidad());
            g.irA(Estado.MENU);
            pasar(g, 40);
            verificar(g.getEstadoActualId() == Estado.MENU, "vuelta al menú");
        });
    }
}
