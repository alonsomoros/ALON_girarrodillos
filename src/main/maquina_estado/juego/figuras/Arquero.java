package main.maquina_estado.juego.figuras;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.EsqueletoPlayer;

public class Arquero extends Figura {

    public Arquero(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(4);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    // Ataca a 3 de altura: si el bastión llega a 3 o más lo frena (baja 3); si no, pasa por encima y golpea la corona
    @Override
    protected java.util.List<Ataque> crearAtaques() {
        return java.util.List.of(ataqueAlEnemigo(this::muroDeTresOMas, () -> {
            if (muroDeTresOMas()) {
                getEnemigo().getBastion().disminuirAltura(3);
            } else {
                getEnemigo().recibirAtaque(getStat1());
            }
        }, 0));
    }

    private boolean muroDeTresOMas() {
        return enemigoTieneBastion() && getEnemigo().getBastion().getAltura() >= 3;
    }

    @Override
    public Ataque.Estilo getEstiloAtaque() {
        return Ataque.Estilo.ESTELA_LARGA;
    }

    @Override
    public int getPrioridad() {
        return 6;
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
