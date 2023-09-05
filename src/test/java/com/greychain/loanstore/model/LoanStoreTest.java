package com.greychain.loanstore.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoanStoreTest {

    private LoanStore loanStore;

    @Before
    public void setUp() {
        loanStore = new LoanStore();
        List<Loan> loans = new ArrayList<>();

        // Add some sample loans to the store
        loans.add(new Loan("L1", "C1", "LEN1", 10000, 10000, LocalDate.of(2023, 5, 6), 1, LocalDate.of(2023, 5, 7), 0.01));
        loans.add(new Loan("L2", "C1", "LEN1", 20000, 5000, LocalDate.of(2023, 5, 1), 1, LocalDate.of(2023, 5, 8), 0.01));
        loans.add(new Loan("L3", "C2", "LEN2", 50000, 30000, LocalDate.of(2023, 4, 4), 2, LocalDate.of(2023, 4, 5), 0.02));
        loans.add(new Loan("L4", "C3", "LEN2", 50000, 30000, LocalDate.of(2023, 4, 4), 2, LocalDate.of(2023, 4, 5), 0.02));

        // Add loans to the loan store
        for (Loan loan : loans) {
            loanStore.addLoan(loan);
        }
    }

    @Test
    public void testAddLoan() {
        Loan loan = new Loan("L5", "C5", "LEN5", 10000, 10000,
                LocalDate.of(2023, 5, 6), 1, LocalDate.of(2023, 5, 7), 0.01);
        assertTrue(loanStore.addLoan(loan));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLoanInvalidPaymentDate() {
        Loan loan = new Loan("L5", "C5", "LEN5", 10000, 10000,
                LocalDate.of(2023, 5, 8), 1, LocalDate.of(2023, 5, 7), 0.01);
        loanStore.addLoan(loan);
    }

    @Test
    public void testAggregateRemainingAmountByLender() {
        Map<String, Double> result = loanStore.aggregateRemainingAmountByLender();
        assertEquals(2, result.size());
        assertEquals(15000.0, result.get("LEN1"), 0.01);
        assertEquals(60000.0, result.get("LEN2"), 0.01);
    }

    @Test
    public void testAggregateRemainingAmountByInterest() {
        Map<Integer, Double> result = loanStore.aggregateRemainingAmountByInterest();
        assertEquals(2, result.size());
        assertEquals(15000.0, result.get(1), 0.01);
        assertEquals(60000.0, result.get(2), 0.01);
    }

    @Test
    public void testAggregateRemainingAmountByCustomer() {
        Map<String, Double> result = loanStore.aggregateRemainingAmountByCustomer();
        assertEquals(3, result.size());
        assertEquals(15000.0, result.get("C1"), 0.01);
        assertEquals(30000.0, result.get("C2"), 0.01);
        assertEquals(30000.0, result.get("C3"), 0.01);
    }

    @Test
    public void testAggregateInterestByLender() {
        Map<String, Double> interestByLender = loanStore.aggregateInterestByLender();
        assertEquals(2, interestByLender.size());
        assertEquals(450.0, interestByLender.get("LEN1"), 0.001);
        assertEquals(1200.0, interestByLender.get("LEN2"), 0.001);
    }

    @Test
    public void testAggregateInterestByInterest() {
        Map<Integer, Double> interestByInterest = loanStore.aggregateInterestByInterest();
        assertEquals(2, interestByInterest.size());
        assertEquals(450.0, interestByInterest.get(1), 0.001);
        assertEquals(1200.0, interestByInterest.get(2), 0.001);
    }

    @Test
    public void testAggregateInterestByCustomer() {
        Map<String, Double> interestByCustomer = loanStore.aggregateInterestByCustomer();
        assertEquals(3, interestByCustomer.size());
        assertEquals(450.0, interestByCustomer.get("C1"), 0.001);
        assertEquals(600.0, interestByCustomer.get("C2"), 0.001);
        assertEquals(600.0, interestByCustomer.get("C3"), 0.001);
    }

    @Test
    public void testAggregatePenaltyByLender() {
        Map<String, Double> penaltyByLender = loanStore.aggregatePenaltyByLender();
        assertEquals(2, penaltyByLender.size());
        assertEquals(181.0, penaltyByLender.get("LEN1"), 0.001);
        assertEquals(1836.0, penaltyByLender.get("LEN2"), 0.001);
    }

    @Test
    public void testAggregatePenaltyByInterest() {
        Map<Integer, Double> penaltyByInterest = loanStore.aggregatePenaltyByInterest();
        assertEquals(2, penaltyByInterest.size());
        assertEquals(181.0, penaltyByInterest.get(1), 0.001);
        assertEquals(1836.0, penaltyByInterest.get(2), 0.001);
    }

    @Test
    public void testAggregatePenaltyByCustomer() {
        Map<String, Double> penaltyByCustomer = loanStore.aggregatePenaltyByCustomer();
        assertEquals(3, penaltyByCustomer.size());
        assertEquals(181.0, penaltyByCustomer.get("C1"), 0.001);
        assertEquals(918.0, penaltyByCustomer.get("C2"), 0.001);
        assertEquals(918.0, penaltyByCustomer.get("C3"), 0.001);
    }

    @Test
    public void testAlertOnDueDate() {
        Loan loan = new Loan("L5", "C5", "LEN5", 10000, 10000,
                LocalDate.of(2023, 9, 6), 1, LocalDate.of(2023, 10, 7), 0.01);
        loanStore.addLoan(loan);

        List<String> alerts = loanStore.checkDueDateAlerts();
        assertEquals(4, alerts.size());

        LocalDate today = LocalDate.now();
        loan.setDueDate(today.minusDays(1));
        alerts = loanStore.checkDueDateAlerts();
        assertEquals(5, alerts.size());
        assertTrue(alerts.get(0).contains("L1"));
    }
}
