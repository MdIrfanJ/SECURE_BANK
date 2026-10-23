import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccountDAO dao = new AccountDAO();
        BankOperations bank = new BankOperations();
        TransactionDAO transactionDAO = new TransactionDAO();
        while (true) {
            System.out.println("\n===== SECUREBANK =====");
            System.out.println("1. Login");
            System.out.println("2. Open New Account");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
                continue;
            }
            int option = sc.nextInt();
            sc.nextLine();
            if (option == 2) {
                System.out.print("Enter account number: ");
                if (!sc.hasNextInt()) {
                    System.out.println("Invalid account number.");
                    sc.nextLine();
                    continue;
                }
                int newAccountNo = sc.nextInt();
                sc.nextLine();

                if (newAccountNo <= 0) {
                    System.out.println("Account number must be positive.");
                    continue;
                }
                System.out.print("Enter account holder name: ");
                String holderName = sc.nextLine().trim();

                System.out.print("Enter account type (SAVINGS/CURRENT): ");
                String accountType = sc.nextLine().trim().toUpperCase();
                if (!accountType.equals("SAVINGS") && !accountType.equals("CURRENT")) {
                    System.out.println("Invalid account type.");
                    continue;
                }
                System.out.print("Enter initial deposit: ");
                if (!sc.hasNextDouble()) {
                    System.out.println("Invalid deposit. Please enter a number.");
                    sc.nextLine();
                    continue;
                }
                double balance = sc.nextDouble();
                sc.nextLine();
                if (!Double.isFinite(balance) || balance < 0
                        || (accountType.equals("SAVINGS") && balance < 1000)) {
                    System.out.println("Invalid initial deposit. Savings accounts require at least Rs.1000.");
                    continue;
                }
                System.out.print("Create a PIN (4-12 digits): ");
                String newPin = sc.nextLine();
                boolean created = dao.createAccount(
                        newAccountNo, holderName, accountType, balance, newPin);
                if (created) {
                    System.out.println("Account created successfully!");
                    System.out.println("Your account number is: " + newAccountNo);
                } else {
                    System.out.println("Account creation unsuccessful. Please check the error above.");
                }

            } else if (option == 1) {
                System.out.print("Enter account number: ");
                if (!sc.hasNextInt()) {
                    System.out.println("Invalid account number.");
                    sc.nextLine();
                    continue;
                }

                int accountNo = sc.nextInt();
                sc.nextLine();

                if (accountNo <= 0) {
                    System.out.println("Account number must be positive.");
                    continue;
                }

                System.out.print("Enter PIN: ");
                String pin = sc.nextLine();

                if (!dao.verifyPin(accountNo, pin)) {
                    System.out.println("\nLogin failed. Access denied.");
                    continue;
                }

                System.out.println("\nLogin successful!");
                System.out.println("Welcome to SecureBank!");

                int choice = 0;
                do {
                    System.out.println("\n===== BANKING MENU =====");
                    System.out.println("1. Check Balance");
                    System.out.println("2. Deposit");
                    System.out.println("3. Withdraw");
                    System.out.println("4. Transfer");
                    System.out.println("5. Transaction History");
                    System.out.println("6. Add Yearly Savings Interest");
                    System.out.println("7. Logout");
                    System.out.print("Enter your choice: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid input. Please enter a number.");
                        sc.nextLine();
                        continue;
                    }

                    choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1:
                            bank.showBalance(accountNo);
                            break;

                        case 2:
                            System.out.print("Enter deposit amount: ");
                            if (!sc.hasNextDouble()) {
                                System.out.println("Invalid amount.");
                                sc.nextLine();
                                break;
                            }
                            double depositAmount = sc.nextDouble();
                            sc.nextLine();
                            if (!Double.isFinite(depositAmount) || depositAmount <= 0) {
                                System.out.println("Amount must be greater than zero.");
                                break;
                            }
                            bank.deposit(accountNo, depositAmount);
                            break;

                        case 3:
                            System.out.print("Enter withdrawal amount: ");
                            if (!sc.hasNextDouble()) {
                                System.out.println("Invalid amount.");
                                sc.nextLine();
                                break;
                            }
                            double withdrawAmount = sc.nextDouble();
                            sc.nextLine();
                            if (!Double.isFinite(withdrawAmount) || withdrawAmount <= 0) {
                                System.out.println("Amount must be greater than zero.");
                                break;
                            }
                            try {
                                bank.withdraw(accountNo, withdrawAmount);
                            } catch (InsufficientFundsException e) {
                                System.out.println(e.getMessage());
                            }
                            break;

                        case 4:
                            System.out.print("Enter receiver account number: ");
                            if (!sc.hasNextInt()) {
                                System.out.println("Invalid receiver account number.");
                                sc.nextLine();
                                break;
                            }

                            int receiverAccount = sc.nextInt();
                            sc.nextLine();

                            if (receiverAccount <= 0 || receiverAccount == accountNo) {
                                System.out.println("Enter a valid account number different from your own.");
                                break;
                            }

                            System.out.print("Enter transfer amount: ");
                            if (!sc.hasNextDouble()) {
                                System.out.println("Invalid transfer amount.");
                                sc.nextLine();
                                break;
                            }

                            double transferAmount = sc.nextDouble();
                            sc.nextLine();

                            if (!Double.isFinite(transferAmount) || transferAmount <= 0) {
                                System.out.println("Amount must be greater than zero.");
                                break;
                            }

                            try {
                                bank.transfer(accountNo, receiverAccount, transferAmount);
                            } catch (InsufficientFundsException e) {
                                System.out.println(e.getMessage());
                            }
                            break;

                        case 5:
                            transactionDAO.showLastFiveTransactions(accountNo);
                            break;

                        case 6:
                            bank.addYearlyInterest(accountNo);
                            break;

                        case 7:
                            System.out.println("Logged out successfully.");
                            break;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } while (choice != 7);

            } else if (option == 3) {
                System.out.println("Thank you for using SecureBank!");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        sc.close();
    }
}
