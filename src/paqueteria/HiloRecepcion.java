package paqueteria;

import java.util.Random;

public class HiloRecepcion extends Thread {
    private ZonaLogistica recepcion;
    private ControlSimulacion control;
    private Estadisticas estadisticas;
    private Bitacora bitacora;
    private Random random;
    private int contador;

    private static final String[] CLIENTES = {
        "Carlos Lopez", "Maria Fernandez", "Juan Perez", "Ana Gomez",
        "Luis Torres", "Sofia Ramirez", "Diego Castro", "Laura Morales"
    };

    private static final String[] CIUDADES = {
        "Barcelona Centro", "Eixample", "Gracia", "Sants",
        "Sant Marti", "Poblenou", "Badalona", "Hospitalet"
    };

    public HiloRecepcion(ZonaLogistica recepcion, ControlSimulacion control, Estadisticas estadisticas, Bitacora bitacora) {
        this.recepcion = recepcion;
        this.control = control;
        this.estadisticas = estadisticas;
        this.bitacora = bitacora;
        this.random = new Random();
        this.contador = 0;
    }

    @Override
    public void run() {
        while (control.estaActiva()) {
            control.esperarSiPausado();
            if (!control.estaActiva()) {
                break;
            }
            Paquete paquete = generarPaquete();
            boolean agregado = recepcion.agregar(paquete);
            if (agregado) {
                estadisticas.registrarGenerado();
                bitacora.registrar(paquete.getCodigo() + " recibido");
            }
            try {
                Thread.sleep(800 + random.nextInt(1201));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private Paquete generarPaquete() {
        contador++;
        String codigo = String.format("PKG-%05d", contador);
        String cliente = CLIENTES[random.nextInt(CLIENTES.length)];
        String ciudad = CIUDADES[random.nextInt(CIUDADES.length)];
        String direccion = "Calle " + (1 + random.nextInt(200));
        double peso = Math.round((0.5 + random.nextDouble() * 9.5) * 10) / 10.0;
        Prioridad[] valores = Prioridad.values();
        Prioridad prioridad = valores[random.nextInt(valores.length)];
        return new Paquete(codigo, cliente, direccion, ciudad, peso, prioridad);
    }
}
