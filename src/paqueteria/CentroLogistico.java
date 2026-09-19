package paqueteria;

public class CentroLogistico {
    private ZonaLogistica recepcion;
    private ZonaLogistica almacen;
    private ZonaLogistica clasificacion;
    private ZonaLogistica empaquetado;
    private ZonaLogistica expedicion;
    private ZonaLogistica entregados;
    private ZonaLogistica devueltos;
    private ControlSimulacion control;
    private Estadisticas estadisticas;
    private Bitacora bitacora;

    private HiloRecepcion hiloRecepcion;
    private HiloAlmacenamiento hiloAlmacenamiento;
    private HiloClasificador[] clasificadores;
    private HiloEmpaquetador[] empaquetadores;
    private HiloExpedicion hiloExpedicion;
    private Repartidor[] repartidores;

    private static final int CAPACIDAD_ILIMITADA = 100000;

    public CentroLogistico() {
        this.recepcion = new ZonaLogistica("Recepcion", 10);
        this.almacen = new ZonaLogistica("Almacen", 20);
        this.clasificacion = new ZonaLogistica("Clasificacion", 10);
        this.empaquetado = new ZonaLogistica("Empaquetado", 8);
        this.expedicion = new ZonaLogistica("Expedicion", 15);
        this.entregados = new ZonaLogistica("Entregados", CAPACIDAD_ILIMITADA);
        this.devueltos = new ZonaLogistica("Devueltos", CAPACIDAD_ILIMITADA);
        this.control = new ControlSimulacion();
        this.estadisticas = new Estadisticas();
        this.bitacora = new Bitacora();
        this.clasificadores = new HiloClasificador[3];
        this.empaquetadores = new HiloEmpaquetador[2];
        this.repartidores = new Repartidor[4];
    }

    public void iniciar() {
        control.iniciar();
        hiloRecepcion = new HiloRecepcion(recepcion, control, estadisticas, bitacora);
        hiloAlmacenamiento = new HiloAlmacenamiento(recepcion, almacen, control, bitacora);
        hiloExpedicion = new HiloExpedicion(empaquetado, expedicion, control, bitacora);
        for (int i = 0; i < clasificadores.length; i++) {
            clasificadores[i] = new HiloClasificador(i + 1, almacen, clasificacion, control, bitacora);
        }
        for (int i = 0; i < empaquetadores.length; i++) {
            empaquetadores[i] = new HiloEmpaquetador(i + 1, clasificacion, empaquetado, control, bitacora);
        }
        String[] nombres = { "Repartidor-1", "Repartidor-2", "Repartidor-3", "Repartidor-4" };
        int[] capacidades = { 5, 4, 6, 5 };
        String[] rutas = { "R01", "R02", "R03", "R04" };
        for (int i = 0; i < repartidores.length; i++) {
            repartidores[i] = new Repartidor(i + 1, nombres[i], capacidades[i], rutas[i], expedicion,
                    entregados, devueltos, control, estadisticas, bitacora);
        }
        bitacora.registrar("Simulacion iniciada");
        hiloRecepcion.start();
        hiloAlmacenamiento.start();
        hiloExpedicion.start();
        for (HiloClasificador clasificador : clasificadores) {
            clasificador.start();
        }
        for (HiloEmpaquetador empaquetador : empaquetadores) {
            empaquetador.start();
        }
        for (Repartidor repartidor : repartidores) {
            repartidor.start();
        }
    }

    public void pausar() {
        control.pausar();
        bitacora.registrar("Simulacion pausada");
    }

    public void reanudar() {
        control.reanudar();
        bitacora.registrar("Simulacion reanudada");
    }

    public void detener() {
        control.detener();
        recepcion.detener();
        almacen.detener();
        clasificacion.detener();
        empaquetado.detener();
        expedicion.detener();
        bitacora.registrar("Simulacion detenida");
    }

    public void reiniciar() {
        detener();
        esperarHilos();
        recepcion.vaciar();
        almacen.vaciar();
        clasificacion.vaciar();
        empaquetado.vaciar();
        expedicion.vaciar();
        entregados.vaciar();
        devueltos.vaciar();
        recepcion.reactivar();
        almacen.reactivar();
        clasificacion.reactivar();
        empaquetado.reactivar();
        expedicion.reactivar();
        estadisticas.reiniciar();
        bitacora.vaciar();
    }

    private void esperarHilos() {
        try {
            if (hiloRecepcion != null) {
                hiloRecepcion.join(1000);
            }
            if (hiloAlmacenamiento != null) {
                hiloAlmacenamiento.join(1000);
            }
            if (hiloExpedicion != null) {
                hiloExpedicion.join(1000);
            }
            for (HiloClasificador clasificador : clasificadores) {
                if (clasificador != null) {
                    clasificador.join(1000);
                }
            }
            for (HiloEmpaquetador empaquetador : empaquetadores) {
                if (empaquetador != null) {
                    empaquetador.join(1000);
                }
            }
            for (Repartidor repartidor : repartidores) {
                if (repartidor != null) {
                    repartidor.join(1000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void registrar(String mensaje) {
        bitacora.registrar(mensaje);
    }

    public String obtenerRegistro() {
        return bitacora.obtenerTexto();
    }

    public ZonaLogistica getRecepcion() {
        return recepcion;
    }

    public ZonaLogistica getAlmacen() {
        return almacen;
    }

    public ZonaLogistica getClasificacion() {
        return clasificacion;
    }

    public ZonaLogistica getEmpaquetado() {
        return empaquetado;
    }

    public ZonaLogistica getExpedicion() {
        return expedicion;
    }

    public ZonaLogistica getEntregados() {
        return entregados;
    }

    public ZonaLogistica getDevueltos() {
        return devueltos;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public HiloClasificador[] getClasificadores() {
        return clasificadores;
    }

    public HiloEmpaquetador[] getEmpaquetadores() {
        return empaquetadores;
    }

    public Repartidor[] getRepartidores() {
        return repartidores;
    }
}
