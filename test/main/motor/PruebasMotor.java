package main.motor;

import static pruebas.Ejecutor.prueba;
import static pruebas.Ejecutor.verificar;

public class PruebasMotor {

    public static void ejecutar() {
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
