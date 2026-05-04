package com.auditorium.ui;

import com.auditorium.service.ShowManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField userIdField;
    private JPasswordField passwordField;
    private ShowManager showManager;

    public LoginFrame() {
        this.showManager = ShowManager.getInstance();
        setupWindow();
        createUI();
    }

    private void setupWindow() {
        setTitle("Auditorium Management System - Authentication");
        setSize(450, 420); // Compact, controlled window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false); // Lock the window size so the layout doesn't break
    }

    private void createUI() {
        // Main background panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241)); // Light gray/blue backdrop
        
        // --- Top Header ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185)); // Professional blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        JLabel titleLabel = new JLabel("Auditorium System Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // --- Center Form Panel (The White "Card") ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(25, 30, 25, 30), // Outer margin
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1) // Subtle border
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); // Padding around each row
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // User ID Label & Field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("User ID:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        userIdField = new JTextField(15);
        userIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userIdField.setPreferredSize(new Dimension(200, 32)); // Force exact size, stop stretching
        formPanel.add(userIdField, gbc);
        
        // Password Label & Field
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 32)); // Force exact size, stop stretching
        formPanel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2; // Make button span across both columns
        gbc.insets = new Insets(25, 10, 10, 10); // Add extra space above the button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(46, 204, 113)); // Green button
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(200, 40));
        
        loginButton.addActionListener(e -> handleLogin());
        formPanel.add(loginButton, gbc);

        // Wrapper panel to keep the white card centered and stop it from stretching to the edges
        JPanel centerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        centerContainer.setOpaque(false); // Transparent so the gray background shows through
        centerContainer.add(formPanel);

        // Assemble the window
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        add(mainPanel);
        
        // Bonus UX: Pressing "Enter" on the keyboard triggers the login button
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (showManager.login(userId, password)) {
                this.dispose(); // Close login window
                new DashboardFrame().setVisible(true); // Launch Dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid User ID or Password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "System error during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}