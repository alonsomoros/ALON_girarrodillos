package main.maquina_estado.juego.figuras;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.EsqueletoPlayer;

public class Sacerdote extends Figura {

    public Sacerdote(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(4);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.SACERDOTE_STATS);
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
