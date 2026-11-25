package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.maquina_estado.IEstadoJuego;
import main.maquina_estado.juego.figuras.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class IA extends EsqueletoPlayer implements IEstadoJuego {

    public IA() {
        super();
        figurasDisponibles = new ArrayList<>(Arrays.asList(new Arquero(this), new Mago(this), new Caballero(this), new Asesino(this), new Ingeniero(this), new Sacerdote(this)));

        seleccionarFigurasAleatorias();
        establecerRodillo(Constantes.RODILLOS.R2_Prob); // Rodillo simple sin "roto" (D5)

    }

    public void actualizar() {

        if (figuraDerecha.posicion != 0 && figuraIzquierda.posicion != 0) {
            figuraDerecha.actualizar();
            figuraIzquierda.actualizar();
        }

        if (!figurasAtacando()) {
            if (terminadoDeProcesarAtaques) {
                getRodillo().abrirRodillo();
            }
        }
    }

    @Override
    public void dibujar(Graphics graficos) {
        this.dibujarVida(graficos);

        // Dibujar bastion
//        if (this.bastion.getAltura() > 0) {
//            this.bastion.dibujarBastion(graficos, this, Constantes.COORDENADAS.BASTION_IA);
//        }

        // Dibujar figuras
        if (getFiguraIzquierda() != null) {
            getFiguraIzquierda().dibujar(graficos, getFiguraIzquierda().getPosicion());
        }

        if (getFiguraDerecha() != null) {
            getFiguraDerecha().dibujar(graficos, getFiguraDerecha().getPosicion());
        }
    }

    protected void dibujarVida(Graphics graficos) {
        Dibujador.TEXTO.dibujar(graficos, getVidaString(), 621, 165, color);
    }

    // Entre tiradas la IA fija los canales que le interesan y vuelve a tirar el resto (D5)
    public void decidirFijados() {
        Constantes.RODILLOS.Elementos[] actual = rodillo.getSalidaRodillo();
        for (int i = 0; i < actual.length; i++) {
            rodillo.setFijado(i, esValioso(actual[i]));
        }
    }

    // Vale la pena conservar los símbolos de valor 2 o 3 y los que dan EXP (fondo azul); el resto se vuelve a tirar
    static boolean esValioso(final Constantes.RODILLOS.Elementos elemento) {
        return elemento.tipo != Constantes.RODILLOS.Tipo.ROTO && (elemento.valor >= 2 || elemento.daExperiencia);
    }

    // La IA elige dos héroes distintos al azar: el izquierdo (posición 1) y el derecho (posición 2)
    private void seleccionarFigurasAleatorias() {
        ArrayList<Figura> barajadas = new ArrayList<>(figurasDisponibles);
        Collections.shuffle(barajadas);

        Figura izquierda = barajadas.get(0);
        izquierda.setPosicion(1);
        setFiguraIzquierda(izquierda);

        Figura derecha = barajadas.get(1);
        derecha.setPosicion(2);
        setFiguraDerecha(derecha);
    }
}
