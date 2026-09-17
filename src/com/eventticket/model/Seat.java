package com.eventticket.model;

import java.io.Serializable;

public class Seat implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Category {
        VIP, REGULAR, ECONOMY
    }

    private String seatNumber; // e.g., "A1", "B3"
    private int row;
    private int col;
    private Category category;
    private double price;
    private boolean isBooked;
    private String bookedByUserId;

    public Seat() {}

    public Seat(String seatNumber, int row, int col, Category category, double price) {
        this.seatNumber = seatNumber;
        this.row = row;
        this.col = col;
        this.category = category;
        this.price = price;
        this.isBooked = false;
        this.bookedByUserId = null;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getBookedByUserId() {
        return bookedByUserId;
    }

    public void setBookedByUserId(String bookedByUserId) {
        this.bookedByUserId = bookedByUserId;
    }

    @Override
    public String toString() {
        return String.format("[%s - %s ($%.2f) %s]", 
            seatNumber, category, price, isBooked ? "(BOOKED)" : "(AVAIL)");
    }
}
