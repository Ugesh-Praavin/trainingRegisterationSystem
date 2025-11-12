package com.trainingapp;

import com.trainingapp.dao.RegistrationDAO;
import com.trainingapp.dao.TrainingDAO;
import com.trainingapp.dao.UserDAO;
import com.trainingapp.models.Registration;
import com.trainingapp.models.Training;
import com.trainingapp.models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Main {

    private JFrame frame;
    private UserDAO userDAO = new UserDAO();
    private TrainingDAO trainingDAO = new TrainingDAO();
    private RegistrationDAO regDAO = new RegistrationDAO();

    // UI components reused
    private DefaultListModel<User> userListModel = new DefaultListModel<>();
    private DefaultListModel<Training> trainingListModel = new DefaultListModel<>();
    private DefaultListModel<String> registrationListModel = new DefaultListModel<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Main window = new Main();
                window.frame.setVisible(true);
                window.refreshAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Main() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Training Program Registration System");
        frame.setBounds(100, 100, 900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // Panel: Users
        JPanel usersPanel = new JPanel(new BorderLayout(8,8));
        usersPanel.setBorder(new EmptyBorder(10,10,10,10));
        JList<User> userList = new JList<>(userListModel);
        usersPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel addUserPanel = new JPanel(new GridLayout(0,2,6,6));
        JTextField userName = new JTextField();
        JTextField userEmail = new JTextField();
        JTextField userPhone = new JTextField();
        JButton btnAddUser = new JButton("Add User");
        addUserPanel.add(new JLabel("Name:")); addUserPanel.add(userName);
        addUserPanel.add(new JLabel("Email:")); addUserPanel.add(userEmail);
        addUserPanel.add(new JLabel("Phone:")); addUserPanel.add(userPhone);
        addUserPanel.add(new JLabel()); addUserPanel.add(btnAddUser);
        usersPanel.add(addUserPanel, BorderLayout.SOUTH);

        btnAddUser.addActionListener((ActionEvent e) -> {
            try {
                String name = userName.getText().trim();
                String email = userEmail.getText().trim();
                String phone = userPhone.getText().trim();
                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Name and email required", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                User u = new User(name, email, phone);
                int id = userDAO.createUser(u);
                if (id > 0) {
                    JOptionPane.showMessageDialog(frame, "User added with id " + id);
                    userName.setText(""); userEmail.setText(""); userPhone.setText("");
                    refreshUsers();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel: Trainings
        JPanel trainingsPanel = new JPanel(new BorderLayout(8,8));
        trainingsPanel.setBorder(new EmptyBorder(10,10,10,10));
        JList<Training> trainingList = new JList<>(trainingListModel);
        trainingsPanel.add(new JScrollPane(trainingList), BorderLayout.CENTER);

        JPanel addTrainingPanel = new JPanel(new GridLayout(0,2,6,6));
        JTextField tTitle = new JTextField();
        JTextField tSeats = new JTextField("20");
        JTextField tStart = new JTextField("2025-12-01");
        JTextField tFee = new JTextField("0.0");
        JTextArea tDesc = new JTextArea(3, 20);
        JButton btnAddTraining = new JButton("Add Training");
        addTrainingPanel.add(new JLabel("Title:")); addTrainingPanel.add(tTitle);
        addTrainingPanel.add(new JLabel("Seats:")); addTrainingPanel.add(tSeats);
        addTrainingPanel.add(new JLabel("Start Date (YYYY-MM-DD):")); addTrainingPanel.add(tStart);
        addTrainingPanel.add(new JLabel("Fee:")); addTrainingPanel.add(tFee);
        addTrainingPanel.add(new JLabel("Description:")); addTrainingPanel.add(new JScrollPane(tDesc));
        addTrainingPanel.add(new JLabel()); addTrainingPanel.add(btnAddTraining);
        trainingsPanel.add(addTrainingPanel, BorderLayout.SOUTH);

        btnAddTraining.addActionListener((ActionEvent e) -> {
            try {
                String title = tTitle.getText().trim();
                int seats = Integer.parseInt(tSeats.getText().trim());
                Date startDate = Date.valueOf(tStart.getText().trim());
                double fee = Double.parseDouble(tFee.getText().trim());
                String desc = tDesc.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Title required", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Training t = new Training(title, desc, seats, startDate, fee);
                int id = trainingDAO.createTraining(t);
                if (id > 0) {
                    JOptionPane.showMessageDialog(frame, "Training added with id " + id);
                    tTitle.setText(""); tSeats.setText("20"); tStart.setText(""); tFee.setText("0.0"); tDesc.setText("");
                    refreshTrainings();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel: Registration
        JPanel registerPanel = new JPanel(new BorderLayout(8,8));
        registerPanel.setBorder(new EmptyBorder(10,10,10,10));

        JPanel regTop = new JPanel(new GridLayout(0,2,6,6));
        JComboBox<User> cbUsers = new JComboBox<>();
        JComboBox<Training> cbTrainings = new JComboBox<>();
        JButton btnRegister = new JButton("Register Selected");
        regTop.add(new JLabel("Select User:")); regTop.add(cbUsers);
        regTop.add(new JLabel("Select Training:")); regTop.add(cbTrainings);
        regTop.add(new JLabel()); regTop.add(btnRegister);
        registerPanel.add(regTop, BorderLayout.NORTH);

        btnRegister.addActionListener((ActionEvent e) -> {
            try {
                User u = (User) cbUsers.getSelectedItem();
                Training t = (Training) cbTrainings.getSelectedItem();
                if (u == null || t == null) {
                    JOptionPane.showMessageDialog(frame, "Select both user and training");
                    return;
                }
                // decrement seat first (simple concurrency-safe check in DB)
                boolean seatOk = trainingDAO.decrementSeat(t.getId());
                if (!seatOk) {
                    JOptionPane.showMessageDialog(frame, "No seats available for selected training", "Full", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Registration r = new Registration(u.getId(), t.getId());
                int id = regDAO.createRegistration(r);
                if (id > 0) {
                    JOptionPane.showMessageDialog(frame, "Registered successfully (id " + id + ")");
                    refreshRegistrations();
                    refreshTrainings();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Panel: View Registrations
        JPanel viewRegPanel = new JPanel(new BorderLayout(8,8));
        viewRegPanel.setBorder(new EmptyBorder(10,10,10,10));
        JList<String> regList = new JList<>(registrationListModel);
        viewRegPanel.add(new JScrollPane(regList), BorderLayout.CENTER);
        JButton btnRefresh = new JButton("Refresh All");
        viewRegPanel.add(btnRefresh, BorderLayout.SOUTH);
        btnRefresh.addActionListener(e -> refreshAll());

        // assemble tabs
        tabs.addTab("Users", usersPanel);
        tabs.addTab("Trainings", trainingsPanel);
        tabs.addTab("Register", registerPanel);
        tabs.addTab("Registrations", viewRegPanel);

        frame.add(tabs, BorderLayout.CENTER);

        // store frequently used comboboxes to refresh content
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                refreshAll();
            }
        });

        // helper to refresh comboboxes when needed
        // We'll keep references and update when refreshAll() called
        frame.pack();

        // keep references for later usage: store combos in fields by adding them to a panel map
        // but simplest approach: when refreshAll runs it will replace models of combo boxes by iterating components:
        // locate and update cbUsers and cbTrainings references directly:
        // (we already have cbUsers and cbTrainings references)
        this.userCombo = cbUsers;
        this.trainingCombo = cbTrainings;
        this.userListModel = userListModel;
        this.trainingListModel = trainingListModel;
        this.registrationListModel = registrationListModel;
    }

    // combo references for refresh
    private JComboBox<User> userCombo;
    private JComboBox<Training> trainingCombo;

    private void refreshAll() {
        refreshUsers();
        refreshTrainings();
        refreshRegistrations();
    }

    private void refreshUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            userListModel.clear();
            userCombo.removeAllItems();
            for (User u : users) {
                userListModel.addElement(u);
                userCombo.addItem(u);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTrainings() {
        try {
            List<Training> trainings = trainingDAO.getAllTrainings();
            trainingListModel.clear();
            trainingCombo.removeAllItems();
            for (Training t : trainings) {
                trainingListModel.addElement(t);
                trainingCombo.addItem(t);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading trainings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshRegistrations() {
        try {
            List<String> regs = regDAO.getAllRegistrationsSummary();
            registrationListModel.clear();
            for (String r : regs) registrationListModel.addElement(r);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading registrations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
