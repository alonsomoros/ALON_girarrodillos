package main.maquina_estado.juego;

import main.herramientas.Dibujador;
import main.maquina_estado.juego.figuras.Mago;

import java.awt.*;

public class Ataque {
    Figura figuraAtacante;
    private Point[] posiciones;
    private long startTime;
    private long duracion;
    private boolean activo;
    private boolean terminadaAnimacion;
    private Image imagen;
    private int index;
    public int ataqueMagoIndex;

    public Ataque(Figura figuraAtacante, Point[] posicion, Image imagen) {
        this.figuraAtacante = figuraAtacante;
        this.posiciones = posicion;
        this.duracion = 2000;
        this.imagen = imagen;
        this.activo = true;
        this.index = 0;
        terminadaAnimacion = false;

        if (figuraAtacante instanceof Mago) {
            ataqueMagoIndex = 0;
        } else {
            ataqueMagoIndex = -1;
        }
    }

    public void actualizar() {
        if (!activo) return;

        long tiempoTranscurrido = System.currentTimeMillis() - startTime;

        if (tiempoTranscurrido <= duracion / 3) {
            index = 0;
            System.out.println("Primer frame");
        } else if (tiempoTranscurrido >= duracion / 3 && tiempoTranscurrido < duracion / 3 * 2) {
            index = 1;
            System.out.println("Segundo frame");
        } else if (tiempoTranscurrido >= duracion / 3 * 2 && tiempoTranscurrido < duracion) {
            index = 2;
            System.out.println("Tercer frame");
        } else {
            System.out.println("Ataque terminado");
            activo = false;
            terminadaAnimacion = true;

            GestorAtaques.procesarFinalAtaque(figuraAtacante, this, figuraAtacante.getDuenio());
        }
    }


    public void dibujar(Graphics g) {
        if (!activo) return;
        Dibujador.IMAGEN.dibujar(g, imagen, posiciones[index]);
        System.out.println(figuraAtacante.getClass().getSimpleName() + /* " de " + figuraAtacante.getDuenio() + */ " atacando");
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean terminadaAnimacion() {
        return terminadaAnimacion;
    }

    public boolean casoMagoPrimerAtaque() {
        return ataqueMagoIndex == 0;
    }

    public void ejecutarAtaque() {
        this.startTime = System.currentTimeMillis();
    }
}

