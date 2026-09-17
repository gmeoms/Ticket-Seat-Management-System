package com.eventticket;

import com.eventticket.model.Booking;
import com.eventticket.model.Event;
import com.eventticket.model.User;
import com.eventticket.service.BookingService;
import com.eventticket.service.EventService;
import com.eventticket.service.ReportService;
import com.eventticket.service.UserService;
import com.eventticket.util.DataStore;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static DataStore dataStore;
    private static UserService userService;
    private static EventService eventService;
    private static BookingService bookingService;
    private static ReportService reportService;

    public static void main(String[] args) {
        // Load persistent data store
        dataStore = DataStore.load();
        userService = new UserService(dataStore);
        eventService = new EventService(dataStore);
        bookingService = new BookingService(dataStore);
        reportService = new ReportService(dataStore);

        Scanner scanner = new Scanner(System.in);
        printHeader();

        boolean running = true;
        while (running) {
            if (!userService.isLoggedIn()) {
                running = showGuestMenu(scanner);
            } else if (userService.isAdmin()) {
                running = showAdminMenu(scanner);
            } else {
                running = showCustomerMenu(scanner);
            }
        }

        System.out.println("\nThank you for using Event Ticket Management System. Goodbye!");
        scanner.close();
    }

    private static void printHeader() {
        System.out.println("==========================================================================");
        System.out.println("           EVENT TICKET & SEAT RESERVATION MANAGEMENT SYSTEM              ");
        System.out.println("                     Java Student Academic Project                        ");
        System.out.println("==========================================================================");
    }

    private static boolean showGuestMenu(Scanner scanner) {
        System.out.println("\n=== MAIN MENU (GUEST) ===");
        System.out.println("1. Login");
        System.out.println("2. Register New Customer Account");
        System.out.println("3. View Upcoming Events");
        System.out.println("4. Exit System");
        System.out.print("Select an option (1-4): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                handleLogin(scanner);
                break;
            case "2":
                handleRegistration(scanner);
                break;
            case "3":
                handleViewEvents(scanner);
                break;
            case "4":
                return false;
            default:
                System.out.println("[Error] Invalid option! Please enter 1-4.");
        }
        return true;
    }

    private static boolean showCustomerMenu(Scanner scanner) {
        User user = userService.getCurrentUser();
        System.out.println("\n=== CUSTOMER PORTAL (" + user.getFullName() + ") ===");
        System.out.println("1. View All Events & Seat Availability");
        System.out.println("2. Search Events by Keyword");
        System.out.println("3. Book Event Tickets");
        System.out.println("4. View My Booked Tickets");
        System.out.println("5. Cancel a Booking");
        System.out.println("6. Export Ticket Receipt (.txt)");
        System.out.println("7. Logout");
        System.out.print("Select an option (1-7): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                handleViewEvents(scanner);
                break;
            case "2":
                handleSearchEvents(scanner);
                break;
            case "3":
                handleBookTickets(scanner);
                break;
            case "4":
                handleViewMyBookings();
                break;
            case "5":
                handleCancelBooking(scanner);
                break;
            case "6":
                handleExportTicket(scanner);
                break;
            case "7":
                userService.logout();
                break;
            default:
                System.out.println("[Error] Invalid option! Please enter 1-7.");
        }
        return true;
    }

    private static boolean showAdminMenu(Scanner scanner) {
        User admin = userService.getCurrentUser();
        System.out.println("\n=== ADMIN CONTROL PANEL (" + admin.getFullName() + ") ===");
        System.out.println("1. View All Events & Seat Maps");
        System.out.println("2. Create New Event");
        System.out.println("3. View System Analytics & Revenue Report");
        System.out.println("4. View All System Bookings");
        System.out.println("5. Cancel Customer Booking");
        System.out.println("6. Register New User (Admin / Customer)");
        System.out.println("7. Logout");
        System.out.print("Select an option (1-7): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                handleViewEvents(scanner);
                break;
            case "2":
                handleCreateEvent(scanner);
                break;
            case "3":
                reportService.printSystemOverviewReport();
                break;
            case "4":
                handleViewAllBookings();
                break;
            case "5":
                handleCancelBooking(scanner);
                break;
            case "6":
                handleAdminRegisterUser(scanner);
                break;
            case "7":
                userService.logout();
                break;
            default:
                System.out.println("[Error] Invalid option! Please enter 1-7.");
        }
        return true;
    }

    private static void handleLogin(Scanner scanner) {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        userService.login(username, password);
    }

    private static void handleRegistration(Scanner scanner) {
        System.out.println("\n--- CUSTOMER REGISTRATION ---");
        System.out.print("Enter Username (3-20 chars): ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password (min 4 chars): ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();

        userService.registerUser(username, password, fullName, email, User.Role.CUSTOMER);
    }

    private static void handleAdminRegisterUser(Scanner scanner) {
        System.out.println("\n--- ADMIN REGISTER USER ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Select Role (1 = Customer, 2 = Admin): ");
        String roleChoice = scanner.nextLine().trim();
        User.Role role = "2".equals(roleChoice) ? User.Role.ADMIN : User.Role.CUSTOMER;

        userService.registerUser(username, password, fullName, email, role);
    }

    private static void handleViewEvents(Scanner scanner) {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("[Info] No events currently scheduled.");
            return;
        }

        System.out.println("\n--- UPCOMING EVENTS LIST ---");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + events.get(i));
        }

        System.out.print("\nDo you want to inspect a seat map? (y/n): ");
        String view = scanner.nextLine().trim();
        if (view.equalsIgnoreCase("y")) {
            System.out.print("Enter Event ID (e.g. E101): ");
            String eventId = scanner.nextLine().trim();
            Event event = eventService.getEventById(eventId);
            if (event != null) {
                eventService.displaySeatMap(event);
            } else {
                System.out.println("[Error] Event ID not found.");
            }
        }
    }

    private static void handleSearchEvents(Scanner scanner) {
        System.out.print("\nEnter search keyword (Title, Category, or Venue): ");
        String keyword = scanner.nextLine().trim();
        List<Event> results = eventService.searchEvents(keyword);

        if (results.isEmpty()) {
            System.out.println("[Info] No matching events found for '" + keyword + "'.");
        } else {
            System.out.println("\n--- SEARCH RESULTS (" + results.size() + " matches) ---");
            for (Event e : results) {
                System.out.println(" - " + e);
            }
        }
    }

    private static void handleCreateEvent(Scanner scanner) {
        System.out.println("\n--- CREATE NEW EVENT ---");
        System.out.print("Enter Event Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Venue Name: ");
        String venue = scanner.nextLine().trim();
        System.out.print("Enter Date & Time (e.g. 2026-11-15 19:00): ");
        String dateTime = scanner.nextLine().trim();
        System.out.print("Enter Category (Music, Sports, Comedy, etc.): ");
        String category = scanner.nextLine().trim();

        try {
            System.out.print("Enter Number of Rows (1-26): ");
            int rows = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Number of Columns (1-30): ");
            int cols = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Base Ticket Price ($): ");
            double basePrice = Double.parseDouble(scanner.nextLine().trim());

            eventService.createEvent(title, venue, dateTime, category, rows, cols, basePrice);
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid number format! Event creation cancelled.");
        }
    }

    private static void handleBookTickets(Scanner scanner) {
        User currentUser = userService.getCurrentUser();
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("[Info] No events available for booking.");
            return;
        }

        System.out.println("\n--- BOOK EVENT TICKETS ---");
        for (Event e : events) {
            System.out.println(" - " + e);
        }

        System.out.print("\nEnter Event ID to book (e.g., E101): ");
        String eventId = scanner.nextLine().trim();
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            System.out.println("[Error] Event ID not found.");
            return;
        }

        // Show seat layout
        eventService.displaySeatMap(event);

        System.out.print("Enter seat numbers to book separated by commas (e.g. A1, A2): ");
        String seatInput = scanner.nextLine().trim();
        if (seatInput.isEmpty()) {
            System.out.println("[Error] No seat numbers entered.");
            return;
        }

        List<String> seatNumbers = Arrays.asList(seatInput.split("\\s*,\\s*"));
        bookingService.createBooking(currentUser, event, seatNumbers);
    }

    private static void handleViewMyBookings() {
        User user = userService.getCurrentUser();
        List<Booking> myBookings = bookingService.getBookingsByUser(user.getUserId());

        System.out.println("\n--- MY BOOKED TICKETS ---");
        if (myBookings.isEmpty()) {
            System.out.println("[Info] You have no active or past bookings.");
        } else {
            for (Booking b : myBookings) {
                System.out.println(" - " + b);
            }
        }
    }

    private static void handleViewAllBookings() {
        List<Booking> allBookings = bookingService.getAllBookings();
        System.out.println("\n--- ALL SYSTEM BOOKINGS ---");
        if (allBookings.isEmpty()) {
            System.out.println("[Info] No bookings exist in the system.");
        } else {
            for (Booking b : allBookings) {
                System.out.println(" - " + b);
            }
        }
    }

    private static void handleCancelBooking(Scanner scanner) {
        System.out.print("\nEnter Booking Ticket ID to cancel (e.g. TKT10001): ");
        String bookingId = scanner.nextLine().trim();
        bookingService.cancelBooking(bookingId, userService.getCurrentUser());
    }

    private static void handleExportTicket(Scanner scanner) {
        User user = userService.getCurrentUser();
        List<Booking> userBookings = bookingService.getBookingsByUser(user.getUserId());
        if (userBookings.isEmpty()) {
            System.out.println("[Info] You have no bookings to export.");
            return;
        }

        System.out.println("\n--- EXPORT E-TICKET RECEIPT ---");
        for (Booking b : userBookings) {
            System.out.println(" - Ticket #" + b.getBookingId() + " (" + b.getEventTitle() + ")");
        }

        System.out.print("Enter Ticket ID to export: ");
        String bookingId = scanner.nextLine().trim();
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking == null) {
            System.out.println("[Error] Ticket booking not found.");
            return;
        }

        Event event = eventService.getEventById(booking.getEventId());
        reportService.exportTicketReceipt(booking, user, event);
    }
}
