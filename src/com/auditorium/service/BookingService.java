package com.auditorium.service;

import com.auditorium.model.*;
import com.auditorium.model.Seat.SeatType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService {
    private ShowManager showManager;
    
    public BookingService() {
        this.showManager = ShowManager.getInstance();
    }
    
    /**
     * Book seats for a show
     */
    public Booking bookSeats(String showId, SeatType seatType, int quantity) 
            throws Exception {
        
        if (!showManager.isLoggedIn()) {
            throw new Exception("Please login first to make a booking");
        }
        
        Show show = showManager.getShow(showId);
        if (show == null) {
            throw new Exception("Show not found");
        }
        
        // Get available seats
        List<Seat> availableSeats = getAvailableSeats(show, seatType);
        
        if (availableSeats.size() < quantity) {
            throw new Exception("Not enough seats available. Available: " + availableSeats.size());
        }
        
        // Allocate seats
        List<Seat> allocatedSeats = availableSeats.subList(0, quantity);
        
        // Calculate total amount
        double pricePerSeat = (seatType == SeatType.BALCONY) ? 
                               show.getBalconyPrice() : show.getOrdinaryPrice();
        double totalAmount = pricePerSeat * quantity;
        
        // Create booking
        Booking booking = new Booking(
            showId, 
            new ArrayList<>(allocatedSeats), 
            totalAmount,
            showManager.getCurrentUser().getSalesPersonId()
        );
        
        // Mark seats as booked
        for (Seat seat : allocatedSeats) {
            seat.book(booking.getBookingId());
        }
        
        // Add booking to system
        showManager.addBooking(booking);
        
        return booking;
    }
    
    /**
     * Cancel a booking
     */
    public double cancelBooking(String bookingId) throws Exception {
        Booking booking = showManager.getBooking(bookingId);
        
        if (booking == null) {
            throw new Exception("Booking not found");
        }
        
        if (booking.isCancelled()) {
            throw new Exception("Booking already cancelled");
        }
        
        Show show = showManager.getShow(booking.getShowId());
        if (show == null) {
            throw new Exception("Show not found");
        }
        
        // Calculate refund
        CancellationService cancellationService = new CancellationService();
        double refund = cancellationService.calculateRefund(booking, show.getShowDate());
        
        // Release seats
        for (Seat seat : booking.getBookedSeats()) {
            seat.release();
        }
        
        // Mark booking as cancelled
        booking.cancel(refund);
        
        return refund;
    }
    
    /**
     * Get available seats for a show
     */
    private List<Seat> getAvailableSeats(Show show, SeatType seatType) {
        List<Seat> allSeats = (seatType == SeatType.BALCONY) ? 
                               show.getBalconySeats() : show.getOrdinarySeats();
        
        return allSeats.stream()
                       .filter(seat -> !seat.isBooked())
                       .collect(Collectors.toList());
    }
    
    /**
     * Query seat availability
     */
    public String querySeatAvailability(String showId) {
        Show show = showManager.getShow(showId);
        if (show == null) {
            return "Show not found";
        }
        
        int availableBalcony = show.getAvailableBalconySeats();
        int availableOrdinary = show.getAvailableOrdinarySeats();
        
        return String.format("Show: %s\nBalcony seats available: %d (₹%.2f each)\nOrdinary seats available: %d (₹%.2f each)",
                show.getShowName(), availableBalcony, show.getBalconyPrice(),
                availableOrdinary, show.getOrdinaryPrice());
    }
}
