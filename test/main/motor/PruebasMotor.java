package main.motor;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

public class PruebasMotor {

    public static void ejecutar() {
        prueba("Interpolador: todas las curvas empiezan en 0 y acaban en 1", () -> {
            Interpolador.Easing[] curvas = {Interpolador.LINEAL, Interpolador.ENTRADA, Interpolador.SALIDA,
                    Interpolador.ENTRADA_SALIDA, Interpolador.RETROCESO, Interpolador.REBOTE, Interpolador.ELASTICO};
            for (int i = 0; i < curvas.length; i++) {
                verificar(Math.abs(curvas[i].aplicar(0)) < 1e-9, "curva " + i + " en 0 = " + curvas[i].aplicar(0));
                verificar(Math.abs(curvas[i].aplicar(1) - 1) < 1e-9, "curva " + i + " en 1 = " + curvas[i].aplicar(1));
            }
        });

        prueba("Interpolador: valores conocidos y monotonía de las curvas suaves", () -> {
            verificar(Math.abs(Interpolador.LINEAL.aplicar(0.5) - 0.5) < 1e-9, "lineal");
            verificar(Math.abs(Interpolador.ENTRADA_SALIDA.aplicar(0.5) - 0.5) < 1e-9, "entrada-salida en el centro");
            verificar(Interpolador.ENTRADA.aplicar(0.5) < 0.5, "entrada arranca lenta");
            verificar(Interpolador.SALIDA.aplicar(0.5) > 0.5, "salida frena");
            Interpolador.Easing[] suaves = {Interpolador.LINEAL, Interpolador.ENTRADA, Interpolador.SALIDA, Interpolador.ENTRADA_SALIDA};
            for (Interpolador.Easing e : suaves) {
                double anterior = -1;
                for (int i = 0; i <= 100; i++) {
                    double v = e.aplicar(i / 100.0);
                    verificar(v >= anterior - 1e-12, "no monótona");
                    anterior = v;
                }
            }
        });

        prueba("Interpolador: el retroceso y el elástico se pasan del destino; el rebote nunca baja de 0", () -> {
            boolean pasaRetroceso = false, pasaElastico = false;
            for (int i = 0; i <= 100; i++) {
                double t = i / 100.0;
                pasaRetroceso |= Interpolador.RETROCESO.aplicar(t) > 1.0001;
                pasaElastico |= Interpolador.ELASTICO.aplicar(t) > 1.0001;
                verificar(Interpolador.REBOTE.aplicar(t) >= 0 && Interpolador.REBOTE.aplicar(t) <= 1.0001, "rebote fuera de [0,1] en " + t);
            }
            verificar(pasaRetroceso && pasaElastico, "deberían pasarse del destino");
        });

        prueba("Interpolador.interpolar: mezcla entre dos valores y limita t fuera de [0,1]", () -> {
            verificar(Interpolador.interpolar(10.0, 20.0, 0.5, Interpolador.LINEAL) == 15.0, "mitad");
            verificar(Interpolador.interpolar(10, 20, -3, Interpolador.LINEAL) == 10, "t < 0");
            verificar(Interpolador.interpolar(10, 20, 9, Interpolador.LINEAL) == 20, "t > 1");
            verificar(Interpolador.interpolar(100, 0, 1, Interpolador.SALIDA) == 0, "hacia abajo");
        });


        prueba("Tiempo: conversión ms <-> ticks y avance del reloj", () -> {
            verificar(Tiempo.msATicks(1000) == 60, "1000 ms = " + Tiempo.msATicks(1000));
            verificar(Tiempo.msATicks(500) == 30, "500 ms");
            verificar(Tiempo.msATicks(0) == 0 && Tiempo.msATicks(-5) == 0, "cero o negativo");
            verificar(Tiempo.msATicks(1) == 1, "una duración positiva dura al menos 1 tick");
            verificar(Tiempo.ticksAMs(30) == 500 && Tiempo.ticksAMs(60) == 1000, "ticks a ms");
            long antes = Tiempo.ahora();
            Tiempo.avanzar();
            Tiempo.avanzar();
            verificar(Tiempo.ahora() == antes + 2, "avanzar");
        });
    }
}
