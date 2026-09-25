package edu.umg.programacion2.proyecto.ui;

import edu.umg.programacion2.proyecto.dao.EmpleadoDAO;
import edu.umg.programacion2.proyecto.modelo.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EmpleadoFrame extends JFrame {

    private final EmpleadoDAO dao = new EmpleadoDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDepto = new JTextField();
    private final JTextField txtCorreo = new JTextField(); // Mejora #1: Campo de texto adicional
    private final JTextField txtSalario = new JTextField();
    private final JTextField txtFecha = new JTextField();
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);

    public EmpleadoFrame() {
        setTitle("Gestión de Empleados - UMG");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Formulario superior con 7 filas
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));

        txtId.setEditable(false);
        formPanel.add(new JLabel("ID (Automático):"));
        formPanel.add(txtId);

        formPanel.add(new JLabel("Nombre Completo:"));
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Departamento:"));
        formPanel.add(txtDepto);

        formPanel.add(new JLabel("Correo Electrónico:"));
        formPanel.add(txtCorreo);

        formPanel.add(new JLabel("Salario Mensual (Q):"));
        formPanel.add(txtSalario);

        formPanel.add(new JLabel("Fecha Contratación (AAAA-MM-DD):"));
        formPanel.add(txtFecha);

        formPanel.add(new JLabel("Estado:"));
        formPanel.add(chkActivo);

        add(formPanel, BorderLayout.NORTH);

        // Tabla central con columna calculada de Antigüedad (Mejora #6)
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Departamento", "Correo", "Salario", "Fecha", "Antigüedad", "Activo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de botones inferior
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnNuevo = new JButton("Limpiar / Nuevo");
        JButton btnGuardar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");

        btnPanel.add(btnNuevo);
        btnPanel.add(btnGuardar);
        btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar);
        add(btnPanel, BorderLayout.SOUTH);

        // Eventos
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> registrarEmpleado());
        btnActualizar.addActionListener(e -> actualizarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());

        cargarDatos();
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        try {
            List<Empleado> empleados = dao.listarTodos();
            for (Empleado emp : empleados) {
                tableModel.addRow(new Object[]{
                        emp.getId(),
                        emp.getNombre(),
                        emp.getDepartamento(),
                        emp.getCorreo(),
                        String.format("%.2f", emp.getSalario()),
                        emp.getFechaContratacion(),
                        emp.getAntiguedadAnios() + " año(s)", // Mejora #6: Calculado en Java
                        emp.isActivo() ? "Sí" : "No"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarEmpleado() {
        try {
            String nombre = txtNombre.getText().trim();
            String depto = txtDepto.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || depto.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre, departamento y correo son obligatorios.");
                return;
            }

            if (!correo.contains("@") || !correo.contains(".")) {
                JOptionPane.showMessageDialog(this, "Ingrese un formato de correo válido (ejemplo: usuario@correo.com).");
                return;
            }

            double salario = Double.parseDouble(txtSalario.getText().trim());
            if (salario <= 0) {
                JOptionPane.showMessageDialog(this, "El salario debe ser mayor a cero.");
                return;
            }

            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            if (fecha.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "La fecha no puede ser futura.");
                return;
            }

            Empleado emp = new Empleado(nombre, depto, correo, fecha, salario, chkActivo.isSelected());
            dao.crear(emp);
            JOptionPane.showMessageDialog(this, "Empleado registrado con éxito.");
            limpiarFormulario();
            cargarDatos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un salario numérico válido.");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido (use AAAA-MM-DD).");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error BD: " + ex.getMessage());
        }
    }

    private void actualizarEmpleado() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla para editar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            String nombre = txtNombre.getText().trim();
            String depto = txtDepto.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || depto.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre, departamento y correo son obligatorios.");
                return;
            }

            if (!correo.contains("@") || !correo.contains(".")) {
                JOptionPane.showMessageDialog(this, "Ingrese un formato de correo válido (ejemplo: usuario@correo.com).");
                return;
            }

            double salario = Double.parseDouble(txtSalario.getText().trim());
            if (salario <= 0) {
                JOptionPane.showMessageDialog(this, "El salario debe ser mayor a cero.");
                return;
            }

            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            if (fecha.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "La fecha no puede ser futura.");
                return;
            }

            Empleado emp = new Empleado(id, nombre, depto, correo, fecha, salario, chkActivo.isSelected());
            boolean ok = dao.actualizar(emp);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Empleado actualizado con éxito.");
                limpiarFormulario();
                cargarDatos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminarEmpleado() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este empleado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idStr);
                dao.eliminarPorId(id);
                JOptionPane.showMessageDialog(this, "Empleado eliminado.");
                limpiarFormulario();
                cargarDatos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            }
        }
    }

    private void cargarSeleccion() {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtNombre.setText(tableModel.getValueAt(row, 1).toString());
            txtDepto.setText(tableModel.getValueAt(row, 2).toString());
            txtCorreo.setText(tableModel.getValueAt(row, 3).toString());
            txtSalario.setText(tableModel.getValueAt(row, 4).toString().replace(",", "."));
            txtFecha.setText(tableModel.getValueAt(row, 5).toString());
            chkActivo.setSelected("Sí".equalsIgnoreCase(tableModel.getValueAt(row, 7).toString()));
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtDepto.setText("");
        txtCorreo.setText("");
        txtSalario.setText("");
        txtFecha.setText("");
        chkActivo.setSelected(true);
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmpleadoFrame().setVisible(true));
    }
}