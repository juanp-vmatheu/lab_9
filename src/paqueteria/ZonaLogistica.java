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
        int indice = indiceMayorPrioridad();
        Paquete paquete = paquetes.eliminarEn(indice);
        notifyAll();
        return paquete;
    }

    public synchronized Paquete tomarPorRuta(String ruta, long esperaMs) {
        long limite = System.currentTimeMillis() + esperaMs;
        int indice = indiceMayorPrioridadDeRuta(ruta);
        while (indice == -1 && activa) {
            long restante = limite - System.currentTimeMillis();
            if (restante <= 0) {
                return null;
            }
            try {
                wait(restante);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            indice = indiceMayorPrioridadDeRuta(ruta);
        }
        if (indice == -1) {
            return null;
        }
        Paquete paquete = paquetes.eliminarEn(indice);
        notifyAll();
        return paquete;
    }

    private int indiceMayorPrioridad() {
        int mejorIndice = -1;
        int mejorNivel = Integer.MAX_VALUE;
        for (int i = 0; i < paquetes.tamanio(); i++) {
            Paquete actual = paquetes.obtener(i);
            if (actual.getPrioridad().getNivel() < mejorNivel) {
                mejorNivel = actual.getPrioridad().getNivel();
                mejorIndice = i;
            }
        }
        return mejorIndice;
    }

    private int indiceMayorPrioridadDeRuta(String ruta) {
        int mejorIndice = -1;
        int mejorNivel = Integer.MAX_VALUE;
        for (int i = 0; i < paquetes.tamanio(); i++) {
            Paquete actual = paquetes.obtener(i);
            if (ruta.equals(actual.getRuta()) && actual.getPrioridad().getNivel() < mejorNivel) {
                mejorNivel = actual.getPrioridad().getNivel();
                mejorIndice = i;
            }
        }
        return mejorIndice;
    }

    public synchronized void detener() {
        activa = false;
        notifyAll();
    }

    public synchronized void reactivar() {
        activa = true;
    }

    public synchronized void vaciar() {
        paquetes.vaciar();
    }

    public synchronized int getTamanio() {
        return paquetes.tamanio();
    }

    public synchronized String contenido() {
        return paquetes.recorrer();
    }

    public synchronized String contenidoDeRuta(String ruta) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < paquetes.tamanio(); i++) {
            Paquete actual = paquetes.obtener(i);
            if (ruta.equals(actual.getRuta())) {
                if (resultado.length() > 0) {
                    resultado.append(", ");
                }
                resultado.append(actual.getCodigo());
            }
        }
        return resultado.toString();
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
