package com.auditorium.ui;

import com.auditorium.service.ShowManager;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private ShowManager showManager;

    public LoginFrame() {
        this.showManager = ShowManager.getInstance();
        
        // Setup Window
        setTitle("Auditorium Management - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        
        // Main Panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Auditorium System Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Attach action to button
        loginButton.addActionListener(e -> handleLogin());
        
        // Allow pressing "Enter" to trigger login
        getRootPane().setDefaultButton(loginButton);
        
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (showManager.login(username, password)) {
            JOptionPane.showMessageDialog(this, 
                "Login Successful! Welcome " + showManager.getCurrentUser().getName(), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // Close login window
            this.dispose();
            
            // Inside LoginFrame.java -> handleLogin() method:
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
            System.out.println("Launching Dashboard for: " + showManager.getCurrentUser().getUsername());
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid credentials. Please try again.", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText(""); // Clear password field on failure
        }
    }
}