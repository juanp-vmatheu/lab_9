package paqueteria;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Bitacora {
    private ListaEnlazada<String> lineas;
    private SimpleDateFormat formato;

    public Bitacora() {
        this.lineas = new ListaEnlazada<String>();
        this.formato = new SimpleDateFormat("HH:mm:ss");
    }

    public synchronized void registrar(String mensaje) {
        String hora = formato.format(new Date());
        lineas.agregar(hora + " | " + mensaje);
    }

    public synchronized String obtenerTexto() {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < lineas.tamanio(); i++) {
            resultado.append(lineas.obtener(i));
            resultado.append("\n");
        }
        return resultado.toString();
    }

    public synchronized void vaciar() {
        lineas.vaciar();
    }
}
