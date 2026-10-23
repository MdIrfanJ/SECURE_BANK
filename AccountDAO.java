
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {
    public boolean createAccount(int accountNo, String holderName,
                                 String accountType, double balance,
                                 String pin) {
        if (accountNo <= 0 || pin == null || !pin.matches("\\d{4,12}")) {
            System.out.println("Invalid account number or PIN. PIN must contain 4 to 12 digits.");
            return false;
        }
        if (holderName == null || holderName.trim().isEmpty()
                || !Double.isFinite(balance) || balance < 0
                || accountType == null
                || !(accountType.equalsIgnoreCase("SAVINGS")
                || accountType.equalsIgnoreCase("CURRENT"))) {
            System.out.println("Invalid account details.");
            return false;
        }
        if (accountType.equalsIgnoreCase("SAVINGS") && balance < 1000) {
            System.out.println("Savings account requires a minimum balance of 1000.");
            return false;
        }

        String sql = "INSERT INTO accounts "
                   + "(account_no, holder_name, account_type, balance, pin_hash) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountNo);
            ps.setString(2, holderName.trim());
            ps.setString(3, accountType.toUpperCase());
            ps.setDouble(4, balance);
            ps.setString(5, PinSecurity.hashPin(pin));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Account creation failed: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyPin(int accountNo, String pin) {
        if (accountNo <= 0 || pin == null || !pin.matches("\\d{4,12}")) {
            System.out.println("Invalid account number or PIN. PIN must contain 4 to 12 digits.");
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String sql = "SELECT pin_hash, locked, failed_attempts "
                           + "FROM accounts WHERE account_no = ? FOR UPDATE";
                String storedHash;
                boolean locked;
                int attempts;

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, accountNo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            System.out.println("Account not found.");
                            return false;
                        }
                        storedHash = rs.getString("pin_hash");
                        locked = rs.getBoolean("locked");
                        attempts = rs.getInt("failed_attempts");
                    }
                }

                if (locked) {
                    con.rollback();
                    System.out.println("Account is locked.");
                    return false;
                }

                if (PinSecurity.verifyPin(pin, storedHash)) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE accounts SET failed_attempts = 0 WHERE account_no = ?")) {
                        ps.setInt(1, accountNo);
                        if (ps.executeUpdate() != 1) {
                            throw new SQLException("Failed to reset login attempts.");
                        }
                    }
                    con.commit();
                    System.out.println("PIN verified successfully.");
                    return true;
                }

                attempts++;
                boolean shouldLock = attempts >= 3;
                String updateSql = "UPDATE accounts SET failed_attempts = ?, locked = ? "
                                 + "WHERE account_no = ?";
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setInt(1, attempts);
                    ps.setBoolean(2, shouldLock);
                    ps.setInt(3, accountNo);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Failed to record incorrect PIN attempt.");
                    }
                }

                con.commit();
                if (shouldLock) {
                    System.out.println("Account locked after 3 incorrect PIN attempts.");
                } else {
                    System.out.println("Incorrect PIN.");
                }
                return false;

            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("PIN verification failed: " + e.getMessage());
            return false;
        }
    }
}
