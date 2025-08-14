/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author Ariel
 */
import javax.swing.*;
import java.awt.*;

public class RecreacionWindow extends JFrame {
    private SistemaGimnasio sistema;
    private MesaRecreativa pingPong;
    private MesaRecreativa billar;
    private JButton[][] botonesPing;
    private JButton[][] botonesBillar;
    private CanchaRecreativa[] futbol;
    private CanchaRecreativa[] tenis;
    private CanchaRecreativa[] baloncesto;

    public RecreacionWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Espacios Recreativos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pingPong = new MesaRecreativa(2); // 2 mesas
        billar = new MesaRecreativa(2);   // 2 mesas
        botonesPing = new JButton[2][pingPong.getCantidadHoras()];
        botonesBillar = new JButton[2][billar.getCantidadHoras()];

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Ping Pong", crearPanelMesas(pingPong, botonesPing, "Ping Pong"));
        tabs.addTab("Billar", crearPanelMesas(billar, botonesBillar, "Billar"));

        add(tabs);
        setVisible(true);
        // Inicializar canchas deportivas
        futbol = new CanchaRecreativa[]{
            new CanchaRecreativa("Fútbol 1", 12),
            new CanchaRecreativa("Fútbol 2", 12)
        };
        baloncesto = new CanchaRecreativa[]{
            new CanchaRecreativa("Baloncesto", 10)
        };
        tenis = new CanchaRecreativa[]{
            new CanchaRecreativa("Tenis 1", 2),
            new CanchaRecreativa("Tenis 2", 2)
        };

// Agregar pestaña de canchas
        tabs.addTab("Canchas Deportivas", crearPanelCanchas());

    }
    private JScrollPane crearPanelCanchas() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    panel.add(crearPanelGrupoCanchas("Fútbol", futbol));
    panel.add(crearPanelGrupoCanchas("Baloncesto", baloncesto));
    panel.add(crearPanelGrupoCanchas("Tenis", tenis));

    return new JScrollPane(panel);
}
private JPanel crearPanelGrupoCanchas(String titulo, CanchaRecreativa[] grupo) {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(grupo.length, 1, 10, 10));
    panel.setBorder(BorderFactory.createTitledBorder(titulo));

    for (CanchaRecreativa cancha : grupo) {
        JPanel canchaPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(cancha.tipo + " | Cupos disponibles: " + cancha.getDisponibles());
        JButton btnAgregar = new JButton("Agregar socio");
        JButton btnEliminar = new JButton("Eliminar socio");
        JButton btnVer = new JButton("Ver jugadores");

        JPanel botones = new JPanel();
        botones.add(btnAgregar);
        botones.add(btnEliminar);
        botones.add(btnVer);

        canchaPanel.add(label, BorderLayout.NORTH);
        canchaPanel.add(botones, BorderLayout.SOUTH);

        panel.add(canchaPanel);

        btnAgregar.addActionListener(e -> {
            if (cancha.getDisponibles() == 0) {
                JOptionPane.showMessageDialog(this, "Cancha llena.");
                return;
            }
            String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
            if (id != null && !id.trim().isEmpty()) {
                if (cancha.registrar(id.trim())) {
                    JOptionPane.showMessageDialog(this, "Socio registrado.");
                    label.setText(cancha.tipo + " | Cupos disponibles: " + cancha.getDisponibles());
                } else {
                    JOptionPane.showMessageDialog(this, "Ya está registrado.");
                }
            }
        });

        btnEliminar.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
            if (id != null && !id.trim().isEmpty()) {
                if (cancha.cancelar(id.trim())) {
                    JOptionPane.showMessageDialog(this, "Socio eliminado.");
                    label.setText(cancha.tipo + " | Cupos disponibles: " + cancha.getDisponibles());
                } else {
                    JOptionPane.showMessageDialog(this, "No está registrado.");
                }
            }
        });

        btnVer.addActionListener(e -> {
            String[] jugadores = cancha.getJugadores();
            if (jugadores.length == 0) {
                JOptionPane.showMessageDialog(this, "No hay jugadores registrados.");
                return;
            }
            StringBuilder sb = new StringBuilder("Jugadores en " + cancha.tipo + ":\n");
            for (String s : jugadores) {
                sb.append("- ").append(s).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        });
    }

    return panel;
}

    private JPanel crearPanelMesas(MesaRecreativa mesa, JButton[][] botones, String tipo) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel grid = new JPanel(new GridLayout(mesa.getCantidadMesas() + 1, mesa.getCantidadHoras() + 1));
        grid.add(new JLabel(""));

        for (String hora : mesa.horarios) {
            grid.add(new JLabel(hora, JLabel.CENTER));
        }

        for (int i = 0; i < mesa.getCantidadMesas(); i++) {
            grid.add(new JLabel(tipo + " " + (i + 1), JLabel.CENTER));

            for (int j = 0; j < mesa.getCantidadHoras(); j++) {
                JButton btn = new JButton("-");
                btn.setBackground(Color.GREEN);
                botones[i][j] = btn;

                final int mesaIndex = i;
                final int horaIndex = j;

                btn.addActionListener(e -> {
                    String actual = mesa.getReserva(mesaIndex, horaIndex);
                    if (actual == null) {
                        String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
                        if (id != null && !id.trim().isEmpty()) {
                            mesa.reservar(mesaIndex, horaIndex, id.trim());
                            actualizarBotones(botones, mesa);
                        }
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(this, "¿Liberar esta reserva?", "Confirmar", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            mesa.liberar(mesaIndex, horaIndex);
                            actualizarBotones(botones, mesa);
                        }
                    }
                });

                grid.add(btn);
            }
        }

        panel.add(new JScrollPane(grid), BorderLayout.CENTER);
        actualizarBotones(botones, mesa);
        return panel;
    }

    private void actualizarBotones(JButton[][] botones, MesaRecreativa mesa) {
        for (int i = 0; i < mesa.getCantidadMesas(); i++) {
            for (int j = 0; j < mesa.getCantidadHoras(); j++) {
                String reserva = mesa.getReserva(i, j);
                JButton btn = botones[i][j];
                if (reserva == null) {
                    btn.setText("-");
                    btn.setBackground(Color.GREEN);
                    btn.setToolTipText("Disponible");
                } else {
                    btn.setText("Ocupado");
                    btn.setBackground(Color.RED);
                    btn.setToolTipText("ID: " + reserva);
                }
            }
        }
    }
}
