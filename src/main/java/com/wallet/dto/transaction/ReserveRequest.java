package com.wallet.dto.transaction;

public class ReserveRequest {
    private Long payerWalletId;
    private Long payeeWalletId;
    private String amount;
    private Long transactionTypeId;

    public ReserveRequest() {
    }

    public Long getPayerWalletId() {
        return payerWalletId;
    }

    public Long getPayeeWalletId() {
        return payeeWalletId;
    }

    public String getAmount() {
        return amount;
    }

    public Long getTransactionTypeId() {
        return transactionTypeId;
    }
}
