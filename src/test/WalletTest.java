package test;

import model.PersonalWallet;
import model.Wallet;
import repository.TransactionRepository;
import repository.WalletRepository;
import service.WalletService;

import java.math.BigDecimal;

public class WalletTest {
    public static void main(String[] args) {

        // Creating repositories
        WalletRepository walletRepository = new WalletRepository();

        TransactionRepository transactionRepository = new TransactionRepository();

        // Creating service
        WalletService walletService =
                new WalletService(
                        walletRepository,
                        transactionRepository
                );

        // Create a personal wallet
        Wallet wallet =
                new PersonalWallet(
                        0,
                        new BigDecimal("10000"),
                        new BigDecimal("50000")
                );


        // IMPORTANT:
        // use an existing user id from users table
        int userId = 1;

        // Save wallet
        walletService.createWallet(
                wallet,
                userId
        );

        System.out.println(
                "Wallet created. ID: "
                + wallet.getWalletId()
        );

        // Deposit
        walletService.depositMoney(
                wallet,
                new BigDecimal("5000"),
                "Salary credited"
        );

        System.out.println(
                "Balance after deposit: "
                + walletService.checkBalance(wallet)
        );

        // Withdraw
        walletService.withdrawMoney(
                wallet,
                new BigDecimal("2000"),
                "Shopping"
        );

        System.out.println(
                "Balance after withdrawal: "
                + walletService.checkBalance(wallet)
        );

        System.out.println(
                "Wallet testing completed"
        );

    }
}
