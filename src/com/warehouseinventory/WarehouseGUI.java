package com.warehouseinventory;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class WarehouseGUI extends JFrame {

    private Warehouse warehouse = new Warehouse();
    private static final String DATA_FILE = "inventory.csv";


    private JTextField skuField, nameField, catField, qtyField, locField;
    private JTable table;
    private DefaultTableModel model;

    public WarehouseGUI() {
        setTitle("Warehouse Stock Locator System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadDataOnStart();
        initUI();
    }


    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("JYOSHINI SRI S   2117240080051", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setForeground(new Color(0, 70, 140)); // optional color
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        skuField = new JTextField();
        nameField = new JTextField();
        catField = new JTextField();
        qtyField = new JTextField();
        locField = new JTextField();

        formPanel.add(new JLabel("SKU:"));
        formPanel.add(skuField);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(catField);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(qtyField);

        formPanel.add(new JLabel("Location (A1-R2-B3):"));
        formPanel.add(locField);


        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 10, 10));

        JButton addBtn = new JButton("Add");
        JButton removeBtn = new JButton("Remove");
        JButton moveBtn = new JButton("Move");
        JButton searchBtn = new JButton("Search");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(moveBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(loadBtn);


        model = new DefaultTableModel(new String[]{"SKU", "Name", "Category", "Quantity", "Location"}, 0);
        table = new JTable(model);
        refreshTable();

        mainPanel.add(formPanel, BorderLayout.WEST);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);


        addBtn.addActionListener(e -> addItem());
        removeBtn.addActionListener(e -> removeItem());
        moveBtn.addActionListener(e -> moveItem());
        searchBtn.addActionListener(e -> searchItem());
        saveBtn.addActionListener(e -> saveData());
        loadBtn.addActionListener(e -> loadData());
    }


    private void loadDataOnStart() {
        try {
            CSVStorage.loadFromFile(warehouse, DATA_FILE);
        } catch (IOException ignored) {}
    }


    private void addItem() {
        try {
            String sku = skuField.getText().trim();
            String name = nameField.getText().trim();
            String cat = catField.getText().trim();
            int qty = Integer.parseInt(qtyField.getText().trim());
            Location loc = Location.parse(locField.getText().trim());

            if (sku.isEmpty()) {
                JOptionPane.showMessageDialog(this, "SKU cannot be empty.");
                return;
            }

            Item item = new Item(sku, name, cat, qty, loc);
            if (warehouse.addItem(item)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Item Added.");
            } else {
                JOptionPane.showMessageDialog(this, "SKU Already Exists!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input!");
        }
    }

    private void removeItem() {
        String sku = JOptionPane.showInputDialog(this, "Enter SKU to remove:");
        if (sku != null && warehouse.removeItem(sku)) {
            refreshTable();
            JOptionPane.showMessageDialog(this, "Item Removed.");
        } else {
            JOptionPane.showMessageDialog(this, "Item Not Found.");
        }
    }


    private void moveItem() {
        String sku = JOptionPane.showInputDialog(this, "Enter SKU to move:");
        if (sku == null) return;

        Item item = warehouse.findItemBySKU(sku);
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Item Not Found.");
            return;
        }

        String newLoc = JOptionPane.showInputDialog(this, "Enter New Location (A1-R2-B3):");
        if (newLoc != null) {
            Location loc = Location.parse(newLoc);
            warehouse.moveItem(sku, loc);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Item Moved.");
        }
    }


    private void searchItem() {
        String input = JOptionPane.showInputDialog(this, "Search by SKU / Name / Category:");
        if (input == null) return;

        String q = input.toLowerCase();
        StringBuilder result = new StringBuilder();

        for (Item i : warehouse.listAllItems()) {
            if (i.getSku().toLowerCase().contains(q) ||
                    i.getName().toLowerCase().contains(q) ||
                    i.getCategory().toLowerCase().contains(q)) {

                result.append(i).append("\n");
            }
        }

        if (result.length() == 0)
            result.append("No results found.");

        JOptionPane.showMessageDialog(this, result.toString());
    }


    private void saveData() {
        try {
            CSVStorage.saveToFile(warehouse, DATA_FILE);
            JOptionPane.showMessageDialog(this, "Data Saved Successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Save Failed.");
        }
    }


    private void loadData() {
        try {
            CSVStorage.loadFromFile(warehouse, DATA_FILE);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Data Loaded.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Load Failed.");
        }
    }


    private void refreshTable() {
        model.setRowCount(0);
        for (Item item : warehouse.listAllItems()) {
            model.addRow(new Object[]{
                    item.getSku(),
                    item.getName(),
                    item.getCategory(),
                    item.getQuantity(),
                    item.getLocation().toString()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WarehouseGUI().setVisible(true));
    }
}
