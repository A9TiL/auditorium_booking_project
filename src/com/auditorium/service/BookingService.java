package com.auditorium.service;

import com.auditorium.model.*;
import com.auditorium.model.Seat.SeatType;
import com.auditorium.util.ValidationUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookingService {
    private ShowManager showManager;
    
    public BookingService() {
        this.showManager = ShowManager.getInstance();
    }
    
    /**
     * Book seats for a show with validation and ticket generation
     */
    public BookingResult bookSeats(String showId, SeatType seatType, int quantity) 
            throws Exception {
        
        // Validation: User must be logged in
        if (!showManager.isLoggedIn()) {
            throw new Exception("Please login first to make a booking");
        }
        
        // Validation: Quantity
        ValidationUtil.validateQuantity(quantity);
        
        // Validation: Show exists
        Show show = showManager.getShow(showId);
        if (show == null) {
            throw new Exception("Show not found");
        }
        
        // Validation: Show is in future
        ValidationUtil.validateBookingAllowed(show.getShowDate(), show.getShowTime());
        
        // 👉 THE FIX: Enforce the strict mathematical limit (Total - Complimentary - Booked)
        int actualAvailable = (seatType == SeatType.BALCONY) ? 
                              show.getAvailableBalconySeats() : show.getAvailableOrdinarySeats();
                              
        if (actualAvailable < quantity) {
            throw new Exception("Not enough " + seatType + " seats available. Only " + actualAvailable + " left.");
        }
        
        // Get available physical seats
        List<Seat> availableSeats = getAvailableSeats(show, seatType);
        
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
        
        // Generate ticket using Builder pattern
        Ticket ticket = generateTicket(show, booking, allocatedSeats, seatType, 
                                      pricePerSeat, totalAmount);
        
        return new BookingResult(booking, ticket);
    }
    
    /**
     * Generate ticket using Builder pattern
     */
    private Ticket generateTicket(Show show, Booking booking, List<Seat> seats,
                                  SeatType seatType, double pricePerSeat, double totalAmount) {
        
        List<String> seatNumbers = seats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());
        
        return new Ticket.TicketBuilder()
                .ticketId("TKT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .showName(show.getShowName())
                .showDate(show.getShowDate())
                .showTime(show.getShowTime())
                .seatNumbers(seatNumbers)
                .seatType(seatType.toString())
                .quantity(seats.size())
                .pricePerSeat(pricePerSeat)
                .totalAmount(totalAmount)
                .bookingId(booking.getBookingId())
                .salesPersonName(showManager.getCurrentUser().getName())
                .build();
    }
    
    /**
     * Cancel a booking with validation
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
        
        // Validation: Show is in future
        ValidationUtil.validateCancellationAllowed(show.getShowDate());
        
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
    
    /**
     * BookingResult - Encapsulates booking and generated ticket
     */
    public static class BookingResult {
        private final Booking booking;
        private final Ticket ticket;
        
        public BookingResult(Booking booking, Ticket ticket) {
            this.booking = booking;
            this.ticket = ticket;
        }
        
        public Booking getBooking() { return booking; }
        public Ticket getTicket() { return ticket; }
        
        @Override
        public String toString() {
            return "Booking successful!\n" + booking + "\n\nTicket generated:\n" + ticket;
        }
    }
}