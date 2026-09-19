package paqueteria;

import java.util.Random;

public class Repartidor extends Thread {
    private int id;
    private String nombre;
    private int capacidad;
    private String ruta;
    private ListaEnlazada<Paquete> carga;
    private EstadoRepartidor estado;
    private int entregados;
    private ZonaLogistica expedicion;
    private ZonaLogistica entregadosZona;
    private ZonaLogistica devueltosZona;
    private ControlSimulacion control;
    private Estadisticas estadisticas;
    private Bitacora bitacora;
    private Random random;

    public Repartidor(int id, String nombre, int capacidad, String ruta, ZonaLogistica expedicion,
                       ZonaLogistica entregadosZona, ZonaLogistica devueltosZona, ControlSimulacion control,
                       Estadisticas estadisticas, Bitacora bitacora) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ruta = ruta;
        this.carga = new ListaEnlazada<Paquete>();
        this.estado = EstadoRepartidor.DISPONIBLE;
        this.entregados = 0;
        this.expedicion = expedicion;
        this.entregadosZona = entregadosZona;
        this.devueltosZona = devueltosZona;
        this.control = control;
        this.estadisticas = estadisticas;
        this.bitacora = bitacora;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (control.estaActiva()) {
            control.esperarSiPausado();
            if (!control.estaActiva()) {
                break;
            }
            setEstado(EstadoRepartidor.CARGANDO);
            cargarVehiculo();
            if (carga.estaVacia()) {
                setEstado(EstadoRepartidor.DISPONIBLE);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }
            setEstado(EstadoRepartidor.EN_RUTA);
            try {
                Thread.sleep(1000 + random.nextInt(1001));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            setEstado(EstadoRepartidor.ENTREGANDO);
            entregarCarga();
            setEstado(EstadoRepartidor.REGRESANDO);
            try {
                Thread.sleep(500 + random.nextInt(501));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            setEstado(EstadoRepartidor.DISPONIBLE);
        }
        setEstado(EstadoRepartidor.FUERA_DE_SERVICIO);
    }

    private void cargarVehiculo() {
        while (carga.tamanio() < capacidad) {
            Paquete paquete = expedicion.tomarPorRuta(ruta, 3000);
            if (paquete == null) {
                break;
            }
            paquete.cambiarEstado(Estado.EN_REPARTO);
            carga.agregar(paquete);
            bitacora.registrar(paquete.getCodigo() + " asignado a " + nombre);
        }
    }

    private void entregarCarga() {
        while (!carga.estaVacia()) {
            control.esperarSiPausado();
            Paquete paquete = carga.eliminarEn(0);
            boolean exito = random.nextDouble() < 0.75;
            if (exito) {
                paquete.cambiarEstado(Estado.ENTREGADO);
                paquete.setTiempoEntrega(System.currentTimeMillis());
                entregadosZona.agregar(paquete);
                incrementarEntregados();
                estadisticas.registrarEntrega(paquete.getTiempoEntrega() - paquete.getTiempoCreacion());
                bitacora.registrar(paquete.getCodigo() + " entregado");
            } else {
                paquete.incrementarIntentos();
                paquete.cambiarEstado(Estado.NUEVO_INTENTO);
                bitacora.registrar(paquete.getCodigo() + " intento " + paquete.getIntentos() + " : cliente ausente");
                if (paquete.getIntentos() >= 3) {
                    paquete.cambiarEstado(Estado.DEVUELTO);
                    devueltosZona.agregar(paquete);
                    estadisticas.registrarDevolucion();
                    bitacora.registrar(paquete.getCodigo() + " devuelto");
                } else {
                    paquete.cambiarEstado(Estado.EN_REPARTO);
                    expedicion.agregar(paquete);
                }
            }
            try {
                Thread.sleep(500 + random.nextInt(501));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public synchronized EstadoRepartidor getEstado() {
        return estado;
    }

    private synchronized void setEstado(EstadoRepartidor estado) {
        this.estado = estado;
    }

    public synchronized int getEntregados() {
        return entregados;
    }

    private synchronized void incrementarEntregados() {
        entregados++;
    }

    public int getCargaActual() {
        return carga.tamanio();
    }
}
