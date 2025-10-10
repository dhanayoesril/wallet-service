package com.wallet.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction {
    private long id;
    private UUID uuid;
    private long payerWalletId;
    private long payeeWalletId;
    private BigDecimal amount;
    private long transactionTypeId;
    private String status;      // "RESERVED", "COMPLETED", "CANCELLED"
    private Instant createdAt;
    private Instant updatedAt;

    public Transaction() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getPayerWalletId() {
        return payerWalletId;
    }

    public void setPayerWalletId(long payerWalletId) {
        this.payerWalletId = payerWalletId;
    }

    public long getPayeeWalletId() {
        return payeeWalletId;
    }

    public void setPayeeWalletId(long payeeWalletId) {
        this.payeeWalletId = payeeWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(long transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
