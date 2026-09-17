# Project Report: Event Ticket & Seat Management System

---

## 2. Introduction

The **Event Ticket & Seat Management System** is a modular, terminal-based Java application developed to streamline event scheduling, dynamic venue seating layout generation, real-time ticket reservations, and executive revenue reporting. 

In modern academic institutions, corporate environments, and entertainment venues, ticket distribution and seating arrangements are critical components of event operations. Manual paper-based booking or fragmented spreadsheet management often leads to seat allocation errors, double-booking conflicts, and opaque revenue reporting.

This project implements a lightweight yet robust technical solution leveraging core Object-Oriented Programming (OOP) concepts, software design patterns, serialized file persistence, and an automated unit testing harness. Designed to operate within a terminal environment (Command Line Interface - CLI), it balances high computational efficiency with a clean user experience for both administrators and attendees.

---

## 3. Problem Statement

In conventional manual or spreadsheet-assisted event management workflows, event organizers face several core operational challenges:

1. **Double-Booking Conflicts**: Simultaneous attempts by multiple users to reserve identical seat locations lead to overlapping tickets and customer dissatisfaction.
2. **Lack of Transparent Seating Layouts**: Attendees cannot visually inspect venue seating grids to distinguish between premium (VIP), standard (Regular), and budget (Economy) seating options.
3. **Manual Revenue & Occupancy Calculations**: Calculating ticket sales metrics, refund amounts upon cancellation, and venue occupancy percentages manually is time-consuming and error-prone.
4. **Unnecessary Framework Complexity**: Enterprise web frameworks often introduce bloated setups, complex server configurations, and database management overhead when a lightweight, self-contained terminal solution is required.

---

## 4. Functional Requirements

The system provides three major functional modules:

### 4.1 User Management & Authentication Module
- **User Registration**: New attendees can register for a Customer account by providing a username, password, full name, and email address.
- **Role-Based Access Control (RBAC)**: System enforces distinct privileges for **Customer** and **Administrator** roles.
- **User Authentication**: Secure login verification against stored credentials and active session tracking.

### 4.2 Event & Seat Layout Management Module
- **Event Creation**: Administrators can schedule new events by configuring title, venue, date/time, event category, seating grid dimensions (rows × columns), and base ticket pricing.
- **Dynamic Tiered Pricing**: Automated categorization and pricing adjustments:
  - **VIP Seats** (Row 1): 50% price markup (`basePrice × 1.50`).
  - **Regular Seats** (Middle Rows): Base price (`basePrice`).
  - **Economy Seats** (Final Row): 15% price discount (`basePrice × 0.85`).
- **Visual ASCII Seat Layout Display**: Real-time CLI grid displaying seat availability (e.g., `[V A1]`, `[R B2]`, `[X BOOK]`).
- **Event Search & Filtering**: Search events by title keyword, category, or venue.

### 4.3 Booking, Cancellation & Reporting Module
- **Multi-Seat Reservation**: Customers can reserve one or multiple seats in a single transaction.
- **Atomic Double-Booking Prevention**: Synchronized validation ensures already-booked seats cannot be selected.
- **Ticket Cancellation & Refunds**: Users can cancel active bookings, releasing reserved seats back to available state and calculating eligible refunds.
- **E-Ticket Receipt Generation**: Generates digital text-based admission receipts exported to `.txt` files (`tickets/Ticket_<ID>.txt`).
- **Executive System Analytics**: Admin reporting detailing gross revenue, total active/cancelled bookings, and venue occupancy percentages.

---

## 5. Non-Functional Requirements

1. **Performance**: Transaction response time under 100 milliseconds for seat locking, ticket calculations, and ASCII layout rendering.
2. **Security**: Role-Based Access Control (RBAC) ensuring standard customers cannot access administrative controls or cancel other users' tickets.
3. **Reliability & Data Integrity**: Synchronized reservation logic preventing race conditions during simultaneous seat requests; persistent storage (`data/system_store.dat`).
4. **Usability**: Intuitive interactive terminal menus, clear error messaging, and readable ASCII seat map visual keys.
5. **Maintainability**: Clean package structure (`model`, `service`, `util`, `test`) separating data definitions, business logic, storage operations, and execution CLI.
6. **Error Handling Strategy**: Robust input validation (regex checks for emails, usernames, dates, seat ranges) preventing application crashes due to malformed user input.

---

## 6. System Architecture

The project follows a decoupled **Layered Architecture** adhering to the separation of concerns principle:

1. **Presentation Layer (`com.eventticket.Main`)**:
   - Manages terminal input/output, interactive menus, ASCII banner formatting, and role-based navigation loops.
2. **Service Layer (`com.eventticket.service.*`)**:
   - Contains core business logic:
     - `UserService`: User registration, password validation, login session.
     - `EventService`: Event CRUD, seat map layout rendering, searching.
     - `BookingService`: Transaction processing, seat locking, double-booking validation, cancellations.
     - `ReportService`: System analytics, occupancy metrics, ticket receipt export.
3. **Model Layer (`com.eventticket.model.*`)**:
   - Encapsulates domain entities: `User`, `Event`, `Seat`, `Booking`.
4. **Data Persistence Layer (`com.eventticket.util.DataStore`)**:
   - Manages Java Object Serialization (`java.io.Serializable`) to save and reload system state locally on disk (`data/system_store.dat`).
5. **Utility & Testing (`com.eventticket.util.*`, `com.eventticket.test.*`)**:
   - `InputValidator`: Regex-based string sanitization.
   - `SystemTestSuite`: Automated test runner executing 28 verification assertions.

---

## 8. Design Decisions & Rationale

1. **Terminal-Based CLI Interface**:
   - *Rationale*: Chosen over GUI frameworks (Swing/JavaFX) to meet assignment requirements for a clean, dependency-free execution environment that compiles and runs on any Java 17 system without platform-specific rendering bugs.
2. **Java Object Serialization for Data Persistence**:
   - *Rationale*: Storing system state using `ObjectOutputStream` and `ObjectInputStream` eliminates external SQL database setups (e.g., MySQL/PostgreSQL), making the repository self-contained and portable for grading.
3. **Tiered Dynamic Pricing Formula**:
   - *Rationale*: Implementing VIP (+50%) and Economy (-15%) tiers directly inside `Event.java` models realistic event venue economics.
4. **Custom Unit Testing Harness**:
   - *Rationale*: Built a zero-dependency test runner (`SystemTestSuite.java`) using static assertion methods, allowing automated unit testing without requiring external Maven/JUnit dependencies.
5. **Data Store Isolation for Unit Testing**:
   - *Rationale*: The test suite operates on `data/test_store.dat`, preventing automated test runs from clearing or corrupting default admin accounts or active user data in `data/system_store.dat`.

---

## 9. Implementation Details

- **Language & Runtime**: Java 17 (LTS)
- **Source Code Structure**:
  - `src/com/eventticket/Main.java`: System entry point and menu handler.
  - `src/com/eventticket/model/`: `User.java`, `Event.java`, `Seat.java`, `Booking.java`.
  - `src/com/eventticket/service/`: `UserService.java`, `EventService.java`, `BookingService.java`, `ReportService.java`.
  - `src/com/eventticket/util/`: `DataStore.java`, `InputValidator.java`.
  - `src/com/eventticket/test/`: `SystemTestSuite.java`.
- **Concurrency & Transaction Handling**:
  - `BookingService.java` utilizes `synchronized` methods during reservation and cancellation procedures to guarantee atomic state updates.
- **File Exporter**:
  - `ReportService.java` uses `PrintWriter` and `FileWriter` to format and generate printable ticket receipts under `tickets/Ticket_<ID>.txt`.

---

## 11. Testing Approach

Testing was conducted using a two-pronged strategy:

1. **Automated Unit Testing**:
   - Developed `SystemTestSuite.java` covering 28 automated test assertions across 5 core modules:
     - *Module 1*: User Registration, Role Assignment, Duplicate User Blocking, Credential Validation.
     - *Module 2*: Event Creation, Grid Dimension Calculation, Tier Pricing Verification (VIP, Regular, Economy).
     - *Module 3*: Seat Locking, Price Aggregation, Atomic Double-Booking Rejection.
     - *Module 4*: Ticket Cancellation, Status Updates, Seat Releasing.
     - *Module 5*: E-Ticket TXT File Generation, DataStore Serialization Save/Reload Integrity.
   - **Test Result**: All 28 unit tests passed cleanly with 0 failures.

2. **Manual Functional Testing**:
   - Verified Admin workflows (Event creation, analytics inspection, global ticket cancellation).
   - Verified Customer workflows (Account registration, login, searching events, multi-seat selection, receipt export).
   - Tested invalid user input handling (wrong password attempts, non-existent seat numbers, invalid date strings).

---

## 12. Challenges Faced

1. **Double-Booking Race Conditions**:
   - *Challenge*: Preventing two simultaneous booking calls from reserving the same seat.
   - *Resolution*: Implemented synchronized transaction locks in `BookingService.java` and explicit pre-reservation checks on `seat.isBooked()`.
2. **Data Storage Integrity Across Test Executions**:
   - *Challenge*: Running automated unit tests cleared default admin credentials in the main application state file.
   - *Resolution*: Refactored `DataStore.java` to accept configurable file paths (`data/test_store.dat` vs `data/system_store.dat`) and implemented `ensureDefaultData()` checks.
3. **CLI Seat Map Rendering Clarity**:
   - *Challenge*: Displaying large seating grids in terminal windows without text wrapping or misalignment.
   - *Resolution*: Designed fixed-width string formatting (`printf(" [%s %-3s] ", catCode, seatNo)`) to maintain strict visual grid alignment.

---

## 13. Learnings & Key Takeaways

1. **Object-Oriented Design**: Gained hands-on experience structuring domain models, services, and utility classes following Encapsulation, Abstraction, and Single Responsibility Principles.
2. **State Management & Serialization**: Learned how Java Object Serialization simplifies local persistence without database overhead.
3. **Defensive Programming & Input Validation**: Understood the necessity of sanitizing CLI inputs via regular expressions to maintain application stability.
4. **Unit Testing Automation**: Realized the value of self-contained test suites in catching regression bugs early during development.

---

## 14. Future Enhancements

1. **GUI Interface**: Upgrade presentation layer using JavaFX or Swing for interactive point-and-click seat selection.
2. **Database Integration**: Replace file serialization with relational database storage (SQLite / MySQL) via JDBC.
3. **Payment Gateway API**: Integrate mock online payment processing (Stripe / PayPal sandbox APIs).
4. **Notification Service**: Add automated email notifications (JavaMail API) sending e-tickets directly to registered customer email addresses.

---

## 15. References

1. Oracle Java Documentation: *Java SE 17 Developer Guide - Object Serialization*. [Online]. Available: https://docs.oracle.com/en/java/javase/17/
2. E. Gamma, R. Helm, R. Johnson, and J. Vlissides, *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994.
3. R. C. Martin, *Clean Code: A Handbook of Agile Software Craftsmanship*. Prentice Hall, 2008.
