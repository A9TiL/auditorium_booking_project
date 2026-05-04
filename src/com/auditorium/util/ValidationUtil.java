package com.auditorium.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Validation utilities for business rules
 */
public class ValidationUtil {
    
    /**
     * Validate that show date is not in the past
     */
    public static boolean isValidShowDate(LocalDate showDate) {
        return showDate != null && !showDate.isBefore(LocalDate.now());
    }
    
    /**
     * Validate that show datetime is not in the past
     */
    public static boolean isValidShowDateTime(LocalDate showDate, LocalTime showTime) {
        if (showDate == null || showTime == null) {
            return false;
        }
        LocalDateTime showDateTime = LocalDateTime.of(showDate, showTime);
        return !showDateTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * Validate booking is allowed (show must be in future)
     */
    public static void validateBookingAllowed(LocalDate showDate, LocalTime showTime) 
            throws IllegalArgumentException {
        if (!isValidShowDateTime(showDate, showTime)) {
            throw new IllegalArgumentException(
                "Cannot book seats for shows in the past or that have already started");
        }
    }
    
    /**
     * Validate cancellation is allowed (show must be in future)
     */
    public static void validateCancellationAllowed(LocalDate showDate) 
            throws IllegalArgumentException {
        if (showDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Cannot cancel bookings for shows that have already occurred");
        }
    }
    
    /**
     * Validate seat quantity
     */
    public static void validateQuantity(int quantity) throws IllegalArgumentException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (quantity > 50) {
            throw new IllegalArgumentException(
                "Cannot book more than 50 seats in a single transaction");
        }
    }
    
    /**
     * Validate price
     */
    public static void validatePrice(double price) throws IllegalArgumentException {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (price > 10000) {
            throw new IllegalArgumentException("Price cannot exceed ₹10,000 per seat");
        }
    }
    
    /**
     * Validate seat allocation
     */
    public static void validateSeatAllocation(int total, int complimentary) 
            throws IllegalArgumentException {
        if (total < 0 || complimentary < 0) {
            throw new IllegalArgumentException("Seat counts cannot be negative");
        }
        if (complimentary > total) {
            throw new IllegalArgumentException(
                "Complimentary seats cannot exceed total seats");
        }
        if (total > 500) {
            throw new IllegalArgumentException("Total seats cannot exceed 500");
        }
    }
    
    /**
     * Validate username format
     */
    public static void validateUsername(String username) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException(
                "Username can only contain letters, numbers, and underscores");
        }
    }
    
    /**
     * Validate password strength
     */
    public static void validatePassword(String password) throws IllegalArgumentException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
    
    /**
     * Get validation message for show date
     */
    public static String getShowDateValidationMessage(LocalDate showDate) {
        if (showDate == null) {
            return "Show date is required";
        }
        if (showDate.isBefore(LocalDate.now())) {
            return "Show date cannot be in the past";
        }
        return "Valid";
    }
}