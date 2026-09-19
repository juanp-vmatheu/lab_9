package paqueteria;

public class ZonaLogistica {
    private String nombre;
    private int capacidad;
    private ListaEnlazada<Paquete> paquetes;
    private boolean activa;

    public ZonaLogistica(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.paquetes = new ListaEnlazada<Paquete>();
        this.activa = true;
    }

    public synchronized boolean agregar(Paquete paquete) {
        while (paquetes.tamanio() >= capacidad && activa) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        if (!activa) {
            return false;
        }
        paquetes.agregar(paquete);
        notifyAll();
        return true;
    }

    public synchronized Paquete tomar() {
        while (paquetes.estaVacia() && activa) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        if (paquetes.estaVacia()) {
            return null;
        }
        Paquete paquete = paquetes.eliminarEn(0);
        notifyAll();
        return paquete;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
