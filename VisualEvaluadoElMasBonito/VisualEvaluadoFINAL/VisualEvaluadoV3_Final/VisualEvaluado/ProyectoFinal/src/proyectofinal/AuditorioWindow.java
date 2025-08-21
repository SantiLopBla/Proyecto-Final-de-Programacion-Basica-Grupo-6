/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

/**
 *
 * @author Ariel
 */
package proyectofinal;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class AuditorioWindow extends JFrame {

    // ===== Backend (inyectado) =====
    private final SistemaGimnasio sistema;
    private final Auditorio auditorio;

    // ===== THEME (similar a Parqueo/Cabinas) =====
    private static final Color COL_BG = new Color(12, 12, 12);
    private static final Color COL_PANEL = new Color(22, 22, 22);
    private static final Color COL_PANEL_2 = new Color(28, 28, 28);
    private static final Color COL_BORDER = new Color(45, 45, 45);
    private static final Color COL_TXT = new Color(230, 230, 230);
    private static final Color COL_TXT_MUTE = new Color(200, 200, 200);

    private static final Color COL_OK = new Color(46, 125, 50);   // KPI verde
    private static final Color COL_ERR = new Color(198, 40, 40);   // KPI rojo
    private static final Color BADGE_OK = new Color(39, 174, 96);
    private static final Color BADGE_NO = new Color(198, 40, 40);

    // Capacidad por sesión
    private static final int CAPACIDAD_MAX = 50;

    // ===== Persistencia en memoria (se resetea al cerrar programa) =====
    private static boolean STATE_READY = false;
    private static String[] S_sesiones;                 // nombres de sesiones
    private static Map<String, java.util.List<String>> S_inscritosPorSesion; // por nombre de sesión

    // ===== Estado UI =====
    private int sesionSel = -1;

    private final JPanel listaPanel = new JPanel(new GridBagLayout());
    private final java.util.List<JPanel> filasSesion = new ArrayList<>();
    private final java.util.List<JLabel> lblCupos = new ArrayList<>();
    private final java.util.List<JLabel> lblEstado = new ArrayList<>();

    // KPIs
    private final JLabel kpiLibres = kpi("🟢 Libres: 0", COL_OK);
    private final JLabel kpiOcupados = kpi("🔴 Ocupados: 0", COL_ERR);

    // Detalle + historial
    private final JLabel lblDetalle = new JLabel();
    private final JTextArea historial = new JTextArea();

    public AuditorioWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        // Se asume que Auditorio provee: sesiones[], getReservas(i), registrar(i,id), cancelar(i,id)
        this.auditorio = new Auditorio();

        // Cargar/inyectar estado estático (solo memoria, no disco)
        cargarEstadoEstatico();

        // Ventana
        setTitle("Auditorio"); // título de la ventana
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COL_BG);
        setLayout(new BorderLayout());

        // ===== Header (titulo + slogan) =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 6, 20));

        JLabel title = new JLabel("AUDITORIO", SwingConstants.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(0, 6, 0, 6));

        JLabel slogan = new JLabel("Hablamos de salud, pensamos en ti", SwingConstants.LEFT);
        slogan.setForeground(new Color(192, 192, 192));
        slogan.setFont(new Font("SansSerif", Font.ITALIC, 16));
        slogan.setBorder(new EmptyBorder(0, 8, 10, 6));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(slogan);

        header.add(left, BorderLayout.WEST);

        // ===== KPIs debajo del header =====
        JPanel kpis = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        kpis.setOpaque(false);
        kpis.setBorder(new EmptyBorder(0, 20, 12, 20));
        kpis.add(kpiLibres);
        kpis.add(kpiOcupados);

        // Contenedor superior que agrupa header arriba y KPIs abajo
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(kpis, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH); // <- ahora si, sin error

        // ===== Centro =====
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Lista de sesiones
        listaPanel.setBackground(COL_PANEL);
        listaPanel.setBorder(new CompoundBorder(new LineBorder(COL_BORDER, 1, true), new EmptyBorder(12, 12, 12, 12)));
        construirListaSesiones();

        JScrollPane scrollLista = new JScrollPane(listaPanel);
        scrollLista.setBorder(new LineBorder(COL_BORDER, 1, true));
        scrollLista.getViewport().setBackground(COL_PANEL);

        // Panel derecho: detalle e historial
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(420, 0));
        right.setBackground(COL_BG);

        JPanel cardDetalle = new JPanel(new BorderLayout());
        cardDetalle.setBackground(COL_PANEL_2);
        cardDetalle.setBorder(new CompoundBorder(new LineBorder(COL_BORDER, 1, true), new EmptyBorder(16, 16, 16, 16)));

        JLabel titDetalle = new JLabel("Detalle");
        titDetalle.setFont(new Font("SansSerif", Font.BOLD, 18));
        titDetalle.setForeground(COL_TXT);
        titDetalle.setBorder(new EmptyBorder(0, 0, 10, 0));

        lblDetalle.setVerticalAlignment(SwingConstants.TOP);
        lblDetalle.setForeground(COL_TXT);
        lblDetalle.setText(html(
                "<div style='color:#ddd;font-size:13px;'>"
                + "<b>Sesión:</b> —<br>"
                + "<b>Cupos libres:</b> —<br>"
                + "<b>Inscritos:</b> —"
                + "</div>"
        ));

        JPanel cardHist = new JPanel(new BorderLayout());
        cardHist.setOpaque(false);

        JLabel titHist = new JLabel("Historial");
        titHist.setFont(new Font("SansSerif", Font.BOLD, 15));
        titHist.setForeground(COL_TXT);
        titHist.setBorder(new EmptyBorder(12, 0, 6, 0));

        historial.setEditable(false);
        historial.setBackground(new Color(30, 30, 30));
        historial.setForeground(COL_TXT);
        historial.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historial.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane histScroll = new JScrollPane(historial);
        histScroll.setBorder(new LineBorder(COL_BORDER, 1, true));

        cardDetalle.add(titDetalle, BorderLayout.NORTH);
        cardDetalle.add(lblDetalle, BorderLayout.CENTER);
        cardHist.add(titHist, BorderLayout.NORTH);
        cardHist.add(histScroll, BorderLayout.CENTER);

        JPanel rightWrap = new JPanel(new BorderLayout(0, 10));
        rightWrap.setOpaque(false);
        rightWrap.add(cardDetalle, BorderLayout.NORTH);
        rightWrap.add(cardHist, BorderLayout.CENTER);
        right.add(rightWrap, BorderLayout.CENTER);

        center.add(scrollLista, BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);
        add(center, BorderLayout.CENTER);

        // ===== Footer (botones) =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(COL_BG);
        footer.setBorder(new EmptyBorder(0, 20, 16, 20));

        JButton bRegistrar = crearBtnTab("Registrar por ID");
        JButton bEliminar = crearBtnTab("Eliminar por ID");
        JButton bBuscar = crearBtnTab("Buscar ID");
        JButton bSalir = crearBtnRojo("Salir");

        bRegistrar.setToolTipText("Inscribe un ID en la sesión seleccionada");
        bEliminar.setToolTipText("Elimina un ID de la sesión seleccionada");
        bBuscar.setToolTipText("Busca en qué sesión(es) está inscrito un ID");

        bRegistrar.addActionListener(e -> registrarPorID());
        bEliminar.addActionListener(e -> eliminarPorID());
        bBuscar.addActionListener(e -> buscarID());
        bSalir.addActionListener(e -> {
            guardarEstadoEstatico(); // persistimos en memoria al volver al menú
            dispose();
        });

        footer.add(bRegistrar);
        footer.add(bEliminar);
        footer.add(bBuscar);
        footer.add(bSalir);

        add(footer, BorderLayout.SOUTH);

        // Guardar al cerrar ventana (NO al cerrar programa)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarEstadoEstatico();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                guardarEstadoEstatico();
            }
        });

        refrescarTodo();
        setVisible(true);
    }

    /* ===================== Estado en memoria (no disco) ===================== */
    private void cargarEstadoEstatico() {
        if (!STATE_READY || S_sesiones == null || S_inscritosPorSesion == null) {
            // Primera vez en toda la ejecución
            S_sesiones = Arrays.copyOf(auditorio.sesiones, auditorio.sesiones.length);
            S_inscritosPorSesion = new HashMap<>();
            for (String s : S_sesiones) {
                S_inscritosPorSesion.put(s, new ArrayList<>());
            }
            STATE_READY = true;
            return;
        }
        // Rehidratar desde el estado estático al backend actual por nombre de sesión
        for (int i = 0; i < auditorio.sesiones.length; i++) {
            String nombre = auditorio.sesiones[i];
            java.util.List<String> ids = S_inscritosPorSesion.get(nombre);
            if (ids == null) {
                continue; // sesión nueva / distinta
            }
            int count = 0;
            for (String id : ids) {
                if (count >= CAPACIDAD_MAX) {
                    break;
                }
                if (auditorio.registrar(i, id)) {
                    count++;
                }
            }
        }
    }

    private void guardarEstadoEstatico() {
        if (!STATE_READY) {
            return;
        }
        // Guardar por nombre la lista de IDs actual
        for (String s : S_sesiones) {
            S_inscritosPorSesion.put(s, new ArrayList<>());
        }
        for (int i = 0; i < auditorio.sesiones.length; i++) {
            String nombre = auditorio.sesiones[i];
            java.util.List<String> dest = S_inscritosPorSesion.computeIfAbsent(nombre, k -> new ArrayList<>());
            dest.clear();
            String[] arr = auditorio.getReservas(i);
            int hasta = Math.min(arr.length, CAPACIDAD_MAX);
            for (int k = 0; k < hasta; k++) {
                dest.add(arr[k]);
            }
        }
    }

    /* ===================== Lista de sesiones ===================== */
    private void construirListaSesiones() {
        filasSesion.clear();
        lblCupos.clear();
        lblEstado.clear();
        listaPanel.removeAll();

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        for (int i = 0; i < auditorio.sesiones.length; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(COL_PANEL_2);
            row.setBorder(new CompoundBorder(new LineBorder(COL_BORDER, 1, true), new EmptyBorder(12, 12, 12, 12)));

            String hora = auditorio.sesiones[i];

            JLabel lblHora = new JLabel("Sesión " + hora);
            lblHora.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblHora.setForeground(COL_TXT);

            JLabel lblCup = new JLabel();
            lblCup.setForeground(COL_TXT);
            lblCup.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JLabel badge = new JLabel("—", SwingConstants.CENTER);
            badge.setOpaque(true);
            badge.setForeground(Color.WHITE);
            badge.setBorder(new EmptyBorder(4, 10, 4, 10));
            badge.setFont(new Font("SansSerif", Font.BOLD, 12));
            badge.setBackground(BADGE_OK);

            JPanel left = new JPanel(new GridLayout(2, 1));
            left.setOpaque(false);
            left.add(lblHora);
            left.add(lblCup);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            right.setOpaque(false);
            right.add(badge);

            row.add(left, BorderLayout.WEST);
            row.add(right, BorderLayout.EAST);

            final int idx = i;
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            row.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    sesionSel = idx;
                    actualizarDetalle(idx);
                    resaltarSeleccion();
                }
            });

            filasSesion.add(row);
            lblCupos.add(lblCup);
            lblEstado.add(badge);

            c.gridx = 0;
            c.gridy = i;
            listaPanel.add(row, c);
        }

        // Relleno vertical
        c.weighty = 1.0;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        c.gridy = auditorio.sesiones.length;
        listaPanel.add(filler, c);

        listaPanel.revalidate();
        listaPanel.repaint();
    }

    private void resaltarSeleccion() {
        for (int i = 0; i < filasSesion.size(); i++) {
            JPanel row = filasSesion.get(i);
            boolean sel = (i == sesionSel);
            row.setBorder(new CompoundBorder(
                    new LineBorder(sel ? new Color(147, 197, 253) : COL_BORDER, 2, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));
        }
    }

    /* ===================== Acciones ===================== */
    private boolean haySeleccion() {
        if (sesionSel < 0) {
            info("Selecciona una sesión en la lista.");
            return false;
        }
        return true;
    }

    private void registrarPorID() {
        if (!haySeleccion()) {
            return;
        }

        // Capacidad fija 50 por sesión
        int inscritosActuales = auditorio.getReservas(sesionSel).length;
        if (CAPACIDAD_MAX - inscritosActuales <= 0) {
            info("Sesión llena (50/50).");
            return;
        }

        String id = JOptionPane.showInputDialog(this, "ID a inscribir:");
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        id = id.trim();

        // === Validación de duplicado por sesión (con mensajes específicos 10:00 a.m. / 3:00 p.m.) ===
        String[] actuales = auditorio.getReservas(sesionSel);
        for (String s : actuales) {
            if (Objects.equals(s, id)) {
                String norm = normalizarHora(auditorio.sesiones[sesionSel]);
                String etiqueta = es10AM(norm) ? "10:00 a.m."
                        : (es3PM(norm) ? "3:00 p.m."
                        : auditorio.sesiones[sesionSel]);
                info("Este usuario ya fue inscrito en la sesión de las " + etiqueta);
                return;
            }
        }

        if (auditorio.getReservas(sesionSel).length >= CAPACIDAD_MAX) {
            info("Sesión llena (50/50).");
            return;
        }

        boolean ok = auditorio.registrar(sesionSel, id);
        if (ok) {
            guardarEstadoEstatico();
            refrescarTodo();
        } else {
            info("No se pudo registrar (quizá ya está inscrito o no hay cupo).");
        }
    }

    private void eliminarPorID() {
        if (!haySeleccion()) {
            return;
        }
        String id = JOptionPane.showInputDialog(this, "ID a eliminar de la sesión:");
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        String[] inscritos = auditorio.getReservas(sesionSel);
        for (String s : inscritos) {
            if (Objects.equals(s, id.trim())) {
                auditorio.cancelar(sesionSel, id.trim());
                guardarEstadoEstatico();
                refrescarTodo();
                return;
            }
        }
        info("Ese ID no está inscrito en esta sesión.");
    }

    private void buscarID() {
        String id = JOptionPane.showInputDialog(this, "ID a buscar:");
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        id = id.trim();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < auditorio.sesiones.length; i++) {
            for (String s : auditorio.getReservas(i)) {
                if (Objects.equals(s, id)) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append("Sesión ").append(auditorio.sesiones[i]);
                }
            }
        }
        info(sb.length() == 0 ? "No hay inscripciones para el ID " + id : sb.toString());
    }

    /* ===================== Refrescos ===================== */
    private void refrescarTodo() {
        actualizarKPIs();
        actualizarLista();
        actualizarHistorial(); // SOLO 10:00 a.m. y 3:00 p.m.
        if (sesionSel >= 0) {
            actualizarDetalle(sesionSel);
        } else {
            lblDetalle.setText(html(
                    "<div style='color:#ddd;font-size:13px;'>"
                    + "<b>Sesión:</b> —<br>"
                    + "<b>Cupos libres:</b> —<br>"
                    + "<b>Inscritos:</b> —"
                    + "</div>"
            ));
        }
    }

    private void actualizarKPIs() {
        int libres = 0, ocup = 0;
        for (int i = 0; i < auditorio.sesiones.length; i++) {
            int inscritos = auditorio.getReservas(i).length;
            int disp = Math.max(0, CAPACIDAD_MAX - inscritos);
            libres += disp;
            ocup += inscritos;
        }
        kpiLibres.setText("🟢 Libres: " + libres);
        kpiOcupados.setText("🔴 Ocupados: " + ocup);
    }

    private void actualizarLista() {
        for (int i = 0; i < auditorio.sesiones.length; i++) {
            int inscritos = auditorio.getReservas(i).length;
            int disp = Math.max(0, CAPACIDAD_MAX - inscritos);
            JLabel cap = lblCupos.get(i);
            JLabel badge = lblEstado.get(i);

            // Solo mostrar: "<disponibles>/50 cupos disponibles"
            cap.setText(disp + "/" + CAPACIDAD_MAX + " cupos disponibles");

            boolean llena = (disp == 0);
            badge.setText(llena ? "LLENA" : "DISPONIBLE");
            badge.setBackground(llena ? BADGE_NO : BADGE_OK);
        }
        resaltarSeleccion();
    }

    private void actualizarDetalle(int idx) {
        String ses = auditorio.sesiones[idx];
        int inscritos = auditorio.getReservas(idx).length;
        int disp = Math.max(0, CAPACIDAD_MAX - inscritos);
        lblDetalle.setText(html(
                "<div style='color:#ddd;font-size:13px;'>"
                + "<b>Sesión:</b> " + ses + "<br>"
                + "<b>Cupos libres:</b> " + disp + " / " + CAPACIDAD_MAX + "<br>"
                + "<b>Inscritos:</b> " + inscritos
                + "</div>"
        ));
    }

    /**
     * HISTORIAL: solo dos apartados fijos (10:00 a.m. y 3:00 p.m.). No se lista
     * nada más aunque existan otras sesiones.
     */
    private void actualizarHistorial() {
        StringBuilder sb10 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();

        for (int i = 0; i < auditorio.sesiones.length; i++) {
            String horaOrig = auditorio.sesiones[i];
            String norm = normalizarHora(horaOrig);
            String[] ins = auditorio.getReservas(i);

            if (es10AM(norm)) {
                if (ins.length != 0) {
                    for (String id : ins) {
                        sb10.append("- ").append(id).append('\n');
                    }
                }
            } else if (es3PM(norm)) {
                if (ins.length != 0) {
                    for (String id : ins) {
                        sb3.append("- ").append(id).append('\n');
                    }
                }
            }
            // otras horas se ignoran en el historial
        }

        StringBuilder finalText = new StringBuilder();
        finalText.append("10:00 a.m.\n");
        finalText.append(sb10.length() == 0 ? "(sin inscritos)\n" : sb10.toString());
        finalText.append("\n");

        finalText.append("3:00 p.m.\n");
        finalText.append(sb3.length() == 0 ? "(sin inscritos)\n" : sb3.toString());
        finalText.append("\n");

        historial.setText(finalText.toString());
        historial.setCaretPosition(0);
    }

    // ===== Normalización robusta de etiquetas de hora =====
    private static String normalizarHora(String s) {
        if (s == null) {
            return "";
        }
        String k = s.toLowerCase(Locale.ROOT);
        k = k.replace(".", "")
                .replace(" ", "")
                .replace(":", "")
                .replace("hs", "")
                .replace("hrs", "");
        return k;
    }

    private static boolean es10AM(String k) {
        return k.equals("10") || k.equals("10am") || k.equals("1000") || k.equals("1000am") || k.equals("10a");
    }

    private static boolean es3PM(String k) {
        return k.equals("3") || k.equals("3pm") || k.equals("300pm") || k.equals("15") || k.equals("1500") || k.equals("1500pm");
    }

    /* ===================== Helpers UI ===================== */
    private static JLabel kpi(String text, Color bg) {
        JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(bg.darker());
        l.setForeground(Color.WHITE);
        l.setBorder(new EmptyBorder(6, 10, 6, 10));
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
    }

    private JButton crearBtnTab(String t) {
        final Color TAB_BG_ACTIVE = new Color(70, 70, 70);
        JButton b = new JButton(t);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(TAB_BG_ACTIVE);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(TAB_BG_ACTIVE, 1.08f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(TAB_BG_ACTIVE);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(TAB_BG_ACTIVE, 0.92f));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(TAB_BG_ACTIVE, 1.06f));
            }
        });
        return b;
    }

    private JButton crearBtnRojo(String t) {
        JButton b = new JButton(t);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(COL_ERR);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(COL_ERR, 1.08f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(COL_ERR);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(COL_ERR, 0.92f));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(COL_ERR, 1.06f));
            }
        });
        return b;
    }

    private static Color adjust(Color c, float factor) {
        int r = Math.min(255, Math.max(0, Math.round(c.getRed() * factor)));
        int g = Math.min(255, Math.max(0, Math.round(c.getGreen() * factor)));
        int b = Math.min(255, Math.max(0, Math.round(c.getBlue() * factor)));
        return new Color(r, g, b);
    }

    private static String html(String inner) {
        return "<html>" + inner + "</html>";
    }

    private static void info(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
