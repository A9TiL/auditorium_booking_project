package com.auditorium.main;

import com.auditorium.model.*;
import com.auditorium.model.Seat.SeatType;
import com.auditorium.service.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Main {
    private static ShowManager showManager = ShowManager.getInstance();
    private static BookingService bookingService = new BookingService();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== STUDENTS AUDITORIUM MANAGEMENT SYSTEM ===\n");
        
        // Initialize sample data
        initializeSampleData();
        
        boolean running = true;
        while (running) {
            if (!showManager.isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private static void initializeSampleData() {
        // Create sample show
        Show show1 = new Show("SH001", "Classical Music Concert", 
                             LocalDate.now().plusDays(5), 
                             LocalTime.of(18, 30));
        show1.configureSeatAllocation(50, 100, 10, 20);
        show1.setPricing(500.0, 250.0);
        showManager.addShow(show1, 50, 100);
        
        // Add a sample sales person
        SalesPerson sp1 = new SalesPerson("SP001", "Rajesh Kumar", "rajesh", "pass123");
        showManager.addSalesPerson(sp1);
        
        System.out.println("✓ Sample data initialized");
        System.out.println("  Login as manager: username='manager', password='admin123'");
        System.out.println("  Login as sales person: username='rajesh', password='pass123'\n");
    }
    
    private static void showLoginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (showManager.login(username, password)) {
            System.out.println("✓ Login successful! Welcome, " + 
                             showManager.getCurrentUser().getName());
        } else {
            System.out.println("✗ Invalid credentials");
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. View All Shows");
        System.out.println("2. Check Seat Availability");
        System.out.println("3. Book Seats");
        System.out.println("4. Cancel Booking");
        System.out.println("5. View Reports");
        System.out.println("6. Logout");
        System.out.print("\nChoice: ");
        
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1": viewAllShows(); break;
            case "2": checkAvailability(); break;
            case "3": bookSeats(); break;
            case "4": cancelBooking(); break;
            case "5": viewReports(); break;
            case "6": showManager.logout(); System.out.println("Logged out"); break;
            default: System.out.println("Invalid choice");
        }
    }
    
    private static void viewAllShows() {
        System.out.println("\n=== ALL SHOWS ===");
        for (Show show : showManager.getAllShows()) {
            System.out.println(show);
            System.out.println("  Available - Balcony: " + show.getAvailableBalconySeats() + 
                             " | Ordinary: " + show.getAvailableOrdinarySeats());
        }
    }
    
    private static void checkAvailability() {
        System.out.print("\nEnter Show ID (e.g., SH001): ");
        String showId = scanner.nextLine();
        
        String availability = bookingService.querySeatAvailability(showId);
        System.out.println("\n" + availability);
    }
    
    private static void bookSeats() {
        try {
            System.out.print("\nEnter Show ID: ");
            String showId = scanner.nextLine();
            
            System.out.print("Seat Type (BALCONY/ORDINARY): ");
            String typeStr = scanner.nextLine().toUpperCase();
            SeatType seatType = SeatType.valueOf(typeStr);
            
            System.out.print("Number of seats: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            
            Booking booking = bookingService.bookSeats(showId, seatType, quantity);
            
            System.out.println("\n✓ BOOKING SUCCESSFUL!");
            System.out.println(booking);
            System.out.println("Seats: " + booking.getBookedSeats().stream()
                    .map(Seat::getSeatNumber)
                    .reduce((a, b) -> a + ", " + b).orElse(""));
            
        } catch (Exception e) {
            System.out.println("✗ Booking failed: " + e.getMessage());
        }
    }
    
    private static void cancelBooking() {
        try {
            System.out.print("\nEnter Booking ID: ");
            String bookingId = scanner.nextLine();
            
            double refund = bookingService.cancelBooking(bookingId);
            
            System.out.println("✓ Booking cancelled successfully!");
            System.out.println("Refund amount: ₹" + String.format("%.2f", refund));
            
        } catch (Exception e) {
            System.out.println("✗ Cancellation failed: " + e.getMessage());
        }
    }
    
    private static void viewReports() {
        System.out.println("\n=== REPORTS ===");
        System.out.println("Total Revenue: ₹" + String.format("%.2f", showManager.getTotalRevenue()));
        
        System.out.println("\nSales Person Report:");
        showManager.getSalesPersonReport().forEach((name, sales) -> 
            System.out.println("  " + name + ": ₹" + String.format("%.2f", sales))
        );
        
        System.out.println("\nAll Bookings:");
        for (Booking b : showManager.getAllBookings()) {
            System.out.println("  " + b);
        }
    }
}
