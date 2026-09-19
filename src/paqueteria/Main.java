package paqueteria;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CentroLogistico centro = new CentroLogistico();
        centro.iniciar();
        String anterior = "";
        for (int i = 0; i < 20; i++) {
            Thread.sleep(1000);
            String actual = centro.obtenerRegistro();
            if (actual.length() > anterior.length()) {
                System.out.print(actual.substring(anterior.length()));
            }
            anterior = actual;
        }
        centro.detener();
        System.out.println("Simulacion detenida.");
    }
}
