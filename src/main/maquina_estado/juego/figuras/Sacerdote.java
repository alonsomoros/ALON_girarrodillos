package main.maquina_estado.juego.figuras;

import main.herramientas.Depurar;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.EsqueletoPlayer;

import java.awt.*;

public class Sacerdote extends Figura {


    public Sacerdote(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(4);

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

    // Cura la corona de su equipo y da energía a su compañero
    @Override
    protected void aplicarEfecto(Ataque ataque, EsqueletoPlayer enemigo) {
        if (getDuenio().getVida() != Constantes.VIDA_MAXIMA) {
            getDuenio().recibirCuracion(getStat1());
        }

        if (getCompaniero().getActivador().getAltura() > 0) {
            getCompaniero().getActivador().disminuirAltura(getStat2());
        }
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
