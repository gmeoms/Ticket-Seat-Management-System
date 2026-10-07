# Data Dictionary

| Table | Important attributes | Purpose |
|---|---|---|
| USERS | user_id, full_name, email | Customer records |
| VENUES | venue_id, venue_name, city | Physical locations |
| SCREENS | screen_id, venue_id, total_seats | Individual screens/areas |
| SEATS | seat_id, screen_id, row, number, type | Physical seat inventory |
| EVENTS | event_id, event_name, event_type | Movie/concert/etc. |
| SHOWS | show_id, event_id, screen_id, date, time, price | Scheduled occurrence |
| BOOKINGS | booking_id, user_id, show_id, status, total | Booking header |
| BOOKING_SEATS | booking_seat_id, booking_id, seat_id | Seats selected in a booking |
| PAYMENTS | payment_id, booking_id, method, status | Payment transaction |
| TICKETS | ticket_id, booking_id, ticket_code | Issued ticket |
