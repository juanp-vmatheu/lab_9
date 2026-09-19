package paqueteria;

import javax.swing.*;
import java.awt.*;

public class VentanaEstadisticas extends JDialog {

    public VentanaEstadisticas(JFrame padre, CentroLogistico centro) {
        super(padre, "Estadisticas", true);
        setSize(320, 380);
        setLocationRelativeTo(padre);
        setLayout(new GridLayout(0, 1));

        Estadisticas estadisticas = centro.getEstadisticas();
        add(new JLabel("Paquetes generados: " + estadisticas.getGenerados()));
        add(new JLabel("Entregados: " + estadisticas.getEntregados()));
        add(new JLabel("Devueltos: " + estadisticas.getDevueltos()));
        add(new JLabel("En proceso: " + estadisticas.enProceso()));
        add(new JLabel(String.format("Tiempo promedio: %.1f s", estadisticas.tiempoPromedio())));
        add(new JSeparator());

        Repartidor[] repartidores = centro.getRepartidores();
        for (int i = 0; i < repartidores.length; i++) {
            if (repartidores[i] != null) {
                add(new JLabel(repartidores[i].getNombre() + ": " + repartidores[i].getEntregados() + " entregados"));
            } else {
                add(new JLabel("Repartidor-" + (i + 1) + ": 0 entregados"));
            }
        }
    }
}
