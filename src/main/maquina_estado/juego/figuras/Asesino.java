package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.EsqueletoPlayer;

import java.awt.*;

public class Asesino extends Figura {

    public Asesino(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(3);
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
