package com.eventticket.service;

import com.eventticket.model.Booking;
import com.eventticket.model.Event;
import com.eventticket.model.User;
import com.eventticket.util.DataStore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReportService {
    private final DataStore dataStore;

    public ReportService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void printSystemOverviewReport() {
        List<User> users = dataStore.getUsers();
        List<Event> events = dataStore.getEvents();
        List<Booking> bookings = dataStore.getBookings();

        double totalRevenue = 0.0;
        int activeBookingsCount = 0;
        int cancelledBookingsCount = 0;

        for (Booking b : bookings) {
            if (b.getStatus() == Booking.BookingStatus.CONFIRMED) {
                totalRevenue += b.getTotalPrice();
                activeBookingsCount++;
            } else {
                cancelledBookingsCount++;
            }
        }

        System.out.println("\n==========================================================================");
        System.out.println("                 SYSTEM ANALYTICS & EXECUTIVE REPORT");
        System.out.println("==========================================================================");
        System.out.printf(" Total Registered Users:     %d\n", users.size());
        System.out.printf(" Total Scheduled Events:     %d\n", events.size());
        System.out.printf(" Total Active Tickets:       %d\n", activeBookingsCount);
        System.out.printf(" Total Cancelled Tickets:    %d\n", cancelledBookingsCount);
        System.out.printf(" Gross System Revenue:       $%.2f\n", totalRevenue);
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" EVENT OCCUPANCY BREAKDOWN:");

        for (Event e : events) {
            double occupancy = e.getTotalSeats() > 0 ? 
                ((double) e.getBookedSeatsCount() / e.getTotalSeats()) * 100.0 : 0.0;
            System.out.printf("   - [%s] %-30s | Booked: %2d/%2d (%5.1f%%)\n",
                    e.getEventId(), e.getTitle(), e.getBookedSeatsCount(), e.getTotalSeats(), occupancy);
        }
        System.out.println("==========================================================================\n");
    }

    public boolean exportTicketReceipt(Booking booking, User user, Event event) {
        if (booking == null || user == null || event == null) {
            System.out.println("[Error] Cannot export receipt: missing data.");
            return false;
        }

        String ticketsDir = "tickets";
        try {
            Files.createDirectories(Paths.get(ticketsDir));
            String filePath = ticketsDir + File.separator + "Ticket_" + booking.getBookingId() + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println("=========================================================");
                writer.println("               EVENT ADMISSION TICKET                    ");
                writer.println("=========================================================");
                writer.println(" Ticket Reference : " + booking.getBookingId());
                writer.println(" Event Title      : " + event.getTitle());
                writer.println(" Category         : " + event.getCategory());
                writer.println(" Venue            : " + event.getVenue());
                writer.println(" Date & Time      : " + event.getDateTime());
                writer.println("---------------------------------------------------------");
                writer.println(" Attendee Name    : " + user.getFullName() + " (" + user.getEmail() + ")");
                writer.println(" Reserved Seats   : " + String.join(", ", booking.getSeatNumbers()));
                writer.printf( " Total Paid       : $%.2f\n", booking.getTotalPrice());
                writer.println(" Booking Status   : " + booking.getStatus());
                writer.println(" Issued At        : " + booking.getBookingTimestamp());
                writer.println("=========================================================");
                writer.println("   Please present this digital/printed ticket at entry.   ");
                writer.println("=========================================================");
            }

            System.out.println("[Success] E-Ticket receipt exported to file: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("[Error] Failed to export ticket receipt: " + e.getMessage());
            return false;
        }
    }
}
