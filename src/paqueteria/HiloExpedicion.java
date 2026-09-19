package paqueteria;

public class HiloExpedicion extends Thread {
    private ZonaLogistica empaquetado;
    private ZonaLogistica expedicion;
    private ControlSimulacion control;
    private Bitacora bitacora;

    public HiloExpedicion(ZonaLogistica empaquetado, ZonaLogistica expedicion, ControlSimulacion control, Bitacora bitacora) {
        this.empaquetado = empaquetado;
        this.expedicion = expedicion;
        this.control = control;
        this.bitacora = bitacora;
    }

    @Override
    public void run() {
        while (control.estaActiva()) {
            control.esperarSiPausado();
            if (!control.estaActiva()) {
                break;
            }
            Paquete paquete = empaquetado.tomar();
            if (paquete == null) {
                continue;
            }
            paquete.cambiarEstado(Estado.EN_EXPEDICION);
            boolean agregado = expedicion.agregar(paquete);
            if (agregado) {
                bitacora.registrar(paquete.getCodigo() + " en expedicion : Ruta " + paquete.getRuta());
            }
        }
    }
}
