package ar.edu.unlu.compilador;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IDE extends JFrame {
    private static final Color COLOR_FONDO = new Color(245, 247, 250);
    private static final Color COLOR_TEXTO = new Color(35, 44, 58);
    private static final Color COLOR_SECUNDARIO = new Color(103, 116, 135);
    private static final Color COLOR_BORDE = new Color(220, 226, 234);
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);

    private JTextArea textAreaCodigo;
    private JTextArea textAreaSalida;
    private JTable tablaSimbolos;
    private SymbolTableModel modeloTablaSimbolos;
    private JLabel etiquetaEstado;
    private File archivoActual;

    public IDE() {
        setTitle("Compilador - Teoría de la Computación I - Grupo 3 - Entrega 2 (Analizador Sintáctico)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.background", COLOR_FONDO);
        UIManager.put("TabbedPane.foreground", COLOR_TEXTO);
        UIManager.put("TabbedPane.contentAreaColor", Color.WHITE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setFocusable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 14));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panelPrincipal.add(crearEncabezado(), BorderLayout.NORTH);

        textAreaCodigo = new JTextArea();
        textAreaCodigo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textAreaCodigo.setForeground(COLOR_TEXTO);
        textAreaCodigo.setBackground(Color.WHITE);
        textAreaCodigo.setCaretColor(COLOR_PRIMARIO);
        textAreaCodigo.setMargin(new Insets(12, 12, 12, 12));
        textAreaCodigo.setTabSize(4);
        JScrollPane scrollCodigo = crearScrollPane(textAreaCodigo);

        textAreaSalida = new JTextArea();
        textAreaSalida.setEditable(false);
        textAreaSalida.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textAreaSalida.setForeground(COLOR_TEXTO);
        textAreaSalida.setBackground(new Color(250, 251, 253));
        textAreaSalida.setLineWrap(true);
        textAreaSalida.setWrapStyleWord(true);
        textAreaSalida.setMargin(new Insets(10, 12, 10, 12));
        JScrollPane scrollSalida = crearScrollPane(textAreaSalida);

        JPanel panelEditor = crearPanelSeccion("Código fuente", scrollCodigo);
        JPanel panelSalida = crearPanelSeccion("Salida de compilación", scrollSalida);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelEditor, panelSalida);
        splitPane.setResizeWeight(0.66);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(8);
        splitPane.setContinuousLayout(true);

        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        tabbedPane.addTab("Código", panelPrincipal);
        tabbedPane.addTab("Tabla de símbolos", crearPanelTablaSimbolos());

        setContentPane(tabbedPane);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout(0, 12));
        encabezado.setOpaque(false);

        JPanel titulos = new JPanel();
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.setOpaque(false);
        JLabel titulo = new JLabel("Compilador");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(COLOR_TEXTO);
        JLabel subtitulo = new JLabel("Analizador sintáctico · Teoría de la Computación I · Grupo 3");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_SECUNDARIO);
        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(subtitulo);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        JButton btnAbrir = crearBoton("Abrir archivo", false);
        JButton btnGuardar = crearBoton("Guardar", false);
        JButton btnCompilar = crearBoton("Compilar", true);
        btnAbrir.addActionListener(e -> abrirArchivo());
        btnGuardar.addActionListener(e -> guardarArchivo());
        btnCompilar.addActionListener(e -> compilar());
        acciones.add(btnAbrir);
        acciones.add(btnGuardar);
        acciones.add(btnCompilar);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setOpaque(false);
        inferior.add(acciones, BorderLayout.WEST);
        etiquetaEstado = new JLabel("Listo para compilar");
        etiquetaEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        etiquetaEstado.setForeground(COLOR_SECUNDARIO);
        inferior.add(etiquetaEstado, BorderLayout.EAST);

        encabezado.add(titulos, BorderLayout.NORTH);
        encabezado.add(inferior, BorderLayout.SOUTH);
        return encabezado;
    }

    private JPanel crearPanelSeccion(String titulo, JScrollPane contenido) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        etiqueta.setForeground(COLOR_TEXTO);
        panel.add(etiqueta, BorderLayout.NORTH);
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelTablaSimbolos() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Tabla de símbolos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(COLOR_TEXTO);
        JLabel subtitulo = new JLabel("Identificadores y constantes encontrados durante el análisis");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitulo.setForeground(COLOR_SECUNDARIO);

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(subtitulo);

        modeloTablaSimbolos = new SymbolTableModel();
        tablaSimbolos = new JTable(modeloTablaSimbolos) {
            @Override
            public String getToolTipText(MouseEvent event) {
                int fila = rowAtPoint(event.getPoint());
                int columna = columnAtPoint(event.getPoint());
                if (fila >= 0 && columna >= 0) {
                    Object valor = getValueAt(fila, columna);
                    return valor == null ? null : valor.toString();
                }
                return null;
            }
        };
        tablaSimbolos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaSimbolos.setForeground(COLOR_TEXTO);
        tablaSimbolos.setBackground(Color.WHITE);
        tablaSimbolos.setRowHeight(30);
        tablaSimbolos.setFillsViewportHeight(true);
        tablaSimbolos.setShowGrid(true);
        tablaSimbolos.setGridColor(new Color(229, 233, 239));
        tablaSimbolos.setIntercellSpacing(new Dimension(1, 1));
        tablaSimbolos.setSelectionBackground(new Color(219, 234, 254));
        tablaSimbolos.setSelectionForeground(COLOR_TEXTO);
        tablaSimbolos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaSimbolos.setRowSelectionAllowed(true);
        tablaSimbolos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeaderStyle.configurar(tablaSimbolos);
        int[] anchos = {230, 150, 140, 400, 100};
        for (int i = 0; i < anchos.length; i++) {
            tablaSimbolos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JScrollPane scrollTabla = crearScrollPane(tablaSimbolos);
        scrollTabla.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel tarjetaTabla = new JPanel(new BorderLayout());
        tarjetaTabla.setBackground(Color.WHITE);
        tarjetaTabla.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        tarjetaTabla.add(scrollTabla, BorderLayout.CENTER);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(tarjetaTabla, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearScrollPane(Component componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JButton crearBoton(String texto, boolean primario) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primario ? COLOR_PRIMARIO : COLOR_BORDE),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        boton.setBackground(primario ? COLOR_PRIMARIO : Color.WHITE);
        boton.setForeground(primario ? Color.WHITE : COLOR_TEXTO);
        boton.setOpaque(true);
        return boton;
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoActual))) {
                StringBuilder contenido = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
                textAreaCodigo.setText(contenido.toString());
                etiquetaEstado.setText("Archivo abierto: " + archivoActual.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        if (archivoActual == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));

            int resultado = fileChooser.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivoActual = fileChooser.getSelectedFile();
                if (!archivoActual.getName().endsWith(".txt")) {
                    archivoActual = new File(archivoActual.getPath() + ".txt");
                }
            } else {
                return;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoActual))) {
            writer.write(textAreaCodigo.getText());
            etiquetaEstado.setText("Archivo guardado: " + archivoActual.getName());
            JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void compilar() {
        String codigo = textAreaCodigo.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay código para compilar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        textAreaSalida.setText("");
        modeloTablaSimbolos.setSimbolos(Collections.emptyList());

        Lexico lexer = null;
        Sintactico parser = null;

        try {
            lexer = new Lexico(new StringReader(codigo));
            parser = new Sintactico(lexer);
            parser.parse();
        } catch (Exception ex) {
            if (parser == null) {
                textAreaSalida.setText("Error durante la compilación: " + ex.getMessage());
                etiquetaEstado.setText("Error durante la compilación");
                return;
            }
        }

        StringBuilder salida = new StringBuilder();

        for (String linea : parser.getReglas()) {
            salida.append(linea).append("\n");
        }

        for (String error : lexer.errores) {
            salida.append(error).append("\n");
        }
        for (String error : parser.getErroresSintacticos()) {
            salida.append(error).append("\n");
        }
        List<String> semanticos = parser.getErroresSemanticos();
        for (String error : semanticos) {
            salida.append(error).append("\n");
        }

        int totalErrores = lexer.errores.size()
                + parser.getErroresSintacticos().size()
                + semanticos.size();
        if (totalErrores == 0) {
            salida.append("Compilación exitosa\n");
            etiquetaEstado.setText("Compilación exitosa");
        } else {
            salida.append("Compilación con ").append(totalErrores).append(" error(es)\n");
            etiquetaEstado.setText("Compilación finalizada con " + totalErrores + " error(es)");
        }

        textAreaSalida.setText(salida.toString());

        List<Simbolo> simbolos = new ArrayList<>();
        for (Map.Entry<String, Simbolo> entry : lexer.tablaSimbolos.getEntries()) {
            simbolos.add(entry.getValue());
        }
        modeloTablaSimbolos.setSimbolos(simbolos);

        try {
            lexer.tablaSimbolos.escribirArchivo("ts.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar ts.txt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class SymbolTableModel extends AbstractTableModel {
        private final String[] columnas = {"Nombre", "Token", "Tipo", "Valor", "Longitud"};
        private List<Simbolo> simbolos = Collections.emptyList();

        private void setSimbolos(List<Simbolo> simbolos) {
            this.simbolos = new ArrayList<>(simbolos);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return simbolos.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int columna) {
            return columnas[columna];
        }

        @Override
        public Object getValueAt(int fila, int columna) {
            Simbolo simbolo = simbolos.get(fila);
            switch (columna) {
                case 0:
                    return simbolo.getNombre();
                case 1:
                    return simbolo.getToken();
                case 2:
                    return simbolo.getTipo().isEmpty() ? "—" : simbolo.getTipo();
                case 3:
                    return simbolo.getValor().isEmpty() ? "—" : simbolo.getValor();
                case 4:
                    return simbolo.getLongitud().isEmpty() ? "—" : simbolo.getLongitud();
                default:
                    throw new IndexOutOfBoundsException("Columna inválida: " + columna);
            }
        }
    }

    private static class JTableHeaderStyle {
        private static void configurar(JTable tabla) {
            tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
            tabla.getTableHeader().setForeground(COLOR_TEXTO);
            tabla.getTableHeader().setBackground(new Color(238, 242, 247));
            tabla.getTableHeader().setPreferredSize(new Dimension(0, 36));
            tabla.getTableHeader().setReorderingAllowed(false);

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value, boolean selected, boolean focused, int row, int column) {
                    super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    setToolTipText(value == null ? null : value.toString());
                    if (!selected) {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                        setForeground(COLOR_TEXTO);
                    }
                    return this;
                }
            };
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IDE ide = new IDE();
            ide.setVisible(true);
        });
    }
}
