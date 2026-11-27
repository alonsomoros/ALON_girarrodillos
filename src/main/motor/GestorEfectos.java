package main.motor;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Lista de efectos activos, repartidos en 3 capas: FONDO (detrás de todo), MEDIO y FRENTE (encima de todo).
 * Actualiza, dibuja y retira los terminados. Tiene un límite para que una ráfaga de partículas no hunda los FPS.
 */
public class GestorEfectos {

    public enum Capa {
        FONDO, MEDIO, FRENTE
    }

    public static final int LIMITE_POR_DEFECTO = 400;

    private final Map<Capa, List<Efecto>> capas = new EnumMap<>(Capa.class);
    private final int limite;

    public GestorEfectos() {
        this(LIMITE_POR_DEFECTO);
    }

    public GestorEfectos(final int limite) {
        this.limite = limite;
        for (Capa capa : Capa.values()) {
            capas.put(capa, new ArrayList<>());
        }
    }

    public boolean agregar(final Efecto efecto) {
        return agregar(Capa.MEDIO, efecto);
    }

    /** Añade un efecto a una capa. Devuelve false (y lo descarta) si se alcanzó el límite. */
    public boolean agregar(final Capa capa, final Efecto efecto) {
        if (cantidad() >= limite) {
            return false;
        }
        capas.get(capa).add(efecto);
        return true;
    }

    /** Actualiza todos los efectos y retira los terminados. Un efecto puede añadir otros mientras se actualiza. */
    public void actualizar() {
        for (Capa capa : Capa.values()) {
            List<Efecto> lista = capas.get(capa);
            int n = lista.size(); // Los añadidos durante esta pasada empiezan a actualizarse en la siguiente
            for (int i = 0; i < n; i++) {
                lista.get(i).actualizar();
            }
            lista.removeIf(Efecto::terminado);
        }
    }

    public void dibujar(final Graphics graficos) {
        for (Capa capa : Capa.values()) {
            dibujar(graficos, capa);
        }
    }

    public void dibujar(final Graphics graficos, final Capa capa) {
        List<Efecto> lista = capas.get(capa);
        for (int i = 0; i < lista.size(); i++) {
            lista.get(i).dibujar(graficos);
        }
    }

    public void limpiar() {
        for (List<Efecto> lista : capas.values()) {
            lista.clear();
        }
    }

    public int cantidad() {
        int total = 0;
        for (List<Efecto> lista : capas.values()) {
            total += lista.size();
        }
        return total;
    }

    public int cantidad(final Capa capa) {
        return capas.get(capa).size();
    }
}
