
public class TestOperations {
    public static void main(String[] args) {
        Account savings = new SavingsAccount(2001, "Test Savings", 5000);
        Account current = new CurrentAccount(2002, "Test Current", 2000);

        savings.displayAccount();
        savings.withdraw(1000);

        System.out.println();

        current.displayAccount();
        current.withdraw(3000);
        System.out.println("Savings balance after withdrawal: Rs." + savings.getBalance());
System.out.println("Current balance after withdrawal: Rs." + current.getBalance());
    }
}
