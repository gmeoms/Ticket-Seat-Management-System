package com.eventticket.service;

import com.eventticket.model.Booking;
import com.eventticket.model.Event;
import com.eventticket.model.Seat;
import com.eventticket.model.User;
import com.eventticket.util.DataStore;
import com.eventticket.util.InputValidator;

import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private final DataStore dataStore;

    public BookingService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public synchronized Booking createBooking(User user, Event event, List<String> requestedSeatNumbers) {
        if (user == null) {
            System.out.println("[Error] Must be logged in to book tickets.");
            return null;
        }
        if (event == null) {
            System.out.println("[Error] Invalid event specified.");
            return null;
        }
        if (requestedSeatNumbers == null || requestedSeatNumbers.isEmpty()) {
            System.out.println("[Error] No seats selected.");
            return null;
        }

        // Validate seats existence and availability
        List<Seat> seatsToBook = new ArrayList<>();
        double totalPrice = 0.0;

        for (String seatNo : requestedSeatNumbers) {
            String cleanSeatNo = seatNo.trim().toUpperCase();
            Seat seat = event.getSeatByNumber(cleanSeatNo);
            if (seat == null) {
                System.out.println("[Error] Seat '" + cleanSeatNo + "' does not exist in event layout.");
                return null;
            }
            if (seat.isBooked()) {
                System.out.println("[Error] Seat '" + cleanSeatNo + "' is already booked by another user!");
                return null;
            }
            seatsToBook.add(seat);
            totalPrice += seat.getPrice();
        }

        // Perform booking transaction
        List<String> confirmedSeatNumbers = new ArrayList<>();
        for (Seat seat : seatsToBook) {
            seat.setBooked(true);
            seat.setBookedByUserId(user.getUserId());
            confirmedSeatNumbers.add(seat.getSeatNumber());
        }

        String bookingId = "TKT" + (dataStore.getBookings().size() + 10001);
        String timestamp = InputValidator.getFormattedCurrentTime();

        Booking booking = new Booking(bookingId, user.getUserId(), event.getEventId(), event.getTitle(), confirmedSeatNumbers, totalPrice, timestamp);
        dataStore.getBookings().add(booking);
        dataStore.save();

        System.out.println("\n[Success] Booking Confirmed!");
        System.out.println("  Booking ID: " + bookingId);
        System.out.println("  Event Title: " + event.getTitle());
        System.out.println("  Seats Booked: " + String.join(", ", confirmedSeatNumbers));
        System.out.printf("  Total Amount Paid: $%.2f\n\n", totalPrice);

        return booking;
    }

    public synchronized boolean cancelBooking(String bookingId, User currentUser) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            System.out.println("[Error] Booking ticket #" + bookingId + " not found.");
            return false;
        }

        if (currentUser.getRole() != User.Role.ADMIN && !booking.getUserId().equals(currentUser.getUserId())) {
            System.out.println("[Error] Unauthorized! You can only cancel your own bookings.");
            return false;
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            System.out.println("[Info] Booking #" + bookingId + " is already cancelled.");
            return false;
        }

        // Find associated event
        Event event = null;
        for (Event e : dataStore.getEvents()) {
            if (e.getEventId().equals(booking.getEventId())) {
                event = e;
                break;
            }
        }

        if (event != null) {
            for (String seatNo : booking.getSeatNumbers()) {
                Seat seat = event.getSeatByNumber(seatNo);
                if (seat != null) {
                    seat.setBooked(false);
                    seat.setBookedByUserId(null);
                }
            }
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        dataStore.save();

        System.out.println("[Success] Booking #" + bookingId + " has been successfully cancelled.");
        System.out.printf("[Info] Refund of $%.2f issued for seats (%s).\n", booking.getTotalPrice(), String.join(", ", booking.getSeatNumbers()));
        return true;
    }

    public Booking getBookingById(String bookingId) {
        for (Booking b : dataStore.getBookings()) {
            if (b.getBookingId().equalsIgnoreCase(bookingId)) {
                return b;
            }
        }
        return null;
    }

    public List<Booking> getBookingsByUser(String userId) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking b : dataStore.getBookings()) {
            if (b.getUserId().equals(userId)) {
                userBookings.add(b);
            }
        }
        return userBookings;
    }

    public List<Booking> getAllBookings() {
        return dataStore.getBookings();
    }
}
