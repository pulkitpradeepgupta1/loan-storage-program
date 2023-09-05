package com.greychain.loanstore.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoanStore {
    private final List<Loan> loans = new ArrayList<>();

    public boolean addLoan(Loan loan) {
        if (loan.getPaymentDate().isAfter(loan.getDueDate())) {
            throw new IllegalArgumentException("Payment date cannot be greater than due date");
        }

        return loans.add(loan);
    }

    public Map<String, Double> aggregateRemainingAmountByLender() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getLenderId, Collectors.summingDouble(Loan::getRemainingAmount)));
    }

    public Map<Integer, Double> aggregateRemainingAmountByInterest() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getInterestPerDay, Collectors.summingDouble(Loan::getRemainingAmount)));
    }

    public Map<String, Double> aggregateRemainingAmountByCustomer() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getCustomerId, Collectors.summingDouble(Loan::getRemainingAmount)));
    }

    public Map<String, Double> aggregateInterestByLender() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getLenderId, Collectors.summingDouble(Loan::calculateInterest)));
    }

    public Map<Integer, Double> aggregateInterestByInterest() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getInterestPerDay, Collectors.summingDouble(Loan::calculateInterest)));
    }

    public Map<String, Double> aggregateInterestByCustomer() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getCustomerId, Collectors.summingDouble(Loan::calculateInterest)));
    }

    public Map<String, Double> aggregatePenaltyByLender() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getLenderId, Collectors.summingDouble(Loan::calculatePenalty)));
    }

    public Map<Integer, Double> aggregatePenaltyByInterest() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getInterestPerDay, Collectors.summingDouble(Loan::calculatePenalty)));
    }

    public Map<String, Double> aggregatePenaltyByCustomer() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getCustomerId, Collectors.summingDouble(Loan::calculatePenalty)));
    }

    public List<String> checkDueDateAlerts() {
        LocalDate currentDate = LocalDate.now();
        return loans.stream()
                .filter(loan -> currentDate.isAfter(loan.getDueDate()))
                .map(loan -> "Loan " + loan.getLoanId() + " is overdue")
                .collect(Collectors.toList());
    }
}
