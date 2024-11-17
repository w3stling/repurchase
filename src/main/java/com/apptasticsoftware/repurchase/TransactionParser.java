package com.apptasticsoftware.repurchase;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * A utility class for parsing transaction data from a JSON string.
 * This class provides a static method to convert a JSON string into a list of {@link Transaction} objects.
 */
public class TransactionParser {
    private static final Logger LOGGER = Logger.getLogger(TransactionParser.class.getName());

    private TransactionParser() {
        // Prevent instantiation
    }

    /**
     * Parses transaction data from a JSON string. The JSON string is expected to have a specific structure
     * containing transaction details like company name, type, date, price, quantity, and value. The method uses
     * GSON library to parse JSON.
     *
     * @param inputStream The stream containing the transaction data.
     * @return A list of {@link Transaction} objects representing the parsed transactions. Returns an empty list if the
     *         input JSON is empty or does not contain transactions.
     * @throws IOException If an I/O error occurs during parsing.
     */
    public static List<Transaction> parseTransactions(InputStream inputStream) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("data")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String dataName = reader.nextName();
                    if (dataName.equals("transactionData")) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String transactionDataName = reader.nextName();
                            if (transactionDataName.equals("rowsData")) {
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    reader.nextName(); // Skip the date key
                                    reader.beginObject();
                                    while (reader.hasNext()) {
                                        String rowsDataName = reader.nextName();
                                        if (rowsDataName.equals("rows")) {
                                            reader.beginArray();
                                            while (reader.hasNext()) {
                                                Row row = gson.fromJson(reader, Row.class);
                                                Transaction transaction = new Transaction(
                                                        row.getCompany(),
                                                        row.getType(),
                                                        LocalDate.parse(row.getDate()),
                                                        parseDouble(row.getPrice()).orElse(null),
                                                        parseDouble(row.getQuantity()).orElse(0.0),
                                                        parseDouble(row.getValue()).orElse(0.0),
                                                        null);
                                                transactions.add(transaction);
                                            }
                                            reader.endArray();
                                        } else {
                                            reader.skipValue();
                                        }
                                    }
                                    reader.endObject();
                                }
                                reader.endObject();
                            } else {
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();

        return transactions;
    }

    /**
     * Inner class representing a single row of transaction data.
     * This class is used internally by the {@link TransactionParser} to map JSON data to Java objects.
     */
    public static class Row {
        private String company_name;
        private String type;
        private String date;
        private String price;
        private String quantity;
        private String value;

        /**
         * Gets the company name.
         * @return The company name.
         */
        public String getCompany() {
            return company_name;
        }

        /**
         * Gets the transaction type.
         * @return The transaction type.
         */
        public String getType() {
            return type;
        }

        /**
         * Gets the transaction date as String.
         * @return The transaction date as String.
         */
        public String getDate() {
            return date;
        }

        /**
         * Gets the transaction price as String.
         * @return The price as String.
         */
        public String getPrice() {
            return price;
        }

        /**
         * Gets the transaction quantity as String.
         * @return The quantity as String.
         */
        public String getQuantity() {
            return quantity;
        }

        /**
         * Gets the transaction value as String.
         * @return The value as String.
         */
        public String getValue() {
            return value;
        }
    }

    private static Optional<Double> parseDouble(String value) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        df.setParseBigDecimal(true);

        try {
            double number = df.parse(value).doubleValue();
            return Optional.of(number);
        } catch (ParseException e) {
            LOGGER.warning("Error parsing string to double: " + e.getMessage());
            return Optional.empty();
        }
    }
}