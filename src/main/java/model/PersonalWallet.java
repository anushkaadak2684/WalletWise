package model;

import model.enums.WalletType;

import java.math.BigDecimal;

public class PersonalWallet extends Wallet {
    private BigDecimal monthlySpendingLimit;

    public PersonalWallet() {
        super();
        this.monthlySpendingLimit = new BigDecimal("50000");
    }

    public PersonalWallet(int walletId,
                          BigDecimal balance,
                          BigDecimal monthlySpendingLimit) {
        super(walletId, balance, WalletType.PERSONAL);
        this.monthlySpendingLimit = monthlySpendingLimit != null ? monthlySpendingLimit : new BigDecimal("50000");
    }

    public BigDecimal getMonthlySpendingLimit() {
        return monthlySpendingLimit;
    }

    public void setMonthlySpendingLimit(BigDecimal monthlySpendingLimit) {
        if (monthlySpendingLimit == null || monthlySpendingLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly spending limit must be greater than zero");
        }
        this.monthlySpendingLimit = monthlySpendingLimit;
    }

    @Override
    public BigDecimal calculateTransactionLimit() {
        return monthlySpendingLimit;
    }

    @Override
    public boolean isLimitExceeded(BigDecimal newAmount) {
        if (monthlySpendingLimit == null || monthlySpendingLimit.compareTo(BigDecimal.ZERO) <= 0 || newAmount == null) {
            return false;
        }
        return newAmount.compareTo(monthlySpendingLimit) > 0;
    }

    @Override
    public String getLimitWarningMessage() {
        return "Monthly spending limit exceeded (Limit: ₹" + monthlySpendingLimit + ")";
    }

    public void showPersonalWalletBenefits() {
        System.out.println("Personal Wallet Benefits:");
        System.out.println("- Expense tracking enabled");
        System.out.println("- Savings goals supported");
        System.out.println("- Monthly spending limit: ₹" + monthlySpendingLimit);
    }

    @Override
    public String toString() {
        return "PersonalWallet{" +
                "walletId=" + getWalletId() +
                ", balance=" + getBalance() +
                ", monthlySpendingLimit=" + monthlySpendingLimit +
                '}';
    }
}
