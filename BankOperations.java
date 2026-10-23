
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankOperations {
    public void showBalance(int accountNo) {
        String sql = "SELECT holder_name, account_type, balance FROM accounts WHERE account_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n===== ACCOUNT DETAILS =====");
                    System.out.println("Holder: " + rs.getString("holder_name"));
                    System.out.println("Account Type: " + rs.getString("account_type"));
                    System.out.printf("Balance: Rs. %.2f%n", rs.getDouble("balance"));
                } else {
                    System.out.println("Account not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Unable to retrieve balance: " + e.getMessage());
        }
    }

    public boolean deposit(int accountNo, double amount) {
        if (accountNo <= 0 || !Double.isFinite(amount) || amount <= 0) {
            System.out.println("Invalid account number or deposit amount.");
            return false;
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String sql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setDouble(1, amount);
                    ps.setInt(2, accountNo);
                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        System.out.println("Account not found.");
                        return false;
                    }
                }
                recordTransaction(con, accountNo, "DEPOSIT", amount);
                con.commit();
                System.out.println("Deposit successful.");
                return true;
            } catch (SQLException e) {
                rollback(con, e);
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Deposit failed: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(int accountNo, double amount) throws InsufficientFundsException {
        if (accountNo <= 0 || !Double.isFinite(amount) || amount <= 0) {
            System.out.println("Invalid account number or withdrawal amount.");
            return false;
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String type;
                double balance;
                String sql = "SELECT account_type, balance FROM accounts WHERE account_no = ? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, accountNo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            System.out.println("Account not found.");
                            return false;
                        }
                        type = rs.getString("account_type");
                        balance = rs.getDouble("balance");
                    }
                }
                double newBalance = balance - amount;
                if ("SAVINGS".equalsIgnoreCase(type) && newBalance < 1000) {
                    con.rollback();
                    throw new InsufficientFundsException(
                        "Withdrawal denied: Savings account must maintain Rs. 1000.");
                }
                if ("CURRENT".equalsIgnoreCase(type) && newBalance < -5000) {
                    con.rollback();
                    throw new InsufficientFundsException(
                        "Withdrawal denied: Current account overdraft limit exceeded.");
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE accounts SET balance = ? WHERE account_no = ?")) {
                    ps.setDouble(1, newBalance);
                    ps.setInt(2, accountNo);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Account balance was not updated.");
                    }
                }
                recordTransaction(con, accountNo, "WITHDRAWAL", amount);
                con.commit();
                System.out.println("Withdrawal successful.");
                return true;
            } catch (SQLException e) {
                rollback(con, e);
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
            return false;
        }
    }

    public boolean transfer(int fromAccount, int toAccount, double amount)
            throws InsufficientFundsException {
        if (fromAccount <= 0 || toAccount <= 0 || fromAccount == toAccount
                || !Double.isFinite(amount) || amount <= 0) {
            System.out.println("Invalid transfer details.");
            return false;
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String fromType = null;
                double fromBalance = 0;
                boolean sourceFound = false;
                boolean destinationFound = false;
                String sql = "SELECT account_no, account_type, balance FROM accounts "
                           + "WHERE account_no IN (?, ?) ORDER BY account_no FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, fromAccount);
                    ps.setInt(2, toAccount);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int foundAccount = rs.getInt("account_no");
                            if (foundAccount == fromAccount) {
                                fromType = rs.getString("account_type");
                                fromBalance = rs.getDouble("balance");
                                sourceFound = true;
                            }
                            if (foundAccount == toAccount) {
                                destinationFound = true;
                            }
                        }
                    }
                }
                if (!sourceFound) {
                    con.rollback();
                    System.out.println("Source account not found.");
                    return false;
                }
                if (!destinationFound) {
                    con.rollback();
                    System.out.println("Destination account not found.");
                    return false;
                }
                double newBalance = fromBalance - amount;
                if ("SAVINGS".equalsIgnoreCase(fromType) && newBalance < 1000) {
                    con.rollback();
                    throw new InsufficientFundsException(
                        "Transfer denied: Savings account must maintain Rs. 1000.");
                }
                if ("CURRENT".equalsIgnoreCase(fromType) && newBalance < -5000) {
                    con.rollback();
                    throw new InsufficientFundsException(
                        "Transfer denied: Current account overdraft limit exceeded.");
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE accounts SET balance = balance - ? WHERE account_no = ?")) {
                    ps.setDouble(1, amount);
                    ps.setInt(2, fromAccount);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Source account was not updated.");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE accounts SET balance = balance + ? WHERE account_no = ?")) {
                    ps.setDouble(1, amount);
                    ps.setInt(2, toAccount);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Destination account was not updated.");
                    }
                }
                recordTransaction(con, fromAccount, "TRANSFER_OUT", amount);
                recordTransaction(con, toAccount, "TRANSFER_IN", amount);
                con.commit();
                System.out.println("Transfer successful.");
                return true;
            } catch (SQLException e) {
                rollback(con, e);
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            return false;
        }
    }

    public boolean addYearlyInterest(int accountNo) {
        if (accountNo <= 0) {
            System.out.println("Invalid account number.");
            return false;
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String type;
                double balance;
                String sql = "SELECT account_type, balance FROM accounts WHERE account_no = ? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, accountNo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            System.out.println("Account not found.");
                            return false;
                        }
                        type = rs.getString("account_type");
                        balance = rs.getDouble("balance");
                    }
                }
                if (!"SAVINGS".equalsIgnoreCase(type)) {
                    con.rollback();
                    System.out.println("Yearly interest is available only for savings accounts.");
                    return false;
                }
                double interest = balance * 0.04;
                if (!Double.isFinite(interest) || !Double.isFinite(balance + interest)) {
                    con.rollback();
                    System.out.println("Unable to calculate interest for this balance.");
                    return false;
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE accounts SET balance = balance + ? WHERE account_no = ?")) {
                    ps.setDouble(1, interest);
                    ps.setInt(2, accountNo);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Account balance was not updated.");
                    }
                }
                recordTransaction(con, accountNo, "INTEREST", interest);
                con.commit();
                System.out.printf("Yearly interest added: Rs. %.2f%n", interest);
                System.out.printf("New balance: Rs. %.2f%n", balance + interest);
                System.out.println("Note: This is a manual demo; repeated selections add interest again.");
                return true;
            } catch (SQLException e) {
                rollback(con, e);
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Unable to add interest: " + e.getMessage());
            return false;
        }
    }

    private void recordTransaction(Connection con, int accountNo,
                                   String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions (account_no, transaction_type, amount) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Transaction record was not saved.");
            }
        }
    }

    private void rollback(Connection con, SQLException original) {
        try {
            con.rollback();
        } catch (SQLException rollbackError) {
            original.addSuppressed(rollbackError);
        }
    }
}
