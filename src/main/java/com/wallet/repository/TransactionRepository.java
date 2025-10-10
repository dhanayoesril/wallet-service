package com.wallet.repository;

import com.wallet.db.Database;
import com.wallet.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;

public class TransactionRepository {

    private static Transaction mapTx(ResultSet rs) throws SQLException {
        Transaction tx = new Transaction();
        tx.setId(rs.getLong("id"));
        tx.setUuid((UUID) rs.getObject("uuid"));
        tx.setPayerWalletId(rs.getLong("payer_wallet_id"));
        tx.setPayeeWalletId(rs.getLong("payee_wallet_id"));
        tx.setAmount(rs.getBigDecimal("amount"));
        tx.setTransactionTypeId(rs.getLong("transaction_type_id"));
        tx.setStatus(rs.getString("status"));
        Timestamp ct = rs.getTimestamp("created_at");
        Timestamp ut = rs.getTimestamp("updated_at");
        if (ct != null) tx.setCreatedAt(ct.toInstant());
        if (ut != null) tx.setUpdatedAt(ut.toInstant());
        return tx;
    }

    public Transaction reserve(UUID uuid,
                               long payerWalletId,
                               long payeeWalletId,
                               BigDecimal amount,
                               long transactionTypeId) throws SQLException {

        final String updWallet =
            "UPDATE wallets " +
                "   SET balance = balance - ?, " +
                "       reserved_balance = reserved_balance + ? " +
                " WHERE id = ? " +
                "   AND balance >= ?";

        final String insTx =
            "INSERT INTO transactions (uuid, payer_wallet_id, payee_wallet_id, amount, transaction_type_id, status) " +
                "VALUES (?, ?, ?, ?, ?, 'RESERVED') " +
                "RETURNING id, uuid, payer_wallet_id, payee_wallet_id, amount, transaction_type_id, status, created_at, updated_at";

        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement u = c.prepareStatement(updWallet)) {
                    u.setBigDecimal(1, amount);
                    u.setBigDecimal(2, amount);
                    u.setLong(3, payerWalletId);
                    u.setBigDecimal(4, amount);
                    int updated = u.executeUpdate();
                    if (updated == 0) {
                        c.rollback();
                        throw new SQLException("insufficient_funds", "23514");
                    }
                }

                try (PreparedStatement i = c.prepareStatement(insTx)) {
                    i.setObject(1, uuid, Types.OTHER); // PostgreSQL UUID
                    i.setLong(2, payerWalletId);
                    i.setLong(3, payeeWalletId);
                    i.setBigDecimal(4, amount);
                    i.setLong(5, transactionTypeId);

                    try (ResultSet rs = i.executeQuery()) {
                        if (!rs.next()) {
                            c.rollback();
                            throw new SQLException("insert_failed");
                        }

                        Transaction tx = mapTx(rs);
                        c.commit();
                        c.setAutoCommit(true);
                        return tx;
                    }
                }
            } catch (SQLException e) {
                c.rollback();
                c.setAutoCommit(true);
                throw e;
            }
        }
    }
}
