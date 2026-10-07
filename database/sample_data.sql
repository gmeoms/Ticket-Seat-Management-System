USE ticket_seat_management;

-- Demo users
INSERT INTO users(full_name,email,phone) VALUES
('Aarav Sharma','aarav@example.com','9876500001'),
('Priya Verma','priya@example.com','9876500002'),
('Rahul Mehta','rahul@example.com','9876500003');

-- Venues
INSERT INTO venues(venue_name,city,address) VALUES
('City Centre Multiplex','Bhopal','MP Nagar, Bhopal'),
('Grand Arena','Indore','Vijay Nagar, Indore');

-- Screens (IDs are retrieved rather than assumed)
INSERT INTO screens(venue_id,screen_name,total_seats)
SELECT venue_id,'Screen 1',20 FROM venues WHERE venue_name='City Centre Multiplex' AND city='Bhopal';
INSERT INTO screens(venue_id,screen_name,total_seats)
SELECT venue_id,'Screen 2',12 FROM venues WHERE venue_name='City Centre Multiplex' AND city='Bhopal';
INSERT INTO screens(venue_id,screen_name,total_seats)
SELECT venue_id,'Main Arena',16 FROM venues WHERE venue_name='Grand Arena' AND city='Indore';

-- Seats for Screen 1
INSERT INTO seats(screen_id,seat_row,seat_number,seat_type)
SELECT s.screen_id, r.seat_row, n.seat_number,
       CASE WHEN r.seat_row='A' THEN 'PREMIUM' ELSE 'REGULAR' END
FROM screens s
CROSS JOIN (SELECT 'A' AS seat_row UNION ALL SELECT 'B' UNION ALL SELECT 'C' UNION ALL SELECT 'D') r
CROSS JOIN (SELECT 1 AS seat_number UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) n
WHERE s.screen_name='Screen 1'
  AND s.venue_id=(SELECT venue_id FROM venues WHERE venue_name='City Centre Multiplex' AND city='Bhopal');

-- Seats for Screen 2
INSERT INTO seats(screen_id,seat_row,seat_number,seat_type)
SELECT s.screen_id, r.seat_row, n.seat_number, 'REGULAR'
FROM screens s
CROSS JOIN (SELECT 'A' AS seat_row UNION ALL SELECT 'B' UNION ALL SELECT 'C') r
CROSS JOIN (SELECT 1 AS seat_number UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) n
WHERE s.screen_name='Screen 2'
  AND s.venue_id=(SELECT venue_id FROM venues WHERE venue_name='City Centre Multiplex' AND city='Bhopal');

-- Seats for Main Arena
INSERT INTO seats(screen_id,seat_row,seat_number,seat_type)
SELECT s.screen_id, r.seat_row, n.seat_number,
       CASE WHEN r.seat_row IN ('A','B') THEN 'PREMIUM' ELSE 'REGULAR' END
FROM screens s
CROSS JOIN (SELECT 'A' AS seat_row UNION ALL SELECT 'B' UNION ALL SELECT 'C' UNION ALL SELECT 'D') r
CROSS JOIN (SELECT 1 AS seat_number UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) n
WHERE s.screen_name='Main Arena'
  AND s.venue_id=(SELECT venue_id FROM venues WHERE venue_name='Grand Arena' AND city='Indore');

-- Events
INSERT INTO events(event_name,event_type,duration_minutes,description) VALUES
('The Last Journey','MOVIE',140,'Sample movie event'),
('Live Beats 2026','CONCERT',120,'Sample live concert');

-- Shows
INSERT INTO shows(event_id,screen_id,show_date,start_time,base_price)
SELECT e.event_id,s.screen_id,'2026-10-10','18:30:00',250
FROM events e JOIN screens s ON s.screen_name='Screen 1'
JOIN venues v ON v.venue_id=s.venue_id
WHERE e.event_name='The Last Journey' AND v.venue_name='City Centre Multiplex';

INSERT INTO shows(event_id,screen_id,show_date,start_time,base_price)
SELECT e.event_id,s.screen_id,'2026-10-11','21:00:00',300
FROM events e JOIN screens s ON s.screen_name='Screen 1'
JOIN venues v ON v.venue_id=s.venue_id
WHERE e.event_name='The Last Journey' AND v.venue_name='City Centre Multiplex';

INSERT INTO shows(event_id,screen_id,show_date,start_time,base_price)
SELECT e.event_id,s.screen_id,'2026-10-12','19:00:00',800
FROM events e JOIN screens s ON s.screen_name='Main Arena'
JOIN venues v ON v.venue_id=s.venue_id
WHERE e.event_name='Live Beats 2026' AND v.venue_name='Grand Arena';

-- Two demonstration bookings for the first show.
-- IDs are looked up by business keys, not assumed.
INSERT INTO bookings(user_id,show_id,booking_status,total_amount)
SELECT u.user_id,sh.show_id,'CONFIRMED',250
FROM users u JOIN shows sh
WHERE u.email='aarav@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00';

INSERT INTO bookings(user_id,show_id,booking_status,total_amount)
SELECT u.user_id,sh.show_id,'CONFIRMED',250
FROM users u JOIN shows sh
WHERE u.email='priya@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00';

INSERT INTO booking_seats(booking_id,seat_id,price)
SELECT b.booking_id,s.seat_id,250
FROM bookings b JOIN users u ON u.user_id=b.user_id
JOIN shows sh ON sh.show_id=b.show_id
JOIN seats s ON s.screen_id=sh.screen_id
WHERE u.email='aarav@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00'
  AND s.seat_row='A' AND s.seat_number=1;

INSERT INTO booking_seats(booking_id,seat_id,price)
SELECT b.booking_id,s.seat_id,250
FROM bookings b JOIN users u ON u.user_id=b.user_id
JOIN shows sh ON sh.show_id=b.show_id
JOIN seats s ON s.screen_id=sh.screen_id
WHERE u.email='priya@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00'
  AND s.seat_row='A' AND s.seat_number=2;

INSERT INTO payments(booking_id,payment_method,amount,payment_status,paid_at)
SELECT b.booking_id,'UPI',250,'SUCCESS',CURRENT_TIMESTAMP
FROM bookings b JOIN users u ON u.user_id=b.user_id
JOIN shows sh ON sh.show_id=b.show_id
WHERE u.email='aarav@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00';

INSERT INTO payments(booking_id,payment_method,amount,payment_status,paid_at)
SELECT b.booking_id,'CARD',250,'SUCCESS',CURRENT_TIMESTAMP
FROM bookings b JOIN users u ON u.user_id=b.user_id
JOIN shows sh ON sh.show_id=b.show_id
WHERE u.email='priya@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00';

INSERT INTO tickets(booking_id,ticket_code)
SELECT b.booking_id,'TKT-DEMO001'
FROM bookings b JOIN users u ON u.user_id=b.user_id
JOIN shows sh ON sh.show_id=b.show_id
WHERE u.email='aarav@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00';

INSERT INTO tickets(booking_id,ticket_code)
SELECT b.booking_id,'TKT-DEMO002'
FROM bookings b JOIN users u ON u.user_id=b.user_id
JOIN shows sh ON sh.show_id=b.show_id
WHERE u.email='priya@example.com' AND sh.show_date='2026-10-10' AND sh.start_time='18:30:00';
