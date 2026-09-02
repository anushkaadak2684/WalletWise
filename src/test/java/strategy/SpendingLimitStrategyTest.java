package strategy;

import model.BusinessWallet;
import model.PersonalWallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class SpendingLimitStrategyTest {

    @Test
    @DisplayName("PersonalLimitStrategy identifies when spending exceeds monthly limit")
    void testPersonalLimitStrategy() {
        PersonalWallet wallet = new PersonalWallet(1, BigDecimal.ZERO, new BigDecimal("10000.00"));

        assertFalse(wallet.isLimitExceeded(new BigDecimal("5000.00")));
        assertFalse(wallet.isLimitExceeded(new BigDecimal("10000.00")));
        assertTrue(wallet.isLimitExceeded(new BigDecimal("10000.01")));

        assertTrue(wallet.getLimitWarningMessage().contains("10000.00"));
    }

    @Test
    @DisplayName("BusinessLimitStrategy identifies when transaction exceeds per-transaction limit")
    void testBusinessLimitStrategy() {
        BusinessWallet wallet = new BusinessWallet(2, BigDecimal.ZERO, new BigDecimal("500000.00"));

        assertFalse(wallet.isLimitExceeded(new BigDecimal("250000.00")));
        assertFalse(wallet.isLimitExceeded(new BigDecimal("500000.00")));
        assertTrue(wallet.isLimitExceeded(new BigDecimal("500000.01")));

        assertTrue(wallet.getLimitWarningMessage().contains("500000.00"));
    }
}
