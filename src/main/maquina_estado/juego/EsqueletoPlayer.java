package main.maquina_estado.juego;

import main.Constantes;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class EsqueletoPlayer {
    protected int vida;
    protected ArrayList<Figura> figurasDisponibles;
    protected Figura figuraIzquierda, figuraDerecha;
    protected Figura figuraIndex;
    protected Rodillo rodillo;
    protected Bastion bastion;
    protected int seleccionandoIzquierda = 0;
    protected boolean terminadoDeProcesarAtaques;
    protected boolean elegidas = false;

    protected Color color = new Color(0xFF8A7D72, true);
    private EsqueletoPlayer enemigo;


    public EsqueletoPlayer() {
        this.vida = Constantes.VIDA;
        this.bastion = new Bastion();

        figuraIzquierda = null;
        figuraDerecha = null;
        terminadoDeProcesarAtaques = false;
    }

    public void setFiguraIzquierda(final Figura figura) {
        this.figuraIzquierda = figura;
    }

    public void setFiguraDerecha(final Figura figura) {
        this.figuraDerecha = figura;
    }

    public int getVida() {
        return this.vida;
    }

    public String getVidaString() {
        if (vida < 10) {
            return 0 + "   " + vida;
        } else {
            return vida / 10 + "   " + vida % 10;
        }
    }

    public void setVida(int vidaActualizada) {
        if (vidaActualizada < 0) {
            this.vida = 0;
        } else this.vida = Math.min(vidaActualizada, Constantes.VIDA_MAXIMA);
    }

    public void recibirAtaque(int danio) {
        this.setVida(this.getVida() - danio);
    }

    public void recibirCuracion(int curacion) {
        this.setVida(this.getVida() + curacion);
    }

    public Figura getFiguraIzquierda() {
        return this.figuraIzquierda;
    }

    public Figura getFiguraDerecha() {
        return this.figuraDerecha;
    }

    // El héroe al que le queda menos energía para actuar; si empatan, el que está enfrente del atacante
    public Figura getFiguraMasCercaDeActuar(Figura atacante) {
        int alturaIzquierda = figuraIzquierda.getActivador().getAltura();
        int alturaDerecha = figuraDerecha.getActivador().getAltura();
        if (alturaIzquierda < alturaDerecha) {
            return figuraIzquierda;
        } else if (alturaDerecha < alturaIzquierda) {
            return figuraDerecha;
        }
        return getFiguraEnfrente(atacante);
    }

    public Figura getFiguraEnfrente(Figura figura) {

        if (figura.getPosicion() % 2 == 0) {
            return this.figuraDerecha;
        } else {
            return this.figuraIzquierda;
        }
    }

    public Rodillo getRodillo() {
        return rodillo;
    }

    public Bastion getBastion() {
        return this.bastion;
    }

    public boolean tieneBation() {
        return bastion.getAltura() > 0;
    }

    public boolean tieneFigurasElegidas() {
        return figuraIzquierda != null && figuraDerecha != null && elegidas;
    }

    public boolean seleccionandoFiguras() {
        return seleccionandoIzquierda == 0 || seleccionandoIzquierda == 1;
    }

    protected boolean figurasAtacando() {
        return figuraDerecha.estaAtacando || figuraIzquierda.estaAtacando;
    }

    public void establecerRodillo(HashMap<Constantes.RODILLOS.Elementos, Double> p) {
        HashMap<Constantes.RODILLOS.Elementos, Double>[] rodilloPlayer = new HashMap[5];
        for (int i = 0; i < 5; i++) {
            rodilloPlayer[i] = p;
        }
        rodillo = new Rodillo(rodilloPlayer);
    }

    public void establecerRodillo(HashMap<Constantes.RODILLOS.Elementos, Double> p1, HashMap<Constantes.RODILLOS.Elementos, Double> p2, HashMap<Constantes.RODILLOS.Elementos, Double> p3, HashMap<Constantes.RODILLOS.Elementos, Double> p4, HashMap<Constantes.RODILLOS.Elementos, Double> p5) {
        HashMap<Constantes.RODILLOS.Elementos, Double>[] rodilloPlayer = new HashMap[5];

        rodilloPlayer[0] = p1;
        rodilloPlayer[1] = p2;
        rodilloPlayer[2] = p3;
        rodilloPlayer[3] = p4;
        rodilloPlayer[4] = p5;

        rodillo = new Rodillo(rodilloPlayer);
    }

    public void setEnemigo(EsqueletoPlayer enemigo) {
        this.enemigo = enemigo;
    }

    public EsqueletoPlayer getEnemigo() {
        return enemigo;
    }

    protected void calcularSalidaRodillo(String[] rodillos) {
        int ataqueC = 0, ataqueR = 0, experienciaC = 0, experienciaR = 0, muralla = 0;

        for (String elemento : rodillos) {
            switch (elemento) {
                case "cuadrado1":
                    ataqueC += 1;
                    break;
                case "cuadrado2":
                    ataqueC += 2;
                    break;
                case "cuadrado3":
                    ataqueC += 3;
                    break;
                case "cuadradoNivel1":
                    experienciaC += 1;
                    ataqueC += 1;
                    break;
                case "cuadradoNivel2":
                    experienciaC += 2;
                    ataqueC += 2;
                    break;
                case "cuadradoNivel3":
                    experienciaC += 3;
                    ataqueC += 3;
                    break;
                case "rombo1":
                    ataqueR += 1;
                    break;
                case "rombo2":
                    ataqueR += 2;
                    break;
                case "rombo3":
                    ataqueR += 3;
                    break;
                case "romboNivel1":
                    experienciaR += 1;
                    ataqueR += 1;
                    break;
                case "romboNivel2":
                    experienciaR += 2;
                    ataqueR += 2;
                    break;
                case "romboNivel3":
                    experienciaR += 3;
                    ataqueR += 3;
                    break;
                case "martillo1":
                    muralla += 1;
                    break;
                case "martillo2":
                    muralla += 2;
                    break;
                case "martillo3":
                    muralla += 3;
                    break;
            }
        }

        if (ataqueC >= 3) {
            figuraIzquierda.getActivador().disminuirAltura(ataqueC - 2);
        }
        if (ataqueR >= 3) {
            figuraDerecha.getActivador().disminuirAltura(ataqueR - 2);
        }
        if (experienciaC >= 1) {
            figuraIzquierda.aumentarExperiencia(experienciaC);
        }
        if (experienciaR >= 1) {
            figuraDerecha.aumentarExperiencia(experienciaR);
        }
        if (muralla >= 3) {
            bastion.aumentarAltura(muralla - 2);
        }

    }

    // Si el activador de la figura llegó a 0 y no está ya atacando, lanza todos sus ataques
    protected void lanzarAtaque(final Figura figura) {
        if (figura.getActivador().getAltura() == 0 && !figura.estaAtacando) {
            figura.atacarNormal(); // Crea y encola todos sus ataques (el Mago, 2)

        }
    }

    protected void actualizarEstados() {

        lanzarAtaque(figuraIzquierda);
        lanzarAtaque(figuraDerecha);

        if (!figurasAtacando()) {
            terminadoDeProcesarAtaques = true;
        }
    }

}
