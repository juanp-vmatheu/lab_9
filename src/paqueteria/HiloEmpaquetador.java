package paqueteria;

public class HiloEmpaquetador extends Thread {
    private int id;
    private ZonaLogistica clasificacion;
    private ZonaLogistica empaquetado;
    private ControlSimulacion control;
    private Bitacora bitacora;
    private Paquete paqueteActual;

    public HiloEmpaquetador(int id, ZonaLogistica clasificacion, ZonaLogistica empaquetado, ControlSimulacion control, Bitacora bitacora) {
        this.id = id;
        this.clasificacion = clasificacion;
        this.empaquetado = empaquetado;
        this.control = control;
        this.bitacora = bitacora;
        this.paqueteActual = null;
    }

    @Override
    public void run() {
        while (control.estaActiva()) {
            control.esperarSiPausado();
            if (!control.estaActiva()) {
                break;
            }
            Paquete paquete = clasificacion.tomar();
            if (paquete == null) {
                continue;
            }
            setPaqueteActual(paquete);
            paquete.cambiarEstado(Estado.EMPAQUETANDO);
            bitacora.registrar(paquete.getCodigo() + " tomado por Empaquetador-" + id);
            try {
                Thread.sleep(tiempoDeEmpaquetado(paquete.getPeso()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                setPaqueteActual(null);
                break;
            }
            paquete.cambiarEstado(Estado.EMPAQUETADO);
            boolean agregado = empaquetado.agregar(paquete);
            if (agregado) {
                bitacora.registrar(paquete.getCodigo() + " empaquetado");
            }
            setPaqueteActual(null);
        }
    }

    private long tiempoDeEmpaquetado(double peso) {
        if (peso <= 2.0) {
            return 1000;
        } else if (peso <= 5.0) {
            return 2000;
        } else {
            return 3000;
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
