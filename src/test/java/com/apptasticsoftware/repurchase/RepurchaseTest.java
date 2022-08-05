package com.apptasticsoftware.repurchase;

import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RepurchaseTest {

    @Test
    public void repurchase() throws IOException {
        Repurchase repurchase = new Repurchase();
        List<Transaction> transactions = repurchase.getTransactions().collect(Collectors.toList());
        assertTrue(transactions.size() > 10);

        Transaction transaction = transactions.get(0);
        assertNotNull(transaction);
        assertNotNull(transaction.getDate());
        assertNotNull(transaction.getType());
        assertNotNull(transaction.getCompany());
        assertNotNull(transaction.getComment());
        assertNotNull(transaction.getPrice());
        assertNotNull(transaction.getPrice());
        assertTrue(transaction.getQuantity() >= 0.0);
        assertTrue(transaction.getValue() >= 0.0);
    }

    @Test
    public void futureDate() throws IOException {
        LocalDate end = LocalDate.now().plusMonths(2);
        LocalDate start = end.minusDays(10);
        Repurchase repurchase = new Repurchase();
        List<Transaction> transactions = repurchase.getTransactions(start, end).collect(Collectors.toList());
        assertEquals(0, transactions.size());
    }

    @Test
    public void badInput() throws IOException {
        try {
            LocalDate end = LocalDate.now();
            LocalDate start = end.minusDays(10);
            Repurchase repurchase = new Repurchase();
            repurchase.getTransactions(end, start);
            fail();
        }
        catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
