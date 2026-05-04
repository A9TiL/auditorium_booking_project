package com.auditorium.service;

import com.auditorium.model.*;
import java.util.*;

/**
 * ReportService - Advanced reporting with percentage calculations
 */
public class ReportService {
    private ShowManager showManager;
    
    public ReportService() {
        this.showManager = ShowManager.getInstance();
    }
    
    /**
     * Generate comprehensive show report
     */
    public ShowReport generateShowReport(String showId) {
        Show show = showManager.getShow(showId);
        if (show == null) {
            return null;
        }
        
        return new ShowReport(show);
    }
    
    /**
     * Generate sales person performance report
     */
    public Map<String, SalesPerformance> generateSalesPerformanceReport() {
        Map<String, SalesPerformance> report = new HashMap<>();
        
        for (SalesPerson sp : showManager.getAllSalesPersons()) {
            SalesPerformance perf = new SalesPerformance(
                sp.getName(),
                sp.getTotalSales(),
                sp.getTransactionCount(),
                calculateCommission(sp.getTotalSales())
            );
            report.put(sp.getSalesPersonId(), perf);
        }
        
        return report;
    }
    
    /**
     * Calculate commission (5% of total sales)
     */
    public double calculateCommission(double totalSales) {
        return totalSales * 0.05; // 5% commission
    }
    
    /**
     * Get overall system statistics
     */
    public SystemStatistics getSystemStatistics() {
        int totalShows = showManager.getAllShows().size();
        int totalBookings = showManager.getAllBookings().size();
        int activeBookings = (int) showManager.getAllBookings().stream()
                .filter(b -> !b.isCancelled())
                .count();
        int cancelledBookings = totalBookings - activeBookings;
        
        double totalRevenue = showManager.getTotalRevenue();
        double totalRefunded = showManager.getAllBookings().stream()
                .filter(Booking::isCancelled)
                .mapToDouble(Booking::getRefundAmount)
                .sum();
        
        return new SystemStatistics(
            totalShows,
            totalBookings,
            activeBookings,
            cancelledBookings,
            totalRevenue,
            totalRefunded
        );
    }
    
    // Inner class: Show Report
    public static class ShowReport {
        private String showName;
        private int totalBalconySeats;
        private int totalOrdinarySeats;
        private int bookedBalconySeats;
        private int bookedOrdinarySeats;
        private double balconyPercentage;
        private double ordinaryPercentage;
        private double balconyRevenue;
        private double ordinaryRevenue;
        private double totalRevenue;
        
        public ShowReport(Show show) {
            this.showName = show.getShowName();
            this.totalBalconySeats = show.getBalconySeats().size();
            this.totalOrdinarySeats = show.getOrdinarySeats().size();
            
            this.bookedBalconySeats = (int) show.getBalconySeats().stream()
                    .filter(Seat::isBooked)
                    .count();
            this.bookedOrdinarySeats = (int) show.getOrdinarySeats().stream()
                    .filter(Seat::isBooked)
                    .count();
            
            this.balconyPercentage = totalBalconySeats > 0 ? 
                    (bookedBalconySeats * 100.0 / totalBalconySeats) : 0.0;
            this.ordinaryPercentage = totalOrdinarySeats > 0 ? 
                    (bookedOrdinarySeats * 100.0 / totalOrdinarySeats) : 0.0;
            
            this.balconyRevenue = bookedBalconySeats * show.getBalconyPrice();
            this.ordinaryRevenue = bookedOrdinarySeats * show.getOrdinaryPrice();
            this.totalRevenue = balconyRevenue + ordinaryRevenue;
        }
        
        // Getters
        public String getShowName() { return showName; }
        public int getTotalBalconySeats() { return totalBalconySeats; }
        public int getTotalOrdinarySeats() { return totalOrdinarySeats; }
        public int getBookedBalconySeats() { return bookedBalconySeats; }
        public int getBookedOrdinarySeats() { return bookedOrdinarySeats; }
        public double getBalconyPercentage() { return balconyPercentage; }
        public double getOrdinaryPercentage() { return ordinaryPercentage; }
        public double getBalconyRevenue() { return balconyRevenue; }
        public double getOrdinaryRevenue() { return ordinaryRevenue; }
        public double getTotalRevenue() { return totalRevenue; }
        
        @Override
        public String toString() {
            return String.format(
                "Show: %s\n" +
                "Balcony: %d/%d (%.1f%%) - Revenue: ₹%.2f\n" +
                "Ordinary: %d/%d (%.1f%%) - Revenue: ₹%.2f\n" +
                "Total Revenue: ₹%.2f",
                showName,
                bookedBalconySeats, totalBalconySeats, balconyPercentage, balconyRevenue,
                bookedOrdinarySeats, totalOrdinarySeats, ordinaryPercentage, ordinaryRevenue,
                totalRevenue
            );
        }
    }
    
    // Inner class: Sales Performance
    public static class SalesPerformance {
        private String salesPersonName;
        private double totalSales;
        private int transactionCount;
        private double commission;
        private double averageTransactionValue;
        
        public SalesPerformance(String name, double sales, int count, double commission) {
            this.salesPersonName = name;
            this.totalSales = sales;
            this.transactionCount = count;
            this.commission = commission;
            this.averageTransactionValue = count > 0 ? sales / count : 0.0;
        }
        
        // Getters
        public String getSalesPersonName() { return salesPersonName; }
        public double getTotalSales() { return totalSales; }
        public int getTransactionCount() { return transactionCount; }
        public double getCommission() { return commission; }
        public double getAverageTransactionValue() { return averageTransactionValue; }
        
        @Override
        public String toString() {
            return String.format(
                "%s: ₹%.2f (%d transactions) - Commission: ₹%.2f - Avg: ₹%.2f",
                salesPersonName, totalSales, transactionCount, commission, averageTransactionValue
            );
        }
    }
    
    // Inner class: System Statistics
    public static class SystemStatistics {
        private int totalShows;
        private int totalBookings;
        private int activeBookings;
        private int cancelledBookings;
        private double totalRevenue;
        private double totalRefunded;
        private double cancellationRate;
        
        public SystemStatistics(int shows, int bookings, int active, int cancelled, 
                               double revenue, double refunded) {
            this.totalShows = shows;
            this.totalBookings = bookings;
            this.activeBookings = active;
            this.cancelledBookings = cancelled;
            this.totalRevenue = revenue;
            this.totalRefunded = refunded;
            this.cancellationRate = bookings > 0 ? (cancelled * 100.0 / bookings) : 0.0;
        }
        
        // Getters
        public int getTotalShows() { return totalShows; }
        public int getTotalBookings() { return totalBookings; }
        public int getActiveBookings() { return activeBookings; }
        public int getCancelledBookings() { return cancelledBookings; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getTotalRefunded() { return totalRefunded; }
        public double getCancellationRate() { return cancellationRate; }
        
        @Override
        public String toString() {
            return String.format(
                "System Statistics:\n" +
                "Total Shows: %d | Total Bookings: %d\n" +
                "Active: %d | Cancelled: %d (%.1f%%)\n" +
                "Revenue: ₹%.2f | Refunded: ₹%.2f",
                totalShows, totalBookings,
                activeBookings, cancelledBookings, cancellationRate,
                totalRevenue, totalRefunded
            );
        }
    }
}