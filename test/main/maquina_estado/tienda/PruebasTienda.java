package main.maquina_estado.tienda;

import java.io.File;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

public class PruebasTienda {

    // Cuántas veces sale un símbolo roto en 3000 giros del rodillo del jugador recién creado
    private static int rotosEnGiros() {
        main.maquina_estado.juego.Player jugador = new main.maquina_estado.juego.Player();
        int rotos = 0;
        for (int i = 0; i < 3000; i++) {
            jugador.getRodillo().girarRodillo();
            for (main.Constantes.RODILLOS.Elementos e : jugador.getRodillo().getSalidaRodillo()) {
                if (e == main.Constantes.RODILLOS.Elementos.roto) {
                    rotos++;
                }
            }
        }
        return rotos;
    }

    public static void ejecutar() {
        prueba("GestorTienda: clic en una fila sin monedas avisa; con monedas compra y equipa; luego equipa", () -> {
            Progreso.usarSoloEnMemoria();
            GestorTienda t = new GestorTienda();
            t.accionSobreFila(2); // cuesta 10, hay 0
            verificar(t.getMensaje().startsWith("No tienes"), "mensaje: " + t.getMensaje());
            verificar(!Progreso.getInstance().estaComprado(2), "compró sin dinero");
            Progreso.getInstance().ganarMonedas(15);
            t.accionSobreFila(2);
            verificar(Progreso.getInstance().estaComprado(2) && Progreso.getInstance().getEquipado() == 2, "no compró/equipó");
            verificar(Progreso.getInstance().getMonedas() == 5, "monedas " + Progreso.getInstance().getMonedas());
            t.accionSobreFila(0); // el inicial ya es suyo: solo se equipa
            verificar(Progreso.getInstance().getEquipado() == 0 && Progreso.getInstance().getMonedas() == 5, "equipar no debe cobrar");
            Progreso.usarSoloEnMemoria();
        });


        prueba("el jugador usa el rodillo equipado (Rodillo 1 tiene rotos; el 2 no)", () -> {
            Progreso.usarSoloEnMemoria();
            verificar(rotosEnGiros() > 0, "el Rodillo 1 debería dar símbolos rotos");
            Progreso.getInstance().ganarMonedas(5);
            Progreso.getInstance().comprar(1); // lo equipa
            verificar(rotosEnGiros() == 0, "el Rodillo 2 no debería dar rotos");
            Progreso.usarSoloEnMemoria(); // deja el estado limpio para las demás pruebas
        });


        prueba("la tienda tiene un rodillo, nombre, descripción y precio por cada hueco", () -> {
            int n = Progreso.PRECIOS.length;
            verificar(main.Constantes.RODILLOS.DE_TIENDA.length == n, "tablas");
            verificar(main.Constantes.RODILLOS.NOMBRES_TIENDA.length == n, "nombres");
            verificar(main.Constantes.RODILLOS.DESCRIPCIONES_TIENDA.length == n, "descripciones");
            for (java.util.HashMap<main.Constantes.RODILLOS.Elementos, Double> t : main.Constantes.RODILLOS.DE_TIENDA) {
                verificar(t != null && !t.isEmpty(), "tabla vacía");
            }
        });


        prueba("Progreso nuevo: 0 monedas, solo el Rodillo 1 y equipado", () -> {
            Progreso p = new Progreso(null);
            verificar(p.getMonedas() == 0, "monedas");
            verificar(p.estaComprado(0) && !p.estaComprado(1), "comprados");
            verificar(p.getEquipado() == 0, "equipado");
        });

        prueba("comprar sin monedas suficientes falla y no cambia nada", () -> {
            Progreso p = new Progreso(null);
            p.ganarMonedas(4);
            verificar(!p.comprar(1), "compró el de 5 con 4 monedas");
            verificar(p.getMonedas() == 4 && !p.estaComprado(1) && p.getEquipado() == 0, "estado cambiado");
        });

        prueba("comprar descuenta el precio, lo marca comprado y lo equipa", () -> {
            Progreso p = new Progreso(null);
            p.ganarMonedas(12);
            verificar(p.comprar(1), "no compró");
            verificar(p.getMonedas() == 7 && p.estaComprado(1) && p.getEquipado() == 1, "monedas " + p.getMonedas());
            verificar(!p.comprar(1), "volvió a cobrar lo ya comprado");
            verificar(p.getMonedas() == 7, "se cobró dos veces");
        });

        prueba("equipar: solo lo comprado", () -> {
            Progreso p = new Progreso(null);
            p.ganarMonedas(5);
            verificar(!p.equipar(1), "equipó uno sin comprar");
            p.comprar(1);
            verificar(p.equipar(0) && p.getEquipado() == 0, "volver al Rodillo 1");
            verificar(p.equipar(1) && p.getEquipado() == 1, "equipar el comprado");
            verificar(!p.equipar(9) && !p.comprar(-1), "índices inválidos");
        });

        prueba("guardar y cargar conserva monedas, comprados y equipado", () -> {
            File f = File.createTempFile("girar", ".properties");
            f.deleteOnExit();
            Progreso a = new Progreso(f);
            a.ganarMonedas(30);
            a.comprar(1);
            a.comprar(3);
            a.equipar(1);
            Progreso b = new Progreso(f);
            verificar(b.getMonedas() == 30 - 5 - 20, "monedas " + b.getMonedas());
            verificar(b.estaComprado(0) && b.estaComprado(1) && !b.estaComprado(2) && b.estaComprado(3), "comprados");
            verificar(b.getEquipado() == 1, "equipado " + b.getEquipado());
        });

        prueba("un guardado corrupto no rompe: se parte de cero", () -> {
            File f = File.createTempFile("girar", ".properties");
            f.deleteOnExit();
            java.nio.file.Files.write(f.toPath(), "monedas=abc\nrodillosComprados=x,99\nrodilloEquipado=3\n".getBytes());
            Progreso p = new Progreso(f);
            verificar(p.getMonedas() == 0 && p.getEquipado() == 0 && p.estaComprado(0), "estado inesperado");
        });
    }
}
