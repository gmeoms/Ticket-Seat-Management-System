USE ticket_seat_management;
-- 1. List all shows
SELECT sh.show_id,e.event_name,v.venue_name,s.screen_name,sh.show_date,sh.start_time,sh.base_price
FROM shows sh JOIN events e ON e.event_id=sh.event_id JOIN screens s ON s.screen_id=sh.screen_id JOIN venues v ON v.venue_id=s.venue_id;
-- 2. Available seats for a show
SELECT * FROM available_seats WHERE show_id=1 ORDER BY seat_row,seat_number;
-- 3. Occupancy by show
SELECT sh.show_id,e.event_name,COUNT(bs.booking_seat_id) booked_seats,COUNT(s.seat_id) total_seats,
ROUND(COUNT(bs.booking_seat_id)*100.0/COUNT(s.seat_id),2) occupancy_percent
FROM shows sh JOIN events e ON e.event_id=sh.event_id JOIN seats s ON s.screen_id=sh.screen_id
LEFT JOIN booking_seats bs ON bs.seat_id=s.seat_id
LEFT JOIN bookings b ON b.booking_id=bs.booking_id AND b.show_id=sh.show_id AND b.booking_status IN ('PENDING','CONFIRMED')
GROUP BY sh.show_id,e.event_name;
-- 4. Revenue by event
SELECT e.event_name,SUM(p.amount) revenue FROM payments p JOIN bookings b ON b.booking_id=p.booking_id
JOIN shows sh ON sh.show_id=b.show_id JOIN events e ON e.event_id=sh.event_id WHERE p.payment_status='SUCCESS' GROUP BY e.event_id,e.event_name;
-- 5. Customer booking history
SELECT u.full_name,t.ticket_code,e.event_name,sh.show_date,sh.start_time,b.total_amount
FROM users u JOIN bookings b ON b.user_id=u.user_id JOIN tickets t ON t.booking_id=b.booking_id
JOIN shows sh ON sh.show_id=b.show_id JOIN events e ON e.event_id=sh.event_id ORDER BY b.booking_time DESC;
-- 6. Most popular event
SELECT e.event_name,COUNT(bs.booking_seat_id) tickets_sold FROM events e JOIN shows sh ON sh.event_id=e.event_id
JOIN bookings b ON b.show_id=sh.show_id JOIN booking_seats bs ON bs.booking_id=b.booking_id
WHERE b.booking_status='CONFIRMED' GROUP BY e.event_id ORDER BY tickets_sold DESC LIMIT 1;
