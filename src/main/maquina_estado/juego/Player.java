package main.maquina_estado.juego;

import main.Constantes;
import main.control.GestorControles;
import main.herramientas.Dibujador;
import main.maquina_estado.IEstadoJuego;
import main.maquina_estado.tienda.Progreso;

import main.maquina_estado.juego.figuras.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Player extends EsqueletoPlayer implements IEstadoJuego {

    // ------------------- Constructor -------------------

    public Player() {
        super();
        figurasDisponibles = new ArrayList<>(Arrays.asList(new Arquero(this), new Mago(this), new Caballero(this), new Asesino(this), new Ingeniero(this), new Sacerdote(this)));
        figuraIndex = figurasDisponibles.get(0);

        // El jugador usa el rodillo que tiene equipado en la tienda
        establecerRodillo(Constantes.RODILLOS.DE_TIENDA[Progreso.getInstance().getEquipado()]);
    }

    // ------------------- Estado Juego -------------------

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

    public void dibujar(Graphics graficos) {

        // Dibujar vida
        this.dibujarVida(graficos);

        // Dibujar bastion
        if (this.bastion.getAltura() > 0 || this.bastion.estaAnimando()) {
            this.bastion.dibujarBastion(graficos, this, Constantes.COORDENADAS.BASTION_PLAYER);
        }

        // Dibujar rodillo
        rodillo.dibujarRodillo(graficos, true);
        rodillo.dibujarPuntero(graficos);


        // Dibujar figuras
        if (getFiguraIzquierda() != null) {
            getFiguraIzquierda().dibujar(graficos, getFiguraIzquierda().getPosicion());
        }

        if (getFiguraDerecha() != null) {
            getFiguraDerecha().dibujar(graficos, getFiguraDerecha().getPosicion());
        }

    }

    // ------------------- Métodos -------------------

    public void seleccionFiguras(ArrayList<Figura> figuras) { // No se pueden elegir ni dos iguales ni nulo
        long tiempoActual = System.nanoTime();

        if (figuraIndex == null || !figuras.contains(figuraIndex)) {
            figuraIndex = figuras.get(0); // Inicializar con la primera figura si es nulo o no válido
        }

        if (seleccionandoIzquierda == 0) {
            figuraIzquierda = figuras.get(figuras.indexOf(figuraIndex));
            figuraIzquierda.setPosicion(3);

            GestorControles.teclado.procesarTecla(GestorControles.teclado.derecha, () -> {
                figuraIndex = figuras.get((figuras.indexOf(figuraIndex) + 1) % figuras.size());
                figuraIzquierda = figuraIndex;
            }, tiempoActual);

            GestorControles.teclado.procesarTecla(GestorControles.teclado.izquierda, () -> {
                figuraIndex = figuras.get((figuras.indexOf(figuraIndex) + figuras.size() - 1) % figuras.size());
                figuraIzquierda = figuraIndex;
            }, tiempoActual);

        } else if (seleccionandoIzquierda == 1) {
            figuraDerecha = figuras.get(figuras.indexOf(figuraIndex));
            figuraDerecha.setPosicion(4);

            GestorControles.teclado.procesarTecla(GestorControles.teclado.derecha, () -> {
                figuraIndex = figuras.get((figuras.indexOf(figuraIndex) + 1) % figuras.size());
                figuraDerecha = figuraIndex;
            }, tiempoActual);

            GestorControles.teclado.procesarTecla(GestorControles.teclado.izquierda, () -> {
                figuraIndex = figuras.get((figuras.indexOf(figuraIndex) + figuras.size() - 1) % figuras.size());
                figuraDerecha = figuraIndex;
            }, tiempoActual);
        }


        GestorControles.teclado.procesarTecla(GestorControles.teclado.espacio, () -> {
            if (seleccionandoIzquierda > 1) {
                return;
            } else if (seleccionandoIzquierda == 0) {
                this.figurasDisponibles.remove(figuraIzquierda);
                figuras.remove(figuraIzquierda);

            } else if (seleccionandoIzquierda == 1) {
                this.figurasDisponibles.remove(figuraDerecha);
                figuras.remove(figuraDerecha);
                elegidas = true;
            }
            this.seleccionandoIzquierda++;
        }, tiempoActual);

    }

    protected void dibujarVida(Graphics graficos) {
        dibujarCorona(graficos, new Point(621, 460), new Point(620, 452), 405);
    }

}
