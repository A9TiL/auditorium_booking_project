# Students Auditorium Management System

## 🎯 Current Status: **STEP 1 & 2 COMPLETE** ✓

### ✅ What's Implemented (Console Version with OOP)

**Core Models:**
- `Show` - Manages show details, pricing, seat allocation
- `Seat` - Individual seat with type (Balcony/Ordinary) and booking status
- `Booking` - Represents a booking transaction with cancellation support
- `SalesPerson` - User authentication and sales tracking

**Design Patterns Implemented:**
- ✅ **Singleton** - `ShowManager` (central controller)
- ✅ **Factory** - `SeatFactory` (creates seats)
- ⏳ **Builder** - `TicketBuilder` (to be added)

**Services:**
- `ShowManager` - Singleton managing all shows, bookings, sales persons
- `BookingService` - Handles booking and seat allocation logic
- `CancellationService` - Time-based refund calculation (3-tier logic)

**Key Features Working:**
- ✓ Login/Logout system
- ✓ Show creation with dynamic pricing
- ✓ Seat availability queries
- ✓ Booking with automatic seat assignment
- ✓ Cancellation with smart refund calculation
- ✓ Sales tracking per person
- ✓ Revenue reports

---

## 🚀 How to Run (Console Version)

```bash
# Compile
cd auditorium_project
javac -d bin $(find src -name "*.java")

# Run
java -cp bin com.auditorium.main.Main
```

**Test Credentials:**
- Manager: username=`manager`, password=`admin123`
- Sales Person: username=`rajesh`, password=`pass123`

---

## 📋 Next Steps

### **STEP 3: Collections + Advanced Logic** 
- [ ] Add `TicketBuilder` (Builder pattern)
- [ ] Implement percentage calculations for seat booking
- [ ] Add date validation (prevent past-date bookings)
- [ ] Commission calculation logic
- [ ] Multiple shows per date support

### **STEP 4: Swing UI**
- [ ] Login Frame
- [ ] Main Dashboard
- [ ] Show Management Panel
- [ ] Booking Panel with seat selection
- [ ] Cancellation Panel
- [ ] Reports Panel

### **STEP 5: File Persistence**
- [ ] Save/load shows to/from files
- [ ] Save/load bookings
- [ ] Save/load sales persons
- [ ] Auto-save on changes

---

## 🏗️ Package Structure

```
src/
├── com.auditorium.model/      ← Domain entities
├── com.auditorium.service/    ← Business logic
├── com.auditorium.factory/    ← Factory pattern
├── com.auditorium.builder/    ← Builder pattern (to add)
├── com.auditorium.ui/         ← Swing GUI (to add)
└── com.auditorium.main/       ← Entry point
```

---

## 💡 Creative Decisions Made

1. **Time-Delta Cancellation Logic**: Single method calculates refund based on days until show
2. **Factory for Seats**: Automatic sequential numbering (B001, B002 for balcony)
3. **Singleton ShowManager**: One source of truth for all data
4. **Immutable Booking IDs**: Auto-generated unique IDs (BK + UUID)

---

## 📊 What Makes This Different

- **No database needed** - Pure Java collections
- **Smart cancellation** - Automatically calculates based on date difference
- **Sales tracking** - Built-in commission calculation support
- **Clean separation** - Model → Service → UI layers


