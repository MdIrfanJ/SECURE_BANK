public class TestPin {
    public static void main(String[] args) {
        AccountDAO dao = new AccountDAO();
        dao.verifyPin(1001, "1234");
    }
}
