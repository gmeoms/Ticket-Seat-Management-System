# Flask + MySQL Demo

This demo uses the **same MySQL database** as the DBMS project. It does not create or use a separate SQLite database.

## Configure PowerShell
Set these environment variables in the same terminal before starting Flask:

```powershell
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="YOUR_MYSQL_PASSWORD"
$env:MYSQL_DATABASE="ticket_seat_management"
```

Then run:

```powershell
pip install -r backend\requirements.txt
python backend\app.py
```

Open http://127.0.0.1:5000

The web app reads users, shows, venues, screens and seats from MySQL and writes bookings, booking_seats, payments and tickets back to MySQL.
