package com.auditorium.model;

public class Seat {
    public enum SeatType {
        BALCONY, ORDINARY
    }
    
    private String seatNumber;
    private SeatType seatType;
    private boolean isBooked;
    private String bookingId; // Reference to booking
    
    public Seat(String seatNumber, SeatType seatType) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = false;
        this.bookingId = null;
    }
    
    // Book this seat
    public void book(String bookingId) {
        this.isBooked = true;
        this.bookingId = bookingId;
    }
    
    // Release this seat
    public void release() {
        this.isBooked = false;
        this.bookingId = null;
    }
    
    // Getters
    public String getSeatNumber() { return seatNumber; }
    public SeatType getSeatType() { return seatType; }
    public boolean isBooked() { return isBooked; }
    public String getBookingId() { return bookingId; }
    
    @Override
    public String toString() {
        return seatNumber + " [" + seatType + "]" + (isBooked ? " - BOOKED" : " - AVAILABLE");
    }
}
