package com.auditorium.service;

import com.auditorium.model.Booking;
import com.auditorium.model.Seat;
import com.auditorium.model.Seat.SeatType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CancellationService {
    
    /**
     * Calculate refund based on cancellation timing
     * Rules:
     * - Before 3 days: ₹5 per ticket
     * - Within 3 days and 1 day: ₹10 (ordinary), ₹15 (balcony)
     * - Last day: 50% deduction
     */
    public double calculateRefund(Booking booking, LocalDate showDate) {
        if (booking.isCancelled()) {
            throw new IllegalStateException("Booking already cancelled");
        }
        
        LocalDate today = LocalDate.now();
        long daysUntilShow = ChronoUnit.DAYS.between(today, showDate);
        
        double totalPaid = booking.getTotalAmount();
        double totalDeduction = 0.0;
        
        if (daysUntilShow > 3) {
            // Before 3 days: ₹5 per ticket
            totalDeduction = booking.getBookedSeats().size() * 5.0;
        } 
        else if (daysUntilShow >= 1 && daysUntilShow <= 3) {
            // Within 3 days: ₹10 (ordinary), ₹15 (balcony)
            for (Seat seat : booking.getBookedSeats()) {
                if (seat.getSeatType() == SeatType.BALCONY) {
                    totalDeduction += 15.0;
                } else {
                    totalDeduction += 10.0;
                }
            }
        } 
        else {
            // Last day: 50% deduction
            totalDeduction = totalPaid * 0.5;
        }
        
        return totalPaid - totalDeduction;
    }
    
    // Get cancellation policy message
    public String getCancellationPolicy(LocalDate showDate) {
        LocalDate today = LocalDate.now();
        long daysUntilShow = ChronoUnit.DAYS.between(today, showDate);
        
        if (daysUntilShow > 3) {
            return "Cancellation charge: ₹5 per ticket";
        } else if (daysUntilShow >= 1) {
            return "Cancellation charge: ₹10 (ordinary), ₹15 (balcony) per ticket";
        } else {
            return "Cancellation charge: 50% of total amount";
        }
    }
}
