package com.eventticket.util;

import com.eventticket.model.Booking;
import com.eventticket.model.Event;
import com.eventticket.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_DATA_DIR = "data";
    private static final String DEFAULT_DATA_FILE = "data/system_store.dat";

    private String dataFilePath;
    private List<User> users;
    private List<Event> events;
    private List<Booking> bookings;

    public DataStore() {
        this(DEFAULT_DATA_FILE);
    }

    public DataStore(String dataFilePath) {
        this.dataFilePath = dataFilePath;
        this.users = new ArrayList<>();
        this.events = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public static DataStore load() {
        return load(DEFAULT_DATA_FILE);
    }

    public static DataStore load(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                DataStore store = (DataStore) ois.readObject();
                store.dataFilePath = filePath;
                store.ensureDefaultData();
                return store;
            } catch (Exception e) {
                System.err.println("[Warning] Failed to load data file. Initializing new storage state.");
            }
        }
        
        DataStore newStore = new DataStore(filePath);
        newStore.seedDefaultData();
        newStore.save();
        return newStore;
    }

    public void save() {
        try {
            File file = new File(dataFilePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(this);
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to save data: " + e.getMessage());
        }
    }

    public void ensureDefaultData() {
        boolean hasAdmin = false;
        for (User u : users) {
            if ("admin".equalsIgnoreCase(u.getUsername())) {
                hasAdmin = true;
                break;
            }
        }

        if (!hasAdmin) {
            users.add(new User("U100", "admin", "admin123", "System Administrator", "admin@eventticket.com", User.Role.ADMIN));
        }

        boolean hasJohn = false;
        for (User u : users) {
            if ("john_doe".equalsIgnoreCase(u.getUsername())) {
                hasJohn = true;
                break;
            }
        }
        if (!hasJohn) {
            users.add(new User("U101", "john_doe", "user123", "John Doe", "john@example.com", User.Role.CUSTOMER));
        }

        if (events.isEmpty()) {
            seedDefaultEvents();
        }
        save();
    }

    private void seedDefaultData() {
        users.clear();
        users.add(new User("U100", "admin", "admin123", "System Administrator", "admin@eventticket.com", User.Role.ADMIN));
        users.add(new User("U101", "john_doe", "user123", "John Doe", "john@example.com", User.Role.CUSTOMER));

        seedDefaultEvents();
    }

    private void seedDefaultEvents() {
        events.clear();
        events.add(new Event("E101", "Grand Music Festival 2026", "City Arena", "2026-10-15 19:00", "Music", 4, 6, 50.0));
        events.add(new Event("E102", "Tech Innovations Summit", "Convention Center", "2026-11-20 09:30", "Conference", 3, 5, 120.0));
        events.add(new Event("E103", "Championship Basketball Final", "National Stadium", "2026-12-05 18:00", "Sports", 5, 5, 75.0));
        events.add(new Event("E104", "Stand-Up Comedy Night", "Laugh Club Auditorium", "2026-10-28 20:00", "Comedy", 3, 4, 35.0));
        events.add(new Event("E105", "Symphony Orchestra Special", "Royal Symphony Hall", "2026-11-05 19:30", "Theater", 4, 5, 90.0));
        events.add(new Event("E106", "International Film Festival", "Grand Multiplex", "2026-12-12 17:00", "Cinema", 4, 6, 40.0));
    }
}
