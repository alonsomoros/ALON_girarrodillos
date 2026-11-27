package main.maquina_estado.juego;

import main.AudioPlayer;
import main.Constantes;

import main.control.GestorControles;
import main.herramientas.CargadorRecursos;
import main.herramientas.Depurar;
import main.herramientas.Dibujador;
import main.herramientas.Triangulo;
import main.maquina_estado.IAudio;
import main.maquina_estado.IEstadoJuego;
import main.maquina_estado.tienda.Progreso;



import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class GestorGirarrodillos implements IEstadoJuego, IAudio {
    Player player; // De paquete: las pruebas los usan
    IA ia;
    private ColaAcciones acciones; // Orden de acción de la ronda (se recrea con cada partida)
    private int turno;
    private ResultadoPartida resultado = ResultadoPartida.JUGANDO;
    private int monedasGanadas; // Recompensa de esta partida (0 mientras se juega)
    private static final int FRAMES_POR_PARPADEO = 45; // 750 ms a 60 actualizaciones por segundo

    private boolean circuloParpadeante, puedeFijar;
    private boolean parpadeando;
    private int contadorParpadeo;

    public GestorGirarrodillos() {
        nuevaPartida();
    }

    // Empieza una partida desde cero: nuevos jugador e IA, turno 0
    public void nuevaPartida() {
        ia = new IA();
        player = new Player();

        player.setEnemigo(ia);
        ia.setEnemigo(player);
        acciones = new ColaAcciones(player, ia);
        player.getRodillo().setSonoro(true); // El rodillo de la IA no suena

        crearParpadeoLuz();
        puedeFijar = false;
        turno = 0;
        resultado = ResultadoPartida.JUGANDO;
        monedasGanadas = 0;
    }

    // Quién ha ganado según las coronas (ambas a 0 es empate)
    ResultadoPartida evaluarResultado() {
        boolean playerCaido = player.getVida() <= 0;
        boolean iaCaida = ia.getVida() <= 0;
        if (playerCaido && iaCaida) {
            return ResultadoPartida.EMPATE;
        } else if (iaCaida) {
            return ResultadoPartida.VICTORIA;
        } else if (playerCaido) {
            return ResultadoPartida.DERROTA;
        }
        return ResultadoPartida.JUGANDO;
    }

    // Cierra la partida: guarda el resultado, da las monedas y suena el efecto
    void terminarPartida(final ResultadoPartida nuevoResultado) {
        resultado = nuevoResultado;
        switch (resultado) {
            case VICTORIA:
                monedasGanadas = Progreso.MONEDAS_VICTORIA;
                break;
            case EMPATE:
                monedasGanadas = Progreso.MONEDAS_EMPATE;
                break;
            default:
                monedasGanadas = Progreso.MONEDAS_DERROTA;
                break;
        }
        Progreso.getInstance().ganarMonedas(monedasGanadas);

        String sonido = resultado == ResultadoPartida.VICTORIA ? Constantes.RUTAS.EFECTOS.VICTORIA : Constantes.RUTAS.EFECTOS.DERROTA;
        AudioPlayer.getInstance().playSoundOnce(sonido);
    }

    int getMonedasGanadas() {
        return monedasGanadas;
    }

    public ResultadoPartida getResultado() {
        return resultado;
    }


    @Override
    public void actualizar() {
        if (resultado != ResultadoPartida.JUGANDO) {
            return; // Partida terminada: Enter vuelve al menú (GestorEstados)
        }
        actualizarParpadeo();
        player.getRodillo().actualizar(); // Sonidos de frenada y de fijar
        ia.getRodillo().actualizar();

        if (player.seleccionandoFiguras()) {
            player.seleccionFiguras(player.figurasDisponibles);
        }
        if (player.tieneFigurasElegidas()) {
            if (player.figurasAtacando() || ia.figurasAtacando() || acciones.estaActiva()) {
                acciones.avanzar(); // Lanza la siguiente acción cuando termina la anterior
                player.actualizar();
                ia.actualizar();
            } else if (player.getRodillo().estaGirando() || ia.getRodillo().estaGirando()) {
                return; // Los rodillos giran: el turno espera a que paren todos los canales
            } else { // Si no hay figuras atacando
                ResultadoPartida actual = evaluarResultado(); // Ya se resolvieron todos los ataques
                if (actual != ResultadoPartida.JUGANDO) {
                    terminarPartida(actual);
                    return;
                }
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

        player.calcularSalidaRodillo(player.getRodillo().getSalidaRodillo());
        ia.calcularSalidaRodillo(ia.getRodillo().getSalidaRodillo());
        acciones.iniciarRonda(); // Los héroes activados actúan de uno en uno, por prioridad

        turno = 0;
    }

    private void cerrarRodillos() {
        player.getRodillo().cerrarRodillo();
        ia.getRodillo().cerrarRodillo();
    }

    private void abrirRodillos() {
        player.getRodillo().abrirRodillo();
        ia.getRodillo().abrirRodillo();
    }

    @Override
    public void dibujar(Graphics graficos) {
        // Dibujar fondo
        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, new Rectangle(0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO), new Color(0x4a3432));

        ia.getRodillo().dibujarRodillo(graficos, false);

        Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.FONDOS.TABLERO), 0, 0);

        ia.dibujar(graficos);
        player.dibujar(graficos); // Se dibuja el rodillo


        if (player.seleccionandoIzquierda == 0) {
            dibujarTrianguloEleccion(graficos, Triangulo.TRIANGULO1, Triangulo.TRIANGULO2);
        } else if (player.seleccionandoIzquierda == 1) {
            dibujarTrianguloEleccion(graficos, Triangulo.TRIANGULO3, Triangulo.TRIANGULO4);
        }
        dibujarLucesTurno(graficos);
        dibujarFinDePartida(graficos);
    }

    private void dibujarFinDePartida(Graphics graficos) {
        if (resultado == ResultadoPartida.JUGANDO) {
            return;
        }
        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, new Rectangle(0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO), new Color(0, 0, 0, 170));

        String titulo;
        Color color;
        switch (resultado) {
            case VICTORIA:
                titulo = "VICTORIA";
                color = new Color(0xE4DEB0);
                break;
            case DERROTA:
                titulo = "DERROTA";
                color = new Color(0xD66A5E);
                break;
            default:
                titulo = "EMPATE";
                color = new Color(0xD6BFAD);
                break;
        }
        Dibujador.TEXTO.dibujarConSombra(graficos, titulo, Constantes.MITAD_VENTANA_ANCHO, Constantes.MITAD_VENTANA_ALTO, 90, color);
        Dibujador.TEXTO.dibujar(graficos, "+" + monedasGanadas + (monedasGanadas == 1 ? " moneda" : " monedas") + "  (total: " + Progreso.getInstance().getMonedas() + ")",
                Constantes.MITAD_VENTANA_ANCHO, Constantes.MITAD_VENTANA_ALTO + 40, 24, new Color(0xE4DEB0));
        Dibujador.TEXTO.dibujar(graficos, "Enter: volver al menu", Constantes.MITAD_VENTANA_ANCHO, Constantes.MITAD_VENTANA_ALTO + 80, 24, Color.WHITE);
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
            if (turno >= 1) { // Después de la 1.ª tirada la IA ya puede fijar canales
                ia.decidirFijados();
            }
            player.getRodillo().girarRodillo();

            ia.getRodillo().girarRodillo();
            turno++;
            Depurar.log("Turno: " + turno);
        }, tiempoActual);

        if (puedeFijar) {
            GestorControles.teclado.procesarTecla(GestorControles.teclado.espacio, () -> {
                player.getRodillo().fijarCanal(player.getRodillo().getPunteroPos());
            }, tiempoActual);
        }

        GestorControles.teclado.procesarTecla(GestorControles.teclado.derecha, () -> {
            player.getRodillo().moverPunteroDerecha();
        }, tiempoActual);

        GestorControles.teclado.procesarTecla(GestorControles.teclado.izquierda, () -> {
            player.getRodillo().moverPunteroIzquierda();
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