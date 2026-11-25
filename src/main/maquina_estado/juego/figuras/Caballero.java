package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.EsqueletoPlayer;

import java.awt.*;

public class Caballero extends Figura {

    public Caballero(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(3);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    @Override
    public void atacar(Graphics graphics) {
        Depurar.log("Caballero atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        Depurar.log("Caballero atacando especial");
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
