DROP DATABASE IF EXISTS ticket_seat_management;
CREATE DATABASE ticket_seat_management;
USE ticket_seat_management;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  phone VARCHAR(15) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE venues (
  venue_id INT AUTO_INCREMENT PRIMARY KEY,
  venue_name VARCHAR(120) NOT NULL,
  city VARCHAR(80) NOT NULL,
  address VARCHAR(255) NOT NULL
);

CREATE TABLE screens (
  screen_id INT AUTO_INCREMENT PRIMARY KEY,
  venue_id INT NOT NULL,
  screen_name VARCHAR(80) NOT NULL,
  total_seats INT NOT NULL CHECK (total_seats > 0),
  UNIQUE (venue_id, screen_name),
  FOREIGN KEY (venue_id) REFERENCES venues(venue_id) ON DELETE CASCADE
);

CREATE TABLE seats (
  seat_id INT AUTO_INCREMENT PRIMARY KEY,
  screen_id INT NOT NULL,
  seat_row CHAR(2) NOT NULL,
  seat_number INT NOT NULL CHECK (seat_number > 0),
  seat_type ENUM('REGULAR','PREMIUM','RECLINER') DEFAULT 'REGULAR',
  UNIQUE (screen_id, seat_row, seat_number),
  FOREIGN KEY (screen_id) REFERENCES screens(screen_id) ON DELETE CASCADE
);

CREATE TABLE events (
  event_id INT AUTO_INCREMENT PRIMARY KEY,
  event_name VARCHAR(150) NOT NULL,
  event_type ENUM('MOVIE','CONCERT','SPORT','THEATRE','OTHER') NOT NULL,
  duration_minutes INT CHECK (duration_minutes > 0),
  description TEXT
);

CREATE TABLE shows (
  show_id INT AUTO_INCREMENT PRIMARY KEY,
  event_id INT NOT NULL,
  screen_id INT NOT NULL,
  show_date DATE NOT NULL,
  start_time TIME NOT NULL,
  base_price DECIMAL(10,2) NOT NULL CHECK (base_price >= 0),
  status ENUM('SCHEDULED','CANCELLED','COMPLETED') DEFAULT 'SCHEDULED',
  FOREIGN KEY (event_id) REFERENCES events(event_id),
  FOREIGN KEY (screen_id) REFERENCES screens(screen_id),
  UNIQUE (screen_id, show_date, start_time)
);

CREATE TABLE bookings (
  booking_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  show_id INT NOT NULL,
  booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  booking_status ENUM('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
  total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (show_id) REFERENCES shows(show_id)
);

CREATE TABLE booking_seats (
  booking_seat_id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT NOT NULL,
  seat_id INT NOT NULL,
  price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
  FOREIGN KEY (seat_id) REFERENCES seats(seat_id),
  UNIQUE (booking_id, seat_id)
);

CREATE TABLE payments (
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT NOT NULL UNIQUE,
  payment_method ENUM('CARD','UPI','NET_BANKING','CASH') NOT NULL,
  amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
  payment_status ENUM('PENDING','SUCCESS','FAILED','REFUNDED') DEFAULT 'PENDING',
  paid_at TIMESTAMP NULL,
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

CREATE TABLE tickets (
  ticket_id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT NOT NULL,
  ticket_code VARCHAR(30) NOT NULL UNIQUE,
  issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

CREATE VIEW available_seats AS
SELECT sh.show_id, sh.show_date, sh.start_time, s.seat_id, s.seat_row, s.seat_number, s.seat_type
FROM shows sh
JOIN seats s ON s.screen_id = sh.screen_id
WHERE sh.status = 'SCHEDULED'
AND NOT EXISTS (
  SELECT 1 FROM booking_seats bs
  JOIN bookings b ON b.booking_id = bs.booking_id
  WHERE bs.seat_id = s.seat_id AND b.show_id = sh.show_id AND b.booking_status IN ('PENDING','CONFIRMED')
);

DELIMITER $$
CREATE TRIGGER trg_booking_seat_show_match
BEFORE INSERT ON booking_seats
FOR EACH ROW
BEGIN
  DECLARE v_show INT;
  DECLARE v_screen INT;
  DECLARE v_booking_screen INT;
  SELECT b.show_id INTO v_show FROM bookings b WHERE b.booking_id = NEW.booking_id;
  SELECT screen_id INTO v_screen FROM seats WHERE seat_id = NEW.seat_id;
  SELECT screen_id INTO v_booking_screen FROM shows WHERE show_id = v_show;
  IF v_screen <> v_booking_screen THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seat does not belong to the show screen';
  END IF;
  IF EXISTS (
    SELECT 1 FROM booking_seats bs JOIN bookings b ON b.booking_id = bs.booking_id
    WHERE bs.seat_id = NEW.seat_id AND b.show_id = v_show AND b.booking_status IN ('PENDING','CONFIRMED')
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Seat is already reserved for this show';
  END IF;
END$$

CREATE PROCEDURE book_seat(
  IN p_user_id INT,
  IN p_show_id INT,
  IN p_seat_id INT,
  IN p_payment_method VARCHAR(20)
)
BEGIN
  DECLARE v_price DECIMAL(10,2);
  DECLARE v_booking_id INT;
  START TRANSACTION;
  SELECT base_price INTO v_price FROM shows WHERE show_id = p_show_id AND status='SCHEDULED' FOR UPDATE;
  IF v_price IS NULL THEN
    ROLLBACK;
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Show is unavailable';
  END IF;
  INSERT INTO bookings(user_id,show_id,booking_status,total_amount) VALUES(p_user_id,p_show_id,'CONFIRMED',v_price);
  SET v_booking_id=LAST_INSERT_ID();
  INSERT INTO booking_seats(booking_id,seat_id,price) VALUES(v_booking_id,p_seat_id,v_price);
  INSERT INTO payments(booking_id,payment_method,amount,payment_status,paid_at) VALUES(v_booking_id,p_payment_method,v_price,'SUCCESS',CURRENT_TIMESTAMP);
  INSERT INTO tickets(booking_id,ticket_code) VALUES(v_booking_id,CONCAT('TKT-',UUID_SHORT()));
  COMMIT;
  SELECT v_booking_id AS booking_id;
END$$
DELIMITER ;
