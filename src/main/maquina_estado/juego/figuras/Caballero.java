package main.maquina_estado.juego.figuras;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.IA;
import main.maquina_estado.juego.Player;

import java.awt.*;

public class Caballero extends Figura {

    public Caballero(Player player) {
        super(player);
        alturaDefault = 3;
        activador = new Activador(alturaDefault);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    public Caballero(IA ia) {
        super(ia);
        alturaDefault = 3;
        activador = new Activador(alturaDefault);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    @Override
    public void atacar(Graphics graphics) {
        System.out.println("Caballero atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        System.out.println("Caballero atacando especial");
    }

    public int getStat1() {
        return switch (nivel) {
            case 1 -> 3;
            case 2 -> 5;
            case 3 -> 7;
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
