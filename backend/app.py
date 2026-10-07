"""Ticket & Seat Management System - Flask + MySQL demo backend."""
from flask import Flask, jsonify, request, send_from_directory
import os
import uuid
import mysql.connector
from mysql.connector import Error, IntegrityError

BASE = os.path.dirname(__file__)
FRONTEND = os.path.join(BASE, "../frontend")
app = Flask(__name__, static_folder=FRONTEND, static_url_path="")


def db():
    return mysql.connector.connect(
        host=os.getenv("MYSQL_HOST", "localhost"),
        port=int(os.getenv("MYSQL_PORT", "3306")),
        user=os.getenv("MYSQL_USER", "root"),
        password=os.getenv("MYSQL_PASSWORD", ""),
        database=os.getenv("MYSQL_DATABASE", "ticket_seat_management"),
    )


def rows_to_dicts(cursor):
    columns = cursor.column_names
    return [dict(zip(columns, row)) for row in cursor.fetchall()]


@app.get("/")
def home():
    return send_from_directory(FRONTEND, "index.html")


@app.get("/api/health")
def health():
    conn = None
    cur = None
    try:
        conn = db()
        cur = conn.cursor()
        cur.execute("SELECT DATABASE() AS database_name")
        row = cur.fetchone()
        return jsonify({"ok": True, "database": row[0]})
    except Error as e:
        return jsonify({"ok": False, "error": str(e)}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@app.get("/api/users")
def users():
    conn = cur = None
    try:
        conn = db(); cur = conn.cursor()
        cur.execute("SELECT user_id, full_name, email, phone FROM users ORDER BY full_name")
        return jsonify(rows_to_dicts(cur))
    except Error as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cur: cur.close()
        if conn: conn.close()


@app.post("/api/users")
def create_user():
    data = request.get_json(silent=True) or {}
    full_name = str(data.get("full_name", "")).strip()
    email = str(data.get("email", "")).strip()
    phone = str(data.get("phone", "")).strip() or None

    if not full_name or not email:
        return jsonify({"error": "Name and email are required."}), 400
    if "@" not in email or "." not in email.split("@")[-1]:
        return jsonify({"error": "Enter a valid email address."}), 400
    if phone and (not phone.isdigit() or not 7 <= len(phone) <= 15):
        return jsonify({"error": "Phone must contain 7 to 15 digits."}), 400

    conn = cur = None
    try:
        conn = db()
        cur = conn.cursor()
        cur.execute(
            "INSERT INTO users(full_name, email, phone) VALUES(%s, %s, %s)",
            (full_name, email, phone),
        )
        user_id = cur.lastrowid
        conn.commit()
        return jsonify({"user_id": user_id, "full_name": full_name, "email": email, "phone": phone}), 201
    except IntegrityError as e:
        if conn:
            conn.rollback()
        msg = str(e)
        if "email" in msg.lower():
            msg = "A customer with this email already exists."
        elif "phone" in msg.lower():
            msg = "A customer with this phone number already exists."
        return jsonify({"error": msg}), 409
    except Error as e:
        if conn:
            conn.rollback()
        return jsonify({"error": str(e)}), 500
    finally:
        if cur:
            cur.close()
        if conn:
            conn.close()


@app.get("/api/shows")
def shows():
    conn = cur = None
    try:
        conn = db(); cur = conn.cursor()
        cur.execute("""
            SELECT sh.show_id, e.event_name, e.event_type, e.duration_minutes,
                   e.description, v.venue_name, v.city, v.address,
                   s.screen_name, DATE_FORMAT(sh.show_date, '%Y-%m-%d') AS show_date,
                   TIME_FORMAT(sh.start_time, '%H:%i:%s') AS start_time,
                   sh.base_price, sh.status
            FROM shows sh
            JOIN events e ON e.event_id = sh.event_id
            JOIN screens s ON s.screen_id = sh.screen_id
            JOIN venues v ON v.venue_id = s.venue_id
            WHERE sh.status = 'SCHEDULED'
            ORDER BY sh.show_date, sh.start_time
        """)
        return jsonify(rows_to_dicts(cur))
    except Error as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cur: cur.close()
        if conn: conn.close()


@app.get("/api/shows/<int:show_id>/seats")
def seats(show_id):
    conn = cur = None
    try:
        conn = db(); cur = conn.cursor()
        cur.execute("""
            SELECT s.seat_id, s.seat_row, s.seat_number, s.seat_type,
                   CASE WHEN EXISTS (
                       SELECT 1
                       FROM booking_seats bs
                       JOIN bookings b ON b.booking_id = bs.booking_id
                       WHERE bs.seat_id = s.seat_id
                         AND b.show_id = %s
                         AND b.booking_status IN ('PENDING','CONFIRMED')
                   ) THEN 1 ELSE 0 END AS booked
            FROM seats s
            JOIN shows sh ON sh.screen_id = s.screen_id
            WHERE sh.show_id = %s
            ORDER BY s.seat_row, s.seat_number
        """, (show_id, show_id))
        return jsonify(rows_to_dicts(cur))
    except Error as e:
        return jsonify({"error": str(e)}), 500
    finally:
        if cur: cur.close()
        if conn: conn.close()


@app.post("/api/book")
def book():
    data = request.get_json(silent=True) or {}
    try:
        user_id = int(data["user_id"])
        show_id = int(data["show_id"])
        seat_ids = [int(x) for x in data.get("seat_ids", [])]
        payment_method = data.get("payment_method", "UPI")
    except (KeyError, TypeError, ValueError):
        return jsonify({"error": "Invalid booking details."}), 400

    if not seat_ids:
        return jsonify({"error": "Select at least one seat."}), 400
    if len(set(seat_ids)) != len(seat_ids):
        return jsonify({"error": "Duplicate seat selected."}), 400
    if payment_method not in {"CARD", "UPI", "NET_BANKING", "CASH"}:
        return jsonify({"error": "Invalid payment method."}), 400

    conn = cur = None
    try:
        conn = db()
        conn.start_transaction()
        cur = conn.cursor(dictionary=True)

        cur.execute("""
            SELECT sh.show_id, sh.screen_id, sh.base_price, sh.status,
                   e.event_name, s.screen_name, v.venue_name, v.city
            FROM shows sh
            JOIN events e ON e.event_id = sh.event_id
            JOIN screens s ON s.screen_id = sh.screen_id
            JOIN venues v ON v.venue_id = s.venue_id
            WHERE sh.show_id = %s
            FOR UPDATE
        """, (show_id,))
        show = cur.fetchone()
        if not show or show["status"] != "SCHEDULED":
            raise ValueError("Show is unavailable.")

        # Lock the requested seats and verify they belong to this show's screen.
        placeholders = ",".join(["%s"] * len(seat_ids))
        cur.execute(
            f"SELECT seat_id, seat_row, seat_number, seat_type FROM seats "
            f"WHERE screen_id=%s AND seat_id IN ({placeholders}) FOR UPDATE",
            [show["screen_id"], *seat_ids],
        )
        found = cur.fetchall()
        if len(found) != len(seat_ids):
            raise ValueError("One or more selected seats do not belong to this show's screen.")

        cur.execute(f"""
            SELECT bs.seat_id
            FROM booking_seats bs
            JOIN bookings b ON b.booking_id = bs.booking_id
            WHERE b.show_id=%s
              AND b.booking_status IN ('PENDING','CONFIRMED')
              AND bs.seat_id IN ({placeholders})
            FOR UPDATE
        """, [show_id, *seat_ids])
        already = [r["seat_id"] for r in cur.fetchall()]
        if already:
            raise ValueError("Seat(s) already booked: " + ", ".join(str(x) for x in already))

        cur.execute("SELECT user_id FROM users WHERE user_id=%s", (user_id,))
        if not cur.fetchone():
            raise ValueError("Selected customer does not exist.")

        total = float(show["base_price"]) * len(seat_ids)
        cur.execute(
            "INSERT INTO bookings(user_id, show_id, booking_status, total_amount) VALUES(%s,%s,'CONFIRMED',%s)",
            (user_id, show_id, total),
        )
        booking_id = cur.lastrowid

        for seat_id in seat_ids:
            cur.execute(
                "INSERT INTO booking_seats(booking_id, seat_id, price) VALUES(%s,%s,%s)",
                (booking_id, seat_id, show["base_price"]),
            )

        cur.execute(
            "INSERT INTO payments(booking_id,payment_method,amount,payment_status,paid_at) "
            "VALUES(%s,%s,%s,'SUCCESS',CURRENT_TIMESTAMP)",
            (booking_id, payment_method, total),
        )
        ticket_code = "TKT-" + uuid.uuid4().hex[:10].upper()
        cur.execute(
            "INSERT INTO tickets(booking_id,ticket_code) VALUES(%s,%s)",
            (booking_id, ticket_code),
        )
        conn.commit()

        seat_labels = [f"{r['seat_row']}{r['seat_number']}" for r in found]
        return jsonify({
            "booking_id": booking_id,
            "ticket_code": ticket_code,
            "total": total,
            "customer": user_id,
            "event_name": show["event_name"],
            "venue_name": show["venue_name"],
            "screen_name": show["screen_name"],
            "seats": seat_labels,
        })
    except (ValueError, IntegrityError, Error) as e:
        if conn:
            conn.rollback()
        return jsonify({"error": str(e)}), 400
    finally:
        if cur: cur.close()
        if conn: conn.close()


if __name__ == "__main__":
    app.run(debug=True)
