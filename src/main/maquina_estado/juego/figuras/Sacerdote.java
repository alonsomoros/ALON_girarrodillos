package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.IA;
import main.maquina_estado.juego.Player;

import java.awt.*;

public class Sacerdote extends Figura {


    public Sacerdote(Player player) {
        super(player);
        alturaDefault = 4;
        activador = new Activador(alturaDefault);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.SACERDOTE_STATS);
    }

    public Sacerdote(IA ia) {
        super(ia);
        alturaDefault = 4;
        activador = new Activador(alturaDefault);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.SACERDOTE_STATS);
    }


//    @Override
//    public void atacar(Graphics g) {
//        estaAtacando = true;
//        ataque.setStartTime(System.currentTimeMillis());
//        Depurar.log("Sacerdote atacando");
////        Dibujador.IMAGEN.dibujar(g, ataqueImagen, new Point(50,500));
//    }

    @Override
    public void atacar(Graphics graphics) {
        Depurar.log("Sacerdote atacando");
    }

    @Override
    public void atacarEspecial(Graphics g) {
        Depurar.log("Sacerdote atacando especial");
    }

    public int getStat1() { // Curacion
        return switch (nivel) {
            case 1 -> 1;
            case 2, 3 -> 2;
            default -> -1;
        };
    }

    public int getStat2() { // Activador
        return switch (nivel) {
            case 1, 2 -> 2;
            case 3 -> 3;
            default -> -1;
        };
    }
}
