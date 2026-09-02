package strategy;

import model.Wallet;
import java.math.BigDecimal;

public interface SpendingLimitStrategy {
    boolean isLimitExceeded(Wallet wallet, BigDecimal newAmount);
    String getLimitWarningMessage(Wallet wallet);
}
