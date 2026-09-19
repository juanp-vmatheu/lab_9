package paqueteria;

public class Estadisticas {
    private int generados;
    private int entregados;
    private int devueltos;
    private long sumaTiempos;

    public synchronized void registrarGenerado() {
        generados++;
    }

    public synchronized void registrarEntrega(long tiempoMs) {
        entregados++;
        sumaTiempos += tiempoMs;
    }

    public synchronized void registrarDevolucion() {
        devueltos++;
    }

    public synchronized int getGenerados() {
        return generados;
    }

    public synchronized int getEntregados() {
        return entregados;
    }

    public synchronized int getDevueltos() {
        return devueltos;
    }

    public synchronized int enProceso() {
        return generados - entregados - devueltos;
    }

    public synchronized double tiempoPromedio() {
        if (entregados == 0) {
            return 0.0;
        }
        return (sumaTiempos / (double) entregados) / 1000.0;
    }

    public synchronized void reiniciar() {
        generados = 0;
        entregados = 0;
        devueltos = 0;
        sumaTiempos = 0;
    }
}
