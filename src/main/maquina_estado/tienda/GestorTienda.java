package main.maquina_estado.tienda;

import main.AudioPlayer;
import main.Constantes;
import main.control.Raton;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.herramientas.MedidorString;
import main.maquina_estado.IAudio;
import main.maquina_estado.IEstadoJuego;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Tienda: se gastan las monedas ganadas en partidas para comprar y equipar rodillos mejores.
 * Diseño en docs/tienda.md.
 */
public class GestorTienda implements IEstadoJuego, IAudio {

    private static final int ANCHO_FILA = 640;
    private static final int ALTO_FILA = 56;
    private static final int X_FILA = (Constantes.ANCHO_JUEGO - ANCHO_FILA) / 2;
    private static final int Y_PRIMERA_FILA = 190;
    private static final int SEPARACION = 72;
    private static final int FRAMES_MENSAJE = 150; // 2,5 s a 60 actualizaciones por segundo

    private static final Color COLOR_TEXTO = new Color(0xE1EAE7);
    private static final Color COLOR_RESALTADO = new Color(0x89D4D9);
    private static final Color COLOR_EQUIPADO = new Color(0xE4DEB0);
    private static final Color COLOR_ERROR = new Color(0xD66A5E);

    private final Rectangle[] filas = new Rectangle[Progreso.PRECIOS.length];
    private String mensaje = "";
    private int framesMensaje = 0;

    public GestorTienda() {
        for (int i = 0; i < filas.length; i++) {
            filas[i] = new Rectangle(X_FILA, Y_PRIMERA_FILA + i * SEPARACION, ANCHO_FILA, ALTO_FILA);
        }
    }

    @Override
    public void actualizar() {
        if (framesMensaje > 0) {
            framesMensaje--;
        }

        Raton raton = Raton.getInstance();
        int fila = filaEn(raton.obtenerRectanguloPosicion());
        if (fila >= 0 && raton.obtenerClickIzquierdo()) {
            raton.reiniciarClickIzquierdo(); // Un clic = una sola acción
            AudioPlayer.getInstance().playSoundOnce(Constantes.RUTAS.EFECTOS.CLICK_BOTON);
            accionSobreFila(fila);
        }
    }

    // Índice de la fila que toca el rectángulo (el ratón) o -1
    private int filaEn(final Rectangle puntero) {
        for (int i = 0; i < filas.length; i++) {
            if (filas[i].intersects(puntero)) {
                return i;
            }
        }
        return -1;
    }

    // Clic en una fila: si es nuevo se compra (y se equipa); si ya lo tienes, se equipa
    void accionSobreFila(final int fila) {
        Progreso progreso = Progreso.getInstance();
        if (progreso.estaComprado(fila)) {
            progreso.equipar(fila);
            mostrarMensaje(Constantes.RODILLOS.NOMBRES_TIENDA[fila] + " equipado");
        } else if (progreso.comprar(fila)) {
            mostrarMensaje(Constantes.RODILLOS.NOMBRES_TIENDA[fila] + " comprado y equipado");
        } else {
            mostrarMensaje("No tienes monedas suficientes");
        }
    }

    private void mostrarMensaje(final String texto) {
        mensaje = texto;
        framesMensaje = FRAMES_MENSAJE;
    }

    String getMensaje() {
        return framesMensaje > 0 ? mensaje : "";
    }

    @Override
    public void dibujar(Graphics graficos) {
        // Dibujar fondo
        Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarEscalada(Constantes.RUTAS.FONDOS.TIENDA, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO), 0, 0);


        Progreso progreso = Progreso.getInstance();
        Dibujador.TEXTO.dibujarConSombra(graficos, "TIENDA", Constantes.MITAD_VENTANA_ANCHO, 110, 60, COLOR_TEXTO);
        Dibujador.TEXTO.dibujarConSombra(graficos, "Monedas: " + progreso.getMonedas(), Constantes.MITAD_VENTANA_ANCHO, 160, 26, COLOR_EQUIPADO);

        int hover = filaEn(Raton.getInstance().obtenerRectanguloPosicion());
        for (int i = 0; i < filas.length; i++) {
            dibujarFila(graficos, i, i == hover, progreso);
        }

        // Descripción del rodillo bajo el ratón y mensajes de la última acción
        if (hover >= 0) {
            Dibujador.TEXTO.dibujar(graficos, Constantes.RODILLOS.DESCRIPCIONES_TIENDA[hover], Constantes.MITAD_VENTANA_ANCHO, 520, 20, COLOR_TEXTO);
        }
        if (framesMensaje > 0) {
            boolean error = mensaje.startsWith("No tienes");
            Dibujador.TEXTO.dibujarConSombra(graficos, mensaje, Constantes.MITAD_VENTANA_ANCHO, 570, 24, error ? COLOR_ERROR : COLOR_EQUIPADO);
        }
        Dibujador.TEXTO.dibujar(graficos, "Enter: volver al menu", Constantes.MITAD_VENTANA_ANCHO, 625, 16, COLOR_TEXTO);
    }

    private void dibujarFila(final Graphics graficos, final int i, final boolean resaltada, final Progreso progreso) {
        Rectangle fila = filas[i];
        boolean equipado = progreso.getEquipado() == i;

        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, fila, new Color(0, 0, 0, resaltada ? 215 : 175));
        Color borde = equipado ? COLOR_EQUIPADO : (resaltada ? COLOR_RESALTADO : new Color(255, 255, 255, 70));
        Dibujador.RECTANGULO_CONTORNO.dibujar(graficos, fila, equipado ? 3 : 2, borde);

        Dibujador.TEXTO.dibujar(graficos, Constantes.RODILLOS.NOMBRES_TIENDA[i], new Point(fila.x + 24, fila.y + 36), 26, COLOR_TEXTO);

        String estado;
        Color colorEstado = COLOR_TEXTO;
        if (equipado) {
            estado = "EQUIPADO";
            colorEstado = COLOR_EQUIPADO;
        } else if (progreso.estaComprado(i)) {
            estado = "COMPRADO";
        } else {
            estado = Progreso.PRECIOS[i] + " monedas";
            if (progreso.getMonedas() < Progreso.PRECIOS[i]) {
                colorEstado = COLOR_ERROR;
            }
        }
        int ancho = MedidorString.medirAnchoPixeles(graficos, estado, graficos.getFont(), 22);
        Dibujador.TEXTO.dibujar(graficos, estado, new Point(fila.x + fila.width - 24 - ancho, fila.y + 36), 22, colorEstado);
    }

    public void activarAudio() {
        AudioPlayer.getInstance().stopSound();
        AudioPlayer.getInstance().playSound(Constantes.RUTAS.MUSICA.TIENDA);
    }
}
