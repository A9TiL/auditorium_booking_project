package com.auditorium.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Show {
    private String showId;
    private String showName;
    private LocalDate showDate;
    private LocalTime showTime;
    private int totalBalconySeats;
    private int totalOrdinarySeats;
    private int complimentaryBalcony;
    private int complimentaryOrdinary;
    private double balconyPrice;
    private double ordinaryPrice;
    
    private List<Seat> balconySeats;
    private List<Seat> ordinarySeats;
    
    // Constructor
    public Show(String showId, String showName, LocalDate showDate, LocalTime showTime) {
        this.showId = showId;
        this.showName = showName;
        this.showDate = showDate;
        this.showTime = showTime;
        this.balconySeats = new ArrayList<>();
        this.ordinarySeats = new ArrayList<>();
    }
    
    // Configure seat allocation
    public void configureSeatAllocation(int totalBalcony, int totalOrdinary, 
                                       int compBalcony, int compOrdinary) {
        this.totalBalconySeats = totalBalcony;
        this.totalOrdinarySeats = totalOrdinary;
        this.complimentaryBalcony = compBalcony;
        this.complimentaryOrdinary = compOrdinary;
    }
    
    // Set pricing
    public void setPricing(double balconyPrice, double ordinaryPrice) {
        this.balconyPrice = balconyPrice;
        this.ordinaryPrice = ordinaryPrice;
    }
    
    // Get available seats (total - complimentary - booked)
    public int getAvailableBalconySeats() {
        int saleable = totalBalconySeats - complimentaryBalcony;
        long booked = balconySeats.stream().filter(Seat::isBooked).count();
        return saleable - (int)booked;
    }
    
    public int getAvailableOrdinarySeats() {
        int saleable = totalOrdinarySeats - complimentaryOrdinary;
        long booked = ordinarySeats.stream().filter(Seat::isBooked).count();
        return saleable - (int)booked;
    }
    
    // Getters
    public String getShowId() { return showId; }
    public String getShowName() { return showName; }
    public LocalDate getShowDate() { return showDate; }
    public LocalTime getShowTime() { return showTime; }
    public double getBalconyPrice() { return balconyPrice; }
    public double getOrdinaryPrice() { return ordinaryPrice; }
    public List<Seat> getBalconySeats() { return balconySeats; }
    public List<Seat> getOrdinarySeats() { return ordinarySeats; }
    
    @Override
    public String toString() {
        return String.format("Show: %s | Date: %s | Time: %s | Balcony: ₹%.2f | Ordinary: ₹%.2f",
                showName, showDate, showTime, balconyPrice, ordinaryPrice);
    }
}
