package main.maquina_estado.tienda;

import java.io.File;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

public class PruebasTienda {

    public static void ejecutar() {
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
