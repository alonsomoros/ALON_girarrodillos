package main.maquina_estado.juego;

import main.maquina_estado.*;
import main.herramientas.*;
import main.Constantes;
import main.control.*;


import java.awt.*;
import java.util.*;


public class GestorGirarrodillos implements IEstadoJuego, IAudio {
    private static Player player;
    private static IA ia;
    private int turno;
    private static final int FRAMES_POR_PARPADEO = 45; // 750 ms a 60 actualizaciones por segundo

    private boolean circuloParpadeante, puedeFijar;
    private boolean parpadeando;
    private int contadorParpadeo;

    public GestorGirarrodillos() {

        if (ia == null) {
            ia = new IA();
        }

        if (player == null) {
            player = new Player();
        }

        player.setEnemigo(ia);
        ia.setEnemigo(player);

        crearParpadeoLuz();
        puedeFijar = false;
        turno = 0;
    }

    @Override
    public void actualizar() {
        actualizarParpadeo();

        if (player.seleccionandoFiguras()) {
            player.seleccionFiguras(player.figurasDisponibles);
        }
        if (player.tieneFigurasElegidas()) {
            if (player.figurasAtacando() || ia.figurasAtacando()) {
                player.actualizar();
                ia.actualizar();
            } else { // Si no hay figuras atacando
                switch (turno) {
                    case 0:
                        abrirRodillos();
                        if (!parpadeando) {
                            puedeFijar = false;
                            crearParpadeoLuz();
                        }
                        procesarControles();
                        break;
                    case 1:
                        if (parpadeando) {
                            parpadeando = false;
                            circuloParpadeante = false;
                            puedeFijar = true;
                        }
                        procesarControles();
                        break;
                    case 2:
                        procesarControles();
                        break;
                    case 3:
                        procesarUltimoTurno();
                        break;
                }
            }
        }
    }

    private void procesarUltimoTurno() {

        cerrarRodillos();

        player.calcularSalidaRodillo(Player.getRodillo().getSalidaRodillo());
        player.actualizarEstados();

        ia.calcularSalidaRodillo(IA.getRodillo().getSalidaRodillo());
        ia.actualizarEstados();

        turno = 0;
    }

    private void cerrarRodillos() {
        Player.getRodillo().cerrarRodillo();
        IA.getRodillo().cerrarRodillo();
    }

    private void abrirRodillos() {
        Player.getRodillo().abrirRodillo();
        IA.getRodillo().abrirRodillo();
    }

    @Override
    public void dibujar(Graphics graficos) {
        // Dibujar fondo
        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, new Rectangle(0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO), new Color(0x4a3432));

        IA.getRodillo().dibujarRodillo(graficos, false);

        Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FONDOS.TABLERO), 0, 0);

        ia.dibujar(graficos);
        player.dibujar(graficos); // Se dibuja el rodillo


        if (player.seleccionandoIzquierda == 0) {
            dibujarTrianguloEleccion(graficos, Triangulo.TRIANGULO1, Triangulo.TRIANGULO2);
        } else if (player.seleccionandoIzquierda == 1) {
            dibujarTrianguloEleccion(graficos, Triangulo.TRIANGULO3, Triangulo.TRIANGULO4);
        }
        dibujarLucesTurno(graficos);
    }

    private void dibujarTrianguloEleccion(Graphics graficos, Triangulo t1, Triangulo t2) {
        Color colorTriangulo = new Color(0xD6BFAD);
        Dibujador.TRIANGULO.dibujar(graficos, t1, colorTriangulo);
        Dibujador.TRIANGULO.dibujar(graficos, t2, colorTriangulo);
    }

    private void dibujarLucesTurno(Graphics graficos) {
        int xLuz = 1100;
        int yLuz = 530;

        for (int i = 0; i < 3; i++) {
            Dibujador.CIRCULO.dibujar(graficos, xLuz - i * 10, yLuz + i * 20, 10, new Color(0x2E1E13));
        }

        if (circuloParpadeante) {
            for (int i = 0; i < 3; i++) {
                Dibujador.CIRCULO.dibujar(graficos, xLuz - i * 10, yLuz + i * 20, 10, new Color(0xE4DEB0));
            }
        } else {
            for (int i = 0; i < turno; i++) {
                Dibujador.CIRCULO.dibujar(graficos, xLuz - i * 10, yLuz + i * 20, 10, new Color(0xE4DEB0));
            }
        }
    }


    private void procesarControles() {
        long tiempoActual = System.nanoTime();

        GestorControles.teclado.procesarTecla(GestorControles.teclado.teclaE, () -> {
            Player.getRodillo().girarRodillo();
            IA.getRodillo().girarRodillo();
            turno++;
            Depurar.log("Turno: " + turno);
        }, tiempoActual);

        if (puedeFijar) {
            GestorControles.teclado.procesarTecla(GestorControles.teclado.espacio, () -> {
                Player.getRodillo().fijarCanal(Player.getRodillo().getPunteroPos());
            }, tiempoActual);
        }

        GestorControles.teclado.procesarTecla(GestorControles.teclado.derecha, () -> {
            Player.getRodillo().moverPunteroDerecha();
        }, tiempoActual);

        GestorControles.teclado.procesarTecla(GestorControles.teclado.izquierda, () -> {
            Player.getRodillo().moverPunteroIzquierda();
        }, tiempoActual);
    }


    // El parpadeo se cuenta en actualizaciones del bucle principal (sin hilo aparte)
    private void crearParpadeoLuz() {
        parpadeando = true;
        circuloParpadeante = true;
        contadorParpadeo = 0;
    }

    private void actualizarParpadeo() {
        if (parpadeando && ++contadorParpadeo >= FRAMES_POR_PARPADEO) {
            contadorParpadeo = 0;
            circuloParpadeante = !circuloParpadeante;
        }
    }


    @Override
    public void activarAudio() {
    }

}