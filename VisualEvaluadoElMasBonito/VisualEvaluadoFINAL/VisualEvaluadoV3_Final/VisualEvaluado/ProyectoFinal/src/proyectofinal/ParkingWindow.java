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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

public class ParkingWindow extends JFrame {

    // Emojis
    private static final String EMO_LIBRE = "🅿️";
    private static final String EMO_OCUP = "❌";
    private static final String EMO_DIS = "♿";
    private static final String EMO_EXC = "👨🏻‍💻";

    // Colores base
    private static final Color COL_BG = new Color(12, 12, 12);
    private static final Color COL_PANEL = new Color(22, 22, 22);
    private static final Color COL_TXT = new Color(230, 230, 230);

    private static final Color COL_LIBRE = new Color(46, 125, 50);   // verde
    private static final Color COL_OCUP = new Color(198, 40, 40);   // rojo
    private static final Color COL_DIS = new Color(21, 101, 192);  // azul
    private static final Color COL_EXC = new Color(251, 192, 45);  // amarillo

    // Pestañas gris VIP
    private static final Color TAB_BG = new Color(48, 48, 48);
    private static final Color TAB_BG_ACTIVE = new Color(70, 70, 70);
    private static final Color TAB_TXT = Color.WHITE;

    private final SistemaGimnasio sistema;

    // ---- DISEÑO BASE ----
    private final char[][] baseG1 = {
        {'D', 'D', 'D', 'E', 'E'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'}
    };
    private final char[][] baseG2 = {
        {'E', 'E', 'D', 'D', 'D'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'}
    };
    private final char[][] baseG3 = {
        {'E', 'D', 'D', 'D', 'E'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'},
        {'L', 'L', 'L', 'L', 'L'}
    };

    // ---- ESTADO ACTUAL (modificable) ----
    private final char[][] g1 = copia(baseG1);
    private final char[][] g2 = copia(baseG2);
    private final char[][] g3 = copia(baseG3);
    private final String[][] g1IDs = new String[g1.length][g1[0].length];
    private final String[][] g2IDs = new String[g2.length][g2[0].length];
    private final String[][] g3IDs = new String[g3.length][g3[0].length];

    // Cache ID -> Nombre
    private final String[] cacheIds = new String[300];
    private final String[] cacheNombres = new String[300];

    // ---- PERSISTENCIA EN MEMORIA (DE LA APP) ----
    private static boolean STATE_READY = false;
    private static char[][] S_g1, S_g2, S_g3;
    private static String[][] S_g1IDs, S_g2IDs, S_g3IDs;
    private static String[] S_cacheIds, S_cacheNombres;

    // UI
    private JLabel chipLibres, chipOcupados, chipDis, chipExc;
    private JTabbedPane tabs;

    public ParkingWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Zona Elite — Parqueo");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(COL_BG);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // Cargar estado guardado (si lo hay) ANTES de construir los paneles
        cargarEstado();

        root.add(crearHeader(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setBackground(COL_BG);
        tabs.setForeground(COL_TXT);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder());

        // Quitar borde blanco del contenido
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }

            @Override
            protected void installDefaults() {
                super.installDefaults();
                contentBorderInsets = new Insets(0, 0, 0, 0);
                tabAreaInsets = new Insets(0, 0, 0, 0);
                tabInsets = new Insets(0, 0, 0, 0);
                selectedTabPadInsets = new Insets(0, 0, 0, 0);
                lightHighlight = highlight = shadow = darkShadow = focus = COL_BG;
            }
        });

        // Contenidos
        tabs.addTab("", crearPanelNivel("P1", baseG1, g1, g1IDs));
        tabs.addTab("", crearPanelNivel("P2", baseG2, g2, g2IDs));
        tabs.addTab("", crearPanelNivel("P3", baseG3, g3, g3IDs));

        // Cabeceras personalizadas
        tabs.setTabComponentAt(0, crearTabHeader("P1"));
        tabs.setTabComponentAt(1, crearTabHeader("P2"));
        tabs.setTabComponentAt(2, crearTabHeader("P3"));
        actualizarTabHeaders();
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                actualizarTabHeaders();
            }
        });

        root.add(tabs, BorderLayout.CENTER);
        root.add(crearAcciones(), BorderLayout.SOUTH);

        actualizarChips();

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

        setVisible(true);
    }

    // Header
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("PARQUEO — ZONA ELITE", SwingConstants.LEFT);
        titulo.setForeground(COL_TXT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setBorder(new EmptyBorder(0, 6, 6, 6));
        header.add(titulo, BorderLayout.NORTH);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        chips.setOpaque(false);

        chipLibres = crearChip("🅿️ Libres: 0", COL_LIBRE);
        chipOcupados = crearChip("❌ Ocupados: 0", COL_OCUP);
        chipDis = crearChip("♿ Discapacitados: 0", COL_DIS);
        chipExc = crearChip("👨🏻‍💻 Exclusivos: 0", COL_EXC);

        chips.add(chipLibres);
        chips.add(chipOcupados);
        chips.add(chipDis);
        chips.add(chipExc);

        header.add(chips, BorderLayout.CENTER);
        return header;
    }

    // Acciones
    private JPanel crearAcciones() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setOpaque(false);

        JButton btnConsultar = crearBtnTab("Consultar ocupantes");
        JButton btnBuscar = crearBtnTab("Buscar por ID");
        JButton btnCerrar = crearBtnRojo("Cerrar");

        btnConsultar.addActionListener(e -> consultarOcupantes());
        btnBuscar.addActionListener(e -> buscarPorID()); // ahora SOLO busca y muestra ubicación
        btnCerrar.addActionListener(e -> {
            guardarEstado();
            dispose();
        });

        actions.add(btnConsultar);
        actions.add(btnBuscar);
        actions.add(btnCerrar);

        return actions;
    }

    // Panel de cada nivel
    private JPanel crearPanelNivel(String nombre, char[][] base, char[][] estado, String[][] ids) {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COL_PANEL);
        panel.setBorder(new EmptyBorder(10, 10, 12, 10));

        JLabel lbl = new JLabel("Nivel " + nombre, SwingConstants.CENTER);
        lbl.setForeground(COL_TXT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(estado.length + 1, estado[0].length + 1, 6, 6));
        grid.setOpaque(false);

        grid.add(crearHdr(""));
        for (int c = 1; c <= estado[0].length; c++) {
            grid.add(crearHdr(String.valueOf(c)));
        }

        for (int i = 0; i < estado.length; i++) {
            grid.add(crearHdr(String.valueOf((char) ('A' + i))));
            for (int j = 0; j < estado[0].length; j++) {
                grid.add(crearCelda(base, estado, ids, i, j));
            }
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JLabel crearHdr(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(new Color(200, 200, 200));
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
    }

    private JButton crearCelda(char[][] base, char[][] actual, String[][] ids, int i, int j) {
        JButton b = new JButton();
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        b.setForeground(Color.WHITE);

        aplicarEstiloEstado(b, actual[i][j]);
        b.setText(emojiDe(actual[i][j]));
        b.setToolTipText(tooltip(actual, ids, i, j));

        b.addActionListener(e -> clickCelda(base, actual, ids, i, j, b));
        return b;
    }

    // Lógica de clic — pide SOLO el ID para reservar/liberar.
    // Se reactivan las confirmaciones para ♿ y 👨🏻‍💻.
    private void clickCelda(char[][] base, char[][] actual, String[][] ids, int i, int j, JButton btn) {
        char estado = actual[i][j];
        char baseTipo = base[i][j];

        if (estado == 'O') {
            // Liberar pidiendo SOLO el ID y validando coincidencia
            String id = pedirID("Liberar", "Ingrese el ID del socio que desea liberar:");
            if (id == null) {
                return;
            }

            String idActual = ids[i][j];
            if (id.equals(idActual)) {
                actual[i][j] = baseTipo;
                ids[i][j] = null;
                refrescarCelda(btn, actual, base, ids, i, j);
                actualizarChips();
                guardarEstado();
                darkInfo("Liberación", "El espacio fue liberado correctamente.");
            } else {
                darkWarn("Liberación", "El ID no coincide con el ocupante del espacio.");
            }
            return;
        }

        // Reservar:
        // L = normal (solo pide ID)
        // D = discapacitado (CONFIRMAR y luego pide ID)
        // E = exclusivo (CONFIRMAR y luego pide ID)
        if (estado == 'L') {
            String id = pedirID("Reservar", "Ingrese ID del socio:");
            if (id == null) {
                return;
            }

            ocupar(actual, ids, i, j, id);
            refrescarCelda(btn, actual, base, ids, i, j);
            actualizarChips();
            guardarEstado();
            return;
        }

        if (estado == 'D') {
            int ok = darkConfirm("Reserva en ♿", "¿Confirma que la persona cumple la condición de discapacidad?");
            if (ok != JOptionPane.YES_OPTION) {
                return;
            }

            String id = pedirID("Reservar ♿", "Ingrese ID del socio:");
            if (id == null) {
                return;
            }

            ocupar(actual, ids, i, j, id);
            refrescarCelda(btn, actual, base, ids, i, j);
            actualizarChips();
            guardarEstado();
            return;
        }

        if (estado == 'E') {
            int ok = darkConfirm("Reserva en 👨🏻‍💻", "¿Reservar este lugar exclusivo para entrenador/trabajador?");
            if (ok != JOptionPane.YES_OPTION) {
                return;
            }

            String id = pedirID("Reservar 👨🏻‍💻", "Ingrese ID del socio/trabajador:");
            if (id == null) {
                return;
            }

            ocupar(actual, ids, i, j, id);
            refrescarCelda(btn, actual, base, ids, i, j);
            actualizarChips();
            guardarEstado();
        }
    }

    private void ocupar(char[][] actual, String[][] ids, int i, int j, String id) {
        actual[i][j] = 'O';
        ids[i][j] = id;
        nombreSocio(id); // precargar nombre (si aplica) sin pedir más inputs
    }

    private void refrescarCelda(JButton b, char[][] actual, char[][] base, String[][] ids, int i, int j) {
        b.setText(emojiDe(actual[i][j]));
        aplicarEstiloEstado(b, actual[i][j]);
        b.setToolTipText(tooltip(actual, ids, i, j));
    }

    // Presentación
    private String emojiDe(char estado) {
        return switch (estado) {
            case 'O' ->
                EMO_OCUP;
            case 'L' ->
                EMO_LIBRE;
            case 'D' ->
                EMO_DIS;
            case 'E' ->
                EMO_EXC;
            default ->
                EMO_LIBRE;
        };
    }

    private void aplicarEstiloEstado(JButton b, char estado) {
        switch (estado) {
            case 'O' -> {
                b.setBackground(COL_OCUP);
                b.setForeground(Color.WHITE);
            }
            case 'L' -> {
                b.setBackground(COL_LIBRE);
                b.setForeground(Color.WHITE);
            }
            case 'D' -> {
                b.setBackground(COL_DIS);
                b.setForeground(Color.WHITE);
            }
            case 'E' -> {
                b.setBackground(COL_EXC);
                b.setForeground(Color.BLACK);
            }
            default -> {
                b.setBackground(new Color(66, 66, 66));
                b.setForeground(Color.WHITE);
            }
        }
    }

    private String tooltip(char[][] actual, String[][] ids, int i, int j) {
        char e = actual[i][j];
        if (e == 'O') {
            String id = ids[i][j];
            String nom = nombreSocio(id);
            return "Ocupado por ID " + id + (nom == null ? "" : " (" + nom + ")");
        }
        return switch (e) {
            case 'L' ->
                "Libre";
            case 'D' ->
                "Discapacitados (requiere confirmación)";
            case 'E' ->
                "Exclusivo entrenador/trabajador (requiere confirmación)";
            default ->
                "N/A";
        };
    }

    private void actualizarChips() {
        int[] c = new int[]{0, 0, 0, 0};
        contar(g1, c);
        contar(g2, c);
        contar(g3, c);
        chipLibres.setText("🅿️ Libres: " + c[0]);
        chipOcupados.setText("❌ Ocupados: " + c[1]);
        chipDis.setText("♿ Discapacitados: " + c[2]);
        chipExc.setText("👨🏻‍💻 Exclusivos: " + c[3]);
    }

    private void contar(char[][] m, int[] acc) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                switch (m[i][j]) {
                    case 'L' ->
                        acc[0]++;
                    case 'O' ->
                        acc[1]++;
                    case 'D' ->
                        acc[2]++;
                    case 'E' ->
                        acc[3]++;
                }
            }
        }
    }

    // Consultas
    private void consultarOcupantes() {
        StringBuilder sb = new StringBuilder();
        sb.append("OCUPANTES (ID y Nombre)\n\n");
        listarNivel("P1", g1, g1IDs, sb);
        listarNivel("P2", g2, g2IDs, sb);
        listarNivel("P3", g3, g3IDs, sb);

        JTextArea area = crearArea(sb.toString());
        JScrollPane sp = new JScrollPane(area);
        sp.getViewport().setBackground(COL_BG);
        sp.setBorder(null);
        sp.setViewportBorder(null);

        JPanel cont = new JPanel(new BorderLayout());
        cont.setBackground(COL_BG);
        JLabel t = new JLabel("Consulta de ocupantes", SwingConstants.CENTER);
        t.setForeground(COL_TXT);
        t.setFont(new Font("SansSerif", Font.BOLD, 18));
        t.setBorder(new EmptyBorder(10, 10, 10, 10));

        cont.add(t, BorderLayout.NORTH);
        cont.add(sp, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, cont, "Consulta", JOptionPane.PLAIN_MESSAGE);
    }

    private void listarNivel(String nombre, char[][] m, String[][] ids, StringBuilder sb) {
        sb.append("Nivel ").append(nombre).append(":\n");
        boolean alguno = false;
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (m[i][j] == 'O') {
                    alguno = true;
                    String id = ids[i][j];
                    String nom = nombreSocio(id);
                    sb.append("  - (").append((char) ('A' + i)).append(",").append(j + 1).append(")  ")
                            .append("ID: ").append(id)
                            .append(nom == null ? "" : "  |  Nombre: " + nom)
                            .append("\n");
                }
            }
        }
        if (!alguno) {
            sb.append("  (sin ocupantes)\n");
        }
        sb.append("\n");
    }

    // ======= CAMBIO: buscarPorID ahora solo ubica y muestra, NO libera =======
    private void buscarPorID() {
        String id = pedirID("Buscar por ID", "Ingrese el ID del socio:");
        if (id == null) {
            return;
        }

        int[] pos = buscarPosicion(g1, g1IDs, id);
        String piso = null;
        int tabIndex = -1;

        if (pos != null) {
            piso = "P1 · Fila " + (char) ('A' + pos[0]) + ", Columna " + (pos[1] + 1);
            tabIndex = 0;
        } else {
            pos = buscarPosicion(g2, g2IDs, id);
            if (pos != null) {
                piso = "P2 · Fila " + (char) ('A' + pos[0]) + ", Columna " + (pos[1] + 1);
                tabIndex = 1;
            } else {
                pos = buscarPosicion(g3, g3IDs, id);
                if (pos != null) {
                    piso = "P3 · Fila " + (char) ('A' + pos[0]) + ", Columna " + (pos[1] + 1);
                    tabIndex = 2;
                }
            }
        }

        if (pos != null) {
            // Seleccionar el piso donde está el ID para que el usuario lo vea
            if (tabIndex >= 0 && tabIndex < tabs.getTabCount()) {
                tabs.setSelectedIndex(tabIndex);
                actualizarTabHeaders();
            }
            // Mostrar la ubicación exacta sin modificar el estado
            String nom = nombreSocio(id);
            String detalle = "El ID " + id + (nom == null ? "" : " (" + nom + ")")
                    + " se encuentra en: " + piso + ".";
            darkInfo("Resultado de búsqueda", detalle);
        } else {
            darkWarn("Resultado de búsqueda", "No se encontró ese ID.");
        }
    }
    // ======= FIN DEL CAMBIO =======

    private int[] buscarPosicion(char[][] m, String[][] ids, String id) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (m[i][j] == 'O' && id.equals(ids[i][j])) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private String buscarEn(String nombre, char[][] m, String[][] ids, String id) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (m[i][j] == 'O' && id.equals(ids[i][j])) {
                    return nombre + " · Fila " + (char) ('A' + i) + ", Columna " + (j + 1);
                }
            }
        }
        return null;
    }

    // Entradas
    private String pedirID(String title, String msg) {
        String id = darkInput(title, msg);
        if (id == null) {
            return null;
        }
        id = id.trim();
        return id.isEmpty() ? null : id;
    }

    private static char[][] copia(char[][] src) {
        char[][] c = new char[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, c[i], 0, src[0].length);
        }
        return c;
    }

    // Nombre por ID (reflection + cache) — sin pedir más inputs al usuario
    private String nombreSocio(String id) {
        if (id == null) {
            return null;
        }

        if (sistema != null) {
            try {
                Method m = sistema.getClass().getMethod("getNombreSocioPorId", String.class);
                Object r = m.invoke(sistema, id);
                if (r != null) {
                    return String.valueOf(r);
                }
            } catch (NoSuchMethodException ignored) {
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
        }

        // Solo cache (no pedir nombre al usuario)
        return getNombreCache(id);
    }

    private String getNombreCache(String id) {
        for (int i = 0; i < cacheIds.length; i++) {
            if (id.equals(cacheIds[i])) {
                return cacheNombres[i];
            }
        }
        return null;
    }

    private void setNombreCache(String id, String nombre) {
        for (int i = 0; i < cacheIds.length; i++) {
            if (cacheIds[i] == null || id.equals(cacheIds[i])) {
                cacheIds[i] = id;
                cacheNombres[i] = nombre;
                return;
            }
        }
    }

    // Chips y botones
    private JLabel crearChip(String txt, Color base) {
        JLabel l = new JLabel(txt);
        l.setOpaque(true);
        l.setBackground(base.darker());
        l.setForeground(base == COL_EXC ? Color.BLACK : Color.WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setBorder(new EmptyBorder(6, 10, 6, 10));
        return l;
    }

    private JButton crearBtnTab(String t) {
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
                b.setBackground(adjust(TAB_BG_ACTIVE, 1.08f));
            }
        });
        return b;
    }

    private JButton crearBtnRojo(String t) {
        JButton b = new JButton(t);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(COL_OCUP);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(COL_OCUP, 1.08f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(COL_OCUP);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(COL_OCUP, 0.92f));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                b.setBackground(adjust(COL_OCUP, 1.08f));
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

    // Pestañas custom
    private JComponent crearTabHeader(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(TAB_TXT);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(true);
        p.setBorder(new EmptyBorder(8, 16, 8, 16));
        p.add(lbl);

        p.putClientProperty("tabLabel", lbl);
        return p;
    }

    private void actualizarTabHeaders() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            Component c = tabs.getTabComponentAt(i);
            if (c instanceof JPanel p) {
                boolean selected = i == tabs.getSelectedIndex();
                p.setBackground(selected ? TAB_BG_ACTIVE : TAB_BG);
                JLabel lbl = (JLabel) p.getClientProperty("tabLabel");
                if (lbl != null) {
                    lbl.setForeground(TAB_TXT);
                }
            }
        }
    }

    // Diálogos oscuros
    private int darkConfirm(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        return JOptionPane.showConfirmDialog(this, estilizarHtml(msg), title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    private void darkInfo(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        JOptionPane.showMessageDialog(this, estilizarHtml(msg), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void darkWarn(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        JOptionPane.showMessageDialog(this, estilizarHtml(msg), title, JOptionPane.WARNING_MESSAGE);
    }

    private String darkInput(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        return JOptionPane.showInputDialog(this, estilizarHtml(msg), title, JOptionPane.QUESTION_MESSAGE);
    }

    private Object estilizarHtml(String msg) {
        return "<html><div style='color:#e6e6e6; font-family:Sans-Serif; font-size:12px; width:380px;'>"
                + msg + "</div></html>";
    }

    // Área de texto
    private JTextArea crearArea(String txt) {
        JTextArea a = new JTextArea(txt, 20, 60);
        a.setEditable(false);
        a.setWrapStyleWord(true);
        a.setLineWrap(true);
        a.setFont(new Font("Monospaced", Font.PLAIN, 13));
        a.setForeground(COL_TXT);
        a.setBackground(new Color(18, 18, 18));
        a.setBorder(new EmptyBorder(14, 14, 14, 14));
        return a;
    }

    // ---------- PERSISTENCIA (en memoria mientras corre la app) ----------
    private void cargarEstado() {
        if (STATE_READY && S_g1 != null && S_g2 != null && S_g3 != null) {
            copyInto(S_g1, g1);
            copyInto(S_g2, g2);
            copyInto(S_g3, g3);
            copyInto(S_g1IDs, g1IDs);
            copyInto(S_g2IDs, g2IDs);
            copyInto(S_g3IDs, g3IDs);
            if (S_cacheIds != null && S_cacheNombres != null) {
                System.arraycopy(S_cacheIds, 0, cacheIds, 0, Math.min(S_cacheIds.length, cacheIds.length));
                System.arraycopy(S_cacheNombres, 0, cacheNombres, 0, Math.min(S_cacheNombres.length, cacheNombres.length));
            }
        } else {
            guardarEstado(); // inicializa los estáticos con el estado por defecto
        }
    }

    private void guardarEstado() {
        S_g1 = deepCopy(g1);
        S_g2 = deepCopy(g2);
        S_g3 = deepCopy(g3);
        S_g1IDs = deepCopy(g1IDs);
        S_g2IDs = deepCopy(g2IDs);
        S_g3IDs = deepCopy(g3IDs);

        S_cacheIds = new String[cacheIds.length];
        S_cacheNombres = new String[cacheNombres.length];
        System.arraycopy(cacheIds, 0, S_cacheIds, 0, cacheIds.length);
        System.arraycopy(cacheNombres, 0, S_cacheNombres, 0, cacheNombres.length);

        STATE_READY = true;
    }

    // util de copias
    private static char[][] deepCopy(char[][] src) {
        char[][] out = new char[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, out[i], 0, src[0].length);
        }
        return out;
    }

    private static String[][] deepCopy(String[][] src) {
        String[][] out = new String[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, out[i], 0, src[0].length);
        }
        return out;
    }

    private static void copyInto(char[][] src, char[][] dst) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, src[0].length);
        }
    }

    private static void copyInto(String[][] src, String[][] dst) {
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, src[0].length);
        }
    }

    // ---- Refresco no-visual (reconstruye panel del nivel sin cambiar tema/estilo) ----
    private void refrescarNivel(int tabIndex) {
        if (tabIndex == 0) {
            tabs.setComponentAt(0, crearPanelNivel("P1", baseG1, g1, g1IDs));
        } else if (tabIndex == 1) {
            tabs.setComponentAt(1, crearPanelNivel("P2", baseG2, g2, g2IDs));
        } else if (tabIndex == 2) {
            tabs.setComponentAt(2, crearPanelNivel("P3", baseG3, g3, g3IDs));
        }
        // Mantener encabezados y estilos tal cual
        actualizarTabHeaders();
    }
}
