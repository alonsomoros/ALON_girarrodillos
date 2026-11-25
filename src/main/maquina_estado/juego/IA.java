package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.maquina_estado.IEstadoJuego;
import main.maquina_estado.juego.figuras.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class IA extends EsqueletoPlayer implements IEstadoJuego {

    public IA() {
        super();
        figurasDisponibles = new ArrayList<>(Arrays.asList(new Arquero(this), new Mago(this), new Caballero(this), new Asesino(this), new Ingeniero(this), new Sacerdote(this)));

        seleccionFiguras(figurasDisponibles);
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
    public void dibujar(Graphics graficos) { // Hay que hacer una clase IA que sea parecida
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

    public void seleccionarFigurasAleatoria(ArrayList<Figura> figurasDisponibles) {
        int i = 0;
        while (i < 2) {
            int index = (int) (Math.random() * figurasDisponibles.size());
            Figura figura = figurasDisponibles.get(index);
            if (figuraIzquierda == null) {
                setFiguraIzquierda(figura);
                i++;
            } else if (figuraDerecha == null && figuraIzquierda != figura) {
                setFiguraDerecha(figura);
                i++;
            }


        }
    }

    public void seleccionFiguras(ArrayList<Figura> figurasDisponibles) { // Arreglar para que sea agnóstico de los números
        Figura f1 = figurasDisponibles.get(0);
        f1.setPosicion(1);
        Figura f2 = figurasDisponibles.get(1);
        f2.setPosicion(2);
        setFiguraIzquierda(f1);
        setFiguraDerecha(f2);
    }
}
