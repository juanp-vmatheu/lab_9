package paqueteria;

public class Paquete {
    private String codigo;
    private String cliente;
    private String direccion;
    private String ciudad;
    private double peso;
    private Prioridad prioridad;
    private Estado estado;
    private String ruta;
    private int intentos;
    private long tiempoCreacion;
    private long tiempoEntrega;

    public Paquete(String codigo, String cliente, String direccion, String ciudad, double peso, Prioridad prioridad) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.peso = peso;
        this.prioridad = prioridad;
        this.estado = Estado.RECIBIDO;
        this.ruta = null;
        this.intentos = 0;
        this.tiempoCreacion = System.currentTimeMillis();
        this.tiempoEntrega = 0;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public double getPeso() {
        return peso;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public synchronized Estado getEstado() {
        return estado;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public synchronized int getIntentos() {
        return intentos;
    }

    public synchronized void incrementarIntentos() {
        intentos++;
    }

    public long getTiempoCreacion() {
        return tiempoCreacion;
    }

    public long getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(long tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }

    @Override
    public String toString() {
        return codigo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Paquete)) {
            return false;
        }
        Paquete otro = (Paquete) obj;
        return codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}
