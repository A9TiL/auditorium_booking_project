# 📦 Project Structure Overview

```
auditorium_project/
│
├── src/
│   └── com/auditorium/
│       │
│       ├── model/                    [Domain Layer - 4 classes]
│       │   ├── Show.java            ✓ Show entity with pricing & seats
│       │   ├── Seat.java            ✓ Seat with type & booking status
│       │   ├── Booking.java         ✓ Booking transaction record
│       │   └── SalesPerson.java     ✓ User with authentication
│       │
│       ├── service/                  [Business Logic Layer - 3 services]
│       │   ├── ShowManager.java     ✓ Singleton controller
│       │   ├── BookingService.java  ✓ Seat allocation logic
│       │   └── CancellationService.java ✓ Refund calculator
│       │
│       ├── factory/                  [Design Patterns]
│       │   └── SeatFactory.java     ✓ Factory pattern
│       │
│       ├── builder/                  [To be added in Step 3]
│       │   └── TicketBuilder.java   ⏳ Builder pattern
│       │
│       ├── ui/                       [To be added in Step 4]
│       │   └── [Swing components]   ⏳ GUI layer
│       │
│       └── main/
│           └── Main.java            ✓ Console entry point
│
├── bin/                             [Compiled .class files]
│   └── com/auditorium/...
│
└── README.md                        ✓ Documentation

```

## 🎯 Design Pattern Implementation Status

| Pattern   | Class              | Status | Purpose                    |
|-----------|-------------------|--------|----------------------------|
| Singleton | ShowManager       | ✅ Done | Central system controller  |
| Factory   | SeatFactory       | ✅ Done | Create seats with numbering|
| Builder   | TicketBuilder     | ⏳ Next | Build complex tickets      |

## 📊 Line Count by Layer

```
Model Layer:        ~120 lines  (4 classes)
Service Layer:      ~240 lines  (3 classes)
Factory Layer:      ~30 lines   (1 class)
Console UI:         ~150 lines  (1 class)
────────────────────────────────────────
Total:              ~540 lines
```

## 🔄 Data Flow

```
User Input → Main.java
    ↓
ShowManager (Singleton)
    ↓
BookingService ← CancellationService
    ↓
SeatFactory → Seat, Booking, Show
    ↓
Result → User
```

## ✨ Key Architectural Decisions

1. **Separation of Concerns**: Model ≠ Service ≠ UI
2. **Single Responsibility**: Each class has one job
3. **DRY Principle**: Refund logic centralized in CancellationService
4. **Encapsulation**: Private fields, public methods
5. **Type Safety**: Enums for SeatType, not strings
