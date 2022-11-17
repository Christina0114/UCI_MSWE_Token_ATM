package com.capstone.tokenatm.service;

import java.util.Date;

public class AssignmentStatus {

    //Assignment name
    private String name;

    //Assignment due date
    private String deadline;

    //Assignment grade
    private double grade;

    //Maximum possible grade for this assignment
    private double maxGrade;

    private String status;

    public String getStatus() {
        return status;
    }

    public int getToken_required() {
        return token_required;
    }

    private int token_required;

    public AssignmentStatus(String name, String deadline, double grade, double maxGrade, String status, int token_required) {
        this.name = name;
        this.deadline = deadline;
        this.grade = grade;
        this.maxGrade = maxGrade;
        this.status = status;
        this.token_required = token_required;
    }

    public String getName() {
        return name;
    }

    public String getDeadline() {
        return deadline;
    }

    public double getGrade() {
        return grade;
    }

    public double getMaxGrade() {
        return maxGrade;
    }
}
