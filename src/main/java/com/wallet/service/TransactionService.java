package com.wallet.service;

import com.wallet.dto.transaction.ReserveRequest;
import com.wallet.dto.transaction.ReserveResponse;
import com.wallet.model.Transaction;
import com.wallet.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class TransactionService {

    private final TransactionRepository repo = new TransactionRepository();

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String format(Instant t) {
        return t == null ? null : DateTimeFormatter.ISO_INSTANT.format(t);
    }

    public ReserveResponse reserve(ReserveRequest req) throws Exception {
        if (req == null) throw new IllegalArgumentException("body_required");

        Long payerId = req.getPayerWalletId();
        Long payeeId = req.getPayeeWalletId();
        String amtStr = req.getAmount();
        Long typeId = req.getTransactionTypeId();

        if (payerId == null || payeeId == null || isBlank(amtStr) || typeId == null)
            throw new IllegalArgumentException("missing_required_fields");
        if (Objects.equals(payerId, payeeId))
            throw new IllegalArgumentException("payer_and_payee_must_differ");

        BigDecimal amount;
        try {
            amount = new BigDecimal(amtStr).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("invalid_amount");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount_must_be_positive");

        UUID uuid = UUID.randomUUID();

        Transaction tx = repo.reserve(uuid, payerId, payeeId, amount, typeId);

        String created = format(tx.getCreatedAt());
        String amountOut = tx.getAmount() == null ? null : tx.getAmount().setScale(2, RoundingMode.DOWN).toPlainString();

        return new ReserveResponse(
            tx.getId(),
            tx.getUuid() != null ? tx.getUuid().toString() : null,
            tx.getPayerWalletId(),
            tx.getPayeeWalletId(),
            amountOut,
            tx.getStatus(), // "RESERVED"
            created
        );
    }
}
