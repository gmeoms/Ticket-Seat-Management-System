package com.eventticket.model;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum BookingStatus {
        CONFIRMED, CANCELLED
    }

    private String bookingId;
    private String userId;
    private String eventId;
    private String eventTitle;
    private List<String> seatNumbers;
    private double totalPrice;
    private String bookingTimestamp;
    private BookingStatus status;

    public Booking() {}

    public Booking(String bookingId, String userId, String eventId, String eventTitle, List<String> seatNumbers, double totalPrice, String bookingTimestamp) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.seatNumbers = seatNumbers;
        this.totalPrice = totalPrice;
        this.bookingTimestamp = bookingTimestamp;
        this.status = BookingStatus.CONFIRMED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBookingTimestamp() {
        return bookingTimestamp;
    }

    public void setBookingTimestamp(String bookingTimestamp) {
        this.bookingTimestamp = bookingTimestamp;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Ticket #%s | Event: %s | Seats: %s | Total: $%.2f | Status: %s | Date: %s",
                bookingId, eventTitle, String.join(", ", seatNumbers), totalPrice, status, bookingTimestamp);
    }
}
