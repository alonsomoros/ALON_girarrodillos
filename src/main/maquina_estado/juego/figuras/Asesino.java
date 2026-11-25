package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.IA;
import main.maquina_estado.juego.Player;

import java.awt.*;

public class Asesino extends Figura {

    public Asesino(Player player) {
        super(player);
        alturaDefault = 3;
        activador = new Activador(alturaDefault);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.ASESINO_STATS);
    }

    public Asesino(IA ia) {
        super(ia);
        alturaDefault = 3;
        activador = new Activador(alturaDefault);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.ASESINO_STATS);
    }

    @Override
    public void atacar(Graphics graphics) {
        Depurar.log("Asesino atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        Depurar.log("Asesino atacando especial");
    }

    public int getStat1() {
        return switch (nivel) {
            case 1, 2 -> 1;
            case 3 -> 2;
            default -> -1;
        };
    }

    public int getStat2() {
        return switch (nivel) {
            case 1 -> 1;
            case 2, 3 -> 2;
            default -> -1;
        };
    }
}
