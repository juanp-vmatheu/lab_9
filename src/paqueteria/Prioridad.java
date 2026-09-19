package paqueteria;

public enum Prioridad {
    URGENTE(1),
    ALTA(2),
    NORMAL(3),
    BAJA(4);

    private final int nivel;

    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }
}
