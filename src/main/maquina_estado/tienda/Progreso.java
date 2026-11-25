package main.maquina_estado.tienda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Lo que el jugador conserva entre partidas: monedas, rodillos comprados y rodillo equipado.
 * Se guarda en guardado.properties (java.util.Properties). Ver docs/tienda.md.
 */
public class Progreso {

    public static final String ARCHIVO_POR_DEFECTO = "guardado.properties";

    /** Precio de cada rodillo (índice = rodillo). El rodillo 0 es el inicial y es gratis. */
    public static final int[] PRECIOS = {0, 5, 10, 20};

    public static final int MONEDAS_VICTORIA = 3;
    public static final int MONEDAS_EMPATE = 1;
    public static final int MONEDAS_DERROTA = 1;

    private static Progreso instance;

    private final File archivo; // null = solo en memoria (pruebas)
    private int monedas;
    private final boolean[] comprados = new boolean[PRECIOS.length];
    private int equipado;

    /** Progreso nuevo; si el archivo existe lo carga, y si es null no guarda nada (pruebas). */
    public Progreso(final File archivo) {
        this.archivo = archivo;
        comprados[0] = true;
        cargar();
    }

    public static synchronized Progreso getInstance() {
        if (instance == null) {
            instance = new Progreso(new File(ARCHIVO_POR_DEFECTO));
        }
        return instance;
    }

    /** Las pruebas usan un progreso en memoria para no tocar el guardado real. */
    public static synchronized void usarSoloEnMemoria() {
        instance = new Progreso(null);
    }

    public int getMonedas() {
        return monedas;
    }

    public int getEquipado() {
        return equipado;
    }

    public boolean estaComprado(final int rodillo) {
        return rodilloValido(rodillo) && comprados[rodillo];
    }

    public void ganarMonedas(final int cantidad) {
        if (cantidad > 0) {
            monedas += cantidad;
            guardar();
        }
    }

    /** Compra (y equipa) el rodillo si no lo tiene y alcanzan las monedas. */
    public boolean comprar(final int rodillo) {
        if (!rodilloValido(rodillo) || comprados[rodillo] || monedas < PRECIOS[rodillo]) {
            return false;
        }
        monedas -= PRECIOS[rodillo];
        comprados[rodillo] = true;
        equipado = rodillo;
        guardar();
        return true;
    }

    /** Equipa un rodillo ya comprado. */
    public boolean equipar(final int rodillo) {
        if (!estaComprado(rodillo)) {
            return false;
        }
        equipado = rodillo;
        guardar();
        return true;
    }

    private boolean rodilloValido(final int rodillo) {
        return rodillo >= 0 && rodillo < PRECIOS.length;
    }

    private void cargar() {
        if (archivo == null || !archivo.exists()) {
            return;
        }
        Properties p = new Properties();
        try (InputStream in = new FileInputStream(archivo)) {
            p.load(in);
        } catch (IOException e) {
            System.err.println("No se pudo leer " + archivo + ": " + e.getMessage());
            return;
        }
        monedas = Math.max(0, leerEntero(p.getProperty("monedas"), 0));
        for (String parte : p.getProperty("rodillosComprados", "0").split(",")) {
            int r = leerEntero(parte.trim(), -1);
            if (rodilloValido(r)) {
                comprados[r] = true;
            }
        }
        int eq = leerEntero(p.getProperty("rodilloEquipado"), 0);
        equipado = estaComprado(eq) ? eq : 0;
    }

    private static int leerEntero(final String texto, final int porDefecto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    private void guardar() {
        if (archivo == null) {
            return;
        }
        StringBuilder lista = new StringBuilder();
        for (int i = 0; i < comprados.length; i++) {
            if (comprados[i]) {
                lista.append(lista.length() > 0 ? "," : "").append(i);
            }
        }
        Properties p = new Properties();
        p.setProperty("monedas", String.valueOf(monedas));
        p.setProperty("rodillosComprados", lista.toString());
        p.setProperty("rodilloEquipado", String.valueOf(equipado));
        try (OutputStream out = new FileOutputStream(archivo)) {
            p.store(out, "Girarrodillos: progreso del jugador");
        } catch (IOException e) {
            System.err.println("No se pudo guardar " + archivo + ": " + e.getMessage());
        }
    }
}
