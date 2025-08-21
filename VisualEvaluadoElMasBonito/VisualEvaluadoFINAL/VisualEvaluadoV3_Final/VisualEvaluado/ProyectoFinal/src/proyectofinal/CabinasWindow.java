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
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

public class CabinasWindow extends JFrame {

    // ======= THEME (igual al estilo VIP del parqueo) =======
    private static final Color COL_BG = new Color(12, 12, 12);
    private static final Color COL_PANEL = new Color(22, 22, 22);
    private static final Color COL_TXT = new Color(230, 230, 230);

    private static final Color COL_LIBRE = new Color(46, 125, 50);   // verde
    private static final Color COL_OCUP = new Color(198, 40, 40);    // rojo
    private static final Color TAB_BG = new Color(48, 48, 48);
    private static final Color TAB_BG_ACTIVE = new Color(70, 70, 70);
    private static final Color TAB_TXT = Color.WHITE;

    // ======= DEFAULTS =======
    private static final int DEFAULT_CABINAS = 4;
    private static final int DEFAULT_H_INI = 9;   // 09:00
    private static final int DEFAULT_H_FIN = 18;  // 18:00 (6pm)  -> turnos de una hora inclusivos por la hora "de inicio"

    // ======= PERSISTENCIA ESTÁTICA (mientras el programa esté vivo) =======    // (no se resetea al cerrar esta ventana; solo al cerrar toda la app)
    private static boolean STATE_READY = false;
    private static int S_cabinasCount;
    private static int S_horaInicio24;
    private static int S_horaFin24;
    private static String[] S_horasEtiquetas;  // etiquetas "9am", "10am", ...
    private static char[][] S_estado;          // 'L' libre, 'O' ocupado
    private static String[][] S_ids;           // guarda ID del socio
    private static String[] S_cacheIds;        // cache ID -> nombre para consultas
    private static String[] S_cacheNombres;

    // ======= INSTANCIA =======
    private final SistemaGimnasio sistema;
    private int cabinasCount;
    private String[] horas;
    private char[][] estado;
    private String[][] ids;
    private String[] cacheIds = new String[400];
    private String[] cacheNoms = new String[400];

    // UI
    private JPanel gridPanel;
    private JButton[][] botones;
    private JLabel chipLibres, chipOcupados;
    private JTabbedPane tabs; // solo para headers bonitos de acciones

    public CabinasWindow(SistemaGimnasio sistema) {
        this.sistema = sistema;
        setTitle("Cabinas Insonorizadas — Reservas por Hora");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Root
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(COL_BG);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        // Cargar o inicializar estado estático
        cargarEstadoEstatico();

        // Copiar a instancia (defensivo)
        this.cabinasCount = S_cabinasCount;
        this.horas = copyOf(S_horasEtiquetas);
        this.estado = deepCopy(S_estado);
        this.ids = deepCopy(S_ids);
        if (S_cacheIds != null) {
            System.arraycopy(S_cacheIds, 0, cacheIds, 0, Math.min(S_cacheIds.length, cacheIds.length));
        }
        if (S_cacheNombres != null) {
            System.arraycopy(S_cacheNombres, 0, cacheNoms, 0, Math.min(S_cacheNombres.length, cacheNoms.length));
        }

        // Header
        root.add(crearHeader(), BorderLayout.NORTH);

        // Centro: grilla
        gridPanel = new JPanel();
        gridPanel.setBackground(COL_PANEL);
        gridPanel.setBorder(new EmptyBorder(10, 10, 12, 10));
        gridPanel.setLayout(new GridLayout(cabinasCount + 1, horas.length + 1, 6, 6));
        construirGrilla(); // crea botones[][]

        JScrollPane sp = new JScrollPane(gridPanel);
        sp.setBorder(null);
        sp.getViewport().setBackground(COL_PANEL);
        root.add(sp, BorderLayout.CENTER);

        // Acciones (abajo)
        root.add(crearAcciones(), BorderLayout.SOUTH);

        actualizarChips();

        // Guardar al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarEstadoEstatico(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                guardarEstadoEstatico(true);
            }
        });

        setVisible(true);
    }

    // ======= HEADER / CHIPS =======
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Panel con título + slogan
        JPanel tituloPanel = new JPanel(new GridLayout(2, 1));
        tituloPanel.setOpaque(false);

        // 🔹 Título principal (VIP)
        JLabel titulo = new JLabel("CABINAS INSONORIZADAS", SwingConstants.LEFT);
        titulo.setForeground(new Color(255, 255, 255)); // dorado elegante
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setBorder(new EmptyBorder(0, 6, 0, 6));

        // 🔹 Slogan
        JLabel slogan = new JLabel("Un espacio solo para ti", SwingConstants.LEFT);
        slogan.setForeground(new Color(192, 192, 192)); // gris plata
        slogan.setFont(new Font("SansSerif", Font.ITALIC, 16));
        slogan.setBorder(new EmptyBorder(0, 8, 10, 6));

        tituloPanel.add(titulo);
        tituloPanel.add(slogan);

        header.add(tituloPanel, BorderLayout.NORTH);

        // 🔹 Chips (Libres / Ocupados)
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        chips.setOpaque(false);
        chipLibres = crearChip("Libres: 0", COL_LIBRE);
        chipOcupados = crearChip("Ocupados: 0", COL_OCUP);
        chips.add(chipLibres);
        chips.add(chipOcupados);
        header.add(chips, BorderLayout.CENTER);

        return header;
    }

    private JLabel crearChip(String txt, Color base) {
        JLabel l = new JLabel(txt);
        l.setOpaque(true);
        l.setBackground(base.darker());
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setBorder(new EmptyBorder(6, 10, 6, 10));
        return l;
    }

    // ======= ACCIONES =======
    private JPanel crearAcciones() {
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);

        tabs = new JTabbedPane();
        tabs.setBackground(COL_BG);
        tabs.setForeground(COL_TXT);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });

        // Panel acciones
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setOpaque(false);

        JButton btnConsultar = crearBtn("Consultar Reservas"); // <<<< Texto solicitado
        JButton btnBuscar = crearBtn("Buscar por ID");
        JButton btnConfig = crearBtn("Modificar distribución");
        JButton btnSalir = crearBtnRojo("Salir");

        btnConsultar.addActionListener(e -> consultarReservas());
        btnBuscar.addActionListener(e -> buscarPorID());
        btnConfig.addActionListener(e -> modificarDistribucion());
        btnSalir.addActionListener(e -> {
            guardarEstadoEstatico(true);
            dispose();  // <<<< SE SALE DE LA VENTANA
        });

        actions.add(btnConsultar);
        actions.add(btnBuscar);
        actions.add(btnConfig);
        actions.add(btnSalir);

        south.add(actions, BorderLayout.CENTER);
        return south;
    }

    private JButton crearBtn(String t) {
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

    // ======= GRID =======
    private void construirGrilla() {
        gridPanel.removeAll();

        // Esquina superior izquierda
        gridPanel.add(crearHdr(""));

        // Encabezados de columnas (horas)
        for (String h : horas) {
            gridPanel.add(crearHdr(h));
        }

        // Filas de cabinas
        botones = new JButton[cabinasCount][horas.length];

        for (int i = 0; i < cabinasCount; i++) {
            gridPanel.add(crearHdr("Cabina " + (i + 1)));
            for (int j = 0; j < horas.length; j++) {
                JButton btn = new JButton();
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setFont(new Font("SansSerif", Font.BOLD, 13));
                botones[i][j] = btn;

                aplicarEstilo(btn, estado[i][j]);
                btn.setToolTipText(toolTip(i, j));

                final int fi = i, fj = j;
                btn.addActionListener(e -> clickCelda(fi, fj, btn));

                gridPanel.add(btn);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JLabel crearHdr(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(new Color(200, 200, 200));
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
    }

    private void aplicarEstilo(JButton b, char e) {
        if (e == 'O') {
            b.setBackground(COL_OCUP);
            b.setForeground(Color.WHITE);
            b.setText("Ocupado");
        } else {
            b.setBackground(COL_LIBRE);
            b.setForeground(Color.WHITE);
            b.setText("Libre");
        }
    }

    private String toolTip(int i, int j) {
        if (estado[i][j] == 'O') {
            String id = ids[i][j];
            String nom = obtenerNombreSocio(id);
            if (nom != null) {
                return "Reservado por: " + nom + " (ID " + id + ")";
            } else {
                return "Reservado (ID " + id + ")";
            }
        }
        return "Disponible";
    }

    private void clickCelda(int i, int j, JButton btn) {
        if (estado[i][j] == 'O') {
            if (confirmDark("Liberar turno", "¿Desea liberar esta cabina en el horario " + horas[j] + "?") == JOptionPane.YES_OPTION) {
                estado[i][j] = 'L';
                ids[i][j] = null;
                aplicarEstilo(btn, 'L');
                btn.setToolTipText(toolTip(i, j));
                actualizarChips();
                guardarEstadoEstatico(false);
            }
            return;
        }

        String id = inputDark("Reservar", "Ingrese el ID del socio para reservar la Cabina " + (i + 1) + " a las " + horas[j] + ":");
        if (id == null) {
            return;
        }
        id = id.trim();
        if (id.isEmpty()) {
            return;
        }

        // Guardamos ID (en consulta se mostrará NOMBRE)
        estado[i][j] = 'O';
        ids[i][j] = id;

        // Intentamos precargar nombre (control socios o preguntar)
        obtenerNombreSocio(id);

        aplicarEstilo(btn, 'O');
        btn.setToolTipText(toolTip(i, j));
        actualizarChips();
        guardarEstadoEstatico(false);
    }

    private void actualizarChips() {
        int libres = 0, ocup = 0;
        for (int i = 0; i < cabinasCount; i++) {
            for (int j = 0; j < horas.length; j++) {
                if (estado[i][j] == 'O') {
                    ocup++;
                } else {
                    libres++;
                }
            }
        }
        chipLibres.setText("Libres: " + libres);
        chipOcupados.setText("Ocupados: " + ocup);
    }

    // ======= CONSULTAS =======
    private void consultarReservas() {
        StringBuilder sb = new StringBuilder();
        sb.append("RESERVAS ACTIVAS (se muestra NOMBRE; se guarda ID)\n\n");
        boolean alguna = false;

        for (int i = 0; i < cabinasCount; i++) {
            boolean filaImpreso = false;
            for (int j = 0; j < horas.length; j++) {
                if (estado[i][j] == 'O') {
                    if (!filaImpreso) {
                        sb.append("Cabina ").append(i + 1).append(":\n");
                        filaImpreso = true;
                    }
                    String id = ids[i][j];
                    String nom = obtenerNombreSocio(id);
                    sb.append("  · ").append(horas[j]).append("  —  ")
                            .append(nom != null ? nom : "(sin nombre)")
                            .append("  [ID: ").append(id).append("]\n");
                    alguna = true;
                }
            }
            if (filaImpreso) {
                sb.append("\n");
            }
        }

        if (!alguna) {
            sb.append("(no hay reservas activas)\n");
        }

        JTextArea area = new JTextArea(sb.toString(), 20, 60);
        area.setEditable(false);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setForeground(COL_TXT);
        area.setBackground(new Color(18, 18, 18));
        area.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane sp = new JScrollPane(area);
        sp.getViewport().setBackground(COL_BG);
        sp.setBorder(null);

        JOptionPane.showMessageDialog(this, sp, "Consulta de reservas", JOptionPane.PLAIN_MESSAGE);
    }

    private void buscarPorID() {
        // Debe SOLO pedir el ID del socio (sin pedir nombre extra)
        String id = inputDark("Buscar por ID", "Ingrese el ID del socio:");
        if (id == null) {
            return;
        }
        id = id.trim();
        if (id.isEmpty()) {
            return;
        }

        // Usar únicamente el cache para el nombre (no preguntar nada aquí)
        String nom = getNombreCache(id);

        StringBuilder sb = new StringBuilder();
        sb.append("Resultados para ");
        if (nom != null) {
            sb.append(nom).append(" (ID ").append(id).append(")\n\n");
        } else {
            sb.append("ID ").append(id).append("\n\n");
        }

        boolean found = false;
        for (int i = 0; i < cabinasCount; i++) {
            for (int j = 0; j < horas.length; j++) {
                if (estado[i][j] == 'O' && id.equals(ids[i][j])) {
                    sb.append("· Cabina ").append(i + 1).append(" — ").append(horas[j]).append("\n");
                    found = true;
                }
            }
        }
        if (!found) {
            sb.append("(sin reservas)");
        }

        infoDark("Búsqueda", sb.toString());
    }

    // ======= MODIFICAR DISTRIBUCIÓN =======
    private void modificarDistribucion() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COL_BG);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.EAST;

        JLabel lCab = new JLabel("Cantidad de cabinas (1-12): ");
        lCab.setForeground(COL_TXT);
        p.add(lCab, gc);

        gc.gridx = 1;
        SpinnerNumberModel mCab = new SpinnerNumberModel(cabinasCount, 1, 12, 1);
        JSpinner spCab = new JSpinner(mCab);
        p.add(spCab, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        JLabel lIni = new JLabel("Hora inicio (0-23): ");
        lIni.setForeground(COL_TXT);
        p.add(lIni, gc);

        gc.gridx = 1;
        int curIni = S_horaInicio24 > 0 ? S_horaInicio24 : DEFAULT_H_INI;
        JSpinner spIni = new JSpinner(new SpinnerNumberModel(curIni, 0, 23, 1));
        p.add(spIni, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        JLabel lFin = new JLabel("Hora fin (1-24): ");
        lFin.setForeground(COL_TXT);
        p.add(lFin, gc);

        gc.gridx = 1;
        int curFin = S_horaFin24 > 0 ? S_horaFin24 : DEFAULT_H_FIN;
        JSpinner spFin = new JSpinner(new SpinnerNumberModel(curFin, 1, 24, 1));
        p.add(spFin, gc);

        int r = JOptionPane.showConfirmDialog(this, p, "Modificar distribución", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }

        int nuevoCab = (Integer) spCab.getValue();
        int hIni = (Integer) spIni.getValue();
        int hFin = (Integer) spFin.getValue();

        if (hFin <= hIni) {
            warnDark("Configuración inválida", "La hora fin debe ser mayor que la hora inicio.");
            return;
        }

        String[] nuevasHoras = generarEtiquetasHoras(hIni, hFin);
        // Crear nuevas estructuras preservando coincidencias
        char[][] nuevoEstado = new char[nuevoCab][nuevasHoras.length];
        String[][] nuevoIds = new String[nuevoCab][nuevasHoras.length];
        for (int i = 0; i < nuevoCab; i++) {
            for (int j = 0; j < nuevasHoras.length; j++) {
                nuevoEstado[i][j] = 'L';
            }
        }

        // Mapear por nombre de hora y por índice de cabina (intersección)
        for (int i = 0; i < Math.min(cabinasCount, nuevoCab); i++) {
            for (int j = 0; j < horas.length; j++) {
                String h = horas[j];
                int idx = indexOf(nuevasHoras, h);
                if (idx >= 0) {
                    nuevoEstado[i][idx] = estado[i][j];
                    nuevoIds[i][idx] = ids[i][j];
                }
            }
        }

        // Reemplazar estructuras
        cabinasCount = nuevoCab;
        horas = nuevasHoras;
        estado = nuevoEstado;
        ids = nuevoIds;

        // Reconstruir UI
        gridPanel.setLayout(new GridLayout(cabinasCount + 1, horas.length + 1, 6, 6));
        construirGrilla();
        actualizarChips();

        // Guardar persistencia
        S_cabinasCount = cabinasCount;
        S_horaInicio24 = hIni;
        S_horaFin24 = hFin;
        S_horasEtiquetas = copyOf(horas);
        guardarEstadoEstatico(false);

        infoDark("Distribución actualizada", "Se aplicaron los cambios de distribución.");
    }

    // ======= NOMBRE POR ID (usa reflection a SistemaGimnasio/ControlSocios y cache) =======    // (esta SÍ puede preguntar el nombre cuando haga falta, pero NO se usa al buscar)
    private String obtenerNombreSocio(String id) {
        if (id == null) {
            return null;
        }

        // 1) Intento directo: sistema.getNombreSocioPorId(String)
        if (sistema != null) {
            try {
                Method m = sistema.getClass().getMethod("getNombreSocioPorId", String.class);
                Object r = m.invoke(sistema, id);
                if (r != null) {
                    String nom = String.valueOf(r);
                    setNombreCache(id, nom);
                    guardarEstadoEstatico(false);
                    return nom;
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
                                String nom = String.valueOf(r2);
                                setNombreCache(id, nom);
                                guardarEstadoEstatico(false);
                                return nom;
                            }
                        } catch (NoSuchMethodException ignored2) {
                            /* continuar */ }
                    }
                } catch (Exception ignored3) {
                    /* continuar */ }
            } catch (Exception ignored) {
                /* continuar */ }
        }

        // 3) Cache local
        String c = getNombreCache(id);
        if (c != null) {
            return c;
        }

        // 4) Preguntar una vez (para consulta futura)
        String nom = inputDark("Nombre del socio", "Ingrese el nombre para el ID " + id + " (solo para mostrar en consultas):");
        if (nom != null && !nom.trim().isEmpty()) {
            nom = nom.trim();
            setNombreCache(id, nom);
            guardarEstadoEstatico(false);
            return nom;
        }
        return null;
    }

    private String getNombreCache(String id) {
        for (int i = 0; i < cacheIds.length; i++) {
            if (id.equals(cacheIds[i])) {
                return cacheNoms[i];
            }
        }
        return null;
    }

    private void setNombreCache(String id, String nom) {
        for (int i = 0; i < cacheIds.length; i++) {
            if (cacheIds[i] == null || id.equals(cacheIds[i])) {
                cacheIds[i] = id;
                cacheNoms[i] = nom;
                return;
            }
        }
    }

    // ======= PERSISTENCIA ESTÁTICA (en memoria del proceso) =======    // (se conserva al cerrar/abrir esta ventana; se pierde al cerrar el programa)
    private void cargarEstadoEstatico() {
        if (STATE_READY && S_estado != null && S_ids != null && S_horasEtiquetas != null && S_cabinasCount > 0) {
            return; // ya está listo
        }

        // Inicial por defecto
        S_cabinasCount = DEFAULT_CABINAS;
        S_horaInicio24 = DEFAULT_H_INI;
        S_horaFin24 = DEFAULT_H_FIN;
        S_horasEtiquetas = generarEtiquetasHoras(S_horaInicio24, S_horaFin24);

        S_estado = new char[S_cabinasCount][S_horasEtiquetas.length];
        S_ids = new String[S_cabinasCount][S_horasEtiquetas.length];
        for (int i = 0; i < S_cabinasCount; i++) {
            for (int j = 0; j < S_horasEtiquetas.length; j++) {
                S_estado[i][j] = 'L';
            }
        }
        S_cacheIds = new String[400];
        S_cacheNombres = new String[400];

        STATE_READY = true;
    }

    private void guardarEstadoEstatico(boolean copyFromInstance) {
        if (copyFromInstance) {
            // copiar lo actual a los estáticos
            S_cabinasCount = cabinasCount;
            S_horasEtiquetas = copyOf(horas);
            S_estado = deepCopy(estado);
            S_ids = deepCopy(ids);
            S_horaInicio24 = S_horaInicio24 > 0 ? S_horaInicio24 : DEFAULT_H_INI;
            S_horaFin24 = S_horaFin24 > 0 ? S_horaFin24 : DEFAULT_H_FIN;

            if (S_cacheIds == null || S_cacheNombres == null) {
                S_cacheIds = new String[400];
                S_cacheNombres = new String[400];
            }
            System.arraycopy(cacheIds, 0, S_cacheIds, 0, Math.min(cacheIds.length, S_cacheIds.length));
            System.arraycopy(cacheNoms, 0, S_cacheNombres, 0, Math.min(cacheNoms.length, S_cacheNombres.length));
        } else {
            // asegurar que lo estático quede sincronizado
            S_cabinasCount = cabinasCount;
            S_horasEtiquetas = copyOf(horas);
            S_estado = deepCopy(estado);
            S_ids = deepCopy(ids);
            if (S_cacheIds == null || S_cacheNombres == null) {
                S_cacheIds = new String[400];
                S_cacheNombres = new String[400];
            }
            System.arraycopy(cacheIds, 0, S_cacheIds, 0, Math.min(cacheIds.length, S_cacheIds.length));
            System.arraycopy(cacheNoms, 0, S_cacheNombres, 0, Math.min(cacheNoms.length, S_cacheNombres.length));
        }
        STATE_READY = true;
    }

    // ======= UTIL =======
    private static String[] generarEtiquetasHoras(int hIni24, int hFin24Inclusivo) {
        // Turnos de una hora, inclusive: 9-18 => 10 slots (9,10,11,12,13,14,15,16,17,18)
        int slots = (hFin24Inclusivo - hIni24) + 1;
        if (slots < 1) {
            slots = 1;
        }
        String[] out = new String[slots];
        for (int k = 0; k < slots; k++) {
            int h = hIni24 + k;
            out[k] = etiquetaHora(h);
        }
        return out;
    }

    private static String etiquetaHora(int h24) {
        int h = h24 % 24;
        if (h == 0) {
            return "12am";
        }
        if (h == 12) {
            return "12pm";
        }
        if (h < 12) {
            return h + "am";
        }
        return (h - 12) + "pm";
    }

    private static int indexOf(String[] arr, String v) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(v)) {
                return i;
            }
        }
        return -1;
    }

    private static String[] copyOf(String[] src) {
        String[] out = new String[src.length];
        System.arraycopy(src, 0, out, 0, src.length);
        return out;
    }

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

    // ======= DIÁLOGOS DARK =======    // (mismo look&feel que parqueo)
    private int confirmDark(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        return JOptionPane.showConfirmDialog(this, html(msg), title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    private void infoDark(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        JOptionPane.showMessageDialog(this, html(msg), title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void warnDark(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        JOptionPane.showMessageDialog(this, html(msg), title, JOptionPane.WARNING_MESSAGE);
    }

    private String inputDark(String title, String msg) {
        UIManager.put("OptionPane.background", COL_BG);
        UIManager.put("Panel.background", COL_BG);
        UIManager.put("OptionPane.messageForeground", COL_TXT);
        return JOptionPane.showInputDialog(this, html(msg), title, JOptionPane.QUESTION_MESSAGE);
    }

    private Object html(String msg) {
        return "<html><div style='color:#e6e6e6; font-family:Sans-Serif; font-size:12px; width:420px;'>" + msg + "</div></html>";
    }
}
