import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminOperations {

    public static void viewAllAccounts() {
        String sql = "select account_no,holder_name,account_type,balance,locked from accounts";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== ALL ACCOUNTS =====");

            while (rs.next()) {
                System.out.println("Account Number: " + rs.getString("account_no"));
                System.out.println("Holder Name: " + rs.getString("holder_name"));
                System.out.println("Account Type: " + rs.getString("account_type"));
                System.out.println("Balance: Rs." + rs.getDouble("balance"));
                System.out.println("Locked: " + rs.getBoolean("locked"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void searchAccount(Scanner sc) {
        System.out.print("Enter account number: ");
        String accountNo = sc.nextLine();

        String sql = "select account_no,holder_name,account_type,balance,locked from accounts where account_no=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, accountNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n===== ACCOUNT DETAILS =====");
                    System.out.println("Account Number: " + rs.getString("account_no"));
                    System.out.println("Holder Name: " + rs.getString("holder_name"));
                    System.out.println("Account Type: " + rs.getString("account_type"));
                    System.out.println("Balance: Rs." + rs.getDouble("balance"));
                    System.out.println("Locked: " + rs.getBoolean("locked"));
                } else {
                    System.out.println("Account not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }   
public static void changeAccountStatus(Scanner sc) {
    System.out.print("Enter account number: ");
    String accountNo = sc.nextLine().trim();
    if (accountNo.isEmpty()) {
        System.out.println("Account number cannot be empty.");
        return;
    }

    String checkSql = "select locked from accounts where account_no=?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement check = con.prepareStatement(checkSql)) {

        check.setString(1, accountNo);

        try (ResultSet rs = check.executeQuery()) {
            if (!rs.next()) {
                System.out.println("Account not found.");
                return;
            }

            boolean currentlyLocked = rs.getBoolean("locked");
            boolean newStatus = !currentlyLocked;

            String updateSql;

            if (newStatus) {
                updateSql = "update accounts set locked=? where account_no=?";
            } else {
                updateSql = "update accounts set locked=?, failed_attempts=0 where account_no=?";
            }

            try (PreparedStatement update = con.prepareStatement(updateSql)) {
                update.setBoolean(1, newStatus);
                update.setString(2, accountNo);

                int rows = update.executeUpdate();

                if (rows > 0) {
                    System.out.println(newStatus
                            ? "Account locked successfully."
                            : "Account unlocked successfully.");

                    if (!newStatus) {
                        System.out.println("Failed PIN attempts reset successfully.");
                    }
                }
            }
        }

    } catch (SQLException e) {
        System.out.println("Error: " + e.getMessage());
    }
}
    public static void createAdmin(Scanner sc) {
        System.out.print("Enter new admin username: ");
        String username = sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        System.out.print("Enter password (minimum 8 characters): ");
        String password = sc.nextLine();

        if (password.length() < 8) {
            System.out.println("Password must contain at least 8 characters.");
            return;
        }

        System.out.println("Choose role:");
        System.out.println("1. ACCOUNT_MANAGER");
        System.out.println("2. AUDITOR");
        System.out.print("Enter choice: ");

        int choice;

        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }

        String role;

        if (choice == 1) {
            role = "ACCOUNT_MANAGER";
        } else if (choice == 2) {
            role = "AUDITOR";
        } else {
            System.out.println("Invalid role choice.");
            return;
        }

        String sql = "insert into admins(username,password_hash,role) values(?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, PinSecurity.hashPin(password));
            ps.setString(3, role);

            ps.executeUpdate();

            System.out.println("Admin created successfully.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Username already exists.");
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    public static void removeAdmin(Scanner sc) {
        System.out.print("Enter admin username to remove: ");
        String username = sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        String checkSql = "select role from admins where username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setString(1, username);

        try (ResultSet rs = check.executeQuery()) {
            if (!rs.next()) {
                System.out.println("Admin not found.");
                return;
            }

            String role = rs.getString("role");

            if (role.equals("SUPER_ADMIN")) {
                System.out.println("SUPER_ADMIN accounts cannot be removed.");
                return;
            }
        }

        System.out.print("Are you sure you want to remove " + username + "? (yes/no): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Removal cancelled.");
            return;
        }

        String deleteSql = "delete from admins where username=? and role in ('ACCOUNT_MANAGER','AUDITOR')";

        try (PreparedStatement delete = con.prepareStatement(deleteSql)) {
            delete.setString(1, username);

            int rows = delete.executeUpdate();

            if (rows > 0) {
                System.out.println("Admin removed successfully.");
            } else {
                System.out.println("Admin could not be removed.");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error removing admin: " + e.getMessage());
    }
}
public static void viewAllAdmins() {
    String sql = "select username,role from admins order by role,username";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        System.out.println("\n========== ADMIN DIRECTORY ==========");
        System.out.printf("%-20s %-20s%n", "USERNAME", "DESIGNATION");
        System.out.println("-------------------------------------");

        while (rs.next()) {
            String username = rs.getString("username");
            String role = rs.getString("role");

            String designation;

            if (role.equals("SUPER_ADMIN")) {
                designation = "Super Admin";
            } else if (role.equals("ACCOUNT_MANAGER")) {
                designation = "Account Manager";
            } else if (role.equals("AUDITOR")) {
                designation = "Auditor";
            } else {
                designation = role;
            }

            System.out.printf("%-20s %-20s%n", username, designation);
        }

        System.out.println("=====================================");

    } catch (SQLException e) {
        System.out.println("Error displaying admins: " + e.getMessage());
    }
}
}