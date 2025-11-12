package com.trainingapp.models;

import java.sql.Timestamp;

public class Registration {
    private int id;
    private int userId;
    private int trainingId;
    private Timestamp registrationDate;
    private String status;

    public Registration() {}

    public Registration(int id, int userId, int trainingId, Timestamp registrationDate, String status) {
        this.id = id; this.userId = userId; this.trainingId = trainingId;
        this.registrationDate = registrationDate; this.status = status;
    }

    public Registration(int userId, int trainingId) {
        this(0, userId, trainingId, null, "Registered");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTrainingId() { return trainingId; }
    public void setTrainingId(int trainingId) { this.trainingId = trainingId; }

    public Timestamp getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Timestamp registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

