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
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

public class SalaPesasWindow extends JFrame {

    // ======= THEME (basado en el estilo VIP del Parqueo) =======
    private static final Color COL_BG = new Color(12, 12, 12);
    private static final Color COL_PANEL = new Color(22, 22, 22);
    private static final Color COL_TXT = new Color(230, 230, 230);

    private static final Color TAB_BG = new Color(48, 48, 48);
    private static final Color TAB_BG_ACTIVE = new Color(70, 70, 70);

    private static final Color COL_ACCION = TAB_BG_ACTIVE;        // Botones principales
    private static final Color COL_PELIGRO = new Color(198, 40, 40); // Botón rojo "Salir"

    private static final Color LIST_BG = new Color(30, 30, 30);
    private static final Color LIST_BORDER = new Color(180, 180, 180);

    private static final int MAX_CUPOS = 50;

    // ======= Estado / Modelo externo =======
    private final SistemaGimnasio sistema;
    private final SalaPesas salaPesas;

    // ======= UI =======
    private DefaultListModel<String> sociosModel = new DefaultListModel<>();
    private JList<String> sociosList = new JList<>(sociosModel);
    private JLabel chipCapacidad, chipDentro, chipDisponibles;
    private JLabel lblEstado;

    public SalaPesasWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        this.salaPesas = sistema.getSalaPesas();

        aplicarTemaOscuroJOptionPane();

        setTitle("Sala de Pesas — Zona Elite");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(COL_BG);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // Header (título + slogan + chips)
        root.add(crearHeader(), BorderLayout.NORTH);

        // Centro (acciones + lista)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelAcciones(), crearPanelSocios());
        split.setResizeWeight(0.45);
        split.setDividerLocation(450);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        // Footer con botón salir
        root.add(crearFooter(), BorderLayout.SOUTH);

        actualizarEstado();
        setVisible(true);
    }

    // ======= HEADER =======
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Título + slogan
        JPanel tituloPanel = new JPanel(new GridLayout(2, 1));
        tituloPanel.setOpaque(false);

        JLabel titulo = new JLabel("SALA DE PESAS", SwingConstants.LEFT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setBorder(new EmptyBorder(0, 6, 0, 6));

        JLabel slogan = new JLabel("Movimiento, fuerza y bienestar", SwingConstants.LEFT);
        slogan.setForeground(new Color(192, 192, 192));
        slogan.setFont(new Font("SansSerif", Font.ITALIC, 16));
        slogan.setBorder(new EmptyBorder(0, 8, 10, 6));

        tituloPanel.add(titulo);
        tituloPanel.add(slogan);

        header.add(tituloPanel, BorderLayout.NORTH);

        // Chips de estado
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        chips.setOpaque(false);

        chipCapacidad = crearChip("Capacidad: " + MAX_CUPOS);
        chipDentro = crearChip("Dentro: 0");
        chipDisponibles = crearChip("Disponibles: " + MAX_CUPOS);

        chips.add(chipCapacidad);
        chips.add(chipDentro);
        chips.add(chipDisponibles);

        header.add(chips, BorderLayout.CENTER);

        return header;
    }

    private JLabel crearChip(String txt) {
        JLabel l = new JLabel(txt);
        l.setOpaque(true);
        l.setBackground(TAB_BG);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setBorder(new EmptyBorder(6, 10, 6, 10));
        return l;
    }

    // ======= PANEL IZQUIERDO (Acciones) =======
    private JPanel crearPanelAcciones() {
        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.setBackground(COL_PANEL);
        left.setBorder(new EmptyBorder(14, 14, 14, 14));

        lblEstado = new JLabel("Personas actualmente dentro: 0", SwingConstants.CENTER);
        lblEstado.setForeground(COL_TXT);
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblEstado.setBorder(new EmptyBorder(4, 0, 14, 0));
        left.add(lblEstado, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 12, 12));
        buttons.setOpaque(false);

        JButton btnIngresar = crearBtn("Ingresar socio", e -> {
            if (salaPesas.getCantidad() >= MAX_CUPOS) {
                warnDark("Sala llena", "La sala alcanzó su capacidad máxima.");
                return;
            }
            String id = inputDark("Ingreso", "Ingrese el ID del socio que va a ingresar:");
            if (id == null) {
                return;
            }
            id = id.trim();
            if (id.isEmpty()) {
                return;
            }

            boolean ok = salaPesas.ingresar(id);
            if (ok) {
                infoDark("Ingreso registrado", "El socio fue ingresado correctamente.");
            } else {
                warnDark("Duplicado", "Ese socio ya está dentro.");
            }
            actualizarEstado();
        });

        JButton btnSalirSocio = crearBtn("Salir socio", e -> {
            String id = inputDark("Salida", "Ingrese el ID del socio que va a salir:");
            if (id == null) {
                return;
            }
            id = id.trim();
            if (id.isEmpty()) {
                return;
            }

            boolean ok = salaPesas.salir(id);
            if (ok) {
                infoDark("Salida registrada", "El socio salió de la sala.");
            } else {
                warnDark("No encontrado", "No se encontró ese socio dentro.");
            }
            actualizarEstado();
        });

        buttons.add(btnIngresar);
        buttons.add(btnSalirSocio);

        left.add(buttons, BorderLayout.CENTER);
        return left;
    }

    // ======= PANEL DERECHO (Lista de socios) =======
    private JPanel crearPanelSocios() {
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setBackground(COL_PANEL);
        right.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(LIST_BG);
        card.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(LIST_BORDER, 3, true),
                "Socios dentro",
                0, 0,
                new Font("SansSerif", Font.BOLD, 16),
                COL_TXT
        ));

        sociosList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sociosList.setBackground(LIST_BG);
        sociosList.setForeground(COL_TXT);
        sociosList.setSelectionBackground(new Color(212, 172, 13, 160));
        sociosList.setSelectionForeground(Color.BLACK);

        JScrollPane scroll = new JScrollPane(sociosList);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(LIST_BG);

        JLabel hint = new JLabel("Se muestra: Nombre (ID)", SwingConstants.CENTER);
        hint.setForeground(new Color(180, 180, 180));
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setBorder(new EmptyBorder(4, 0, 4, 0));

        card.add(hint, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        right.add(card, BorderLayout.CENTER);
        return right;
    }

    // ======= FOOTER =======
    private JPanel crearFooter() {
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        foot.setBackground(COL_BG);

        JButton btnSalir = crearBtnRojo("Salir", e -> dispose());
        foot.add(btnSalir);
        return foot;
    }

    // ======= BOTONES (estilo Parqueo) =======
    private JButton crearBtn(String texto, ActionListener al) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(COL_ACCION);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(adjust(COL_ACCION, 1.08f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(COL_ACCION);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(adjust(COL_ACCION, 0.92f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(adjust(COL_ACCION, 1.06f));
            }
        });
        return b;
    }

    private JButton crearBtnRojo(String texto, ActionListener al) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(COL_PELIGRO);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(adjust(COL_PELIGRO, 1.08f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(COL_PELIGRO);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(adjust(COL_PELIGRO, 0.92f));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(adjust(COL_PELIGRO, 1.06f));
            }
        });
        return b;
    }

    private Color adjust(Color c, float factor) {
        int r = Math.min(255, Math.max(0, Math.round(c.getRed() * factor)));
        int g = Math.min(255, Math.max(0, Math.round(c.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, Math.round(c.getBlue() * factor)));
        return new Color(r, g, b);
    }

    // ======= LÓGICA =======
    private void actualizarEstado() {
        int dentro = salaPesas.getCantidad();
        int disponibles = Math.max(0, MAX_CUPOS - dentro);

        chipCapacidad.setText("Capacidad: " + MAX_CUPOS);
        chipDentro.setText("Dentro: " + dentro);
        chipDisponibles.setText("Disponibles: " + disponibles);

        lblEstado.setText("Personas actualmente dentro: " + dentro);

        sociosModel.clear();
        for (String id : salaPesas.getSociosActuales()) {
            String nom = obtenerNombreSocio(id);
            sociosModel.addElement((nom != null ? nom : "(Sin nombre)") + "  (" + id + ")");
        }
    }

    /**
     * Intenta resolver el nombre por ID preguntando al sistema (igual que
     * Parqueo).
     */
    private String obtenerNombreSocio(String id) {
        if (id == null) {
            return null;
        }

        // 1) sistema.getNombreSocioPorId(String)
        try {
            Method m = sistema.getClass().getMethod("getNombreSocioPorId", String.class);
            Object r = m.invoke(sistema, id);
            if (r != null) {
                return String.valueOf(r);
            }
        } catch (NoSuchMethodException ignored) {
            // 2) sistema.getControlSocios().obtenerNombrePorId(String)
            try {
                Method mCtrl = sistema.getClass().getMethod("getControlSocios");
                Object ctrl = mCtrl.invoke(sistema);
                if (ctrl != null) {
                    try {
                        Method mNom = ctrl.getClass().getMethod("obtenerNombrePorId", String.class);
                        Object r2 = mNom.invoke(ctrl, id);
                        if (r2 != null) {
                            return String.valueOf(r2);
                        }
                    } catch (NoSuchMethodException ignored2) {
                    }
                }
            } catch (Exception ignored3) {
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    // ======= Diálogos oscuros (consistentes con Parqueo) =======
    private void aplicarTemaOscuroJOptionPane() {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);

        UIManager.put("Label.foreground", COL_TXT);
        UIManager.put("TextField.background", new Color(32, 32, 32));
        UIManager.put("TextField.foreground", COL_TXT);
        UIManager.put("TextField.caretForeground", COL_TXT);

        UIManager.put("Spinner.background", new Color(32, 32, 32));
        UIManager.put("Spinner.foreground", COL_TXT);
        UIManager.put("ComboBox.background", new Color(32, 32, 32));
        UIManager.put("ComboBox.foreground", COL_TXT);
    }

    private void infoDark(String title, String msg) {
        JOptionPane.showMessageDialog(this, html(msg), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void warnDark(String title, String msg) {
        JOptionPane.showMessageDialog(this, html(msg), title, JOptionPane.WARNING_MESSAGE);
    }

    private String inputDark(String title, String msg) {
        return JOptionPane.showInputDialog(this, html(msg), title, JOptionPane.QUESTION_MESSAGE);
    }

    private Object html(String msg) {
        return "<html><div style='color:#e6e6e6; font-family:Sans-Serif; font-size:12px; width:420px;'>" + msg + "</div></html>";
    }
}
