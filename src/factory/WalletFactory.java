package factory;

import model.BusinessWallet;
import model.PersonalWallet;
import model.Wallet;
import model.enums.WalletType;

import java.math.BigDecimal;

public class WalletFactory {

    public static Wallet createWallet(WalletType type, BigDecimal balance, BigDecimal limit) {
        if (type == null) {
            throw new IllegalArgumentException("WalletType cannot be null");
        }

        switch (type) {
            case PERSONAL:
                return new PersonalWallet(0, balance, limit);
            case BUSINESS:
                return new BusinessWallet(0, balance, limit);
            default:
                throw new IllegalArgumentException("Unsupported WalletType: " + type);
        }
    }
}
