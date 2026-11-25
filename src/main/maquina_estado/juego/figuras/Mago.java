package main.maquina_estado.juego.figuras;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.IA;
import main.maquina_estado.juego.Player;

import java.awt.*;

public class Mago extends Figura {

    public Mago(Player player) {
        super(player);
        alturaDefault = 5;
        activador = new Activador(alturaDefault);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    public Mago(IA ia) {
        super(ia);
        alturaDefault = 5;
        activador = new Activador(alturaDefault);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    @Override
    public void atacar(Graphics graphics) {
        System.out.println("Mago atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        System.out.println("Mago atacando especial");
    }

    public int getStat1() {
        return switch (nivel) {
            case 1 -> 2;
            case 2, 3 -> 3;
            default -> -1;
        };
    }

    public int getStat2() {
        return switch (nivel) {
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 5;
            default -> -1;
        };
    }


}
