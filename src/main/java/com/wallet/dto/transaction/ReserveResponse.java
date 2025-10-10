package com.wallet.dto.transaction;

public class ReserveResponse {
    private long id;
    private String uuid;
    private long payerWalletId;
    private long payeeWalletId;
    private String amount;
    private String status;
    private String createdAt;

    public ReserveResponse(long id, String uuid, long payerWalletId, long payeeWalletId,
                           String amount, String status, String createdAt) {
        this.id = id;
        this.uuid = uuid;
        this.payerWalletId = payerWalletId;
        this.payeeWalletId = payeeWalletId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public long getPayerWalletId() {
        return payerWalletId;
    }

    public long getPayeeWalletId() {
        return payeeWalletId;
    }

    public String getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
