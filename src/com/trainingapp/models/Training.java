package com.trainingapp.models;

import java.sql.Date;

public class Training {
    private int id;
    private String title;
    private String description;
    private int seats;
    private Date startDate;
    private double fee;

    public Training() {}

    public Training(int id, String title, String description, int seats, Date startDate, double fee) {
        this.id = id; this.title = title; this.description = description;
        this.seats = seats; this.startDate = startDate; this.fee = fee;
    }

    public Training(String title, String description, int seats, Date startDate, double fee) {
        this(0, title, description, seats, startDate, fee);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    @Override
    public String toString() {
        return id + " - " + title + " (Seats: " + seats + ")";
    }
}

