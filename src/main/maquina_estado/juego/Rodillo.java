package main.maquina_estado.juego;

import main.Constantes;
import main.herramientas.CargadorRecursos;
import main.herramientas.Dibujador;
import main.Constantes.RODILLOS.Elementos;
import main.maquina_estado.IRodillo;
import main.motor.Interpolador;
import main.motor.Tiempo;

import java.awt.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Rodillo {
    private static final int MAX_ELEM = 5;

    /** Alto de una ventana de canal (lateral 35 + central 62 + lateral 35), de una celda del giro y ancho del canal. */
    private static final int ALTO_VENTANA = 132;
    private static final int ALTO_CELDA = 62;
    private static final int ANCHO_CANAL = 68;
    private static final int TICKS_DESTELLO = 10;

    /** Si es false el rodillo cambia de golpe, sin animación (pruebas y simulaciones). */
    public static boolean animar = true;

    private int punteroPos;

    // Estado de la animación de giro (se calcula a partir de Tiempo: no se actualiza nada por frame)
    private final boolean[] girando = new boolean[MAX_ELEM];
    private final long[] inicioGiro = new long[MAX_ELEM];
    private final int[] celdasGiro = new int[MAX_ELEM];
    private final Elementos[][] tira = new Elementos[MAX_ELEM][5 + MAX_ELEM]; // Celdas del giro: 5 + canal

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

        sortear(false);
    }

    private int duracionGiro(final int canal) {
        return 24 + 9 * canal; // Los canales frenan de izquierda a derecha, cada 0,15 s
    }

    private boolean enCurso(final int canal) {
        return girando[canal] && Tiempo.ahora() - inicioGiro[canal] < duracionGiro(canal);
    }

    /** true mientras algún canal sigue girando (el juego espera antes de seguir con el turno). */
    public boolean estaGirando() {
        for (int i = 0; i < MAX_ELEM; i++) {
            if (enCurso(i)) {
                return true;
            }
        }
        return false;
    }

    public void dibujar(Graphics graficos, Point coordenadas) {
        for (int i = 0; i < MAX_ELEM; i++) {
            int x = i == 0 ? coordenadas.x : coordenadas.x + i * 68 + 2;
            int y = coordenadas.y;

            if (enCurso(i)) {
                dibujarGirando(graficos, i, x, y);
            } else {
                Image imagen = CargadorRecursos.cargarImagenCompatibleOpaca("rec/sprites/rodillo/" + lineaPrincipal[i] + ".png");
                Image iLateral1 = CargadorRecursos.cargarImagenCompatibleOpaca("rec/sprites/rodillo/" + lateral1[i] + "Lateral.png");
                Image iLateral2 = CargadorRecursos.cargarImagenCompatibleOpaca("rec/sprites/rodillo/" + lateral2[i] + "Lateral.png");

                Dibujador.IMAGEN.dibujar(graficos, iLateral1, x, y);
                Dibujador.IMAGEN.dibujar(graficos, imagen, x, y + 35);
                Dibujador.IMAGEN.dibujar(graficos, iLateral2, x, y + 62 + 35);
                dibujarDestello(graficos, i, x, y);
            }

            if (fijados[i]) {
                Dibujador.IMAGEN.dibujar(graficos, CargadorRecursos.cargarImagenCompatibleTranslucida(Constantes.RUTAS.ELEMENTOS_RODILLO.FIJADOR), x, y);
            }
        }
    }

    // Canal en movimiento: una tira de símbolos cae y frena sobre la columna final (ya decidida) con un rastro difuminado
    private void dibujarGirando(final Graphics graficos, final int canal, final int x, final int y) {
        double dur = duracionGiro(canal);
        double t = Interpolador.limitar((Tiempo.ahora() - inicioGiro[canal]) / dur);
        double recorrido = (double) ALTO_CELDA * celdasGiro[canal];
        double d = recorrido * (1 - t) * (1 - t);          // Distancia que falta (frena al final)
        double velocidad = 2 * recorrido * (1 - t) / dur;  // Píxeles por actualización

        Graphics2D g = (Graphics2D) graficos.create(x, y, ANCHO_CANAL, ALTO_VENTANA);
        try {
            dibujarColumna(g, canal, (int) d);
            // Rastro: copias más arriba y más transparentes, tanto más separadas cuanto más rápido va
            int estela = (int) Math.min(velocidad * 0.5, 22);
            if (estela >= 2) {
                for (int k = 1; k <= 2; k++) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, k == 1 ? 0.40f : 0.20f));
                    dibujarColumna(g, canal, (int) d - estela * k);
                }
            }
        } finally {
            g.dispose();
        }
    }

    // Dibuja la columna final desplazada d píxeles hacia abajo y, por encima, las celdas aleatorias que van llegando
    private void dibujarColumna(final Graphics2D g, final int canal, final int d) {
        dibujarSi(g, "rec/sprites/rodillo/" + lateral1[canal] + "Lateral.png", d, 35);
        dibujarSi(g, "rec/sprites/rodillo/" + lineaPrincipal[canal] + ".png", d + 35, ALTO_CELDA);
        dibujarSi(g, "rec/sprites/rodillo/" + lateral2[canal] + "Lateral.png", d + 97, 35);
        for (int k = 1; k <= celdasGiro[canal]; k++) {
            dibujarSi(g, "rec/sprites/rodillo/" + tira[canal][k - 1] + ".png", d - ALTO_CELDA * k, ALTO_CELDA);
        }
    }

    // Solo carga y pinta lo que cae dentro de la ventana del canal
    private void dibujarSi(final Graphics2D g, final String ruta, final int y, final int alto) {
        if (y < ALTO_VENTANA && y + alto > 0) {
            g.drawImage(CargadorRecursos.cargarImagenCompatibleOpaca(ruta), 0, y, null);
        }
    }

    // Brillo breve al frenar el canal
    private void dibujarDestello(final Graphics graficos, final int canal, final int x, final int y) {
        if (!girando[canal]) {
            return;
        }
        long desde = Tiempo.ahora() - (inicioGiro[canal] + duracionGiro(canal));
        if (desde >= 0 && desde < TICKS_DESTELLO) {
            Graphics2D g = (Graphics2D) graficos.create(x, y, ANCHO_CANAL, ALTO_VENTANA);
            g.setColor(new Color(1f, 1f, 1f, 0.45f * (1 - desde / (float) TICKS_DESTELLO)));
            g.fillRect(0, 0, ANCHO_CANAL, ALTO_VENTANA);
            g.dispose();
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
        sortear(animar);
    }

    // El resultado se decide aquí, al empezar; la animación solo lo enseña
    private void sortear(final boolean conAnimacion) {
        for (int i = 0; i < MAX_ELEM; i++) {
            girando[i] = false;
            if (!fijados[i]) { // Solo gira si el canal no está fijado
                lateral1[i] = Elementos.values()[(int) (Math.random() * Elementos.values().length)];
                lineaPrincipal[i] = distribuciones[i].tirar();
                lateral2[i] = Elementos.values()[(int) (Math.random() * Elementos.values().length)];

                if (conAnimacion) {
                    girando[i] = true;
                    inicioGiro[i] = Tiempo.ahora();
                    celdasGiro[i] = 5 + i;
                    for (int k = 0; k < celdasGiro[i]; k++) {
                        tira[i][k] = Elementos.values()[(int) (Math.random() * Elementos.values().length)];
                    }
                }
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
