package main.maquina_estado.juego.figuras;

import java.awt.Point;
import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.maquina_estado.juego.Activador;
import main.maquina_estado.juego.Figura;
import main.maquina_estado.juego.Ataque;
import main.maquina_estado.juego.Trampillas;
import main.maquina_estado.juego.EsqueletoPlayer;

public class Asesino extends Figura {

    public Asesino(EsqueletoPlayer duenio) {
        super(duenio);
        activador = new Activador(3);
        stats = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.ASESINO_STATS);
    }

    // Primero retrasa (aparece en la peana del rival más cercano a actuar) y después aparece directamente en la corona
    @Override
    protected java.util.List<Ataque> crearAtaques() {
        Ataque retraso = ataquePersonalizado(() -> {
            Figura objetivo = getEnemigo().getFiguraMasCercaDeActuar(this); // null si solo hay un asesino rival que actúa a la vez
            if (objetivo == null) {
                return java.util.List.of();
            }
            Point peana = Trampillas.esquina(objetivo.getPosicion());
            return java.util.List.of(Ataque.Paso.salir(peana), Ataque.Paso.golpe(peana),
                    Ataque.Paso.efecto(() -> objetivo.getActivador().aumentarAltura(getStat1())), Ataque.Paso.esconder(peana));
        });
        return java.util.List.of(retraso, ataqueEnPunto(this::coronaEnemiga, true, () -> getEnemigo().recibirAtaque(getStat2())));
    }

    @Override
    public Ataque.Estilo getEstiloAtaque() {
        return Ataque.Estilo.ESTELA_CORTA;
    }

    @Override
    public int getPrioridad() {
        return 1;
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
