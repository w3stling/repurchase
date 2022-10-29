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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Repurchase
 */
public class Repurchase {
    private static final Logger LOGGER = Logger.getLogger(Repurchase.class.getName());
    private static final String URL = "https://www.nasdaqomxnordic.com/news/corporate-actions/repurchase-of-own-shares";
    private static final String COOKIE_URL = "https://www.nasdaqomxnordic.com";

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

        try {
            var inputStream = sendRequest(startDate, endDate.plusDays(1));
            return parse(inputStream).stream();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Stream.empty();
        }
    }

    private List<Transaction> parse(InputStream inputStream) throws IOException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        var doc = Jsoup.parse(inputStream, "UTF-8", URL);
        var tableElement = doc.selectFirst("table[id=resultReurchaseId]");

        if (tableElement == null) {
            throw new IOException("Failed to find reurchase table");
        }

        var tableRowElements = tableElement.select("tr[class*=tableTr]");
        var mapper = new TransactionMapper();
        var hasInitHeaders = false;

        for (Element row : tableRowElements) {
            var rowItems = row.select("td");
            if (isInvalidRow(rowItems)) {
                continue;
            }

            if (!hasInitHeaders && TransactionMapper.isHeaderColumn(rowItems)) {
                String[] headers = { rowItems.get(0).text(), rowItems.get(1).text(), rowItems.get(2).text(),
                        rowItems.get(3).text(), rowItems.get(4).text(), rowItems.get(5).text() };

                mapper.initialize(headers);
                hasInitHeaders = true;
            }
            else if (isTransactionRow(rowItems)){
                try {
                    var transaction = createTransaction(mapper, rowItems);
                    if (isValid(transaction)) {
                        transactions.add(transaction);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Exception when paring transaction. ", e);
                }
            }
        }

        return transactions;
    }

    private static Transaction createTransaction(TransactionMapper mapper, Elements rowItems) {
        return new Transaction(mapper.getCompany(rowItems), mapper.getType(rowItems),
                mapper.getDate(rowItems), mapper.getPrice(rowItems),
                mapper.getQuantity(rowItems), mapper.getValue(rowItems),
                mapper.getComment(rowItems));
    }

    private boolean isInvalidRow(Elements row) {
        return row == null || row.size() < 6;
    }
    private boolean isTransactionRow(Elements row) {
        if (isInvalidRow(row)) {
            return false;
        }

        var dateText = row.get(2).text().trim();
        return dateText.length() == 10 && dateText.charAt(4) == '-' && dateText.charAt(7) == '-';
    }

    private static boolean isValid(Transaction transaction) {
        return transaction != null &&
                transaction.getCompany() != null && transaction.getType() != null &&
                transaction.getDate() != null;
    }

    private InputStream sendRequest(LocalDate startDate, LocalDate endDate) throws IOException, InterruptedException {
        Map<Object, Object> data = new HashMap<>();
        data.put("linkparams", "?subsystem=Repurchase&action=getByDate&startDate=" + startDate.toString() + "&endDate=" + endDate.toString());
        data.put("sort", "date");
        data.put("selected", "");
        data.put("languageId", "");

        try {
            CookieHandler.setDefault(new CookieManager());

            var sessionCookie = new HttpCookie("session", "53616c7465645f5f9d467d3ae831ec1b1e7289ef45d256224786e1ed13");
            sessionCookie.setPath("/");
            sessionCookie.setVersion(0);

            var cookieUrl = new URI(COOKIE_URL);
            ((CookieManager) CookieHandler.getDefault()).getCookieStore().add(cookieUrl, sessionCookie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        var request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .uri(URI.create(URL))
                .header("Accept-Encoding", "gzip, deflate")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(15))
                .build();


        var client = HttpClient.newBuilder()
                .cookieHandler(CookieHandler.getDefault())
                .connectTimeout(Duration.ofSeconds(15))
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

    static class TransactionMapper {
        private static final String COLUMN_COMPANY = "Company";
        private static final String COLUMN_TYPE = "Type";
        private static final String COLUMN_DATE = "Date";
        private static final String COLUMN_PRICE = "Price";
        private static final String COLUMN_QUANTITY = "Quantity";
        private static final String COLUMN_VALUE = "Value";
        private static final HashMap<String, Integer> columnName2Index = new HashMap<>();

        void initialize(String[] header) {
            for (int i = 0; i < header.length; ++i) {
                var columnHeaderText = header[i].trim();
                columnName2Index.put(columnHeaderText, i);
            }
        }

        public static boolean isHeaderColumn(Elements rowItems) {
            var text = rowItems.get(0).text().trim();
            return COLUMN_COMPANY.equalsIgnoreCase(text) || COLUMN_TYPE.equalsIgnoreCase(text) ||
                    COLUMN_DATE.equalsIgnoreCase(text) || COLUMN_PRICE.equalsIgnoreCase(text) ||
                    COLUMN_QUANTITY.equalsIgnoreCase(text) ||COLUMN_VALUE.equalsIgnoreCase(text);
        }

        public String getCompany(Elements rowItems) {
            return getText(rowItems, COLUMN_COMPANY);
        }

        public String getType(Elements rowItems) {
            return getText(rowItems, COLUMN_TYPE);
        }

        public LocalDate getDate(Elements rowItems) {
            var text = getText(rowItems, COLUMN_DATE);
            Objects.requireNonNull(text);
            return LocalDate.parse(text);
        }

        public Double getPrice(Elements rowItems) {
            var text = getText(rowItems, COLUMN_PRICE);
            if (text == null || text.isEmpty()) {
                return null;
            }
            for (int i = 0; i < text.length(); ++i) {
                if (Character.isLetter(text.codePointAt(i))) {
                    return null;
                }
            }
            return parseDouble(text);
        }

        public double getQuantity(Elements rowItems) {
            var text = getText(rowItems, COLUMN_QUANTITY);
            return parseDouble(text);
        }

        public double getValue(Elements rowItems) {
            var text = getText(rowItems, COLUMN_VALUE);
            return parseDouble(text);
        }

        public String getComment(Elements rowItems) {
            var text = getText(rowItems, COLUMN_PRICE);
            if (text == null || text.isEmpty()) {
                return null;
            }
            for (int i = 0; i < text.length(); ++i) {
                if (Character.isLetter(text.codePointAt(i))) {
                    return text;
                }
            }
            return null;
        }

        private String getText(Elements rowItems, String column) {
            var index = columnName2Index.get(column);

            if (index == null) {
                return null;
            }

            return Objects.requireNonNull(rowItems.get(index)
                            .selectFirst("td"))
                    .text()
                    .trim();
        }

        private static double parseDouble(String value) {
            if (value == null) {
                return Double.NaN;
            }

            var floatNumber = 0.0;

            try {
                value = value.replace(",","").trim();
                value = value.replace(" ", "");
                floatNumber = Double.parseDouble(value);
            }
            catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to parse double. ", e);
                floatNumber = Double.NaN;
            }

            return floatNumber;
        }
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
