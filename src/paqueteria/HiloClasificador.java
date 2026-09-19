package paqueteria;

import java.util.Random;

public class HiloClasificador extends Thread {
    private int id;
    private ZonaLogistica almacen;
    private ZonaLogistica clasificacion;
    private ControlSimulacion control;
    private Bitacora bitacora;
    private Random random;
    private Paquete paqueteActual;

    public HiloClasificador(int id, ZonaLogistica almacen, ZonaLogistica clasificacion, ControlSimulacion control, Bitacora bitacora) {
        this.id = id;
        this.almacen = almacen;
        this.clasificacion = clasificacion;
        this.control = control;
        this.bitacora = bitacora;
        this.random = new Random();
        this.paqueteActual = null;
    }

    @Override
    public void run() {
        while (control.estaActiva()) {
            control.esperarSiPausado();
            if (!control.estaActiva()) {
                break;
            }
            Paquete paquete = almacen.tomar();
            if (paquete == null) {
                continue;
            }
            setPaqueteActual(paquete);
            paquete.cambiarEstado(Estado.CLASIFICANDO);
            bitacora.registrar(paquete.getCodigo() + " tomado por Clasificador-" + id);
            String ruta = asignarRuta(paquete.getCiudad());
            paquete.setRuta(ruta);
            try {
                Thread.sleep(500 + random.nextInt(1001));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                setPaqueteActual(null);
                break;
            }
            paquete.cambiarEstado(Estado.CLASIFICADO);
            boolean agregado = clasificacion.agregar(paquete);
            if (agregado) {
                bitacora.registrar(paquete.getCodigo() + " clasificado : Ruta " + ruta);
            }
            setPaqueteActual(null);
        }
    }

    private String asignarRuta(String ciudad) {
        switch (ciudad) {
            case "Barcelona Centro":
            case "Eixample":
                return "R01";
            case "Gracia":
            case "Sants":
                return "R02";
            case "Sant Marti":
            case "Poblenou":
                return "R03";
            case "Badalona":
            case "Hospitalet":
                return "R04";
            default:
                return "R01";
        }
    }

    public synchronized Paquete getPaqueteActual() {
        return paqueteActual;
    }

    private synchronized void setPaqueteActual(Paquete paquete) {
        this.paqueteActual = paquete;
    }

    public int getNumero() {
        return id;
    }
}
