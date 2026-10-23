
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MigratePins {
    public static void main(String[] args) {
        String selectSql = "SELECT account_no, pin_hash FROM accounts";
        String updateSql = "UPDATE accounts SET pin_hash = ? WHERE account_no = ? AND pin_hash = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement select = con.prepareStatement(selectSql);
                 ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    int accountNo = rs.getInt("account_no");
                    String storedValue = rs.getString("pin_hash");

                    if (storedValue != null && storedValue.matches("\\d{4,12}")) {
                        String hashedPin = PinSecurity.hashPin(storedValue);

                        try (PreparedStatement update = con.prepareStatement(updateSql)) {
                            update.setString(1, hashedPin);
                            update.setInt(2, accountNo);
                            update.setString(3, storedValue);
                            update.executeUpdate();
                        }
                    }
                }
            }

            con.commit();
            System.out.println("PIN migration completed.");
        } catch (SQLException e) {
            System.out.println("Migration failed: " + e.getMessage());
        }
    }
}
