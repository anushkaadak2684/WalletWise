package strategy;

import model.PersonalWallet;
import model.Wallet;
import java.math.BigDecimal;

public class PersonalLimitStrategy implements SpendingLimitStrategy {

    @Override
    public boolean isLimitExceeded(Wallet wallet, BigDecimal newAmount) {
        if (wallet instanceof PersonalWallet) {
            PersonalWallet pw = (PersonalWallet) wallet;
            if (pw.getMonthlySpendingLimit() == null || pw.getMonthlySpendingLimit().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            if (newAmount == null) {
                return false;
            }
            return newAmount.compareTo(pw.getMonthlySpendingLimit()) > 0;
        }
        return false;
    }

    @Override
    public String getLimitWarningMessage(Wallet wallet) {
        if (wallet instanceof PersonalWallet) {
            return "Monthly spending limit exceeded (Limit: ₹" + ((PersonalWallet) wallet).getMonthlySpendingLimit() + ")";
        }
        return "Monthly spending limit exceeded";
    }
}
