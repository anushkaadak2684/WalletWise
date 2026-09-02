package model;

public class User {

    private int userId;
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;

    // Composition
    private Wallet wallet;

    public User() {
    }

    public User(int userId, String fullName, String email,
                String password, String phoneNumber) {
        this(userId, fullName, email, password, phoneNumber, null);
    }

    public User(int userId, String fullName, String email,
                String password, String phoneNumber, Wallet wallet) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.wallet = wallet;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Wallet getWallet() {
        return wallet;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void displayProfile() {

        System.out.println("\n========== USER PROFILE ==========");

        System.out.println("User ID      : " + userId);
        System.out.println("Name         : " + fullName);
        System.out.println("Email        : " + email);
        System.out.println("Phone Number : " + phoneNumber);

        if (wallet != null) {
            System.out.println("Wallet ID    : " + wallet.getWalletId());
            System.out.println("Wallet Type  : " + wallet.getWalletType());
        }

        System.out.println("==================================");
    }

    @Override
    public String toString() {

        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

}