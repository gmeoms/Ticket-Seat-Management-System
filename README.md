# Event Ticket & Seat Management System 

A modular, terminal-based **Event Ticket & Seat Management System** written in Java. Built using standard Object-Oriented Programming (OOP) principles, modular architecture, serialized file persistence, and CLI interactive visualization.

---

## 🛠️ Technologies & Tools Used

- **Programming Language**: Java 17 
- **Architecture**: Modular Object-Oriented Design 
- **Data Persistence**: Java Object Serialization
- **Testing Framework**: Custom Automated Unit Test Harness 
- **Version Control**: Git & GitHub 
- **Build & Launch Scripts**: Windows Batch 

---

## 🌟 Key Features

1. **User Management & Role-Based Access Control**:
   - Customer & Administrator role separation.
   - User registration and credential authentication.

2. **Event & Seat Layout Management**:
   - Event creation with dynamic row x column grid layouts.
   - Multi-tier seat pricing (VIP 150%, Regular 100%, Economy 85%).
   - Printable ASCII visual seat map showing available (`[V A1]`) vs booked (`[X BOOK]`) seats.

3. **Booking & Transaction Management**:
   - Multi-seat reservation with atomic double-booking prevention.
   - Ticket cancellation with automatic seat release and refund calculations.
   - Export e-ticket admission receipts to `.txt` files in `tickets/` directory.

4. **Analytics & Executive Reporting**:
   - Admin overview reporting gross revenue, seat occupancy percentages, and booking statistics.

5. **Persistence & Quality Assurance**:
   - Automatic local file persistence (`data/system_store.dat`).

---

## 🖥️ Terminal UI Preview / Screenshots

### 1. Interactive Seat Map Layout
```
==========================================================================
                 SEAT MAP LAYOUT FOR: Championship Basketball Final
==========================================================================
                  [ STAGE / SCREEN FRONT ]
--------------------------------------------------------------------------
      Col 1   Col 2   Col 3   Col 4   Col 5

Row A [V A1 ] [V A2 ] [V A3 ] [V A4 ] [V A5 ]
Row B [R B1 ] [R B2 ] [R B3 ] [R B4 ] [R B5 ]
Row C [R C1 ] [R C2 ] [R C3 ] [R C4 ] [R C5 ]
Row D [R D1 ] [R D2 ] [R D3 ] [R D4 ] [R D5 ]
Row E [E E1 ] [E E2 ] [E E3 ] [E E4 ] [E E5 ]
--------------------------------------------------------------------------
Legend: [V] VIP (50% premium)  |  [R] Regular  |  [E] Economy  |  [X BOOK] Booked
==========================================================================
```

### 2. Exported E-Ticket (.txt)
```
=========================================================
               EVENT ADMISSION TICKET                    
=========================================================
 Ticket Reference : TKT10003
 Event Title      : Rock Concert 2026
 Category         : Music
 Venue            : Arena Hall
 Date & Time      : 2026-11-10 20:00
---------------------------------------------------------
 Attendee Name    : Rajat Kumar (rajat@example.com)
 Reserved Seats   : A1, A2
 Total Paid       : $300.00
 Booking Status   : CONFIRMED
 Issued At        : 2026-09-18 01:04
=========================================================
```

---

## 🔑 Default Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` |
| **Sample Customer** | `rajat` | `rajat123` |

*(You can also register new Customer or Admin accounts via the CLI interface).*

---

## 🚀 Installation & Execution Steps

### Prerequisites
- Java Development Kit (JDK 17 or higher installed).

### Windows PowerShell
- **Run Application**:
  ```powershell
  .\run.bat
  ```
- **Run Unit Test Suite**:
  ```powershell
  .\run-tests.bat
  ```

### Windows Command Prompt (cmd.exe)
- **Run Application**:
  ```cmd
  run.bat
  ```
- **Run Unit Test Suite**:
  ```cmd
  run-tests.bat
  ```

### Linux / macOS / Bash Shell
- **Run Application**:
  ```bash
  chmod +x run.sh
  ./run.sh
  ```

---

## 🧪 Instructions for Testing

The system includes a self-contained test suite `SystemTestSuite.java` verifying 28 test assertions across all 5 core system modules.

To execute the unit tests manually via Java CLI:
```bash
javac -d bin -sourcepath src src/com/eventticket/test/SystemTestSuite.java
java -cp bin com.eventticket.test.SystemTestSuite
```

---

## 📂 Project Structure

```
.
├── docs/
│   └── DESIGN_DOCUMENTATION.md      # Full UML diagrams, Architecture, Schema
├── statement.md                     # Problem statement, scope & target users
├── src/
│   └── com/eventticket/
│       ├── Main.java                # CLI Menu Interface & Execution
│       ├── model/
│       │   ├── User.java            # User data model (Admin/Customer roles)
│       │   ├── Event.java           # Event data model & seat map logic
│       │   ├── Seat.java            # Seat entity & pricing tier logic
│       │   └── Booking.java         # Ticket booking model
│       ├── service/
│       │   ├── UserService.java     # Authentication & user sessions
│       │   ├── EventService.java    # Event CRUD & ASCII seat map rendering
│       │   ├── BookingService.java  # Seat reservations & double-booking block
│       │   └── ReportService.java   # Executive reports & E-Ticket TXT export
│       ├── util/
│       │   ├── DataStore.java       # Local disk object storage & seed data
│       │   └── InputValidator.java  # Input validation & timestamps
│       └── test/
│           └── SystemTestSuite.java # Automated test suite (28 assertions)
├── .gitignore                       # Git ignore configuration
├── run.bat                          # Windows CLI launcher
├── run-tests.bat                    # Windows unit test runner
├── run.sh                           # Shell script launcher
└── README.md                        # Project documentation overview
```

---

## 📄 Related Documentation
- [statement.md](file:///c:/Users/RAJAT%20KUMAR/Desktop/New%20folder%20(2)/statement.md): Problem Statement, Scope, Target Users, and High-Level Features.
- [docs/DESIGN_DOCUMENTATION.md](file:///c:/Users/RAJAT%20KUMAR/Desktop/New%20folder%20(2)/docs/DESIGN_DOCUMENTATION.md): System Architecture, Process Flow, UML Diagrams (Use Case, Class, Sequence), and Schema Design.
