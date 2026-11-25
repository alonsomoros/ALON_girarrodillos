package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.IA;
import main.maquina_estado.juego.Player;

import java.awt.*;

public class Ingeniero extends Figura {

    public Ingeniero(Player player) {
        super(player);
        alturaDefault = 4;
        activador = new Activador(alturaDefault);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }
    public Ingeniero(IA ia) {
        super(ia);
        alturaDefault = 4;
        activador = new Activador(alturaDefault);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    @Override
    public void atacar(Graphics graphics) {
        Depurar.log("Ingeniero atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        Depurar.log("Ingeniero atacando especial");
    }

    public int getStat1() {
        return switch (nivel) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 4;
            default -> -1;
        };
    }

    public int getStat2() {
        return switch (nivel) {
            case 1 -> 3;
            case 2, 3 -> 5;
            default -> -1;
        };
    }
}
