package main.control;

import main.Constantes;
import main.herramientas.Dibujador;
import main.herramientas.MedidorString;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class GestorEtiquetas {
    private static ArrayList<Etiqueta> etiquetasMenuPrincipal;
    private static ArrayList<Etiqueta> etiquetasMenuMusica;
    private static GestorEtiquetas instance;

    public GestorEtiquetas() {
        etiquetasMenuPrincipal = new ArrayList<>();
        initEtiquetasMenuPrincipal();
        etiquetasMenuMusica = new ArrayList<>();
        initEtiquetasMenuMusica();
    }

    public static GestorEtiquetas getInstance() {
        if (instance == null) {
            instance = new GestorEtiquetas();
        }
        return instance;

    }

    private void initEtiquetasMenuPrincipal() {
        int[] coordenadasEtiquetas = {
                Constantes.MITAD_VENTANA_ANCHO, Constantes.MITAD_VENTANA_ALTO
        };
        etiquetasMenuPrincipal.add(new Etiqueta(" Jugar", coordenadasEtiquetas[0], coordenadasEtiquetas[1], Constantes.FUENTES.FUENTE_TEXTO, 30));
        etiquetasMenuPrincipal.add(new Etiqueta(" Tienda", coordenadasEtiquetas[0], coordenadasEtiquetas[1] + 50, Constantes.FUENTES.FUENTE_TEXTO, 30));
        etiquetasMenuPrincipal.add(new Etiqueta(" Musica", coordenadasEtiquetas[0], coordenadasEtiquetas[1] + 100, Constantes.FUENTES.FUENTE_TEXTO, 30));
        etiquetasMenuPrincipal.add(new Etiqueta(" Creditos", coordenadasEtiquetas[0], coordenadasEtiquetas[1] + 150, Constantes.FUENTES.FUENTE_TEXTO, 30));
        Etiqueta.resetId();
    }

    private void initEtiquetasMenuMusica() {
        int margen = 0;
        for (File archivo : new File("rec/musica").listFiles()) {
            if (archivo.getName().endsWith(".wav")) {
                String nombre = MedidorString.eliminarSubcadena(archivo.getName(), ".wav");
                margen += 25;
                etiquetasMenuMusica.add(new Etiqueta(nombre, Constantes.ANCHO_JUEGO / 2, 200 + margen, Constantes.FUENTES.FUENTE_TEXTO, 20));
            }
        }
        Etiqueta.resetId();
    }

    public ArrayList<Etiqueta> getEtiquetasMenuPrincipal() {
        return etiquetasMenuPrincipal;
    }

    public ArrayList<Etiqueta> getEtiquetasMenuMusica() {
        return etiquetasMenuMusica;
    }
}
