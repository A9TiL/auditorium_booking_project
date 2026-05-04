package com.auditorium.ui;

import com.auditorium.model.*;
import com.auditorium.model.Seat.SeatType;
import com.auditorium.service.*;
import com.auditorium.service.ReportService.*;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class DashboardFrame extends JFrame {

    private ShowManager showManager;
    private BookingService bookingService;
    private ReportService reportService;
    
    public DashboardFrame() {
        this.showManager = ShowManager.getInstance();
        this.bookingService = new BookingService();
        this.reportService = new ReportService();
        
        setupWindow();
        createUI();
    }
    
    private void setupWindow() {
        setTitle("Auditorium Management System");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        
        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        headerPanel.setBackground(new Color(41, 128, 185)); // Professional Blue
        
        JLabel welcomeLabel = new JLabel("Welcome, " + showManager.getCurrentUser().getName() + " | Role: " + 
                (showManager.getCurrentUser().getSalesPersonId().equals("SM001") ? "Manager" : "Sales"));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            showManager.logout();
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // --- Main Tabs ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Everyone gets these tabs
        tabbedPane.addTab("Check Availability", createAvailabilityPanel());
        tabbedPane.addTab("Book Tickets", createBookingPanel());
        tabbedPane.addTab("Cancel Tickets", createCancellationPanel());
        tabbedPane.addTab("Transaction History", createTransactionHistoryPanel());
        
        // Only Manager gets these tabs
        if (showManager.getCurrentUser().getSalesPersonId().equals("SM001")) {
            tabbedPane.addTab("Manage Shows", createShowManagementPanel());
            tabbedPane.addTab("System Reports", createReportsPanel());
        }
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- 1. SHOW CATALOG & AVAILABILITY PANEL ---
    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left Side: List of Shows
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> showList = new JList<>(listModel);
        showList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showList.setFont(new Font("Segoe UI", Font.BOLD, 14));
        showList.setFixedCellHeight(40); // Make items taller and easier to click
        JScrollPane listScroller = new JScrollPane(showList);
        listScroller.setBorder(BorderFactory.createTitledBorder("Active Shows"));

        // Right Side: Show Details using HTML for beautiful formatting
        JTextPane detailsPane = new JTextPane();
        detailsPane.setContentType("text/html");
        detailsPane.setEditable(false);
        detailsPane.setBackground(new Color(245, 248, 250)); // Light grey/blue background
        JScrollPane detailsScroller = new JScrollPane(detailsPane);
        detailsScroller.setBorder(BorderFactory.createTitledBorder("Show Information"));

        // Split Pane to hold both
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroller, detailsScroller);
        splitPane.setDividerLocation(250); // Set width of the left list
        splitPane.setContinuousLayout(true);
        panel.add(splitPane, BorderLayout.CENTER);

        // Top Panel: Refresh Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("↻ Refresh Catalog");
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);

        // --- Logic: Load Data ---
        Runnable loadShows = () -> {
            listModel.clear();
            for (Show s : showManager.getAllShows()) {
                listModel.addElement(s.getShowId() + " - " + s.getShowName());
            }
            if (!listModel.isEmpty()) {
                showList.setSelectedIndex(0); // Auto-select the first show
            } else {
                detailsPane.setText("<html><body style='font-family:sans-serif; padding:20px;'><h2>No active shows available.</h2></body></html>");
            }
        };

        // --- Logic: Handle Clicks ---
        showList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && showList.getSelectedIndex() != -1) {
                String selectedItem = showList.getSelectedValue();
                String showId = selectedItem.split(" - ")[0]; // Extract SH001
                Show show = showManager.getShow(showId);
                
                if (show != null) {
                    // Build the beautiful HTML details view
                    String html = buildShowDetailsHTML(show);
                    detailsPane.setText(html);
                    // Scroll to top
                    SwingUtilities.invokeLater(() -> detailsScroller.getVerticalScrollBar().setValue(0));
                }
            }
        });

        // Bind refresh button and load initial data
        refreshButton.addActionListener(e -> loadShows.run());
        loadShows.run();

        return panel;
    }

    // Helper method to format the show data beautifully
    private String buildShowDetailsHTML(Show show) {
        return "<html><body style='font-family:\"Segoe UI\", sans-serif; padding:15px; color:#333;'>" +
               "<h1 style='color:#2980b9; margin-bottom:0px;'>" + show.getShowName() + "</h1>" +
               "<h3 style='color:#7f8c8d; margin-top:5px;'>ID: " + show.getShowId() + "</h3>" +
               
               "<hr style='border:1px solid #ddd;'>" +
               
               "<h3>📅 Schedule</h3>" +
               "<table width='100%' style='background:#fff; padding:10px; border-radius:5px; border:1px solid #eee;'>" +
               "<tr><td width='50%'><b>Date:</b> " + show.getShowDate() + "</td>" +
               "<td><b>Time:</b> " + show.getShowTime() + "</td></tr>" +
               "</table>" +

               "<h3>🎟️ Rate Chart & Availability</h3>" +
               "<table width='100%' cellspacing='0' cellpadding='8' style='border-collapse:collapse; background:#fff; border:1px solid #eee;'>" +
               "<tr style='background:#f1f2f6; text-align:left;'>" +
               "<th style='border-bottom:2px solid #ddd;'>Class</th>" +
               "<th style='border-bottom:2px solid #ddd;'>Price</th>" +
               "<th style='border-bottom:2px solid #ddd;'>Available Seats</th>" +
               "</tr>" +
               "<tr>" +
               "<td style='border-bottom:1px solid #eee;'><b>Balcony</b></td>" +
               "<td style='border-bottom:1px solid #eee; color:#27ae60;'><b>₹" + show.getBalconyPrice() + "</b></td>" +
               "<td style='border-bottom:1px solid #eee;'>" + show.getAvailableBalconySeats() + "</td>" +
               "</tr>" +
               "<tr>" +
               "<td><b>Ordinary</b></td>" +
               "<td style='color:#27ae60;'><b>₹" + show.getOrdinaryPrice() + "</b></td>" +
               "<td>" + show.getAvailableOrdinarySeats() + "</td>" +
               "</tr>" +
               "</table>" +
               
               "<br><div style='background:#fff3cd; padding:10px; border-left:4px solid #ffc107; color:#856404;'>" +
               "<b>Info:</b> Booking cancellations are permitted up to 1 day before the show. A standard deduction fee applies." +
               "</div>" +
               
               "</body></html>";
    }
    
    // --- 2. BOOKING PANEL ---
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.add(new JLabel("Show ID:"));
        JTextField showIdField = new JTextField(8);
        formPanel.add(showIdField);
        
        formPanel.add(new JLabel("Seat Type:"));
        JComboBox<SeatType> typeBox = new JComboBox<>(SeatType.values());
        formPanel.add(typeBox);
        
        formPanel.add(new JLabel("Quantity:"));
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        formPanel.add(quantitySpinner);
        
        JButton bookButton = new JButton("Book Seats");
        formPanel.add(bookButton);
        panel.add(formPanel, BorderLayout.NORTH);
        
        // 👉 UPGRADE: Use JTextPane for HTML rendering instead of ASCII text
        JTextPane ticketArea = new JTextPane();
        ticketArea.setContentType("text/html");
        ticketArea.setEditable(false);
        ticketArea.setBackground(new Color(245, 248, 250)); // Match the other modern tabs
        JScrollPane scrollPane = new JScrollPane(ticketArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ticket Output"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        bookButton.addActionListener(e -> {
            try {
                String showId = showIdField.getText().trim();
                SeatType type = (SeatType) typeBox.getSelectedItem();
                int qty = (int) quantitySpinner.getValue();
                
                if(showId.isEmpty()) throw new Exception("Please enter a Show ID.");
                
                BookingService.BookingResult result = bookingService.bookSeats(showId, type, qty);
                Show show = showManager.getShow(showId);
                
                // Reuse our beautiful receipt generator
                String htmlReceipt = buildReceiptHTML(result.getBooking(), show);
                
                // Inject a "Booking Successful" header into the existing HTML
                String successHtml = htmlReceipt.replace(
                    "<h2 style='margin:0; color:#2c3e50;'>OFFICIAL RECEIPT</h2>", 
                    "<h2 style='margin:0; color:#27ae60;'>✅ BOOKING SUCCESSFUL</h2><h3 style='margin:0; color:#2c3e50; margin-top:5px;'>OFFICIAL TICKET</h3>"
                );
                
                ticketArea.setText(successHtml);
                
                // Scroll to the top automatically
                SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return panel;
    }
    
    // --- 3. CANCELLATION PANEL ---
    private JPanel createCancellationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Enter Booking ID:"));
        JTextField bookingIdField = new JTextField(15);
        topPanel.add(bookingIdField);
        
        JButton cancelButton = new JButton("Process Cancellation");
        cancelButton.setBackground(new Color(231, 76, 60)); // Red button for destructive action
        cancelButton.setForeground(Color.BLACK);
        topPanel.add(cancelButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // 👉 UPGRADE: Use JTextPane for HTML rendering
        JTextPane resultArea = new JTextPane();
        resultArea.setContentType("text/html");
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 248, 250));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Cancellation Receipt"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cancelButton.addActionListener(e -> {
            try {
                String bookingId = bookingIdField.getText().trim();
                if(bookingId.isEmpty()) throw new Exception("Please enter a Booking ID.");
                
                // Confirm before cancelling
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to cancel booking " + bookingId + "?", 
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    
                if (confirm != JOptionPane.YES_OPTION) return;
                
                // Process the cancellation in the backend
                bookingService.cancelBooking(bookingId);
                
                // Fetch the newly updated (cancelled) booking and show objects
                Booking cancelledBooking = showManager.getBooking(bookingId);
                Show show = showManager.getShow(cancelledBooking.getShowId());
                
                // Generate the HTML receipt
                String htmlReceipt = buildReceiptHTML(cancelledBooking, show);
                
                // Inject a bold "Cancellation Confirmed" header into the HTML
                String successHtml = htmlReceipt.replace(
                    "<h2 style='margin:0; color:#2c3e50;'>OFFICIAL RECEIPT</h2>", 
                    "<h2 style='margin:0; color:#e74c3c;'>❌ CANCELLATION CONFIRMED</h2><h3 style='margin:0; color:#2c3e50; margin-top:5px;'>REFUND RECEIPT</h3>"
                );
                
                resultArea.setText(successHtml);
                bookingIdField.setText(""); // Clear the input field
                
                // Scroll to top
                SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }

    // --- 4. SHOW MANAGEMENT (MANAGER ONLY) ---
    private JPanel createShowManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create Show Form
        // 👉 FIX: Increased grid rows from 7 to 8 to fit Date and Time
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Show (Note: 5 Comp Seats Auto-Reserved)"));
        
        formPanel.add(new JLabel("Show Name:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);
        
        // 👉 FIX: Added Date Field with a default placeholder
        formPanel.add(new JLabel("Show Date (YYYY-MM-DD):"));
        JTextField dateField = new JTextField(java.time.LocalDate.now().plusDays(1).toString());
        formPanel.add(dateField);

        // 👉 FIX: Added Time Field with a default placeholder
        formPanel.add(new JLabel("Show Time (HH:MM 24hr):"));
        JTextField timeField = new JTextField("18:00");
        formPanel.add(timeField);
        
        formPanel.add(new JLabel("Balcony Seats (Total):"));
        JSpinner balconySpinner = new JSpinner(new SpinnerNumberModel(50, 10, 500, 10));
        formPanel.add(balconySpinner);

        formPanel.add(new JLabel("Ordinary Seats (Total):"));
        JSpinner ordinarySpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 10));
        formPanel.add(ordinarySpinner);
        
        formPanel.add(new JLabel("Balcony Price (Rs):"));
        JTextField balcPriceField = new JTextField("500");
        formPanel.add(balcPriceField);

        formPanel.add(new JLabel("Ordinary Price (Rs):"));
        JTextField ordPriceField = new JTextField("250");
        formPanel.add(ordPriceField);

        JButton createButton = new JButton("Create New Show");
        createButton.setBackground(new Color(46, 204, 113));
        createButton.setForeground(Color.BLACK);
        formPanel.add(new JLabel("")); // Spacer
        formPanel.add(createButton);

        // Delete Show Area
        JPanel actionPanel = new JPanel(new BorderLayout(10,0));
        actionPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.setBorder(BorderFactory.createTitledBorder("Delete Show"));
        JTextField deleteField = new JTextField(10);
        JButton deleteButton = new JButton("Delete ID");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.BLACK);
        deletePanel.add(new JLabel("Show ID:"));
        deletePanel.add(deleteField);
        deletePanel.add(deleteButton);
        
        actionPanel.add(deletePanel, BorderLayout.SOUTH);
        panel.add(actionPanel, BorderLayout.NORTH);
        
        // HTML Database Viewer
        JTextPane displayArea = new JTextPane();
        displayArea.setContentType("text/html");
        displayArea.setEditable(false);
        displayArea.setBackground(new Color(245, 248, 250));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Active Shows Database"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Logic: Build the beautiful HTML table
        Runnable refreshDisplay = () -> {
            StringBuilder html = new StringBuilder();
            html.append("<html><body style='font-family: sans-serif; font-size: 11px; padding: 5px; color: #333;'>");
            html.append("<h2 style='font-size: 14px; color: #2c3e50; margin-bottom: 5px;'>🎫 Active Shows Database</h2><hr color='#bdc3c7'><br>");
            
            if (showManager.getAllShows().isEmpty()) {
                html.append("<i>No active shows in the system.</i>");
            } else {
                html.append("<table width='100%' border='1' cellpadding='6' cellspacing='0' bordercolor='#dddddd'>");
                html.append("<tr bgcolor='#ecf0f1'>");
                html.append("<th align='left' width='15%'>ID</th>");
                html.append("<th align='left' width='35%'>Event Name</th>");
                html.append("<th align='left' width='25%'>Schedule</th>");
                html.append("<th align='left' width='12%'>Balcony</th>");
                html.append("<th align='left' width='12%'>Ordinary</th>");
                html.append("</tr>");

                for(Show s : showManager.getAllShows()) {
                    html.append("<tr>");
                    html.append("<td><font color='#2980b9' face='monospace'><b>").append(s.getShowId()).append("</b></font></td>");
                    html.append("<td><b>").append(s.getShowName()).append("</b></td>");
                    html.append("<td>").append(s.getShowDate()).append("<br><font color='#7f8c8d'>").append(s.getShowTime()).append("</font></td>");
                    html.append("<td><font color='#27ae60'><b>₹").append(s.getBalconyPrice()).append("</b></font><br>").append(s.getAvailableBalconySeats()).append(" left</td>");
                    html.append("<td><font color='#27ae60'><b>₹").append(s.getOrdinaryPrice()).append("</b></font><br>").append(s.getAvailableOrdinarySeats()).append(" left</td>");
                    html.append("</tr>");
                }
                html.append("</table>");
            }
            html.append("</body></html>");
            displayArea.setText(html.toString());
            
            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
        };
        refreshDisplay.run();

        // --- Action Listeners ---
        createButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String dateStr = dateField.getText().trim();
                String timeStr = timeField.getText().trim();
                String bPriceStr = balcPriceField.getText().trim();
                String oPriceStr = ordPriceField.getText().trim();
                
                if (name.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) throw new Exception("Name, Date, and Time cannot be empty.");
                if (bPriceStr.isEmpty() || oPriceStr.isEmpty()) throw new Exception("Prices cannot be empty.");
                
                double bPrice = Double.parseDouble(bPriceStr);
                double oPrice = Double.parseDouble(oPriceStr);
                if (bPrice <= 0 || oPrice <= 0) throw new Exception("Prices must be greater than zero.");

                // 👉 FIX: Parse the user's Date and Time
                java.time.LocalDate showDate = java.time.LocalDate.parse(dateStr);
                java.time.LocalTime showTime = java.time.LocalTime.parse(timeStr);
                
                if (showDate.isBefore(java.time.LocalDate.now())) {
                    throw new Exception("Show date cannot be in the past.");
                }

                String id = "SH" + String.format("%03d", showManager.getAllShows().size() + 1);
                Show newShow = new Show(id, name, showDate, showTime);
                newShow.configureSeatAllocation((int)balconySpinner.getValue(), (int)ordinarySpinner.getValue(), 5, 5);
                newShow.setPricing(bPrice, oPrice);
                
                showManager.addShow(newShow, (int)balconySpinner.getValue(), (int)ordinarySpinner.getValue());
                refreshDisplay.run();
                nameField.setText("");
                JOptionPane.showMessageDialog(this, "Show Created Successfully!");
                
            } catch (java.time.format.DateTimeParseException ex) {
                // Safely catch typos in the date format
                JOptionPane.showMessageDialog(this, "Invalid Date/Time format. Use YYYY-MM-DD and HH:MM (e.g., 2026-12-31 and 18:30).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Prices must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                String id = deleteField.getText().trim();
                if(id.isEmpty()) throw new Exception("Enter a Show ID to delete.");
                int confirm = JOptionPane.showConfirmDialog(this, "Delete show " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    showManager.deleteShow(id);
                    refreshDisplay.run();
                    deleteField.setText("");
                    JOptionPane.showMessageDialog(this, "Show Deleted.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // --- 5. REPORTS PANEL (MANAGER ONLY) ---
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top Panel with Refresh Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("↻ Refresh Analytics");
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // The HTML Display Area
        JTextPane reportArea = new JTextPane();
        reportArea.setContentType("text/html");
        reportArea.setEditable(false);
        reportArea.setBackground(new Color(245, 248, 250));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Executive Analytics Dashboard"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 👉 UPGRADE: Strict HTML 3.2 Layout for Analytics
        Runnable generateReport = () -> {
            SystemStatistics stats = reportService.getSystemStatistics();
            Map<String, SalesPerformance> salesPerf = reportService.generateSalesPerformanceReport();
            
            StringBuilder html = new StringBuilder();
            html.append("<html><body style='font-family: sans-serif; font-size: 11px; padding: 10px; color: #333;'>");
            html.append("<h2 style='color:#2c3e50; font-size: 14px; margin-bottom: 5px;'>📊 System Analytics Dashboard</h2><hr color='#bdc3c7'><br>");

            // --- SECTION 1: GLOBAL METRIC CARDS ---
            html.append("<table width='100%' border='0' cellpadding='5' cellspacing='5'><tr>");
            
            // Card 1: Total Revenue
            html.append("<td bgcolor='#ffffff' width='33%' style='border: 1px solid #dddddd;'>");
            html.append("<font color='#7f8c8d'><b>Total Revenue</b></font><br>");
            html.append("<font color='#27ae60' size='5'><b>₹").append(String.format("%.2f", stats.getTotalRevenue())).append("</b></font></td>");
            
            // Card 2: Active Bookings
            html.append("<td bgcolor='#ffffff' width='33%' style='border: 1px solid #dddddd;'>");
            html.append("<font color='#7f8c8d'><b>Active Bookings</b></font><br>");
            html.append("<font color='#3498db' size='5'><b>").append(stats.getActiveBookings()).append("</b></font> <font color='#95a5a6'>/ ").append(stats.getTotalBookings()).append(" Total</font></td>");
            
            // Card 3: Refunded Amount
            html.append("<td bgcolor='#ffffff' width='33%' style='border: 1px solid #dddddd;'>");
            html.append("<font color='#7f8c8d'><b>Refunded Amount</b></font><br>");
            html.append("<font color='#e74c3c' size='5'><b>₹").append(String.format("%.2f", stats.getTotalRefunded())).append("</b></font> <font color='#95a5a6'>(").append(stats.getCancelledBookings()).append(" cancels)</font></td>");
            
            html.append("</tr></table><br>");

            // --- SECTION 2: SALES TEAM PERFORMANCE ---
            html.append("<font color='#2980b9' size='4'><b>🧑‍💼 Sales Team Performance</b></font><br>");
            html.append("<table width='100%' border='1' cellpadding='6' cellspacing='0' bordercolor='#dddddd'>");
            html.append("<tr bgcolor='#ecf0f1'>");
            html.append("<th align='left'>Sales Person</th>");
            html.append("<th align='left'>Transactions</th>");
            html.append("<th align='left'>Total Sales</th>");
            html.append("<th align='left'>Commission (5%)</th>");
            html.append("</tr>");

            for (SalesPerformance sp : salesPerf.values()) {
                html.append("<tr>");
                html.append("<td><b>").append(sp.getSalesPersonName()).append("</b></td>");
                html.append("<td>").append(sp.getTransactionCount()).append("</td>");
                html.append("<td><font color='#27ae60'><b>₹").append(String.format("%.2f", sp.getTotalSales())).append("</b></font></td>");
                html.append("<td><font color='#8e44ad'><b>₹").append(String.format("%.2f", sp.getCommission())).append("</b></font></td>");
                html.append("</tr>");
            }
            html.append("</table><br><br>");

            // --- SECTION 3: SHOW ANALYTICS ---
            html.append("<font color='#2980b9' size='4'><b>🎭 Show Analytics & Occupancy</b></font><br>");
            html.append("<table width='100%' border='1' cellpadding='6' cellspacing='0' bordercolor='#dddddd'>");
            html.append("<tr bgcolor='#ecf0f1'>");
            html.append("<th align='left'>Show Name</th>");
            html.append("<th align='left'>Balcony Occupancy</th>");
            html.append("<th align='left'>Ordinary Occupancy</th>");
            html.append("<th align='left'>Show Revenue</th>");
            html.append("</tr>");

            java.util.List<Show> allShows = showManager.getAllShows();
            if(allShows.isEmpty()) {
                 html.append("<tr><td colspan='4' align='center'><i><font color='#7f8c8d'>No active shows to analyze.</font></i></td></tr>");
            } else {
                for (Show show : allShows) {
                    ShowReport sr = reportService.generateShowReport(show.getShowId());
                    if(sr != null) {
                        html.append("<tr>");
                        html.append("<td><b>").append(sr.getShowName()).append("</b></td>");
                        
                        html.append("<td>").append(String.format("%.1f%%", sr.getBalconyPercentage()));
                        html.append(" <font color='#7f8c8d'>(").append(sr.getBookedBalconySeats()).append("/").append(sr.getTotalBalconySeats()).append(")</font></td>");
                        
                        html.append("<td>").append(String.format("%.1f%%", sr.getOrdinaryPercentage()));
                        html.append(" <font color='#7f8c8d'>(").append(sr.getBookedOrdinarySeats()).append("/").append(sr.getTotalOrdinarySeats()).append(")</font></td>");
                        
                        html.append("<td><font color='#27ae60'><b>₹").append(String.format("%.2f", sr.getTotalRevenue())).append("</b></font></td>");
                        html.append("</tr>");
                    }
                }
            }
            html.append("</table>");
            html.append("</body></html>");

            reportArea.setText(html.toString());
            SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
        };

        refreshButton.addActionListener(e -> generateReport.run());
        generateReport.run();
        
        return panel;
    }

    // --- 6. TRANSACTION HISTORY PANEL (MODERNIZED UI) ---
    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        boolean isManager = showManager.getCurrentUser().getSalesPersonId().equals("SM001");

        // Left Side: List of Bookings
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> bookingList = new JList<>(listModel);
        bookingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingList.setFont(new Font("Consolas", Font.BOLD, 14));
        bookingList.setFixedCellHeight(40);
        JScrollPane listScroller = new JScrollPane(bookingList);
        listScroller.setBorder(BorderFactory.createTitledBorder(isManager ? "Master Audit Log" : "My Transactions"));

        // Right Side: Beautiful HTML Receipt View
        JTextPane detailsPane = new JTextPane();
        detailsPane.setContentType("text/html");
        detailsPane.setEditable(false);
        detailsPane.setBackground(new Color(245, 248, 250)); // Light grey/blue background
        JScrollPane detailsScroller = new JScrollPane(detailsPane);
        detailsScroller.setBorder(BorderFactory.createTitledBorder("Digital Receipt"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroller, detailsScroller);
        splitPane.setDividerLocation(200);
        splitPane.setContinuousLayout(true);
        panel.add(splitPane, BorderLayout.CENTER);

        // Load Data Logic
        Runnable loadData = () -> {
            listModel.clear();
            String currentUserId = showManager.getCurrentUser().getSalesPersonId();
            
            for (Booking b : showManager.getAllBookings()) {
                if (isManager || b.getSalesPersonId().equals(currentUserId)) {
                    // Add a visual indicator for cancelled vs active
                    String prefix = b.isCancelled() ? "[CANCELLED] " : "[ACTIVE]    ";
                    listModel.addElement(prefix + b.getBookingId());
                }
            }
            if (!listModel.isEmpty()) {
                bookingList.setSelectedIndex(listModel.getSize() - 1); // Auto-select the most recent booking
            } else {
                detailsPane.setText("<html><body style='font-family:sans-serif; padding:20px; color:#7f8c8d;'><h2>No transactions found.</h2></body></html>");
            }
        };

        // Selection Logic: Click a booking to see the receipt
        bookingList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookingList.getSelectedIndex() != -1) {
                String selectedItem = bookingList.getSelectedValue();
                // Extract just the Booking ID (split by space and get the second part)
                String bookingId = selectedItem.split("] ")[1].trim(); 
                Booking booking = showManager.getBooking(bookingId);
                
                if (booking != null) {
                    Show show = showManager.getShow(booking.getShowId());
                    String html = buildReceiptHTML(booking, show);
                    detailsPane.setText(html);
                    SwingUtilities.invokeLater(() -> detailsScroller.getVerticalScrollBar().setValue(0));
                }
            }
        });

        // Top Panel with Refresh Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("↻ Refresh Ledger");
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadData.run());
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);

        loadData.run();
        return panel;
    }

    // Helper method to generate a modern digital receipt
    // Helper method to generate a strictly-aligned, native-sized digital receipt
    private String buildReceiptHTML(Booking booking, Show show) {
        String statusColor = booking.isCancelled() ? "#e74c3c" : "#27ae60";
        String statusText = booking.isCancelled() ? "CANCELLED / REFUNDED" : "CONFIRMED / ACTIVE";
        String showName = (show != null) ? show.getShowName() : "Unknown Show (Deleted)";
        String showDate = (show != null) ? show.getShowDate().toString() : "N/A";
        String showTime = (show != null) ? show.getShowTime().toString() : "N/A";
        
        String ticketId = "TKT-" + booking.getBookingId().substring(2);
        java.util.List<String> seatNums = booking.getBookedSeats().stream().map(Seat::getSeatNumber).toList();
        String seatsString = String.join(", ", seatNums);
        int quantity = booking.getBookedSeats().size();
        
        String seatType = "Unknown";
        double pricePerSeat = 0.0;
        if (!booking.getBookedSeats().isEmpty()) {
            Seat firstSeat = booking.getBookedSeats().get(0);
            seatType = firstSeat.getSeatType().toString();
            if (show != null) {
                pricePerSeat = (firstSeat.getSeatType() == SeatType.BALCONY) ? show.getBalconyPrice() : show.getOrdinaryPrice();
            } else {
                pricePerSeat = booking.getTotalAmount() / quantity;
            }
        }

        StringBuilder html = new StringBuilder();
        // 👉 FIX: Forced 11px native font size
        html.append("<html><body style='font-family: sans-serif; font-size: 11px; padding: 10px; color: #333;'>");
        
        html.append("<h2 style='text-align:center; font-size: 14px; margin: 0px; color: #2c3e50;'>OFFICIAL RECEIPT</h2>");
        html.append("<hr color='#cccccc'>");

        // 👉 FIX: Strict HTML 3.2 Table alignment and widths
        html.append("<table width='100%' cellpadding='3' cellspacing='0'>");
        html.append("<tr><td width='40%' align='right' color='#7f8c8d'><b>Ticket ID:</b></td><td width='60%' style='font-family: monospace;'>").append(ticketId).append("</td></tr>");
        html.append("<tr><td align='right' color='#7f8c8d'><b>Booking ID:</b></td><td style='font-family: monospace;'>").append(booking.getBookingId()).append("</td></tr>");
        html.append("</table><br>");

        html.append("<div style='background-color:").append(statusColor).append("; color:white; text-align:center; padding:5px; font-weight:bold;'>").append(statusText).append("</div><br>");

        html.append("<b style='color:#2980b9;'>Event Details</b>");
        // 👉 FIX: Forced borders and padding for rigid alignment
        html.append("<table width='100%' border='1' cellpadding='5' cellspacing='0' bordercolor='#eeeeee'>");
        html.append("<tr><td width='35%' bgcolor='#fafafa'><b>Event:</b></td><td>").append(showName).append("</td></tr>");
        html.append("<tr><td bgcolor='#fafafa'><b>Date & Time:</b></td><td>").append(showDate).append(" at ").append(showTime).append("</td></tr>");
        html.append("<tr><td bgcolor='#fafafa'><b>Seat Type:</b></td><td>").append(seatType).append("</td></tr>");
        html.append("<tr><td bgcolor='#fafafa'><b>Quantity:</b></td><td>").append(quantity).append("</td></tr>");
        html.append("<tr><td bgcolor='#fafafa'><b>Seat Numbers:</b></td><td style='font-family:monospace;'>").append(seatsString).append("</td></tr>");
        html.append("</table><br>");

        html.append("<b style='color:#2980b9;'>Financial Summary</b>");
        html.append("<table width='100%' border='1' cellpadding='5' cellspacing='0' bordercolor='#eeeeee'>");
        html.append("<tr><td width='50%' bgcolor='#fafafa'><b>Price per Seat:</b></td><td>₹").append(String.format("%.2f", pricePerSeat)).append("</td></tr>");
        html.append("<tr><td bgcolor='#fafafa'><b>Total Paid:</b></td><td style='color:#27ae60;'><b>₹").append(String.format("%.2f", booking.getTotalAmount())).append("</b></td></tr>");
        
        if (booking.isCancelled()) {
            html.append("<tr><td bgcolor='#fafafa'><b>Refund Auth:</b></td><td style='color:#e74c3c;'><b>₹").append(String.format("%.2f", booking.getRefundAmount())).append("</b></td></tr>");
            html.append("<tr><td bgcolor='#fafafa'><b>Cancellation Fee:</b></td><td>₹").append(String.format("%.2f", booking.getTotalAmount() - booking.getRefundAmount())).append("</td></tr>");
        }
        html.append("</table>");

        html.append("<p style='text-align:center; font-size: 10px; color: #95a5a6; margin-top: 15px;'>");
        html.append("Sales ID: ").append(booking.getSalesPersonId()).append(" | Date: ").append(booking.getBookingDateTime().toLocalDate());
        html.append("</p></body></html>");
        
        return html.toString();
    }
}