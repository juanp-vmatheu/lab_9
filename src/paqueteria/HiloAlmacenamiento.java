package paqueteria;

public class HiloAlmacenamiento extends Thread {
    private ZonaLogistica recepcion;
    private ZonaLogistica almacen;
    private ControlSimulacion control;
    private Bitacora bitacora;

    public HiloAlmacenamiento(ZonaLogistica recepcion, ZonaLogistica almacen, ControlSimulacion control, Bitacora bitacora) {
        this.recepcion = recepcion;
        this.almacen = almacen;
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
            Paquete paquete = recepcion.tomar();
            if (paquete == null) {
                continue;
            }
            paquete.cambiarEstado(Estado.ALMACENADO);
            boolean agregado = almacen.agregar(paquete);
            if (agregado) {
                bitacora.registrar(paquete.getCodigo() + " almacenado");
            }
        }
    }
}
