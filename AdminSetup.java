import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminSetup {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== CREATE FIRST SUPER ADMIN =====");

        System.out.print("Enter admin username: ");
        String username = sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            sc.close();
            return;
        }

        System.out.print("Enter admin password (minimum 8 characters): ");
        String password = sc.nextLine();

        if (password.length() < 8) {
            System.out.println("Password must contain at least 8 characters.");
            sc.close();
            return;
        }

        String role = "SUPER_ADMIN";

        String sql = "insert into admins(username,password_hash,role) values(?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, PinSecurity.hashPin(password));
            ps.setString(3, role);

            ps.executeUpdate();

            System.out.println("First Super Admin created successfully!");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Admin username already exists.");
            } else {
                System.out.println("Error creating admin: " + e.getMessage());
            }
        }

        sc.close();
    }
}