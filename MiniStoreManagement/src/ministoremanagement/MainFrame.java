package ministoremanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.*;
import java.util.Vector;

public class MainFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtCategory, txtPrice, txtStock, txtImageFile, txtSearch;

    public MainFrame() {
        // 1. Setup giao diện Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        setTitle("Mini Store Management System - Full Requirement Version");
        setSize(1150, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(45, 52, 54));
        headerPanel.setPreferredSize(new Dimension(1150, 60));
        JLabel lblTitle = new JLabel("QUẢN LÝ CHUỖI CỬA HÀNG MINI");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // --- KHU VỰC TÌM KIẾM ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(new EmptyBorder(10, 0, 0, 20));
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = new JButton("Tìm Kiếm");
        styleButton(btnSearch, new Color(52, 73, 94)); // Màu xanh đen
        btnSearch.addActionListener(e -> searchData());
        
        searchPanel.add(new JLabel("Tìm tên sản phẩm: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // --- BẢNG DỮ LIỆU ---
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return column != 1; }
        };
        
        model.addColumn("ID");              
        model.addColumn("Hình ảnh");        
        model.addColumn("Tên Sản Phẩm");    
        model.addColumn("Danh Mục");        
        model.addColumn("Đơn Giá");         
        model.addColumn("Tồn Kho");         
        model.addColumn("File Ảnh");        

        table = new JTable(model);
        table.setRowHeight(70); 
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setDefaultRenderer(new HeaderColor());

        // Config cột ảnh
        table.getColumnModel().getColumn(1).setCellRenderer(new ImageCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.removeColumn(table.getColumnModel().getColumn(6)); 

        // Căn giữa
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        // SỰ KIỆN CLICK CHUỘT VÀO BẢNG 
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtName.setText(model.getValueAt(row, 2).toString());
                    txtCategory.setText(model.getValueAt(row, 3).toString());
                    txtPrice.setText(model.getValueAt(row, 4).toString());
                    txtStock.setText(model.getValueAt(row, 5).toString());
                    // Cột 6 là cột ẩn chứa tên file
                    Object imgObj = model.getValueAt(row, 6);
                    txtImageFile.setText(imgObj != null ? imgObj.toString() : "");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        // Panel chứa cả Search và Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- KHU VỰC NHẬP LIỆU ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 30, 15, 30));
        bottomPanel.setBackground(new Color(245, 245, 245));

        JPanel inputsContainer = new JPanel(new GridLayout(2, 3, 30, 15));
        inputsContainer.setOpaque(false);

        txtName = new JTextField(); txtCategory = new JTextField();
        txtPrice = new JTextField(); txtStock = new JTextField();
        txtImageFile = new JTextField();

        inputsContainer.add(createInputPanel("Tên Sản Phẩm:", txtName));
        inputsContainer.add(createInputPanel("Danh Mục:", txtCategory));
        inputsContainer.add(createInputPanel("Đơn Giá (VNĐ):", txtPrice));
        inputsContainer.add(createInputPanel("Số Lượng:", txtStock));
        inputsContainer.add(createInputPanel("Tên File Ảnh:", txtImageFile));
        inputsContainer.add(new JPanel(null) {{ setOpaque(false); }}); 

        // --- KHU VỰC NÚT BẤM ( THÊM - SỬA - XÓA) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAdd = new JButton("Thêm");
        styleButton(btnAdd, new Color(46, 204, 113)); // Xanh lá
        btnAdd.addActionListener(e -> addProduct());

        JButton btnUpdate = new JButton("Sửa (Cập Nhật)"); // NÚT MỚI
        styleButton(btnUpdate, new Color(243, 156, 18)); // Vàng cam
        btnUpdate.addActionListener(e -> updateProduct());

        JButton btnDelete = new JButton("Xóa");
        styleButton(btnDelete, new Color(231, 76, 60)); // Đỏ
        btnDelete.addActionListener(e -> deleteProduct());

        JButton btnLoad = new JButton("Reset");
        styleButton(btnLoad, new Color(52, 152, 219)); // Xanh dương
        btnLoad.addActionListener(e -> loadData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate); // Thêm nút Sửa vào
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLoad);

        bottomPanel.add(inputsContainer, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        loadData();
    }

    // --- LOGIC TÌM KIẾM (SEARCH) ---
    private void searchData() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadData(); return; }
        
        model.setRowCount(0);
        // Tìm gần đúng (LIKE) theo tên sản phẩm
        String sql = "SELECT ID, Name, Category, Price, Stock, ImageFile FROM Products WHERE Name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, "%" + keyword + "%"); // Tìm tên có chứa từ khóa
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID")); row.add(""); 
                row.add(rs.getString("Name")); row.add(rs.getString("Category"));
                row.add(rs.getDouble("Price")); row.add(rs.getInt("Stock"));
                row.add(rs.getString("ImageFile"));
                model.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- LOGIC SỬA (UPDATE) ---
    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa trước!");
            return;
        }
        
        int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        String sql = "UPDATE Products SET Name=?, Category=?, Price=?, Stock=?, ImageFile=? WHERE ID=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, txtName.getText());
            pst.setString(2, txtCategory.getText());
            pst.setDouble(3, Double.parseDouble(txtPrice.getText()));
            pst.setInt(4, Integer.parseInt(txtStock.getText()));
            pst.setString(5, txtImageFile.getText());
            pst.setInt(6, id);
            
            pst.executeUpdate();
            loadData();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu khi sửa!");
        }
    }

    // --- CÁC HÀM CŨ (ADD, DELETE, LOAD...) ---
    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Chọn dòng để xóa!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Chắc chắn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        
        int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM Products WHERE ID=?")) {
            pst.setInt(1, id); pst.executeUpdate(); loadData();
            JOptionPane.showMessageDialog(this, "Đã xóa!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addProduct() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("INSERT INTO Products VALUES (?,?,?,?,?)")) {
            pst.setString(1, txtName.getText()); pst.setString(2, txtCategory.getText());
            pst.setDouble(3, Double.parseDouble(txtPrice.getText()));
            pst.setInt(4, Integer.parseInt(txtStock.getText()));
            pst.setString(5, txtImageFile.getText());
            pst.executeUpdate(); loadData();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!"); }
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ID, Name, Category, Price, Stock, ImageFile FROM Products")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID")); row.add(""); 
                row.add(rs.getString("Name")); row.add(rs.getString("Category"));
                row.add(rs.getDouble("Price")); row.add(rs.getInt("Stock"));
                row.add(rs.getString("ImageFile"));
                model.addRow(row);
            }
        } catch (Exception e) {}
    }

    private JPanel createInputPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(10, 5)); panel.setOpaque(false);
        JLabel label = new JLabel(labelText); label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setPreferredSize(new Dimension(100, 25)); 
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13)); textField.setPreferredSize(new Dimension(150, 30)); 
        panel.add(label, BorderLayout.WEST); panel.add(textField, BorderLayout.CENTER); 
        return panel;
    }

    private class HeaderColor extends DefaultTableCellRenderer {
        public HeaderColor() {
            setOpaque(true); setHorizontalAlignment(JLabel.CENTER);
            setBackground(new Color(230, 126, 34)); setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            super.getTableCellRendererComponent(t, v, s, f, r, c); return this;
        }
    }

    private class ImageCellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String filename = (String) model.getValueAt(row, 6); 
            label.setText(""); label.setIcon(null); label.setHorizontalAlignment(JLabel.CENTER);
            if (filename != null && !filename.isEmpty()) {
                URL imageURL = getClass().getResource("/resources/" + filename);
                if (imageURL != null) {
                    ImageIcon icon = new ImageIcon(imageURL);
                    Image scaledImage = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                } else label.setText("No Image");
            } else label.setText("No Image");
            if (isSelected) label.setBackground(table.getSelectionBackground());
            else label.setBackground(table.getBackground());
            return label;
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setPreferredSize(new Dimension(130, 35));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}
