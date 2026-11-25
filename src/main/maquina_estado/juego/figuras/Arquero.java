package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.EsqueletoPlayer;


import java.awt.*;

public class Arquero extends Figura {

    public Arquero(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(4);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    @Override
    public void atacar(Graphics graphics) {
        Depurar.log("Arquero atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        Depurar.log("Arquero atacando especial");
    }


    // Ataca a 3 de altura: si el bastión llega a 3 o más lo frena (baja 3); si no, golpea la corona
    @Override
    protected void aplicarEfecto(Ataque ataque, EsqueletoPlayer enemigo) {
        if (enemigo.tieneBation() && enemigo.getBastion().getAltura() >= 3) {
            enemigo.getBastion().disminuirAltura(3);
        } else {
            enemigo.recibirAtaque(getStat1());
        }
    }

    public int getStat1() {
        return switch (nivel) {
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 6;
            default -> -1;
        };
    }

    public int getStat2() {
        return switch (nivel) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            default -> -1;
        };
    }
}
