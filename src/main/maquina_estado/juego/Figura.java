package main.maquina_estado.juego;

import main.herramientas.Depurar;
import main.Constantes;

import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class Figura implements AtaquesFiguras {
    private Player playerDuenio = null;
    private IA iaDuenio = null;

    protected Image imagenFigura;
    protected Image ataqueImagen;
    protected Image ataqueEspecialImagen;
    protected Image stats;
    // Ataques de esta activación, en orden: se animan de uno en uno
    private final Deque<Ataque> colaAtaques = new ArrayDeque<>();
    private boolean activacionPendiente; // true si al terminar hay que resetear activador y dar XP
    protected int posicion;
    protected int nivel; // Del 1 al 3
    protected int experiencia; // Del 0 al 6
    protected Activador activador;
    protected int alturaDefault;
    protected boolean estaAtacando;
    private int ataquesLanzados; // Solo informativo (depuración y pruebas)


    public Figura(final Player player) {
        playerDuenio = player;
        nivel = 1;
        experiencia = 0;
        imagenFigura = getImagen();
        ataqueImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + this.getClass().getSimpleName().toLowerCase() + ".png");
        ataqueEspecialImagen = CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ATAQUES.RUTA_ATAQUES + "Especial.png");
        estaAtacando = false;
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
        posicion = 0;
    }


    public void actualizar() {
        if (!estaAtacando) {
            return;
        }

        Ataque actual = colaAtaques.peek();
        if (actual != null) {
            actual.actualizar(); // Esto hace que se anime
            if (actual.terminadaAnimacion()) {
                colaAtaques.poll();
                Ataque siguiente = colaAtaques.peek();
                if (siguiente != null) {
                    siguiente.iniciar(); // El siguiente ataque empieza ahora
                }
            }
        }

        if (colaAtaques.isEmpty()) {
            finalizarAtaques();
        }
    }

    // Se llama cuando se acabó la cola de ataques
    private void finalizarAtaques() {
        if (activacionPendiente) {
            activacionPendiente = false;
            activador.resetear(alturaDefault);
            // Sumar +2 de nivel etc
            this.aumentarExperiencia(2);
            if (experiencia == 6) {
                subirNivel(); // En nivel máximo puede encolar la bomba
            }
        }
        estaAtacando = !colaAtaques.isEmpty();
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

        Ataque actual = colaAtaques.peek();
        if (estaAtacando && actual != null) {
            actual.dibujar(graficos);
        }
    }

    /** Cuántos ataques hace al activarse (el Mago hace 2). */
    public int getNumeroAtaques() {
        return 1;
    }

    public int getAtaquesLanzados() {
        return ataquesLanzados;
    }

    private Point[] coordenadasAtaque() {
        switch (posicion) {
            case 1:
                return Constantes.COORDENADAS.ATAQUE_1;
            case 2:
                return Constantes.COORDENADAS.ATAQUE_2;
            case 3:
                return Constantes.COORDENADAS.ATAQUE_3;
            case 4:
                return Constantes.COORDENADAS.ATAQUE_4;
            default:
                return null;
        }
    }

    // Al activarse crea sus ataques (getNumeroAtaques) y los encola. Los siguientes al primero ignoran el bastión.
    public void atacarNormal() {
        Point[] camino = coordenadasAtaque();
        if (camino == null) {
            return; // Figura sin posición asignada
        }

        this.estaAtacando = true;
        this.activacionPendiente = true;
        for (int i = 0; i < getNumeroAtaques(); i++) {
            Ataque nuevo = new Ataque(this, camino, ataqueImagen);
            nuevo.setIgnoraBastion(i > 0);
            colaAtaques.add(nuevo);
            ataquesLanzados++;
        }
        colaAtaques.peek().iniciar(); // Solo corre el primero; el resto empieza al llegarle el turno

        if (Depurar.ACTIVO) { // Solo se construye el texto si hace falta
            Depurar.log("Ataque de " + this.getClass().getSimpleName() + getDuenio().getClass().getSimpleName() + " a " + getEnemigo().getClass().getSimpleName());
        }
    }


    public void atacarEspecial() { // Hace dos de daño independientemente e ignora bastiones
        Point[] camino = coordenadasAtaque();
        if (camino == null) {
            return;
        }

        Ataque bomba = new Ataque(this, camino, ataqueEspecialImagen);
        bomba.setEspecial(true);
        colaAtaques.add(bomba);
        if (colaAtaques.size() == 1) {
            bomba.iniciar();
        }
        this.estaAtacando = true;
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
            atacarEspecial(); // Bomba: 2 de daño directo a la corona enemiga

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
