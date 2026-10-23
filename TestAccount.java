
public class TestAccount {
    public static void main(String[] args) {
        AccountDAO dao = new AccountDAO();

        boolean created = dao.createAccount(
            1001,
            "Test User",
            "SAVINGS",
            5000.00,
            "1234"
        );

        if (created) {
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Account was not created.");
        }
    }
}
