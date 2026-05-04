package com.auditorium.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Booking {
    private String bookingId;
    private String showId;
    private LocalDateTime bookingDateTime;
    private List<Seat> bookedSeats;
    private double totalAmount;
    private String salesPersonId;
    private boolean isCancelled;
    private double refundAmount;
    
    public Booking(String showId, List<Seat> seats, double totalAmount, String salesPersonId) {
        this.bookingId = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.showId = showId;
        this.bookedSeats = seats;
        this.totalAmount = totalAmount;
        this.salesPersonId = salesPersonId;
        this.bookingDateTime = LocalDateTime.now();
        this.isCancelled = false;
        this.refundAmount = 0.0;
    }
    
    // Mark as cancelled
    public void cancel(double refund) {
        this.isCancelled = true;
        this.refundAmount = refund;
    }
    
    // Getters
    public String getBookingId() { return bookingId; }
    public String getShowId() { return showId; }
    public LocalDateTime getBookingDateTime() { return bookingDateTime; }
    public List<Seat> getBookedSeats() { return bookedSeats; }
    public double getTotalAmount() { return totalAmount; }
    public String getSalesPersonId() { return salesPersonId; }
    public boolean isCancelled() { return isCancelled; }
    public double getRefundAmount() { return refundAmount; }
    
    @Override
    public String toString() {
        return String.format("Booking ID: %s | Seats: %d | Amount: ₹%.2f | Status: %s",
                bookingId, bookedSeats.size(), totalAmount, 
                isCancelled ? "CANCELLED" : "ACTIVE");
    }
}
