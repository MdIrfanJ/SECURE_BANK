import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    public static String login(String username, String password) {
        String sql = "select password_hash, role from admins where username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");

                    if (PinSecurity.verifyPin(password, storedHash)) {
                        return rs.getString("role");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Admin login error: " + e.getMessage());
        }

        return null;
    }
}
