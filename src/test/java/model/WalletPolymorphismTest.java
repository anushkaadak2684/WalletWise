package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class WalletPolymorphismTest {

    @Test
    @DisplayName("Polymorphism: PersonalWallet correctly enforces monthly spending limit")
    void testPersonalWalletPolymorphicLimit() {
        Wallet wallet = new PersonalWallet(1, new BigDecimal("5000.00"), new BigDecimal("10000.00"));

        assertEquals(new BigDecimal("10000.00"), wallet.calculateTransactionLimit());
        assertFalse(wallet.isLimitExceeded(new BigDecimal("5000.00")));
        assertFalse(wallet.isLimitExceeded(new BigDecimal("10000.00")));
        assertTrue(wallet.isLimitExceeded(new BigDecimal("10000.01")));

        assertTrue(wallet.getLimitWarningMessage().contains("Monthly spending limit exceeded"));
    }

    @Test
    @DisplayName("Polymorphism: BusinessWallet correctly enforces per-transaction limit")
    void testBusinessWalletPolymorphicLimit() {
        Wallet wallet = new BusinessWallet(2, new BigDecimal("20000.00"), new BigDecimal("500000.00"));

        assertEquals(new BigDecimal("500000.00"), wallet.calculateTransactionLimit());
        assertFalse(wallet.isLimitExceeded(new BigDecimal("250000.00")));
        assertFalse(wallet.isLimitExceeded(new BigDecimal("500000.00")));
        assertTrue(wallet.isLimitExceeded(new BigDecimal("500000.01")));

        assertTrue(wallet.getLimitWarningMessage().contains("Per-transaction limit exceeded"));
    }
}
