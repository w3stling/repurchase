package com.apptastic.repurchase;

import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RepurchaseTest {

    @Test
    public void repurchase() throws IOException {
        Repurchase repurchase = new Repurchase();
        List<Transaction> transactions = repurchase.getTransactions().collect(Collectors.toList());
        assertTrue(transactions.size() > 10);
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
            List<Transaction> transactions = repurchase.getTransactions(end, start).collect(Collectors.toList());
            assertTrue(false);
        }
        catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
