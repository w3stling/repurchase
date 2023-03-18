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

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Repurchase transaction
 */
public class Transaction implements Comparable<Transaction> {
    private final String company;
    private final String type;
    private final LocalDate date;
    private final Double price;
    private final double quantity;
    private final double value;
    private final String comment;

    /**
     * Initializes a newly created <code>Transaction</code> with the given parameters.
     *
     * @param company - company name
     * @param type - transaction type
     * @param date - date of transaction
     * @param price - price
     * @param quantity - quantity
     * @param value - total value of transaction
     * @param comment - comment
     */
    public Transaction(String company, String type, LocalDate date, Double price, double quantity, double value, String comment) {
        this.company = company;
        this.type = type;
        this.date = date;
        this.price = price;
        this.quantity = quantity;
        this.value = value;
        this.comment = comment;
    }

    /**
     * Get company
     * @return company
     */
    public String getCompany() {
        return company;
    }

    /**
     * Get type of transaction
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Get transaction date
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Get price
     * @return price
     */
    public Optional<Double> getPrice() {
        return Optional.ofNullable(price);
    }

    /**
     * Get quantity
     * @return quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Get total value
     * @return value
     */
    public double getValue() {
        return value;
    }

    /**
     * Get comment
     * @return comment
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.getQuantity(), getQuantity()) == 0 && Double.compare(that.getValue(), getValue()) == 0 && Objects.equals(getCompany(), that.getCompany()) && Objects.equals(getType(), that.getType()) && Objects.equals(getDate(), that.getDate()) && getPrice().equals(that.getPrice()) && getComment().equals(that.getComment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCompany(), getType(), getDate(), getPrice(), getQuantity(), getValue(), getComment());
    }

    @Override
    public int compareTo(Transaction o) {
        return date.compareTo(o.date);
    }
}
