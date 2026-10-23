
public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.04;

    public SavingsAccount(int accountNo, String holderName, double balance) {
        super(accountNo, holderName, balance);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return false;
        }
        if (balance - amount < MIN_SAVINGS_BALANCE) {
            System.out.println("Savings account must maintain Rs.1000.");
            return false;
        }
        balance -= amount;
        System.out.println("Withdrawal successful.");
        return true;
    }

    public void addYearlyInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        System.out.println("Interest added: Rs." + interest);
    }
}

