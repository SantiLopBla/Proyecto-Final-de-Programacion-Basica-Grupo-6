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
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ClasesWindow extends JFrame {

    // ===== Paleta VIP (alto contraste) =====
    private static final Color BG_DARK = new Color(12, 12, 12);
    private static final Color PANEL_D = new Color(22, 22, 22);
    private static final Color TXT_LIGHT = new Color(235, 235, 235);
    private static final Color TXT_MUTED = new Color(212, 212, 212);

    private static final Color BTN_RED = new Color(198, 40, 40);
    private static final Color BTN_VIP = new Color(68, 68, 68);
    private static final Color BTN_YELLOW = new Color(212, 172, 13); // Eliminar (amarillo)

    private final SistemaGimnasio sistema;

    // ===== Persistencia en memoria (por sesión) =====
    private static boolean STATE_SAVED = false;
    private static java.util.List<ClaseGrupal> STATE_CLASES = new ArrayList<>();

    // ===== Datos / modelo =====
    private final DefaultListModel<ClaseGrupal> listModel = new DefaultListModel<>();
    private final JList<ClaseGrupal> list = new JList<>(listModel);

    // Área de detalle (para refrescar al instante)
    private final JTextArea infoArea = new JTextArea();

    // distribución por defecto
    private int clasesManana = 3;
    private int clasesNoche = 3;
    private int capacidadMax = 12;

    // catálogos
    private static final String[] TIPOS = {
        "Yoga", "Crossfit", "Funcional", "Pilates", "Zumba", "HIIT", "Spinning", "Box", "TRX"
    };
    private static final String[] HORAS_AM = {"06:00 a. m.", "07:00 a. m.", "08:00 a. m.", "09:00 a. m."};
    private static final String[] HORAS_PM = {"06:00 p. m.", "07:00 p. m.", "08:00 p. m.", "09:00 p. m."};

    public ClasesWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;

        // Suavizado tipográfico
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Estilo oscuro consistente en JOptionPane/inputs
        applyDarkUI();

        setTitle("🕺  Clases Grupales — Zona Elite");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        // Header (solo título + slogan)
        root.add(buildHeader(), BorderLayout.NORTH);

        // Centro: lista + detalle
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildListaPanel(), buildDetallePanel());
        split.setDividerSize(6);
        split.setBorder(null);
        split.setResizeWeight(0.5);
        root.add(split, BorderLayout.CENTER);

        // Footer: acciones (con Salir)
        root.add(buildAccionesPanel(), BorderLayout.SOUTH);

        // Cargar estado guardado o generar por defecto
        cargarEstado();

        // Guardar al cerrar con la X
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarEstado();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                guardarEstado();
            }
        });

        // Refrescar detalle automáticamente cuando cambie el modelo
        listModel.addListDataListener(new ListDataListener() {
            @Override
            public void contentsChanged(ListDataEvent e) {
                renderDetalle();
            }

            @Override
            public void intervalAdded(ListDataEvent e) {
                renderDetalle();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                renderDetalle();
            }
        });

        setVisible(true);
    }

    // =================== UI ===================
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("CLASES GRUPALES", SwingConstants.LEFT);
        title.setForeground(TXT_LIGHT);
        title.setFont(vipFont(true, 26f));

        JLabel slogan = new JLabel("Movimiento, energía y comunidad", SwingConstants.LEFT);
        slogan.setForeground(TXT_MUTED);
        // >>> aquí inclinamos el eslogan tipo cursiva <<<
        slogan.setFont(vipFont(false, 15f).deriveFont(Font.ITALIC));

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(slogan);

        header.add(titles, BorderLayout.WEST);
        return header;
    }

    private JComponent buildListaPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_D);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lbl = new JLabel("Listado de clases del día");
        lbl.setForeground(TXT_LIGHT);
        lbl.setFont(vipFont(true, 16f));
        p.add(lbl, BorderLayout.NORTH);

        // Renderer simple y legible (sin emojis)
        list.setCellRenderer(new ClaseItemRenderer());
        list.setBackground(new Color(26, 26, 26));
        list.setSelectionBackground(new Color(54, 54, 54));
        list.setSelectionForeground(TXT_LIGHT);
        list.setFont(vipFont(false, 16f));
        list.setFixedCellHeight(52);

        // Al cambiar selección, refrescar área de detalle
        list.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                renderDetalle();
            }
        });

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(26, 26, 26));
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    private JComponent buildDetallePanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(PANEL_D);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lbl = new JLabel("Detalle");
        lbl.setForeground(TXT_LIGHT);
        lbl.setFont(vipFont(true, 16f));
        p.add(lbl, BorderLayout.NORTH);

        infoArea.setEditable(false);
        infoArea.setWrapStyleWord(true);
        infoArea.setLineWrap(true);
        infoArea.setForeground(TXT_LIGHT);
        infoArea.setBackground(new Color(18, 18, 18));
        infoArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane sp = new JScrollPane(infoArea);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(18, 18, 18));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildAccionesPanel() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        bar.setOpaque(false);

        JButton bReservar = btnVip("Reservar por ID", e -> reservar());
        JButton bCancelar = btnVip("Cancelar reserva", e -> cancelar());
        JButton bAgregar = btnVip("Agregar clase", e -> agregarClase());
        JButton bEditar = btnVip("Editar clase", e -> editarClase());
        JButton bEliminar = btnAmarillo("Eliminar", e -> eliminarClase()); // AMARILLO
        JButton bSalir = btnRojo("Salir", e -> {
            guardarEstado();
            dispose();
        });

        bar.add(bReservar);
        bar.add(bCancelar);
        bar.add(bAgregar);
        bar.add(bEditar);
        bar.add(bEliminar);
        bar.add(bSalir);
        return bar;
    }

    // =================== LÓGICA ===================
    private void cargarEstado() {
        listModel.clear();
        if (STATE_SAVED) {
            for (ClaseGrupal c : STATE_CLASES) {
                listModel.addElement(c.deepCopy());
            }
        } else {
            regenerarPredefinidas();
            guardarEstado();
        }
        if (!listModel.isEmpty()) {
            list.setSelectedIndex(0);
        }
        renderDetalle();
    }

    private void guardarEstado() {
        STATE_CLASES = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            STATE_CLASES.add(listModel.get(i).deepCopy());
        }
        STATE_SAVED = true;
    }

    private void regenerarPredefinidas() {
        listModel.clear();
        int tIdx = 0, hIdx = 0;
        for (int i = 0; i < clasesManana; i++) {
            String nombre = TIPOS[tIdx % TIPOS.length];
            String horario = HORAS_AM[hIdx % HORAS_AM.length];
            listModel.addElement(new ClaseGrupal(nombre, horario, capacidadMax));
            tIdx++;
            hIdx++;
        }
        hIdx = 0;
        for (int i = 0; i < clasesNoche; i++) {
            String nombre = TIPOS[tIdx % TIPOS.length];
            String horario = HORAS_PM[hIdx % HORAS_PM.length];
            listModel.addElement(new ClaseGrupal(nombre, horario, capacidadMax));
            tIdx++;
            hIdx++;
        }
    }

    private void reservar() {
        ClaseGrupal c = list.getSelectedValue();
        if (c == null) {
            warn("Seleccione una clase.");
            return;
        }
        if (!c.habilitada) {
            warn("La clase no está disponible.");
            return;
        }

        String id = inputDark("Reserva", "Ingrese ID del socio:");
        if (id == null || id.isBlank()) {
            return;
        }

        if (c.reservar(id.trim())) {
            infoOK("Reserva exitosa en %s (%s).".formatted(c.nombre, c.horario));
            refreshUI();
            guardarEstado();
        } else {
            warn("No hay cupo disponible o el ID ya está inscrito.");
        }
    }

    private void cancelar() {
        ClaseGrupal c = list.getSelectedValue();
        if (c == null) {
            warn("Seleccione una clase.");
            return;
        }

        String id = inputDark("Cancelar", "ID del socio a remover:");
        if (id == null || id.isBlank()) {
            return;
        }

        if (c.cancelar(id.trim())) {
            infoOK("Reserva cancelada.");
            refreshUI();
            guardarEstado();
        } else {
            warn("Ese ID no está en la lista.");
        }
    }

    private void agregarClase() {
        ClaseGrupal nueva = dialogClase(null);
        if (nueva != null) {
            listModel.addElement(nueva);
            list.setSelectedIndex(listModel.size() - 1);
            refreshUI();
            guardarEstado();
        }
    }

    private void editarClase() {
        ClaseGrupal c = list.getSelectedValue();
        if (c == null) {
            warn("Seleccione una clase.");
            return;
        }
        ClaseGrupal editada = dialogClase(c);
        if (editada != null) {
            if (editada.capacidad < c.reservados()) {
                warn("La nueva capacidad es menor que los inscritos actuales.");
                return;
            }
            c.nombre = editada.nombre;
            c.horario = editada.horario;
            c.capacidad = editada.capacidad;
            c.habilitada = editada.habilitada;
            refreshUI();
            guardarEstado();
        }
    }

    private void eliminarClase() {
        ClaseGrupal c = list.getSelectedValue();
        if (c == null) {
            warn("Seleccione una clase.");
            return;
        }
        int ok = confirm("Eliminar", "¿Eliminar la clase «%s - %s»?".formatted(c.nombre, c.horario));
        if (ok == JOptionPane.YES_OPTION) {
            int idx = list.getSelectedIndex();
            listModel.removeElement(c);
            if (!listModel.isEmpty()) {
                list.setSelectedIndex(Math.max(0, Math.min(idx, listModel.size() - 1)));
            }
            refreshUI();
            guardarEstado();
        }
    }

    // ====== Refresco instantáneo ======
    private void refreshUI() {
        list.repaint();     // refresca la fila (renderer)
        renderDetalle();    // refresca el panel de detalle
    }

    private void renderDetalle() {
        ClaseGrupal c = list.getSelectedValue();
        if (c == null) {
            infoArea.setText("");
            return;
        }
        infoArea.setText("""
                Nombre: %s
                Horario: %s
                Capacidad: %d
                Reservados: %d
                Cupos libres: %d
                Disponible: %s
                IDs inscritos: %s
                """.formatted(
                c.nombre, c.horario, c.capacidad, c.reservados(),
                c.cuposLibres(), (c.habilitada ? "Sí" : "No"),
                c.ids.isEmpty() ? "(ninguno)" : String.join(", ", c.ids)
        ));
    }

    // =================== Helpers UI ===================
    private JButton btnVip(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(BTN_VIP);
        b.setForeground(Color.WHITE);
        b.setFont(vipFont(true, 14f));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(adjust(BTN_VIP, 1.08f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(BTN_VIP);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(adjust(BTN_VIP, 0.92f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(adjust(BTN_VIP, 1.06f));
            }
        });
        return b;
    }

    private JButton btnRojo(String text, ActionListener al) {
        JButton b = btnVip(text, al);
        b.setBackground(BTN_RED);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(adjust(BTN_RED, 1.08f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(BTN_RED);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(adjust(BTN_RED, 0.92f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(adjust(BTN_RED, 1.06f));
            }
        });
        return b;
    }

    private JButton btnAmarillo(String text, ActionListener al) {
        JButton b = btnVip(text, al);
        b.setBackground(BTN_YELLOW);
        b.setForeground(Color.BLACK); // mejor contraste con amarillo
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(adjust(BTN_YELLOW, 1.08f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(BTN_YELLOW);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(adjust(BTN_YELLOW, 0.92f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(adjust(BTN_YELLOW, 1.06f));
            }
        });
        return b;
    }

    private JPanel wrap(JComponent inner) {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(BG_DARK);
        w.setBorder(new EmptyBorder(10, 10, 10, 10));
        w.add(inner, BorderLayout.CENTER);
        return w;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TXT_LIGHT);
        l.setFont(vipFont(false, 13f));
        return l;
    }

    // ===== Diálogos oscuros y legibles =====
    private void infoOK(String msg) {
        JOptionPane.showMessageDialog(this, html(msg), "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, html(msg), "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private int confirm(String t, String m) {
        return JOptionPane.showConfirmDialog(this, html(m), t, JOptionPane.YES_NO_OPTION);
    }

    // input oscuro con TextField propio (sin cuadro blanco)
    private String inputDark(String title, String prompt) {
        JTextField tf = txt("");
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_DARK);
        panel.add(lbl(prompt), BorderLayout.NORTH);
        panel.add(tf, BorderLayout.CENTER);
        int ok = JOptionPane.showConfirmDialog(this, wrap(panel), title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return (ok == JOptionPane.OK_OPTION) ? tf.getText() : null;
    }

    private Object html(String s) {
        return "<html><div style='color:#ededed; font-family:Sans-Serif; font-size:13px; width:380px;'>"
                + s + "</div></html>";
    }

    private static Color adjust(Color c, float f) {
        int r = Math.min(255, Math.max(0, Math.round(c.getRed() * f)));
        int g = Math.min(255, Math.max(0, Math.round(c.getGreen() * f)));
        int b = Math.min(255, Math.max(0, Math.round(c.getBlue() * f)));
        return new Color(r, g, b);
    }

    // ===== Fuentes (más simples y legibles) =====
    private static Font vipFont(boolean bold, float size) {
        String[] prefs = {"Segoe UI", "Montserrat", "Arial", "SansSerif"};
        String fam = findInstalledFont(prefs);
        int style = bold ? Font.BOLD : Font.PLAIN;
        return new Font(fam, style, Math.round(size));
    }

    private static String findInstalledFont(String[] families) {
        String[] installed = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String cand : families) {
            for (String f : installed) {
                if (f.equalsIgnoreCase(cand)) {
                    return cand;
                }
            }
        }
        return "SansSerif";
    }

    private JTextField txt(String v) {
        JTextField t = new JTextField(v);
        t.setForeground(TXT_LIGHT);
        t.setBackground(new Color(32, 32, 32));
        t.setCaretColor(TXT_LIGHT);
        t.setBorder(new EmptyBorder(8, 10, 8, 10));
        t.setFont(vipFont(false, 14f));
        return t;
    }

    private void applyDarkUI() {
        UIManager.put("OptionPane.background", BG_DARK);
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.messageForeground", TXT_LIGHT);

        UIManager.put("Label.foreground", TXT_LIGHT);
        UIManager.put("TextField.background", new Color(32, 32, 32));
        UIManager.put("TextField.foreground", TXT_LIGHT);
        UIManager.put("TextField.caretForeground", TXT_LIGHT);

        UIManager.put("Spinner.background", new Color(32, 32, 32));
        UIManager.put("Spinner.foreground", TXT_LIGHT);
        UIManager.put("ComboBox.background", new Color(32, 32, 32));
        UIManager.put("ComboBox.foreground", TXT_LIGHT);
    }

    // ====== Renderer simple (sin emojis) ======
    private class ClaseItemRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ClaseGrupal c) {
                setText(c.nombre + "  •  " + c.horario);
            }
            setFont(vipFont(false, 16f));
            setForeground(TXT_LIGHT);
            setBackground(isSelected ? new Color(54, 54, 54) : new Color(26, 26, 26));
            setBorder(new EmptyBorder(8, 10, 8, 10));
            return this;
        }
    }

    // ====== Diálogo alta/edición de clase ======
    private ClaseGrupal dialogClase(ClaseGrupal base) {
        JTextField tfNombre = txt(base == null ? "" : base.nombre);
        JTextField tfHora = txt(base == null ? "" : base.horario);
        JSpinner spCap = new JSpinner(new SpinnerNumberModel(base == null ? capacidadMax : base.capacidad, 1, 50, 1));
        styleSpinner(spCap);
        JCheckBox cbHab = new JCheckBox("Disponible", base == null || base.habilitada);
        cbHab.setForeground(TXT_LIGHT);
        cbHab.setBackground(BG_DARK);
        cbHab.setFont(vipFont(false, 13f));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(BG_DARK);
        form.add(lbl("Nombre:"));
        form.add(tfNombre);
        form.add(lbl("Horario:"));
        form.add(tfHora);
        form.add(lbl("Capacidad:"));
        form.add(spCap);
        form.add(new JLabel());
        form.add(cbHab);

        int ok = JOptionPane.showConfirmDialog(this, wrap(form),
                (base == null ? "Agregar clase" : "Editar clase"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            String n = tfNombre.getText().trim();
            String h = tfHora.getText().trim();
            int cap = (int) spCap.getValue();
            boolean hab = cbHab.isSelected();
            if (n.isEmpty() || h.isEmpty()) {
                warn("Complete nombre y horario.");
                return null;
            }
            return new ClaseGrupal(n, h, cap, hab);
        }
        return null;
    }

    private void styleSpinner(JSpinner sp) {
        sp.setFont(vipFont(false, 13f));
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            de.getTextField().setForeground(TXT_LIGHT);
            de.getTextField().setBackground(new Color(32, 32, 32));
            de.getTextField().setCaretColor(TXT_LIGHT);
            de.getTextField().setBorder(new EmptyBorder(6, 8, 6, 8));
        }
    }

    // =================== Modelo ===================
    public static class ClaseGrupal {

        String nombre;
        String horario;
        int capacidad;
        boolean habilitada = true;
        final LinkedHashSet<String> ids = new LinkedHashSet<>();

        public ClaseGrupal(String nombre, String horario, int capacidad) {
            this(nombre, horario, capacidad, true);
        }

        public ClaseGrupal(String nombre, String horario, int capacidad, boolean habilitada) {
            this.nombre = nombre;
            this.horario = horario;
            this.capacidad = capacidad;
            this.habilitada = habilitada;
        }

        // copia profunda para persistencia segura
        public ClaseGrupal deepCopy() {
            ClaseGrupal c = new ClaseGrupal(nombre, horario, capacidad, habilitada);
            c.ids.addAll(this.ids);
            return c;
        }

        public boolean reservar(String id) {
            if (!habilitada) {
                return false;
            }
            if (ids.contains(id)) {
                return false;
            }
            if (ids.size() >= capacidad) {
                return false;
            }
            ids.add(id);
            return true;
        }

        public boolean cancelar(String id) {
            return ids.remove(id);
        }

        public int reservados() {
            return ids.size();
        }

        public int cuposLibres() {
            return Math.max(0, capacidad - ids.size());
        }

        @Override
        public String toString() {
            return nombre + " | " + horario + " | " + reservados() + "/" + capacidad;
        }
    }
}
