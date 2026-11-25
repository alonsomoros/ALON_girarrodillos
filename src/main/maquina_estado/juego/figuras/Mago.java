package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.EsqueletoPlayer;

import java.awt.*;

public class Mago extends Figura {

    public Mago(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(5);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    @Override
    public void atacar(Graphics graphics) {
        Depurar.log("Mago atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        Depurar.log("Mago atacando especial");
    }

    @Override
    public int getNumeroAtaques() {
        return 2;
    }

    // Ataca x2: el 1.º a nivel de suelo (lo frena el bastión) y el 2.º vuela a altura máxima
    @Override
    protected void aplicarEfecto(Ataque ataque, EsqueletoPlayer enemigo) {
        if (ataque.ignoraBastion()) {
            enemigo.recibirAtaque(getStat1());
        } else {
            golpeNormal(enemigo);
        }
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
