/*
 * MIT License
 *
 * Copyright (c) 2022, Apptastic Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.apptasticsoftware.repurchase;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Repurchase
 */
public class Repurchase {
    // https://www.nasdaq.com/european-market-activity/news/corporate-actions/repurchase-of-own-shares
    private static final String URL = "https://www.nasdaq.com/api/v1/transaction";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";

    /**
     * Get transactions from the last 30 days
     * @return stream of transactions
     * @throws IOException IOException
     */
    public Stream<Transaction> getTransactions() throws IOException {
        return getTransactions(30);
    }

    /**
     * Get transactions from the given days back
     * @param daysBack - days back from today
     * @return stream of transactions
     * @throws IOException IOException
     */
    public Stream<Transaction> getTransactions(int daysBack) throws IOException {
        var endDate = LocalDate.now();
        var startDate = endDate.minusDays(daysBack);
        return getTransactions(startDate, endDate);
    }

    /**
     * Get transactions between the given two dates
     * @param startDate - inclusive start date
     * @param endDate - inclusive end date
     * @return stream of transaction
     * @throws IOException IOException
     */
    public Stream<Transaction> getTransactions(LocalDate startDate, LocalDate endDate) throws IOException {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Bad argument. Start date after end date");
        }

        if (startDate.isAfter(LocalDate.now())) {
            return Stream.empty();
        }

        List<Transaction> transactions = new ArrayList<>();
        for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
            try {
                var inputStream = sendRequest(year);
                var newTransactions = TransactionParser.parseTransactions(inputStream).stream()
                        .filter(transaction -> !startDate.isAfter(transaction.getDate()) && !endDate.isBefore(transaction.getDate()))
                        .collect(Collectors.toList());
                transactions.addAll(newTransactions);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Stream.empty();
            }
        }
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::getDate).reversed()
                        .thenComparing(Transaction::getCompany)
                        .thenComparing(Transaction::getType));
    }

    private InputStream sendRequest(int year) throws IOException, InterruptedException {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "?year=" + year))
                .header("Accept-Encoding", "gzip, deflate")
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(20))
                .build();

        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() >= 400 && response.statusCode() < 600) {
            throw new IOException("Response http status code: " + response.statusCode());
        }

        InputStream inputStream;
        var encoding = response.headers().firstValue("Content-Encoding").orElse("");

        if (encoding.equals("gzip")) {
            inputStream = new GZIPInputStream(response.body());
        }
        else {
            inputStream = response.body();
        }

        return inputStream;
    }
}
