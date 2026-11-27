package main.maquina_estado.menu_inicial;

import main.AudioPlayer;
import main.Constantes;
import main.control.Etiqueta;
import main.control.GestorEtiquetas;
import main.control.Raton;
import main.graficos.SuperficieDeDibujo;
import main.herramientas.CargadorRecursos;
import main.herramientas.Depurar;
import main.herramientas.Dibujador;

import main.herramientas.MedidorString;
import main.maquina_estado.IAudio;
import main.maquina_estado.IEstadoJuego;
import main.maquina_estado.Estado;
import main.maquina_estado.GestorEstados;
import main.maquina_estado.juego.IA;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GestorMenuPrincipal implements IEstadoJuego, IAudio {
    private final BufferedImage imagenFondo;
    private static double indexFotoMenu;
    private static final int[] coordenadasEtiquetas = {
            Constantes.MITAD_VENTANA_ANCHO, Constantes.MITAD_VENTANA_ALTO // Cargar Partida
    };
    // Estado al que lleva cada etiqueta del menú, en el mismo orden que GestorEtiquetas: Jugar, Tienda, Musica, Creditos
    private static final Estado[] DESTINOS = {Estado.JUEGO, Estado.TIENDA, Estado.MUSICA, Estado.CREDITOS};
    private static final ArrayList<Etiqueta> secciones;

    static {
        secciones = GestorEtiquetas.getInstance().getEtiquetasMenuPrincipal();
    }

    private final boolean etiquetaJugar = false;

    private Etiqueta etiquetaActual;
    private final Color colorTransparente = Constantes.principal;



    public GestorMenuPrincipal() {

        indexFotoMenu = Math.floor(Math.random() * 4);
        imagenFondo = CargadorRecursos.cargarEscalada(Constantes.RUTAS.FONDOS.PRINCIPAL + (int) indexFotoMenu + ".png", Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO);


        etiquetaActual = secciones.get(0);
    }

    public void activarAudio() {
        AudioPlayer.getInstance().stopSound();
        AudioPlayer.getInstance().playSound(Constantes.RUTAS.MUSICA.MENU_PRINCIPAL);
    }

    @Override
    public void actualizar() {
        Etiqueta seccionNueva = null;

        for (Etiqueta etiqueta : secciones) {
            if (etiqueta.getHitbox().intersects(Raton.getInstance().obtenerRectanguloPosicion())) {
                seccionNueva = etiqueta;
                if (Raton.getInstance().obtenerClickIzquierdo()) {
//                    if (etiqueta.getNombre().equals(" Jugar")){
//                        Dibujador.IMAGEN.dibujar(SuperficieDeDibujo.getInstance().graficos2D, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ICONO.FLECHA_ETIQUETA), etiqueta.getHitbox().x, etiqueta.getHitbox().y);
//                    }
                    Raton.getInstance().reiniciarClickIzquierdo(); // Un clic = una sola acción
                    AudioPlayer.getInstance().playSoundOnce(Constantes.RUTAS.EFECTOS.CLICK_BOTON);

                    GestorEstados.getInstance().irA(DESTINOS[secciones.indexOf(etiqueta)]);

                }
            }
        }

        etiquetaActual = seccionNueva;

    }


    @Override
    public void dibujar(Graphics graficos) {

        // Dibujar fondo
        Dibujador.IMAGEN.dibujar(graficos,imagenFondo, 0, 0, Constantes.ANCHO_JUEGO, Constantes.ALTO_JUEGO);

        dibujarTitulo(graficos);
        dibujarPaneles(graficos);
        dibujarCopyright(graficos);

//        if (etiquetaJugar) {
//            dibujarEtiquetaJugar(graficos);
//        }

        Dibujador.TEXTO.dibujar(graficos, etiquetaActual != null ? etiquetaActual.getNombre() : "Ninguna", Constantes.ANCHO_JUEGO / 2, 35, 15, Color.WHITE);

//        for (int i = 0; i < 50; i++) {
//            Dibujador.CIRCULO.dibujar(graficos, (int) (Math.random() * Constantes.ANCHO_JUEGO), (int) (Math.random() * Constantes.ALTO_JUEGO), 1, new Color(i * 5, i * 5, i * 5));
//        }
        if (Depurar.ACTIVO) {
            Dibujador.TEXTO.dibujar(graficos, Dibujador.numeroObjetosDibujados + "", 40, 80, Color.WHITE);
        }
    }

    public void dibujarTitulo(Graphics graficos) {

        Dibujador.RECTANGULO_RELLENO.dibujar(graficos, 386, 104, 515, 91, colorTransparente);
        Dibujador.SEMICIRCULO.dibujar(graficos, 386, 149, 46, 90, 180, colorTransparente);
        Dibujador.SEMICIRCULO.dibujar(graficos, 901, 149, 46, 270, 180, colorTransparente);

        int centroPantallaAncho = Constantes.ANCHO_JUEGO / 2;
        int centroPantallaAlto = Constantes.ALTO_JUEGO / 2;
        int margenTitulo = -100;
        Dibujador.TEXTO.dibujarConSombra(graficos, "Girarrodillos", centroPantallaAncho, centroPantallaAlto + margenTitulo, 69, Constantes.FUENTES.FUENTE_TITULO, new Color(0xEDF4F4));
    }

    public void dibujarPaneles(Graphics graficos) { // Como la posicion se estimula al crear el objeto la posicion relativa la pondré dentro del metodo que dibuja
        Etiqueta seccionNueva = null;

        for (Etiqueta etiqueta : secciones) {
//            Dibujador.RECTANGULO_CONTORNO.dibujar(graficos, etiqueta.getHitbox().x, etiqueta.getHitbox().y, etiqueta.getHitbox().width, etiqueta.getHitbox().height, Color.WHITE); // Pos ✓ - Hitbox ✓

            if (etiqueta.getHitbox().intersects(Raton.getInstance().obtenerRectanguloPosicion())) {
//                etiqueta.setNombre(etiqueta.getNombre().replace(" ", "->"));
//                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ICONO.FLECHA_ETIQUETA), etiqueta.getHitbox().x, etiqueta.getHitbox().y);
                Dibujador.ETIQUETA.dibujarEtiquetas(graficos, etiqueta, Constantes.azul_1);
                seccionNueva = etiqueta;
            } else {
//                etiqueta.setNombre(etiqueta.getNombre().replace("->", " "));
                Dibujador.ETIQUETA.dibujarEtiquetas(graficos, etiqueta, Color.white);
            }

        }
        etiquetaActual = seccionNueva;
    }

    public void dibujarCopyright(Graphics graficos) {
        Dibujador.TEXTO.dibujar(graficos, "Copyright @ 2023 Sabotage Studio ~ Alxns", coordenadasEtiquetas[0], Constantes.ALTO_JUEGO, Color.WHITE);
    }

    public static void cambiarFotoMenu() {
        indexFotoMenu = (indexFotoMenu + 1) % 4;
    }


}
