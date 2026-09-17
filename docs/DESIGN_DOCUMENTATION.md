# Event Ticket & Seat Management System - Technical Design Document

## 1. Problem Statement
In educational, corporate, and entertainment domains, organizing events and managing seating arrangements manually or via fragmented spreadsheet tools leads to significant inefficiencies. Key challenges include:
- **Double-booking conflicts**: Simultaneous attempts to reserve identical seats.
- **Lack of transparency**: Inability for attendees to view real-time seat maps (VIP vs Regular vs Economy).
- **Opaque revenue reporting**: Difficulty tracking ticket sales, seat occupancy rates, and cancellation refunds.
- **System inefficiency**: Bloated web applications for simple CLI environments.

The **Event Ticket & Seat Management System** addresses these issues by providing a lightweight, robust Java terminal-based application that manages users, interactive seating layouts, ticket reservations, double-booking prevention, cancellations, and executive reporting.

---

## 2. Project Objectives
1. **Identify & Solve Real-World Problems**: Eliminate double-bookings and provide clear seat grid visualizations in CLI environments.
2. **Modular Technical Solution**: Apply Object-Oriented Programming (OOP) principles (Encapsulation, Inheritance, Abstraction, Polymorphism) and design patterns.
3. **Multi-Role User Management**: Provide secure access control for Admin and Customer roles.
4. **Data Persistence & Reliability**: Save system state locally across restarts without external database dependencies.
5. **Quality Assurance**: Implement comprehensive error validation and automated unit testing.

---

## 3. Requirements Specification

### 3.1 Functional Requirements
The system is divided into three core functional modules:

1. **User Management Module**:
   - User registration (Customer/Admin roles).
   - Authentication (Username & password verification).
   - Session tracking & role-based menu navigation.

2. **Event & Seat Management Module**:
   - Admin creation of events (Title, Venue, Date, Category, Custom Grid Rows x Cols, Base Price).
   - Automated seat map generation with tiered categories (VIP 150%, Regular 100%, Economy 85%).
   - Interactive CLI visualization of seat maps showing real-time seat availability (`[V A1]`, `[X BOOK]`).

3. **Booking & Analytics Module**:
   - Multi-seat selection and real-time transaction processing.
   - Atomic double-booking prevention and seat locking.
   - Ticket cancellation and automatic seat release with refund calculation.
   - System analytics: gross revenue, total active/cancelled tickets, seat occupancy rates.
   - Digital E-Ticket export to formatted text file (`tickets/Ticket_<ID>.txt`).

### 3.2 Non-Functional Requirements
1. **Performance**: Transaction response time under 100ms for seat reservation and layout rendering.
2. **Security**: Role-Based Access Control (RBAC) preventing normal users from accessing admin controls or modifying other users' tickets.
3. **Reliability & Data Integrity**: Synchronized booking methods preventing race conditions during concurrent seat requests; persistent disk storage (`data/system_store.dat`).
4. **Usability**: Clean ASCII terminal interface, formatted tables, and clear visual seat layout keys.
5. **Maintainability**: Clear separation of concerns into `model`, `service`, `util`, and `test` packages.
6. **Error Handling Strategy**: Graceful handling of invalid inputs (wrong formats, non-existent seats, duplicate usernames) with user-friendly warnings rather than application crashes.

---

## 4. System Architecture Diagram

```mermaid
graph TD
    subgraph Client Layer
        CLI[Main Interactive Terminal CLI]
    end

    subgraph Service Layer
        US[UserService]
        ES[EventService]
        BS[BookingService]
        RS[ReportService]
    end

    subgraph Model Layer
        User[User Model]
        Event[Event Model]
        Seat[Seat Model]
        Booking[Booking Model]
    end

    subgraph Data & Storage Layer
        DS[DataStore Manager]
        File[(Persistent Disk Storage / data/system_store.dat)]
        Txt[E-Ticket File Exporter / tickets/*.txt]
    end

    CLI --> US
    CLI --> ES
    CLI --> BS
    CLI --> RS

    US --> User
    ES --> Event
    ES --> Seat
    BS --> Booking
    BS --> Seat

    US --> DS
    ES --> DS
    BS --> DS
    RS --> Txt

    DS <--> File
```

---

## 5. Process Flow Diagram

```mermaid
flowchart TD
    Start([Start Application]) --> LoadData[Load DataStore from Disk]
    LoadData --> LoginMenu{User Logged In?}

    LoginMenu -- No --> GuestOptions[Show Guest Menu]
    GuestOptions --> Register[Register / Login] --> LoginMenu
    GuestOptions --> ViewEventsPublic[View Events] --> GuestOptions

    LoginMenu -- Yes --> RoleCheck{User Role?}

    RoleCheck -- CUSTOMER --> CustMenu[Customer Menu]
    CustMenu --> ViewSeats[View Event Seat Map]
    CustMenu --> BookSeat[Select Event & Seats]
    BookSeat --> ValidateSeat{Seats Available?}
    ValidateSeat -- No --> SeatError[Show Error & Prompt Again] --> CustMenu
    ValidateSeat -- Yes --> ConfirmBook[Lock Seats & Generate Ticket] --> CustMenu
    CustMenu --> CancelCustTicket[Cancel My Ticket] --> CustMenu
    CustMenu --> ExportTicket[Export E-Ticket File] --> CustMenu

    RoleCheck -- ADMIN --> AdminMenu[Admin Control Center]
    AdminMenu --> CreateEvt[Create New Event & Seat Map] --> AdminMenu
    AdminMenu --> ViewReport[View Revenue & Occupancy Report] --> AdminMenu
    AdminMenu --> ViewAllBk[View All System Bookings] --> AdminMenu
    AdminMenu --> CancelAny[Cancel Any Ticket] --> AdminMenu

    CustMenu --> Logout[Logout] --> LoginMenu
    AdminMenu --> Logout --> LoginMenu
```

---

## 6. UML Diagrams

### 6.1 Use Case Diagram

```mermaid
graph LR
    subgraph Actors
        Guest((Guest))
        Customer((Customer))
        Admin((Admin))
    end

    subgraph Event Ticket System
        UC1(Register Account)
        UC2(Login / Authenticate)
        UC3(Browse Events)
        UC4(View Interactive Seat Map)
        UC5(Book Selected Seats)
        UC6(Cancel Own Ticket)
        UC7(Export E-Ticket File)
        UC8(Create New Event)
        UC9(View System Analytics Report)
        UC10(Cancel Any Ticket)
        UC11(Manage System Users)
    end

    Guest --> UC1
    Guest --> UC2
    Guest --> UC3

    Customer --> UC2
    Customer --> UC3
    Customer --> UC4
    Customer --> UC5
    Customer --> UC6
    Customer --> UC7

    Admin --> UC2
    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
```

---

### 6.2 Class Diagram

```mermaid
classDiagram
    class User {
        -String userId
        -String username
        -String password
        -String fullName
        -String email
        -Role role
        +getters/setters()
    }

    class Seat {
        -String seatNumber
        -int row
        -int col
        -Category category
        -double price
        -boolean isBooked
        -String bookedByUserId
        +getters/setters()
    }

    class Event {
        -String eventId
        -String title
        -String venue
        -String dateTime
        -String category
        -int rows
        -int cols
        -double basePrice
        -List~Seat~ seats
        +initializeSeats()
        +getSeatByNumber(seatNo)
        +getAvailableSeatsCount()
    }

    class Booking {
        -String bookingId
        -String userId
        -String eventId
        -String eventTitle
        -List~String~ seatNumbers
        -double totalPrice
        -String bookingTimestamp
        -BookingStatus status
        +getters/setters()
    }

    class DataStore {
        -List~User~ users
        -List~Event~ events
        -List~Booking~ bookings
        +save()
        +load()
    }

    class UserService {
        -DataStore dataStore
        -User currentUser
        +registerUser()
        +login()
        +logout()
    }

    class EventService {
        -DataStore dataStore
        +createEvent()
        +displaySeatMap()
        +searchEvents()
    }

    class BookingService {
        -DataStore dataStore
        +createBooking()
        +cancelBooking()
    }

    Event "1" *-- "many" Seat : contains
    DataStore "1" o-- "many" User : stores
    DataStore "1" o-- "many" Event : stores
    DataStore "1" o-- "many" Booking : stores
    UserService --> DataStore
    EventService --> DataStore
    BookingService --> DataStore
```

---

### 6.3 Sequence Diagram: Seat Reservation & Ticket Generation

```mermaid
sequenceDiagram
    autonumber
    actor Customer
    participant Main as Main CLI
    participant ES as EventService
    participant BS as BookingService
    participant DS as DataStore

    Customer->>Main: Select "Book Tickets" & Enter Event ID (E101)
    Main->>ES: displaySeatMap(E101)
    ES-->>Main: Render Grid Layout (VIP/Regular/Economy)
    Customer->>Main: Enter Seats ("A1, A2")
    Main->>BS: createBooking(User, Event, ["A1", "A2"])
    BS->>BS: Validate seat availability & calculate price
    alt Seats Available
        BS->>BS: Mark seats as Booked (isBooked = true)
        BS->>DS: Add new Booking & save()
        DS-->>BS: Data persisted to disk
        BS-->>Main: Return Confirmed Booking Object
        Main-->>Customer: Display Confirmation & Ticket ID (#TKT10001)
    else Seat Already Booked / Invalid
        BS-->>Main: Return null with Error Message
        Main-->>Customer: Display Double-Booking Error Notice
    end
```

---

## 7. Storage Schema & Data Design

The application uses serialized object storage (`data/system_store.dat`) managed by `DataStore.java`, supplemented by exported text documents (`tickets/Ticket_<ID>.txt`).

### Data Entities & Schema

1. **User Entity**:
   - `userId`: String (Primary Key, e.g. "U101")
   - `username`: String (Unique)
   - `password`: String
   - `fullName`: String
   - `email`: String
   - `role`: Enum (`ADMIN`, `CUSTOMER`)

2. **Event Entity**:
   - `eventId`: String (Primary Key, e.g. "E101")
   - `title`: String
   - `venue`: String
   - `dateTime`: String
   - `category`: String
   - `rows`: Integer
   - `cols`: Integer
   - `basePrice`: Double
   - `seats`: List<Seat> (Nested 1-to-N relationship)

3. **Seat Entity (Nested within Event)**:
   - `seatNumber`: String (Composite Key within event, e.g. "A1")
   - `row`: Integer, `col`: Integer
   - `category`: Enum (`VIP`, `REGULAR`, `ECONOMY`)
   - `price`: Double
   - `isBooked`: Boolean
   - `bookedByUserId`: String (Foreign Key to User)

4. **Booking Entity**:
   - `bookingId`: String (Primary Key, e.g. "TKT10001")
   - `userId`: String (Foreign Key to User)
   - `eventId`: String (Foreign Key to Event)
   - `seatNumbers`: List<String>
   - `totalPrice`: Double
   - `bookingTimestamp`: String
   - `status`: Enum (`CONFIRMED`, `CANCELLED`)
