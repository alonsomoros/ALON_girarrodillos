package main.maquina_estado.juego;

import main.herramientas.Depurar;
import main.herramientas.Dibujador;

import java.awt.*;

public class Ataque {
    /** Duración de la animación de un ataque en ms (las pruebas la acortan). */
    static long duracionPorDefecto = 2000;


    Figura figuraAtacante;
    private Point[] posiciones;
    private long startTime;
    private long duracion;
    private boolean activo;
    private boolean terminadaAnimacion;
    private Image imagen;
    private int index;
    private boolean ignoraBastion; // Vuela por encima del bastión (2.º ataque del Mago)
    private boolean especial;      // Bomba de un héroe de oro

    public Ataque(Figura figuraAtacante, Point[] posicion, Image imagen) {
        this.figuraAtacante = figuraAtacante;
        this.posiciones = posicion;
        this.duracion = duracionPorDefecto;
        this.imagen = imagen;
        this.activo = true;
        this.index = 0;
        terminadaAnimacion = false;
        iniciar(); // Cada ataque lleva su propio cronómetro
    }

    public void actualizar() {
        if (!activo) return;

        long tiempoTranscurrido = System.currentTimeMillis() - startTime;

        if (tiempoTranscurrido <= duracion / 3) {
            index = 0;
            Depurar.log("Primer frame");
        } else if (tiempoTranscurrido >= duracion / 3 && tiempoTranscurrido < duracion / 3 * 2) {
            index = 1;
            Depurar.log("Segundo frame");
        } else if (tiempoTranscurrido >= duracion / 3 * 2 && tiempoTranscurrido < duracion) {
            index = 2;
            Depurar.log("Tercer frame");
        } else {
            Depurar.log("Ataque terminado");
            activo = false;
            terminadaAnimacion = true;

            figuraAtacante.resolverAtaque(this, figuraAtacante.getEnemigo());

        }
    }


    public void dibujar(Graphics g) {
        if (!activo) return;
        Dibujador.IMAGEN.dibujar(g, imagen, posiciones[index]);
        Depurar.log(figuraAtacante.getClass().getSimpleName() + /* " de " + figuraAtacante.getDuenio() + */ " atacando");
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean terminadaAnimacion() {
        return terminadaAnimacion;
    }

    public boolean ignoraBastion() {
        return ignoraBastion;
    }

    public void setIgnoraBastion(final boolean ignoraBastion) {
        this.ignoraBastion = ignoraBastion;
    }

    public boolean esEspecial() {
        return especial;
    }

    public void setEspecial(final boolean especial) {
        this.especial = especial;
    }

    /** (Re)inicia el cronómetro del ataque. */
    public void iniciar() {
        this.startTime = System.currentTimeMillis();
    }

}

