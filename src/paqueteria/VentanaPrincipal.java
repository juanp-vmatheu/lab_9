package paqueteria;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {
    private CentroLogistico centro;

    private JButton botonIniciar;
    private JButton botonPausar;
    private JButton botonReanudar;
    private JButton botonDetener;
    private JButton botonReiniciar;
    private JButton botonEstadisticas;

    private JTextArea areaRecepcion;
    private JProgressBar barraRecepcion;
    private JLabel etiquetaRecepcion;

    private JTextArea areaAlmacen;
    private JProgressBar barraAlmacen;
    private JLabel etiquetaAlmacen;

    private JTextArea areaClasificacion;
    private JProgressBar barraClasificacion;
    private JLabel etiquetaClasificacion;

    private JLabel[] etiquetasEmpaquetador;

    private String[] rutas;
    private JTextArea[] areasRutas;
    private JProgressBar barraExpedicion;
    private JLabel etiquetaExpedicion;

    private JLabel[] etiquetasNombreRepartidor;
    private JLabel[] etiquetasEstadoRepartidor;
    private JLabel[] etiquetasCargaRepartidor;
    private JLabel[] etiquetasEntregadosRepartidor;

    private JTextArea areaLog;
    private int longitudLogMostrado;

    private Timer temporizador;

    public VentanaPrincipal() {
        centro = new CentroLogistico();
        rutas = new String[] { "R01", "R02", "R03", "R04" };
        longitudLogMostrado = 0;

        setTitle("Sistema de Paqueteria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearPanelBotones(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelLog(), BorderLayout.SOUTH);

        actualizarBotones(false, false);

        temporizador = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refrescar();
            }
        });
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        botonIniciar = new JButton("INICIAR");
        botonPausar = new JButton("PAUSAR");
        botonReanudar = new JButton("REANUDAR");
        botonDetener = new JButton("DETENER");
        botonReiniciar = new JButton("REINICIAR");
        botonEstadisticas = new JButton("ESTADISTICAS");

        botonIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                centro.iniciar();
                temporizador.start();
                actualizarBotones(true, false);
            }
        });

        botonPausar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                centro.pausar();
                botonPausar.setEnabled(false);
                botonReanudar.setEnabled(true);
            }
        });

        botonReanudar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                centro.reanudar();
                botonReanudar.setEnabled(false);
                botonPausar.setEnabled(true);
            }
        });

        botonDetener.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                centro.detener();
                actualizarBotones(false, true);
            }
        });

        botonReiniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                centro.reiniciar();
                longitudLogMostrado = 0;
                areaLog.setText("");
                actualizarBotones(false, false);
                refrescar();
            }
        });

        botonEstadisticas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                VentanaEstadisticas ventana = new VentanaEstadisticas(VentanaPrincipal.this, centro);
                ventana.setVisible(true);
            }
        });

        panel.add(botonIniciar);
        panel.add(botonPausar);
        panel.add(botonReanudar);
        panel.add(botonDetener);
        panel.add(botonReiniciar);
        panel.add(botonEstadisticas);
        return panel;
    }

    private void actualizarBotones(boolean iniciada, boolean detenida) {
        botonIniciar.setEnabled(!iniciada && !detenida);
        botonPausar.setEnabled(iniciada);
        botonReanudar.setEnabled(false);
        botonDetener.setEnabled(iniciada);
        botonReiniciar.setEnabled(detenida);
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel filaUno = new JPanel(new GridLayout(1, 3));
        filaUno.add(crearPanelZona("Recepcion"));
        filaUno.add(crearPanelZona("Almacen"));
        filaUno.add(crearPanelZona("Clasificacion"));
        panel.add(filaUno);

        panel.add(crearPanelEmpaquetado());
        panel.add(crearPanelExpedicion());
        panel.add(crearPanelRepartidores());

        return panel;
    }

    private JPanel crearPanelZona(String nombre) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(nombre));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        JProgressBar barra = new JProgressBar();
        JLabel etiqueta = new JLabel("0 / 0", SwingConstants.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(barra, BorderLayout.CENTER);
        panelInferior.add(etiqueta, BorderLayout.SOUTH);

        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.add(panelInferior, BorderLayout.SOUTH);

        if (nombre.equals("Recepcion")) {
            areaRecepcion = area;
            barraRecepcion = barra;
            etiquetaRecepcion = etiqueta;
        } else if (nombre.equals("Almacen")) {
            areaAlmacen = area;
            barraAlmacen = barra;
            etiquetaAlmacen = etiqueta;
        } else if (nombre.equals("Clasificacion")) {
            areaClasificacion = area;
            barraClasificacion = barra;
            etiquetaClasificacion = etiqueta;
        }

        return panel;
    }

    private JPanel crearPanelEmpaquetado() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setBorder(BorderFactory.createTitledBorder("Empaquetado"));
        etiquetasEmpaquetador = new JLabel[2];
        for (int i = 0; i < 2; i++) {
            JLabel etiqueta = new JLabel("Empaquetador " + (i + 1) + ": libre", SwingConstants.CENTER);
            etiquetasEmpaquetador[i] = etiqueta;
            panel.add(etiqueta);
        }
        return panel;
    }

    private JPanel crearPanelExpedicion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Expedicion"));

        JPanel panelRutas = new JPanel(new GridLayout(1, rutas.length));
        areasRutas = new JTextArea[rutas.length];
        for (int i = 0; i < rutas.length; i++) {
            JPanel panelRuta = new JPanel(new BorderLayout());
            panelRuta.setBorder(BorderFactory.createTitledBorder("Ruta " + rutas[i]));
            JTextArea area = new JTextArea();
            area.setEditable(false);
            areasRutas[i] = area;
            panelRuta.add(new JScrollPane(area), BorderLayout.CENTER);
            panelRutas.add(panelRuta);
        }

        barraExpedicion = new JProgressBar();
        etiquetaExpedicion = new JLabel("0 / 0", SwingConstants.CENTER);
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(barraExpedicion, BorderLayout.CENTER);
        panelInferior.add(etiquetaExpedicion, BorderLayout.SOUTH);

        panel.add(panelRutas, BorderLayout.CENTER);
        panel.add(panelInferior, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelRepartidores() {
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.setBorder(BorderFactory.createTitledBorder("Repartidores"));

        etiquetasNombreRepartidor = new JLabel[4];
        etiquetasEstadoRepartidor = new JLabel[4];
        etiquetasCargaRepartidor = new JLabel[4];
        etiquetasEntregadosRepartidor = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            JPanel panelRepartidor = new JPanel();
            panelRepartidor.setLayout(new BoxLayout(panelRepartidor, BoxLayout.Y_AXIS));
            panelRepartidor.setBorder(BorderFactory.createEtchedBorder());

            JLabel nombre = new JLabel("Repartidor-" + (i + 1));
            JLabel estado = new JLabel("Estado: -");
            JLabel carga = new JLabel("Paquetes: 0/0");
            JLabel entregados = new JLabel("Entregados: 0");

            etiquetasNombreRepartidor[i] = nombre;
            etiquetasEstadoRepartidor[i] = estado;
            etiquetasCargaRepartidor[i] = carga;
            etiquetasEntregadosRepartidor[i] = entregados;

            panelRepartidor.add(nombre);
            panelRepartidor.add(estado);
            panelRepartidor.add(carga);
            panelRepartidor.add(entregados);
            panel.add(panelRepartidor);
        }
        return panel;
    }

    private JPanel crearPanelLog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registro del sistema"));
        areaLog = new JTextArea(8, 80);
        areaLog.setEditable(false);
        panel.add(new JScrollPane(areaLog), BorderLayout.CENTER);
        return panel;
    }

    private void refrescar() {
        actualizarZona(centro.getRecepcion(), areaRecepcion, barraRecepcion, etiquetaRecepcion);
        actualizarZona(centro.getAlmacen(), areaAlmacen, barraAlmacen, etiquetaAlmacen);
        actualizarZona(centro.getClasificacion(), areaClasificacion, barraClasificacion, etiquetaClasificacion);

        HiloEmpaquetador[] empaquetadores = centro.getEmpaquetadores();
        for (int i = 0; i < empaquetadores.length; i++) {
            if (empaquetadores[i] == null) {
                continue;
            }
            Paquete actual = empaquetadores[i].getPaqueteActual();
            String texto = "Empaquetador " + (i + 1) + ": " + (actual == null ? "libre" : actual.getCodigo());
            etiquetasEmpaquetador[i].setText(texto);
        }

        ZonaLogistica expedicion = centro.getExpedicion();
        for (int i = 0; i < rutas.length; i++) {
            areasRutas[i].setText(expedicion.contenidoDeRuta(rutas[i]).replace(", ", "\n"));
        }
        barraExpedicion.setMaximum(expedicion.getCapacidad());
        barraExpedicion.setValue(expedicion.getTamanio());
        etiquetaExpedicion.setText(expedicion.getTamanio() + " / " + expedicion.getCapacidad());

        Repartidor[] repartidores = centro.getRepartidores();
        for (int i = 0; i < repartidores.length; i++) {
            if (repartidores[i] == null) {
                continue;
            }
            etiquetasNombreRepartidor[i].setText(repartidores[i].getNombre() + " (" + repartidores[i].getRuta() + ")");
            etiquetasEstadoRepartidor[i].setText("Estado: " + repartidores[i].getEstado());
            etiquetasCargaRepartidor[i].setText("Paquetes: " + repartidores[i].getCargaActual() + "/" + repartidores[i].getCapacidad());
            etiquetasEntregadosRepartidor[i].setText("Entregados: " + repartidores[i].getEntregados());
        }

        String textoLog = centro.obtenerRegistro();
        if (textoLog.length() > longitudLogMostrado) {
            areaLog.append(textoLog.substring(longitudLogMostrado));
            longitudLogMostrado = textoLog.length();
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        }
    }

    private void actualizarZona(ZonaLogistica zona, JTextArea area, JProgressBar barra, JLabel etiqueta) {
        area.setText(zona.contenido().replace(", ", "\n"));
        barra.setMaximum(zona.getCapacidad());
        barra.setValue(zona.getTamanio());
        etiqueta.setText(zona.getTamanio() + " / " + zona.getCapacidad());
    }
}
