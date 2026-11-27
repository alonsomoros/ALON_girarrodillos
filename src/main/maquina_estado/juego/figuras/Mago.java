package main.maquina_estado.juego.figuras;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.EsqueletoPlayer;

public class Mago extends Figura {

    public Mago(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(5);

        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.GUERREROS_STATS);
    }

    // Ataca x2: la 1.ª bola la frena el muro o pega en la corona; la 2.ª ignora cualquier muro y pega en la corona
    @Override
    protected java.util.List<Ataque> crearAtaques() {
        return java.util.List.of(
                ataqueAlEnemigo(() -> enemigoTieneBastion(), () -> golpeNormal(getEnemigo()), 0),
                ataqueAlEnemigo(null, () -> getEnemigo().recibirAtaque(getStat1()), 0.4));
    }

    @Override
    public Ataque.Estilo getEstiloAtaque() {
        return Ataque.Estilo.FUEGO;
    }

    @Override
    public int getPrioridad() {
        return 4;
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
