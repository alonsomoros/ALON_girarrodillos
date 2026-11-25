package main.maquina_estado.juego;

import main.Constantes;

import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import java.awt.*;

public abstract class Figura implements AtaquesFiguras {
    private Player playerDuenio = null;
    private IA iaDuenio = null;

    protected Image imagenFigura;
    protected Image ataqueImagen;
    protected Image ataqueEspecialImagen;
    protected Image stats;
    protected Ataque ataque;
    protected Ataque ataqueEspecial;
    protected int posicion;
    protected int nivel; // Del 1 al 3
    protected int experiencia; // Del 0 al 6
    protected Activador activador;
    protected int alturaDefault;
    protected boolean estaAtacando;
    protected boolean estaAtacandoEspecial;

    public Figura(final Player player) {
        playerDuenio = player;
        nivel = 1;
        experiencia = 0;
        imagenFigura = getImagen();
        ataqueImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + this.getClass().getSimpleName().toLowerCase() + ".png");
        ataqueEspecialImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + "Especial.png");
        estaAtacando = false;
        estaAtacandoEspecial = false;
        posicion = 0;
    }

    public Figura(final IA ia) {
        iaDuenio = ia;
        nivel = 1;
        experiencia = 0;
        imagenFigura = getImagen();
        ataqueImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + this.getClass().getSimpleName().toLowerCase() + ".png");
        ataqueEspecialImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + "Especial.png");
        estaAtacando = false;
        estaAtacandoEspecial = false;
        posicion = 0;
    }


    public void actualizar() {
        if (estaAtacando) {
            ataque.actualizar(); // Esto hace que se anime
            if (ataque.terminadaAnimacion()) { // Ataque termina
                estaAtacando = false;
                activador.resetear(alturaDefault);
                // Sumar +2 de nivel etc
                this.aumentarExperiencia(2);
                if (experiencia == 6) {
                    subirNivel();
                }
            }
        }
    }

    public void dibujar(Graphics graficos, int posicion) {
        switch (posicion) {
            case 1:
                dibujarBase(graficos, Constantes.COORDENADAS.BASE_1, true); // Dibujar la base
                Dibujador.IMAGEN.dibujar(graficos, imagenFigura, Constantes.COORDENADAS.FIGURA_1); // Dibujar la figura
                this.getActivador().dibujarActivador(graficos, Constantes.COORDENADAS.ACTIVADOR_1, true); // Dibujar el activador
                this.dibujarStats(graficos, Constantes.COORDENADAS.STATS_1); // Dibujar los stats
                break;
            case 2:
                dibujarBase(graficos, Constantes.COORDENADAS.BASE_2, false);
                Dibujador.IMAGEN.dibujarRotado(graficos, imagenFigura, Constantes.COORDENADAS.FIGURA_2);
                this.getActivador().dibujarActivador(graficos, Constantes.COORDENADAS.ACTIVADOR_2, false);
                this.dibujarStats(graficos, Constantes.COORDENADAS.STATS_2);
                break;
            case 3:
                dibujarBase(graficos, Constantes.COORDENADAS.BASE_3, true);
                Dibujador.IMAGEN.dibujar(graficos, imagenFigura, Constantes.COORDENADAS.FIGURA_3);
                this.getActivador().dibujarActivador(graficos, Constantes.COORDENADAS.ACTIVADOR_3, true);
                this.dibujarStats(graficos, Constantes.COORDENADAS.STATS_3);
                break;
            case 4:
                dibujarBase(graficos, Constantes.COORDENADAS.BASE_4, false);
                Dibujador.IMAGEN.dibujarRotado(graficos, imagenFigura, Constantes.COORDENADAS.FIGURA_4);
                this.getActivador().dibujarActivador(graficos, Constantes.COORDENADAS.ACTIVADOR_4, false);
                this.dibujarStats(graficos, Constantes.COORDENADAS.STATS_4);
                break;
        }

        if (estaAtacando) {
            ataque.dibujar(graficos);
        } else estaAtacando = false;

        if (estaAtacandoEspecial) {
            ataqueEspecial.dibujar(graficos);
        } else estaAtacandoEspecial = false;
    }

    public void atacarNormal() { // Cuando ataca se crea el ataque y se inicia el tiempo
        this.estaAtacando = true;
        switch (posicion) {
            case 1:
                ataque = new Ataque(this, Constantes.COORDENADAS.ATAQUE_1, ataqueImagen);

                System.out.println("Ataque de " + this.getClass().getSimpleName() + getDuenio().getClass().getSimpleName() + " a " + getEnemigo().getClass().getSimpleName());
                break;
            case 2:
                ataque = new Ataque(this, Constantes.COORDENADAS.ATAQUE_2, ataqueImagen);
                System.out.println("Ataque de " + this.getClass().getSimpleName() + getDuenio().getClass().getSimpleName() + " a " + getEnemigo().getClass().getSimpleName());
                break;
            case 3:
                ataque = new Ataque(this, Constantes.COORDENADAS.ATAQUE_3, ataqueImagen);
                System.out.println("Ataque de " + this.getClass().getSimpleName() + getDuenio().getClass().getSimpleName() + " a " + getEnemigo().getClass().getSimpleName());
                break;
            case 4:
                ataque = new Ataque(this, Constantes.COORDENADAS.ATAQUE_4, ataqueImagen);
                System.out.println("Ataque de " + this.getClass().getSimpleName() + getDuenio().getClass().getSimpleName() + " a " + getEnemigo().getClass().getSimpleName());
                break;
        }
        GestorAtaques.ataques.add(ataque);
        GestorAtaques.ejecutarAtaques();
    }

    public void atacarEspecial() { // Hace dos de daño independientemente e ignora bastiones
        this.estaAtacando = true;
        switch (posicion) {
            case 1:
                ataqueEspecial = new Ataque(this, Constantes.COORDENADAS.ATAQUE_1, ataqueEspecialImagen);
                break;
            case 2:
                ataqueEspecial = new Ataque(this, Constantes.COORDENADAS.ATAQUE_2, ataqueEspecialImagen);
                break;
            case 3:
                ataqueEspecial = new Ataque(this, Constantes.COORDENADAS.ATAQUE_3, ataqueEspecialImagen);
                break;
            case 4:
                ataqueEspecial = new Ataque(this, Constantes.COORDENADAS.ATAQUE_4, ataqueEspecialImagen);
                break;
        }
        GestorAtaques.ataques.add(ataqueEspecial);
        GestorAtaques.ejecutarAtaques();
    }

    public void dibujarBase(Graphics graficos, Point coordenadas, boolean esCuadrado) {
        switch (experiencia) {
            case 0:
                if (esCuadrado) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_CUADRADO_0), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_ROMBO_0), coordenadas);
                }
                break;
            case 1:
                if (esCuadrado) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_CUADRADO_1), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_ROMBO_1), coordenadas);
                }
                break;
            case 2:
                if (esCuadrado) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_CUADRADO_2), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_ROMBO_2), coordenadas);
                }
                break;
            case 3:
                if (esCuadrado) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_CUADRADO_3), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_ROMBO_3), coordenadas);
                }
                break;
            case 4:
                if (esCuadrado) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_CUADRADO_4), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_ROMBO_4), coordenadas);
                }
                break;
            case 5:
                if (esCuadrado) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_CUADRADO_5), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_ROMBO_5), coordenadas);
                }
                break;
            case 6:
                if (esCuadrado) {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_CUADRADO_6), coordenadas);
                } else {
                    Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.BASES.BASE_ROMBO_6), coordenadas);
                }
        }
    }

    public void dibujarStats(Graphics graficos, Point coordenadas) {
        Dibujador.IMAGEN.dibujar(graficos, stats, coordenadas);
        Dibujador.TEXTO.dibujar(graficos, this.getStat1() + "              " + this.getStat2(), coordenadas.x + 100, coordenadas.y + 45, new Color(0xFF8A7D72, true));
    }

    public abstract int getStat1();

    public abstract int getStat2();

    public Image getImagenFigura() {
        return imagenFigura;
    }

    public Activador getActivador() {
        return activador;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public int getAlturaDefault() {
        return alturaDefault;
    }

    public void subirNivel() {
        nivel++;
        setExperiencia(0);

        if (nivel > 3) {
            nivel = 3;
            estaAtacandoEspecial = true;
            atacarEspecial();
        }
        imagenFigura = getImagen();
    }

    public void aumentarExperiencia(int experiencia) {
        if (experiencia > 6) {
            experiencia = 6;
        }
        if (this.experiencia + experiencia > 6) {
            this.experiencia = 6;
        } else {
            this.experiencia += experiencia;
        }
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getPosicion() {
        return posicion;
    }

    public EsqueletoPlayer getDuenio() {
        return playerDuenio != null ? playerDuenio : iaDuenio;
    }

    public EsqueletoPlayer getEnemigo() {
        if (playerDuenio != null) {
            return playerDuenio.getEnemigo();
        } else {
            return iaDuenio.getEnemigo();
        }
    }

    public Image getImagen() {
        return CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FIGURAS.RUTA_FIGURAS + this.getClass().getSimpleName().toLowerCase() + "/" + this.getClass().getSimpleName().toLowerCase() + "_lvl" + getNivel() + ".png");
    }

    public Figura getCompaniero() {
        if (this.getPosicion() % 2 == 0) {
            return playerDuenio.getFiguraIzquierda();
        } else {
            return playerDuenio.getFiguraDerecha();
        }
    }

}
