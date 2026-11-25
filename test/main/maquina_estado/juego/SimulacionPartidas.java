package main.maquina_estado.juego;

import main.Constantes;
import main.Constantes.RODILLOS.Elementos;
import java.util.*;

/**
 * Simula partidas sin ventana: el jugador (rodillo R1) y la IA (rodillo R2) usan la misma estrategia de fijado
 * y héroes al azar. Sirve para equilibrar las probabilidades de los rodillos.
 * Uso: java -cp out\production\Girarodillo;out\test main.maquina_estado.juego.SimulacionPartidas 300
 */
public class SimulacionPartidas {
    static void fijarHeuristica(Rodillo r) {
        Elementos[] s = r.getSalidaRodillo();
        for (int i = 0; i < 5; i++) r.setFijado(i, IA.esValioso(s[i]));
    }

    public static void main(String[] a) throws Exception {
        main.AudioPlayer.efectosActivos = false;
        Ataque.duracionPorDefecto = 0;
        int n = Integer.parseInt(a[0]);
        int ganaIA = 0, ganaPlayer = 0, empates = 0, sinFin = 0; long rondasTotal = 0;
        for (int g = 0; g < n; g++) {
            GestorGirarrodillos gg = new GestorGirarrodillos();
            Player p = gg.player; IA ia = gg.ia;
            // Player elige 2 heroes al azar como la IA
            List<Figura> pool = new ArrayList<>(p.figurasDisponibles);
            Collections.shuffle(pool);
            p.setFiguraIzquierda(pool.get(0)); pool.get(0).setPosicion(3);
            p.setFiguraDerecha(pool.get(1)); pool.get(1).setPosicion(4);
            int ronda = 0;
            while (p.getVida() > 0 && ia.getVida() > 0 && ronda < 200) {
                ronda++;
                p.getRodillo().abrirRodillo(); ia.getRodillo().abrirRodillo();
                for (int t = 0; t < 3; t++) {
                    if (t >= 1) { fijarHeuristica(p.getRodillo()); ia.decidirFijados(); }
                    p.getRodillo().girarRodillo(); ia.getRodillo().girarRodillo();
                }
                p.getRodillo().cerrarRodillo(); ia.getRodillo().cerrarRodillo();
                p.calcularSalidaRodillo(p.getRodillo().getSalidaRodillo()); p.actualizarEstados();
                ia.calcularSalidaRodillo(ia.getRodillo().getSalidaRodillo()); ia.actualizarEstados();
                int guard = 0;
                while ((p.figurasAtacando() || ia.figurasAtacando()) && guard++ < 100000) { p.actualizar(); ia.actualizar(); Thread.sleep(0, 1); }
            }
            rondasTotal += ronda;
            boolean pc = p.getVida() <= 0, ic = ia.getVida() <= 0;
            if (pc && ic) empates++; else if (ic) ganaPlayer++; else if (pc) ganaIA++; else sinFin++;
        }
        System.out.println("n=" + n + " ganaIA=" + ganaIA + " ganaPlayer=" + ganaPlayer + " empates=" + empates + " sinFin=" + sinFin + " rondasMedias=" + (rondasTotal / (double) n));
    }
}
