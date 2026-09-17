package com.eventticket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String title;
    private String venue;
    private String dateTime;
    private String category; // Music, Sports, Tech Conference, Theatre, etc.
    private int rows;
    private int cols;
    private double basePrice;
    private List<Seat> seats;

    public Event() {
        this.seats = new ArrayList<>();
    }

    public Event(String eventId, String title, String venue, String dateTime, String category, int rows, int cols, double basePrice) {
        this.eventId = eventId;
        this.title = title;
        this.venue = venue;
        this.dateTime = dateTime;
        this.category = category;
        this.rows = rows;
        this.cols = cols;
        this.basePrice = basePrice;
        this.seats = new ArrayList<>();
        initializeSeats();
    }

    public void initializeSeats() {
        this.seats.clear();
        for (int r = 1; r <= rows; r++) {
            char rowChar = (char) ('A' + r - 1);
            for (int c = 1; c <= cols; c++) {
                String seatNo = "" + rowChar + c;
                Seat.Category cat;
                double seatPrice = basePrice;

                if (r == 1) {
                    cat = Seat.Category.VIP;
                    seatPrice *= 1.5; // 50% VIP markup
                } else if (r <= rows - 1) {
                    cat = Seat.Category.REGULAR;
                } else {
                    cat = Seat.Category.ECONOMY;
                    seatPrice *= 0.85; // 15% discount for last row
                }

                this.seats.add(new Seat(seatNo, r, c, cat, seatPrice));
            }
        }
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public Seat getSeatByNumber(String seatNumber) {
        for (Seat seat : seats) {
            if (seat.getSeatNumber().equalsIgnoreCase(seatNumber)) {
                return seat;
            }
        }
        return null;
    }

    public int getTotalSeats() {
        return seats.size();
    }

    public int getAvailableSeatsCount() {
        int count = 0;
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                count++;
            }
        }
        return count;
    }

    public int getBookedSeatsCount() {
        return getTotalSeats() - getAvailableSeatsCount();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Venue: %s | Date: %s | Price: $%.2f | Avail: %d/%d",
                eventId, title, venue, dateTime, basePrice, getAvailableSeatsCount(), getTotalSeats());
    }
}
