# Ticket & Seat Management System — DBMS Project

A college-level DBMS project with a MySQL database and a Flask web interface connected to the **same MySQL database**.

## Database setup
1. Open MySQL Workbench.
2. Run `database/schema.sql`.
3. Run `database/sample_data.sql`.
4. Run `database/VERIFY.sql` and confirm the expected rows.

## Web application setup (Windows PowerShell)
From the `ticket_seat_management` folder:

```powershell
python -m venv venv
venv\Scripts\activate
pip install -r backend\requirements.txt

$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="YOUR_MYSQL_PASSWORD"
$env:MYSQL_DATABASE="ticket_seat_management"

python backend\app.py
```

Open `http://127.0.0.1:5000`.

The interface shows real users, events, venues, screens, shows and seats from MySQL. Booking writes to the MySQL `bookings`, `booking_seats`, `payments`, and `tickets` tables.

## Important
The environment variables only apply to the current PowerShell window. If you open a new terminal, set them again.

## Project contents
- `database/schema.sql` — tables, keys, view, trigger and stored procedure
- `database/sample_data.sql` — reliable demo data
- `database/queries.sql` — SQL demonstration queries
- `database/VERIFY.sql` — setup verification
- `diagrams/ER_Diagram.png` — ER diagram
- `docs/PROJECT_REPORT.md` — project report
- `docs/DATA_DICTIONARY.md` — data dictionary
- `backend/app.py` — Flask + MySQL API
- `frontend/index.html` — booking interface

## Added customer management

The web interface now includes **+ Add New Customer**. A customer can be created from the website with name, email, and optional phone number. The Flask API inserts the record into the MySQL `users` table, refreshes the customer list, and automatically selects the newly created customer for booking. Duplicate email/phone values are handled with a clear message.
