package com.greychain.loanstore.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class Loan {
    private String loanId;
    private String customerId;
    private String lenderId;
    private double amount; // total loan amount borrowed
    private double remainingAmount;
    private LocalDate paymentDate; // date by which loan instalment should ideally be paid in full
    private int interestPerDay; // interest accrued on the remaining amount per day
    private LocalDate dueDate; // date by which loan instalment must be paid in full, to avoid penalty
    private double penaltyPerDay; // penalty accrued on the remaining amount per day, after due date
    private Object cancel; // Flexible data type for the "Cancel" field

    public Loan(String loanId, String customerId, String lenderId, double amount, double remainingAmount,
                LocalDate paymentDate, int interestPerDay, LocalDate dueDate, double penaltyPerDay) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.lenderId = lenderId;
        this.amount = amount;
        this.remainingAmount = remainingAmount;
        this.paymentDate = paymentDate;
        this.interestPerDay = interestPerDay;
        this.dueDate = dueDate;
        this.penaltyPerDay = penaltyPerDay;
    }

    public double calculateInterest() {
        // Since dueDate >= paymentDate, we can sure that days >= 0
        long days = ChronoUnit.DAYS.between(paymentDate, dueDate);

        double dailyInterestRate = interestPerDay / 100.0; // Since dailyInterestRate is in percentage
        return remainingAmount * dailyInterestRate * days;
    }

    public double calculatePenalty() {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(dueDate)) {
            return 0.0;
        }

        long overdueDays = ChronoUnit.DAYS.between(dueDate, currentDate);
        double dailyPenaltyRate = penaltyPerDay / 100.0; // Since dailyPenaltyRate is a percentage
        return remainingAmount * dailyPenaltyRate * overdueDays;
    }
}
