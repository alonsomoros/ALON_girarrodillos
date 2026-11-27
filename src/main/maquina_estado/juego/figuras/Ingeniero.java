package main.maquina_estado.juego.figuras;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.EsqueletoPlayer;

public class Ingeniero extends Figura {

    public Ingeniero(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(4);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }
    // Primero el martillo en su lado de la corona (sube su bastión 2) y después el ataque, como el Caballero
    @Override
    protected java.util.List<Ataque> crearAtaques() {
        return java.util.List.of(
                ataqueEnPunto(this::coronaPropia, false, () -> getDuenio().getBastion().aumentarAltura(2)),
                ataqueAlEnemigo(() -> enemigoTieneBastion(), () -> golpeNormal(getEnemigo()), 0));
    }

    @Override
    public int getPrioridad() {
        return 3;
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
