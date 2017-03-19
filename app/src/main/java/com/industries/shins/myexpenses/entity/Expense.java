package com.industries.shins.myexpenses.entity;

/**
 * Created by saga on 06/03/17.
 */
public class Expense {

    public int id;
    public String label;
    public String category;
    public double cost;
    public boolean wasItPaid;
    public float percentageOfTotalSalary;
    public String dueDate;

    public Expense(String label, String category, double cost, boolean wasItPaid,
                   float percetageOfTotalSalary, String dateTimeOfAcquisition) {

        this.label = label;
        this.category = category;
        this.cost = cost;
        this.wasItPaid = wasItPaid;
        this.percentageOfTotalSalary = percetageOfTotalSalary;
        this.dueDate = dateTimeOfAcquisition;
    }

    public Expense(int id, String label, String category, double cost, boolean wasItPaid,
                   float percetageOfTotalSalary, String dateTimeOfAcquisition) {

        this.id = id;
        this.label = label;
        this.category = category;
        this.cost = cost;
        this.wasItPaid = wasItPaid;
        this.percentageOfTotalSalary = percetageOfTotalSalary;
        this.dueDate = dateTimeOfAcquisition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isWasItPaid() {
        return wasItPaid;
    }

    public void setWasItPaid(boolean wasItPaid) {
        this.wasItPaid = wasItPaid;
    }

    public float getPercentageOfTotalSalary() {
        return percentageOfTotalSalary;
    }

    public void setPercentageOfTotalSalary(float percentageOfTotalSalary) {
        this.percentageOfTotalSalary = percentageOfTotalSalary;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
