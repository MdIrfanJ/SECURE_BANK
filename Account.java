public abstract class Account {
    protected int accountNo;
    protected String holderName;
    protected double balance;
    protected static final double MIN_SAVINGS_BALANCE = 1000.0;
    protected static final double MAX_OVERDRAFT = 5000.0;

    public Account(int accountNo, String holderName, double balance) {
        this.accountNo = accountNo;
        this.holderName = holderName;
        this.balance = balance;
    }

    public abstract boolean withdraw(double amount);

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    public void displayAccount() {
        System.out.println("Account Number: " + accountNo);
        System.out.println("Holder Name: " + holderName);
        System.out.println("Balance: Rs." + balance);
    }

    public double getBalance() {
        return balance;
    }
}
