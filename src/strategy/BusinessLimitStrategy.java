package strategy;

import model.BusinessWallet;
import model.Wallet;
import java.math.BigDecimal;

public class BusinessLimitStrategy implements SpendingLimitStrategy {

    @Override
    public boolean isLimitExceeded(Wallet wallet, BigDecimal newAmount) {
        if (wallet instanceof BusinessWallet) {
            BusinessWallet bw = (BusinessWallet) wallet;
            if (bw.getBusinessTransactionLimit() == null || bw.getBusinessTransactionLimit().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            return bw.getBusinessTransactionLimit().compareTo(newAmount) < 0;
        }
        return false;
    }

    @Override
    public String getLimitWarningMessage(Wallet wallet) {
        if (wallet instanceof BusinessWallet) {
            return "Per-transaction limit exceeded (Limit: ₹" + ((BusinessWallet) wallet).getBusinessTransactionLimit() + ")";
        }
        return "Transaction limit exceeded";
    }
}
