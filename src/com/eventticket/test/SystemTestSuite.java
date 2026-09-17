package com.eventticket.test;

import com.eventticket.model.Booking;
import com.eventticket.model.Event;
import com.eventticket.model.Seat;
import com.eventticket.model.User;
import com.eventticket.service.BookingService;
import com.eventticket.service.EventService;
import com.eventticket.service.ReportService;
import com.eventticket.service.UserService;
import com.eventticket.util.DataStore;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SystemTestSuite {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    private static final String TEST_DATA_FILE = "data/test_store.dat";

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("            RUNNING AUTOMATED SYSTEM UNIT TEST SUITE                      ");
        System.out.println("==========================================================================");

        // Clear test data file for clean test execution
        File testDataFile = new File(TEST_DATA_FILE);
        if (testDataFile.exists()) {
            testDataFile.delete();
        }

        DataStore dataStore = DataStore.load(TEST_DATA_FILE);
        UserService userService = new UserService(dataStore);
        EventService eventService = new EventService(dataStore);
        BookingService bookingService = new BookingService(dataStore);
        ReportService reportService = new ReportService(dataStore);

        testUserAuthentication(userService);
        Event testEvent = testEventAndSeatGridCreation(eventService);
        testBookingAndDoubleBookingPrevention(userService, testEvent, bookingService);
        testTicketCancellation(userService, bookingService, eventService);
        testDataPersistenceAndExport(userService, eventService, bookingService, reportService, dataStore);

        System.out.println("\n==========================================================================");
        System.out.printf(" TEST SUMMARY: Total: %d | Passed: %d | Failed: %d\n", testsRun, testsPassed, testsFailed);
        System.out.println("==========================================================================");

        if (testsFailed > 0) {
            System.err.println("\n[FAILURE] Some unit tests failed!");
            System.exit(1);
        } else {
            System.out.println("\n[SUCCESS] ALL UNIT TESTS PASSED SUCCESSFULLY!");
        }
    }

    private static void assertEquals(Object expected, Object actual, String testName) {
        testsRun++;
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            testsFailed++;
            System.out.println("  [FAIL] " + testName + " - Expected: " + expected + ", Got: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String testName) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            testsFailed++;
            System.out.println("  [FAIL] " + testName + " - Condition was false");
        }
    }

    private static void testUserAuthentication(UserService userService) {
        System.out.println("\n--- Module 1: User Authentication & Role Management Tests ---");
        
        User customer = userService.registerUser("alice_w", "pass123", "Alice Walker", "alice@example.com", User.Role.CUSTOMER);
        assertTrue(customer != null, "Customer registration succeeds");
        assertEquals(User.Role.CUSTOMER, customer.getRole(), "User role assigned correctly as CUSTOMER");

        User dup = userService.registerUser("alice_w", "pass123", "Alice Walker", "alice@example.com", User.Role.CUSTOMER);
        assertTrue(dup == null, "Duplicate username registration correctly blocked");

        User loggedIn = userService.login("alice_w", "pass123");
        assertTrue(loggedIn != null, "User login with correct credentials succeeds");
        assertTrue(userService.isLoggedIn(), "UserService tracks active session");

        User failedLogin = userService.login("alice_w", "wrongpass");
        assertTrue(failedLogin == null, "User login with wrong password rejected");
    }

    private static Event testEventAndSeatGridCreation(EventService eventService) {
        System.out.println("\n--- Module 2: Event & Seat Grid Creation Tests ---");

        Event event = eventService.createEvent("Rock Concert 2026", "Arena Hall", "2026-11-10 20:00", "Music", 3, 4, 100.0);
        assertTrue(event != null, "Event creation succeeds");
        assertEquals(12, event.getTotalSeats(), "Seat grid dimensions (3x4 = 12 seats) calculated correctly");
        assertEquals(12, event.getAvailableSeatsCount(), "Initial available seats count equals total seats");

        Seat vipSeat = event.getSeatByNumber("A1");
        assertTrue(vipSeat != null, "Row 1 A1 seat created");
        assertEquals(Seat.Category.VIP, vipSeat.getCategory(), "Row 1 assigned VIP Category");
        assertEquals(150.0, vipSeat.getPrice(), "VIP seat priced with 50% premium ($150.00)");

        Seat regularSeat = event.getSeatByNumber("B1");
        assertEquals(Seat.Category.REGULAR, regularSeat.getCategory(), "Row 2 assigned Regular Category");
        assertEquals(100.0, regularSeat.getPrice(), "Regular seat priced at base price ($100.00)");

        Seat econSeat = event.getSeatByNumber("C1");
        assertEquals(Seat.Category.ECONOMY, econSeat.getCategory(), "Last Row assigned Economy Category");
        assertEquals(85.0, econSeat.getPrice(), "Economy seat priced with 15% discount ($85.00)");

        return event;
    }

    private static void testBookingAndDoubleBookingPrevention(UserService userService, Event event, BookingService bookingService) {
        System.out.println("\n--- Module 3: Booking & Seat Locking Tests ---");

        User user = userService.getCurrentUser();

        Booking booking = bookingService.createBooking(user, event, Arrays.asList("A1", "B1"));
        assertTrue(booking != null, "Booking creation succeeds");
        assertEquals(250.0, booking.getTotalPrice(), "Total price calculated correctly ($150 VIP + $100 Regular = $250)");
        assertEquals(2, booking.getSeatNumbers().size(), "2 seats recorded in booking");

        Booking doubleBooking = bookingService.createBooking(user, event, Arrays.asList("A1"));
        assertTrue(doubleBooking == null, "Double-booking booked seat 'A1' correctly rejected");
    }

    private static void testTicketCancellation(UserService userService, BookingService bookingService, EventService eventService) {
        System.out.println("\n--- Module 4: Ticket Cancellation & Refund Tests ---");

        User user = userService.getCurrentUser();
        List<Booking> userBookings = bookingService.getBookingsByUser(user.getUserId());
        assertTrue(!userBookings.isEmpty(), "User has active bookings");

        Booking bookingToCancel = userBookings.get(0);
        boolean cancelled = bookingService.cancelBooking(bookingToCancel.getBookingId(), user);
        assertTrue(cancelled, "Booking cancellation succeeds");
        assertEquals(Booking.BookingStatus.CANCELLED, bookingToCancel.getStatus(), "Booking status updated to CANCELLED");

        Event event = eventService.getEventById(bookingToCancel.getEventId());
        Seat releasedSeat = event.getSeatByNumber("A1");
        assertTrue(!releasedSeat.isBooked(), "Seat A1 released back to available state after cancellation");
    }

    private static void testDataPersistenceAndExport(UserService userService, EventService eventService, BookingService bookingService, ReportService reportService, DataStore dataStore) {
        System.out.println("\n--- Module 5: Report Export & DataStore Tests ---");

        User user = userService.getCurrentUser();
        Event event = eventService.getAllEvents().get(0);
        Booking newBooking = bookingService.createBooking(user, event, Arrays.asList("B2"));

        boolean exported = reportService.exportTicketReceipt(newBooking, user, event);
        assertTrue(exported, "E-ticket text receipt file created successfully");

        File ticketFile = new File("tickets/Ticket_" + newBooking.getBookingId() + ".txt");
        assertTrue(ticketFile.exists(), "E-ticket TXT file exists on disk");

        dataStore.save();
        DataStore loadedStore = DataStore.load(TEST_DATA_FILE);
        assertTrue(loadedStore.getUsers().size() >= 1, "DataStore reloads users from persistent disk storage");
        assertTrue(loadedStore.getEvents().size() >= 1, "DataStore reloads events from persistent disk storage");
    }
}
