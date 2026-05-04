package com.auditorium.service;

import com.auditorium.model.*;
import com.auditorium.factory.SeatFactory;
import java.util.*;

/**
 * Singleton class managing all shows, bookings, and sales persons
 */
public class ShowManager {
    private static ShowManager instance;
    
    private Map<String, Show> shows;
    private Map<String, Booking> bookings;
    private Map<String, SalesPerson> salesPersons;
    private SalesPerson currentLoggedInPerson;
    
    // Private constructor
    private ShowManager() {
        this.shows = new HashMap<>();
        this.bookings = new HashMap<>();
        this.salesPersons = new HashMap<>();
        this.currentLoggedInPerson = null;
        
        // Initialize with default show manager account
        SalesPerson manager = new SalesPerson("SM001", "Show Manager", "manager", "admin123");
        salesPersons.put(manager.getUsername(), manager);
    }
    
    // Singleton getInstance
    public static ShowManager getInstance() {
        if (instance == null) {
            synchronized (ShowManager.class) {
                if (instance == null) {
                    instance = new ShowManager();
                }
            }
        }
        return instance;
    }
    
    // ===== AUTHENTICATION =====
    public boolean login(String username, String password) {
        SalesPerson person = salesPersons.get(username);
        if (person != null && person.authenticate(password)) {
            currentLoggedInPerson = person;
            return true;
        }
        return false;
    }
    
    public void logout() {
        currentLoggedInPerson = null;
    }
    
    public SalesPerson getCurrentUser() {
        return currentLoggedInPerson;
    }
    
    public boolean isLoggedIn() {
        return currentLoggedInPerson != null;
    }
    
    // ===== SHOW MANAGEMENT =====
    public void addShow(Show show, int balconyCount, int ordinaryCount) {
        // Create seats using factory
        List<Seat> balconySeats = SeatFactory.createBalconySeats(balconyCount);
        List<Seat> ordinarySeats = SeatFactory.createOrdinarySeats(ordinaryCount);
        
        show.getBalconySeats().addAll(balconySeats);
        show.getOrdinarySeats().addAll(ordinarySeats);
        
        shows.put(show.getShowId(), show);
    }
    
    public Show getShow(String showId) {
        return shows.get(showId);
    }
    
    public List<Show> getAllShows() {
        return new ArrayList<>(shows.values());
    }
    
    // ===== BOOKING MANAGEMENT =====
    public void addBooking(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
        
        // Record sale for current sales person
        if (currentLoggedInPerson != null) {
            currentLoggedInPerson.recordSale(booking.getTotalAmount());
        }
    }
    
    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }
    
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }
    
    // ===== SALES PERSON MANAGEMENT =====
    public void addSalesPerson(SalesPerson person) {
        salesPersons.put(person.getUsername(), person);
    }
    
    public List<SalesPerson> getAllSalesPersons() {
        return new ArrayList<>(salesPersons.values());
    }
    
    // ===== REPORTING =====
    public double getTotalRevenue() {
        return bookings.values().stream()
                .filter(b -> !b.isCancelled())
                .mapToDouble(Booking::getTotalAmount)
                .sum();
    }
    
    public Map<String, Double> getSalesPersonReport() {
        Map<String, Double> report = new HashMap<>();
        for (SalesPerson sp : salesPersons.values()) {
            report.put(sp.getName(), sp.getTotalSales());
        }
        return report;
    }
}
