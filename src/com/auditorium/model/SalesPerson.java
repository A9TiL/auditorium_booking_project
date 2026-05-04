package com.auditorium.model;

public class SalesPerson {
    private String salesPersonId;
    private String name;
    private String username;
    private String password;
    private double totalSales;
    private int transactionCount;
    
    public SalesPerson(String salesPersonId, String name, String username, String password) {
        this.salesPersonId = salesPersonId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.totalSales = 0.0;
        this.transactionCount = 0;
    }
    
    // Record a sale
    public void recordSale(double amount) {
        this.totalSales += amount;
        this.transactionCount++;
    }
    
    // Verify password
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
    
    // Getters
    public String getSalesPersonId() { return salesPersonId; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public double getTotalSales() { return totalSales; }
    public int getTransactionCount() { return transactionCount; }
    
    @Override
    public String toString() {
        return String.format("SalesPerson: %s | Sales: ₹%.2f | Transactions: %d",
                name, totalSales, transactionCount);
    }
}
