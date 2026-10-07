USE ticket_seat_management;

SELECT 'TABLE COUNTS' AS section;
SELECT 'users' table_name, COUNT(*) row_count FROM users
UNION ALL SELECT 'venues', COUNT(*) FROM venues
UNION ALL SELECT 'screens', COUNT(*) FROM screens
UNION ALL SELECT 'seats', COUNT(*) FROM seats
UNION ALL SELECT 'events', COUNT(*) FROM events
UNION ALL SELECT 'shows', COUNT(*) FROM shows
UNION ALL SELECT 'bookings', COUNT(*) FROM bookings
UNION ALL SELECT 'booking_seats', COUNT(*) FROM booking_seats
UNION ALL SELECT 'payments', COUNT(*) FROM payments
UNION ALL SELECT 'tickets', COUNT(*) FROM tickets;

SELECT 'SHOWS AND SEATS' AS section;
SELECT sh.show_id,e.event_name,v.venue_name,s.screen_name,sh.show_date,sh.start_time,sh.base_price,
       COUNT(se.seat_id) AS total_seats,
       SUM(CASE WHEN av.seat_id IS NOT NULL THEN 1 ELSE 0 END) AS available_seats
FROM shows sh
JOIN events e ON e.event_id=sh.event_id
JOIN screens s ON s.screen_id=sh.screen_id
JOIN venues v ON v.venue_id=s.venue_id
JOIN seats se ON se.screen_id=s.screen_id
LEFT JOIN available_seats av ON av.show_id=sh.show_id AND av.seat_id=se.seat_id
GROUP BY sh.show_id,e.event_name,v.venue_name,s.screen_name,sh.show_date,sh.start_time,sh.base_price
ORDER BY sh.show_date,sh.start_time;

SELECT 'DEMO BOOKINGS' AS section;
SELECT b.booking_id,u.full_name,e.event_name,sh.show_date,sh.start_time,
       CONCAT(bs.seat_row,bs.seat_number) AS seat, b.total_amount,t.ticket_code
FROM bookings b
JOIN users u ON u.user_id=b.user_id
JOIN shows sh ON sh.show_id=b.show_id
JOIN events e ON e.event_id=sh.event_id
JOIN booking_seats bks ON bks.booking_id=b.booking_id
JOIN seats bs ON bs.seat_id=bks.seat_id
JOIN tickets t ON t.booking_id=b.booking_id
ORDER BY b.booking_id;
