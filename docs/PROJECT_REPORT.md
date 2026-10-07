# Ticket & Seat Management System

## 1. Introduction
The Ticket & Seat Management System is a relational database project that manages venues, screens, physical seats, events, shows, customers, bookings, payments and tickets. Its main purpose is to provide accurate seat availability and prevent duplicate reservations.

## 2. Problem Statement
Manual ticket systems can cause duplicate seat allocation, inaccurate availability, poor booking history and difficulty generating reports. A centralized relational database solves these problems through primary keys, foreign keys, constraints, transactions, views and triggers.

## 3. Objectives
- Maintain venue and screen information.
- Maintain a fixed inventory of seats for each screen.
- Schedule events and shows.
- Display available/booked seats for each show.
- Create and track customer bookings.
- Record payments and generate unique tickets.
- Prevent double booking.
- Generate useful management reports.

## 4. Main Modules
1. User Management
2. Venue and Screen Management
3. Seat Management
4. Event and Show Scheduling
5. Seat Availability
6. Booking and Ticket Generation
7. Payment Management
8. Reports

## 5. Database Design
The database contains 10 main relations: USERS, VENUES, SCREENS, SEATS, EVENTS, SHOWS, BOOKINGS, BOOKING_SEATS, PAYMENTS and TICKETS. The many-to-many relationship between bookings and seats is resolved through BOOKING_SEATS.

## 6. Normalization
**1NF:** All attributes contain atomic values; repeating seat groups are separated into BOOKING_SEATS.

**2NF:** Non-key attributes depend on the complete primary key. Seat details are stored in SEATS rather than repeating them in booking records.

**3NF:** Non-key attributes do not depend transitively on another non-key attribute. Venue, screen, event, payment and ticket information are separated into their own relations.

## 7. Integrity and Security
- Primary keys uniquely identify records.
- Foreign keys enforce relationships.
- UNIQUE constraints prevent duplicate emails, seat definitions and ticket codes.
- CHECK constraints validate positive prices and seat counts.
- A trigger prevents booking a seat already reserved for the same show and ensures the seat belongs to the show's screen.
- The stored procedure `book_seat` performs booking operations in a transaction.

## 8. Booking Workflow
User selects a show → system loads seats for the show's screen → unavailable seats are disabled → user selects seats → booking is created → payment is recorded → ticket code is generated.

## 9. Technologies
- MySQL 8.x database
- SQL
- Python Flask demonstration backend
- HTML/CSS/JavaScript demonstration frontend

## 10. Future Scope
Authentication, real payment gateway integration, temporary seat holds with expiry, QR-code tickets, admin dashboard and email/SMS notifications can be added.

## 11. Conclusion
The project demonstrates core DBMS concepts including relational modeling, normalization, constraints, joins, views, stored procedures, triggers and transactions through a practical ticket booking use case.
