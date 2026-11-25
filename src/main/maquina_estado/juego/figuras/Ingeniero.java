package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.EsqueletoPlayer;

import java.awt.*;

public class Ingeniero extends Figura {

    public Ingeniero(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(4);

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
