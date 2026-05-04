package com.auditorium.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ticket - Immutable representation of a booking ticket
 * Built using TicketBuilder pattern
 */
public class Ticket {
    // Ticket details
    private final String ticketId;
    private final String showName;
    private final LocalDate showDate;
    private final LocalTime showTime;
    private final List<String> seatNumbers;
    private final String seatType;
    private final int quantity;
    private final double pricePerSeat;
    private final double totalAmount;
    private final String bookingId;
    private final String salesPersonName;
    private final LocalDate bookingDate;
    
    // Private constructor - only Builder can create
    private Ticket(TicketBuilder builder) {
        this.ticketId = builder.ticketId;
        this.showName = builder.showName;
        this.showDate = builder.showDate;
        this.showTime = builder.showTime;
        this.seatNumbers = builder.seatNumbers;
        this.seatType = builder.seatType;
        this.quantity = builder.quantity;
        this.pricePerSeat = builder.pricePerSeat;
        this.totalAmount = builder.totalAmount;
        this.bookingId = builder.bookingId;
        this.salesPersonName = builder.salesPersonName;
        this.bookingDate = builder.bookingDate;
    }
    
    // Getters
    public String getTicketId() { return ticketId; }
    public String getShowName() { return showName; }
    public LocalDate getShowDate() { return showDate; }
    public LocalTime getShowTime() { return showTime; }
    public List<String> getSeatNumbers() { return seatNumbers; }
    public String getSeatType() { return seatType; }
    public int getQuantity() { return quantity; }
    public double getPricePerSeat() { return pricePerSeat; }
    public double getTotalAmount() { return totalAmount; }
    public String getBookingId() { return bookingId; }
    public String getSalesPersonName() { return salesPersonName; }
    public LocalDate getBookingDate() { return bookingDate; }
    
    /**
     * Generate printable ticket string
     */
    public String generatePrintableTicket() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        
        StringBuilder ticket = new StringBuilder();
        ticket.append("╔════════════════════════════════════════════════╗\n");
        ticket.append("║         STUDENTS AUDITORIUM TICKET              ║\n");
        ticket.append("╠════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║ Ticket ID    : %-32s ║\n", ticketId));
        ticket.append(String.format("║ Booking ID   : %-32s ║\n", bookingId));
        ticket.append("╠════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║ Show         : %-32s ║\n", showName));
        ticket.append(String.format("║ Date         : %-32s ║\n", showDate.format(dateFormatter)));
        ticket.append(String.format("║ Time         : %-32s ║\n", showTime.format(timeFormatter)));
        ticket.append("╠════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║ Seat Type    : %-32s ║\n", seatType));
        ticket.append(String.format("║ Quantity     : %-32d ║\n", quantity));
        ticket.append(String.format("║ Seat Numbers : %-32s ║\n", 
            seatNumbers.toString().replaceAll("[\\[\\]]", "")));
        ticket.append("╠════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║ Price/Seat   : ₹%-31.2f ║\n", pricePerSeat));
        ticket.append(String.format("║ Total Amount : ₹%-31.2f ║\n", totalAmount));
        ticket.append("╠════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║ Booked by    : %-32s ║\n", salesPersonName));
        ticket.append(String.format("║ Booked on    : %-32s ║\n", bookingDate.format(dateFormatter)));
        ticket.append("╚════════════════════════════════════════════════╝\n");
        
        return ticket.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Ticket[%s] - %s on %s - %d seat(s) - ₹%.2f",
                ticketId, showName, showDate, quantity, totalAmount);
    }
    
    // Inner Builder class
    public static class TicketBuilder {
        private String ticketId;
        private String showName;
        private LocalDate showDate;
        private LocalTime showTime;
        private List<String> seatNumbers;
        private String seatType;
        private int quantity;
        private double pricePerSeat;
        private double totalAmount;
        private String bookingId;
        private String salesPersonName;
        private LocalDate bookingDate;
        
        public TicketBuilder() {
            this.bookingDate = LocalDate.now();
        }
        
        public TicketBuilder ticketId(String ticketId) {
            this.ticketId = ticketId;
            return this;
        }
        
        public TicketBuilder showName(String showName) {
            this.showName = showName;
            return this;
        }
        
        public TicketBuilder showDate(LocalDate showDate) {
            this.showDate = showDate;
            return this;
        }
        
        public TicketBuilder showTime(LocalTime showTime) {
            this.showTime = showTime;
            return this;
        }
        
        public TicketBuilder seatNumbers(List<String> seatNumbers) {
            this.seatNumbers = seatNumbers;
            return this;
        }
        
        public TicketBuilder seatType(String seatType) {
            this.seatType = seatType;
            return this;
        }
        
        public TicketBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public TicketBuilder pricePerSeat(double pricePerSeat) {
            this.pricePerSeat = pricePerSeat;
            return this;
        }
        
        public TicketBuilder totalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public TicketBuilder bookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }
        
        public TicketBuilder salesPersonName(String salesPersonName) {
            this.salesPersonName = salesPersonName;
            return this;
        }
        
        public TicketBuilder bookingDate(LocalDate bookingDate) {
            this.bookingDate = bookingDate;
            return this;
        }
        
        /**
         * Build and validate ticket
         */
        public Ticket build() {
            validateTicket();
            return new Ticket(this);
        }
        
        private void validateTicket() {
            if (ticketId == null || ticketId.isEmpty()) {
                throw new IllegalStateException("Ticket ID is required");
            }
            if (showName == null || showName.isEmpty()) {
                throw new IllegalStateException("Show name is required");
            }
            if (showDate == null) {
                throw new IllegalStateException("Show date is required");
            }
            if (seatNumbers == null || seatNumbers.isEmpty()) {
                throw new IllegalStateException("Seat numbers are required");
            }
            if (quantity <= 0) {
                throw new IllegalStateException("Quantity must be positive");
            }
            if (totalAmount < 0) {
                throw new IllegalStateException("Total amount cannot be negative");
            }
        }
    }
}