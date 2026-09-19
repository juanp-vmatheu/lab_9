package paqueteria;

public class ControlSimulacion {
    private boolean activa;
    private boolean pausado;

    public ControlSimulacion() {
        this.activa = false;
        this.pausado = false;
    }

    public synchronized void iniciar() {
        activa = true;
        pausado = false;
    }

    public synchronized void pausar() {
        pausado = true;
    }

    public synchronized void reanudar() {
        pausado = false;
        notifyAll();
    }

    public synchronized void detener() {
        activa = false;
        pausado = false;
        notifyAll();
    }

    public synchronized boolean estaActiva() {
        return activa;
    }

    public synchronized boolean estaPausado() {
        return pausado;
    }

    public synchronized void esperarSiPausado() {
        while (pausado && activa) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
