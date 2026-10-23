public class CurrentAccount extends Account {
    public CurrentAccount(int accountNo, String holderName, double balance) {
        super(accountNo, holderName, balance);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return false;
        }
        if (balance - amount < -MAX_OVERDRAFT) {
            System.out.println("Current account overdraft limit exceeded.");
            return false;
        }
        balance -= amount;
        System.out.println("Withdrawal successful.");
        return true;
    }
}
 
