import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDetailsDAO {
    public void showBalance(int accountNo) {
        String sql = "SELECT holder_name, account_type, balance " +
                     "FROM accounts WHERE account_no = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n===== ACCOUNT DETAILS =====");
                    System.out.println("Account Holder: " + rs.getString("holder_name"));
                    System.out.println("Account Type: " + rs.getString("account_type"));
                    System.out.println("Available Balance: Rs." + rs.getBigDecimal("balance"));
                } else {
                    System.out.println("Account not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not fetch balance: " + e.getMessage());
        }
    }
}

