-- ONE-CLICK DATABASE SETUP
-- Run this file in MySQL Workbench from top to bottom.
-- It recreates the database, creates all objects, and loads demo data.
SOURCE schema.sql;
SOURCE sample_data.sql;

USE ticket_seat_management;

SELECT 'SETUP COMPLETE' AS status;
SELECT 'Users' AS item, COUNT(*) AS row_count FROM users
UNION ALL SELECT 'Venues', COUNT(*) FROM venues
UNION ALL SELECT 'Screens', COUNT(*) FROM screens
UNION ALL SELECT 'Seats', COUNT(*) FROM seats
UNION ALL SELECT 'Events', COUNT(*) FROM events
UNION ALL SELECT 'Shows', COUNT(*) FROM shows
UNION ALL SELECT 'Bookings', COUNT(*) FROM bookings
UNION ALL SELECT 'Booking Seats', COUNT(*) FROM booking_seats
UNION ALL SELECT 'Payments', COUNT(*) FROM payments
UNION ALL SELECT 'Tickets', COUNT(*) FROM tickets;
