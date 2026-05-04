package com.auditorium.factory;

import com.auditorium.model.Seat;
import com.auditorium.model.Seat.SeatType;
import java.util.ArrayList;
import java.util.List;

public class SeatFactory {
    
    // Factory method to create seats
    public static List<Seat> createSeats(SeatType type, int count) {
        List<Seat> seats = new ArrayList<>();
        String prefix = (type == SeatType.BALCONY) ? "B" : "O";
        
        for (int i = 1; i <= count; i++) {
            String seatNumber = prefix + String.format("%03d", i);
            seats.add(new Seat(seatNumber, type));
        }
        
        return seats;
    }
    
    // Create balcony seats
    public static List<Seat> createBalconySeats(int count) {
        return createSeats(SeatType.BALCONY, count);
    }
    
    // Create ordinary seats
    public static List<Seat> createOrdinarySeats(int count) {
        return createSeats(SeatType.ORDINARY, count);
    }
}
