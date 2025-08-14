/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import javax.swing.*;
import java.awt.*;
import javax.swing.JOptionPane;
import java.awt.event.*;

public class ClasesWindow extends JFrame {
    private SistemaGimnasio sistema;
    private ClaseGrupal[] clases;
    private DefaultListModel<String> listaModel;
    private JList<String> listaClases;

    public ClasesWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Clases Grupales");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ✅ Pre-cargar clases
        clases = new ClaseGrupal[] {
            new ClaseGrupal("Yoga", "8:00 a.m.", 10),
            new ClaseGrupal("Crossfit", "10:00 a.m.", 10),
            new ClaseGrupal("Pilates", "6:00 p.m.", 10),
            new ClaseGrupal("Zumba", "7:00 p.m.", 10),
        };

        listaModel = new DefaultListModel<>();
        actualizarLista();

        listaClases = new JList<>(listaModel);
        add(new JScrollPane(listaClases), BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout());
        JButton btnReservar = new JButton("Reservar");
        JButton btnModificar = new JButton("Modificar Clase");

        botonesPanel.add(btnReservar);
        botonesPanel.add(btnModificar);
        add(botonesPanel, BorderLayout.SOUTH);

        btnReservar.addActionListener(e -> reservarClase());
        btnModificar.addActionListener(e -> modificarClase());

        setVisible(true);
    }

    private void actualizarLista() {
        listaModel.clear();
        for (ClaseGrupal c : clases) {
            listaModel.addElement(c.nombre + " | " + c.horario + " | Cupos: " + (c.capacidad - c.reservados) + "/" + c.capacidad);
        }
    }

    private void reservarClase() {
        int index = listaClases.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase para reservar.");
            return;
        }

        String id = JOptionPane.showInputDialog(this, "Ingrese ID del socio:");
        if (id == null || id.trim().isEmpty()) return;

        boolean exito = clases[index].reservar(id.trim());

        if (exito) {
            JOptionPane.showMessageDialog(this, "Reserva exitosa.");
        } else {
            JOptionPane.showMessageDialog(this, "No hay cupo disponible.");
        }

        actualizarLista();
    }

    private void modificarClase() {
        int index = listaClases.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase para modificar.");
            return;
        }

        ClaseGrupal clase = clases[index];

        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", clase.nombre);
        if (nuevoNombre == null) return;

        String nuevoHorario = JOptionPane.showInputDialog(this, "Nuevo horario:", clase.horario);
        if (nuevoHorario == null) return;

        String nuevaCapacidadStr = JOptionPane.showInputDialog(this, "Nueva capacidad:", clase.capacidad);
        if (nuevaCapacidadStr == null) return;

        try {
            int nuevaCapacidad = Integer.parseInt(nuevaCapacidadStr);
            clase.modificar(nuevoNombre, nuevoHorario, nuevaCapacidad);
            actualizarLista();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacidad inválida.");
        }
    }
}

