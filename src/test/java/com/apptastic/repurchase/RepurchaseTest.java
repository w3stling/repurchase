package com.apptastic.repurchase;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class RepurchaseTest {

    @Test
    public void repurchase() throws IOException {
        Repurchase repurchase = new Repurchase();
        List<Transaction> transactions = repurchase.getTransactions().collect(Collectors.toList());
        assertTrue(transactions.size() > 25);
    }


}
