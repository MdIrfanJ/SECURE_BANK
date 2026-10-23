
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDAO {

    public void showLastFiveTransactions(int accountNo) {
        String sql = "SELECT transaction_id, transaction_type, amount, transaction_time " +
                     "FROM transactions WHERE account_no = ? " +
                     "ORDER BY transaction_id DESC LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, accountNo);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n===== LAST 5 TRANSACTIONS =====");

                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println("Transaction ID: " + rs.getInt("transaction_id"));
                    System.out.println("Transaction Type: " + rs.getString("transaction_type"));
                    System.out.printf("Amount: Rs. %.2f%n", rs.getDouble("amount"));
                    System.out.println("Date and Time: " + rs.getTimestamp("transaction_time"));
                    System.out.println("------------------------------");
                }

                if (!found) {
                    System.out.println("No transactions found for this account.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Unable to retrieve transaction history: " + e.getMessage());
        }
    }
}
