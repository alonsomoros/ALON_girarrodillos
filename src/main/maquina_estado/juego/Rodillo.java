package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.Constantes.RODILLOS.Elementos;
import main.maquina_estado.IRodillo;

import java.awt.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Rodillo {
    private static final int MAX_ELEM = 5;

    private int punteroPos;

    private final Point[] punteroTriangulo;

    private final Elementos[] lineaPrincipal;
    private final boolean[] fijados; // Array para indicar si los canales están fijados
    private final Elementos[] lateral1;
    private final Elementos[] lateral2;

    // Distribución normalizada de cada canal: símbolos en orden fijo y probabilidad acumulada
    private static class Distribucion {
        private final Elementos[] elementos;
        private final double[] acumulada;

        Distribucion(final Map<Elementos, Double> probabilidades) {
            // EnumMap: el orden es siempre el del enum (un HashMap no garantiza orden)
            EnumMap<Elementos, Double> ordenadas = new EnumMap<>(Elementos.class);
            double total = 0;
            for (Map.Entry<Elementos, Double> e : probabilidades.entrySet()) {
                if (e.getValue() > 0) {
                    ordenadas.put(e.getKey(), e.getValue());
                    total += e.getValue();
                }
            }
            if (ordenadas.isEmpty()) { // Sin datos válidos: todos los símbolos por igual
                for (Elementos e : Elementos.values()) {
                    ordenadas.put(e, 1.0);
                }
                total = ordenadas.size();
            }

            elementos = ordenadas.keySet().toArray(new Elementos[0]);
            acumulada = new double[elementos.length];
            double suma = 0;
            for (int i = 0; i < elementos.length; i++) {
                suma += ordenadas.get(elementos[i]) / total; // Normaliza: la suma final es 1
                acumulada[i] = suma;
            }
        }

        Elementos tirar() {
            double p = Math.random();
            for (int i = 0; i < elementos.length; i++) {
                if (p < acumulada[i]) {
                    return elementos[i];
                }
            }
            return elementos[elementos.length - 1]; // Error de coma flotante: nunca devuelve null
        }
    }

    private final Distribucion[] distribuciones;

    public Rodillo(HashMap<Elementos, Double>[] probabilidades) {
        lineaPrincipal = new Elementos[MAX_ELEM];
        lateral1 = new Elementos[MAX_ELEM];
        lateral2 = new Elementos[MAX_ELEM];
        distribuciones = new Distribucion[MAX_ELEM];
        for (int i = 0; i < MAX_ELEM; i++) {
            distribuciones[i] = new Distribucion(probabilidades[i]);
        }

        punteroPos = 0;
        punteroTriangulo = new Point[]{
                new Point(466, 635),
                new Point(481, 625),
                new Point(496, 635)
        };

        fijados = new boolean[MAX_ELEM];

        girarRodillo();
    }

    public void dibujar(Graphics graficos, Point coordenadas) {
        for (int i = 0; i < MAX_ELEM; i++) {
            Image imagen = CargadorRecursos.cargarImagenCompatibleOpaca("rec/sprites/rodillo/" + lineaPrincipal[i] + ".png");
            Image iLateral1 = CargadorRecursos.cargarImagenCompatibleOpaca("rec/sprites/rodillo/" + lateral1[i] + "Lateral.png");
            Image iLateral2 = CargadorRecursos.cargarImagenCompatibleOpaca("rec/sprites/rodillo/" + lateral2[i] + "Lateral.png");

            if (i == 0) {
                Dibujador.IMAGEN.dibujar(graficos, iLateral1, coordenadas.x, coordenadas.y);
                Dibujador.IMAGEN.dibujar(graficos, imagen, coordenadas.x, coordenadas.y + 35);
                Dibujador.IMAGEN.dibujar(graficos, iLateral2, coordenadas.x, coordenadas.y + 62 + 35);
            } else {
                Dibujador.IMAGEN.dibujar(graficos, iLateral1, coordenadas.x + i * 68 + 2, coordenadas.y);
                Dibujador.IMAGEN.dibujar(graficos, imagen, coordenadas.x + i * 68 + 2, coordenadas.y + 35);
                Dibujador.IMAGEN.dibujar(graficos, iLateral2, coordenadas.x + i * 68 + 2, coordenadas.y + 62 + 35);
            }

            if (fijados[i]) {
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ELEMENTOS_RODILLO.FIJADOR), coordenadas.x + i * 68 + 2, coordenadas.y);
            }
        }
    }

    public int getPunteroPos() {
        return punteroPos;
    }

    public void dibujarPuntero(Graphics graficos) {
        Dibujador.TRIANGULO.dibujar(graficos, punteroPos, punteroTriangulo, new Color(0xD6BFAD));
    }

    public void moverPunteroIzquierda() {
        punteroPos--;
        if (punteroPos < 0) {
            punteroPos = 0;
        }
    }

    public void moverPunteroDerecha() {
        punteroPos++;
        if (punteroPos >= MAX_ELEM) {
            punteroPos = MAX_ELEM - 1;
        }
    }

    public void girarRodillo() {
        for (int i = 0; i < MAX_ELEM; i++) {

            if (!fijados[i]) { // Solo gira si el canal no está fijado
                lateral1[i] = Elementos.values()[(int) (Math.random() * Elementos.values().length)];

                lineaPrincipal[i] = distribuciones[i].tirar();


                lateral2[i] = Elementos.values()[(int) (Math.random() * Elementos.values().length)];

            }
        }
    }

    public Elementos[] getSalidaRodillo() {
        return this.lineaPrincipal;
    }

    public boolean estaFijado(int index) {
        return index >= 0 && index < MAX_ELEM && fijados[index];
    }

    public void setFijado(int index, boolean fijado) {
        if (index >= 0 && index < MAX_ELEM) {
            fijados[index] = fijado;
        }
    }

    public void fijarCanal(int index) {
        if (index >= 0 && index < MAX_ELEM) {
            fijados[index] = !fijados[index];
        }
    }


    public void dibujarRodillo(Graphics graficos, boolean player) {
        if (player) {
            this.dibujar(graficos, Constantes.COORDENADAS.RODILLO_PLAYER);
        } else {
            this.dibujar(graficos, Constantes.COORDENADAS.RODILLO_IA);
        }
    }

    public void abrirRodillo() {
        for (int i = 0; i < MAX_ELEM; i++) {
            fijados[i] = false;
        }
    }

    public void cerrarRodillo() {
        for (int i = 0; i < MAX_ELEM; i++) {
            fijados[i] = true;
        }
    }
}
