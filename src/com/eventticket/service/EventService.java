package com.eventticket.service;

import com.eventticket.model.Event;
import com.eventticket.model.Seat;
import com.eventticket.util.DataStore;

import java.util.ArrayList;
import java.util.List;

public class EventService {
    private final DataStore dataStore;

    public EventService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Event createEvent(String title, String venue, String dateTime, String category, int rows, int cols, double basePrice) {
        if (rows <= 0 || cols <= 0 || rows > 26 || cols > 30) {
            System.out.println("[Error] Rows must be between 1 and 26, Cols between 1 and 30.");
            return null;
        }
        if (basePrice <= 0) {
            System.out.println("[Error] Base price must be greater than zero.");
            return null;
        }

        String eventId = "E" + (dataStore.getEvents().size() + 101);
        Event newEvent = new Event(eventId, title, venue, dateTime, category, rows, cols, basePrice);
        dataStore.getEvents().add(newEvent);
        dataStore.save();

        System.out.println("[Success] Event created: " + title + " (ID: " + eventId + ") with " + (rows * cols) + " total seats.");
        return newEvent;
    }

    public List<Event> getAllEvents() {
        return dataStore.getEvents();
    }

    public Event getEventById(String eventId) {
        for (Event e : dataStore.getEvents()) {
            if (e.getEventId().equalsIgnoreCase(eventId)) {
                return e;
            }
        }
        return null;
    }

    public List<Event> searchEvents(String keyword) {
        List<Event> matches = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return matches;
        }
        String term = keyword.toLowerCase().trim();
        for (Event e : dataStore.getEvents()) {
            if (e.getTitle().toLowerCase().contains(term) ||
                e.getCategory().toLowerCase().contains(term) ||
                e.getVenue().toLowerCase().contains(term)) {
                matches.add(e);
            }
        }
        return matches;
    }

    public void displaySeatMap(Event event) {
        if (event == null) {
            System.out.println("[Error] Event not found.");
            return;
        }

        System.out.println("\n==========================================================================");
        System.out.println("                 SEAT MAP LAYOUT FOR: " + event.getTitle());
        System.out.println("==========================================================================");
        System.out.println("                  [ STAGE / SCREEN FRONT ]");
        System.out.println("--------------------------------------------------------------------------");

        int cols = event.getCols();
        
        // Print column headers
        System.out.print("     ");
        for (int c = 1; c <= cols; c++) {
            System.out.printf(" Col %-2d ", c);
        }
        System.out.println("\n");

        for (int r = 1; r <= event.getRows(); r++) {
            char rowChar = (char) ('A' + r - 1);
            System.out.printf("Row %c ", rowChar);
            for (int c = 1; c <= cols; c++) {
                String seatNo = "" + rowChar + c;
                Seat seat = event.getSeatByNumber(seatNo);
                if (seat != null) {
                    if (seat.isBooked()) {
                        System.out.print(" [X BOOK] ");
                    } else {
                        String catCode = seat.getCategory() == Seat.Category.VIP ? "V" :
                                         seat.getCategory() == Seat.Category.REGULAR ? "R" : "E";
                        System.out.printf("[%s %-3s] ", catCode, seatNo);
                    }
                }
            }
            System.out.println();
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Legend: [V] VIP (50% premium)  |  [R] Regular  |  [E] Economy  |  [X BOOK] Booked");
        System.out.println("==========================================================================\n");
    }
}
