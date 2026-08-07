package model;

import model.enums.WalletType;

import java.math.BigDecimal;

public class BusinessWallet extends Wallet {
    private BigDecimal businessTransactionLimit;

    public BusinessWallet() {
        super();
        this.businessTransactionLimit = new BigDecimal("500000");
    }

    public BusinessWallet(int walletId,
                          BigDecimal balance,
                          BigDecimal businessTransactionLimit) {

        super(walletId, balance, WalletType.BUSINESS);
        this.businessTransactionLimit = businessTransactionLimit;
    }


    // Getter
    public BigDecimal getBusinessTransactionLimit() {

        return businessTransactionLimit;
    }


    // Setter
    public void setBusinessTransactionLimit(BigDecimal businessTransactionLimit) {
        
        if (businessTransactionLimit == null || businessTransactionLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Business transaction limit must be greater than zero"
            );
        }

        this.businessTransactionLimit = businessTransactionLimit;
    }
    @Override
    public BigDecimal calculateTransactionLimit() {

        return businessTransactionLimit;
    }

    public void showBusinessWalletBenefits() {

        System.out.println(
                "Business Wallet Benefits:"
        );

        System.out.println(
                "- Higher transaction limits"
        );

        System.out.println(
                "- Business expense tracking"
        );

        System.out.println(
                "- Multiple payment handling"
        );

        System.out.println(
                "Transaction Limit: ₹"
                + businessTransactionLimit
        );
    }
    @Override
    public String toString() {
        return "BusinessWallet{" +
                "businessTransactionLimit=" + businessTransactionLimit +
                '}';
    }
}