# SecureBank — Java Banking Management System

SecureBank is a console-based banking application developed using Java and MySQL. It demonstrates object-oriented programming, database connectivity, transaction management, and basic account security.

## Features

- **PIN Authentication:** Verify account PINs before login.
- **Account Locking:** Lock accounts after three incorrect PIN attempts.
- **Balance Inquiry:** View account details and current balance.
- **Deposit and Withdrawal:** Perform transactions with validation.
- **Fund Transfer:** Transfer money between bank accounts.
- **Transaction History:** Display the latest five transactions.
- **Savings Interest:** Calculate and add 4% interest to savings accounts.
- **Account Rules:** Maintain a minimum savings balance of ₹1,000 and enforce a current-account overdraft limit of ₹5,000.
- **Exception Handling:** Use a custom `InsufficientFundsException`.

## Technologies Used

- Java
- MySQL
- JDBC
- MySQL Connector/J
- Visual Studio Code
- MySQL Workbench

## Object-Oriented Programming Concepts

- **Abstraction:** Abstract `Account` class.
- **Inheritance:** `SavingsAccount` and `CurrentAccount` extend `Account`.
- **Method Overriding:** Account types implement their specific account rules.
- **Polymorphism:** Account behavior can be represented through the parent class.
- **Encapsulation:** Account data and operations are organized into classes.

## Database

The application uses a MySQL database named `securebank`.

The database includes:
- `accounts` — stores account details, balances, and authentication status.
- `transactions` — stores deposits, withdrawals, transfers, and interest entries.

Create the database and tables using your project's SQL setup script before running the application.

## How to Run

### 1. Prerequisites
Install Java JDK and MySQL. Ensure the MySQL Connector/J JAR is available in the project's `lib` folder.

### 2. Configure the Database
Configure your MySQL connection in `DBConnection.java`. Keep passwords out of public source code; use environment variables or a local configuration file that is excluded from Git.

### 3. Compile
Open a terminal in the project directory and run:

```powershell
javac -cp ".;lib\*" *.java
```

### 4. Run
Execute:

```powershell
java -cp ".;lib\*" Main
```

### 5. Use the Application
Log in with an account number and PIN, then select an option from the banking menu.

## Project Structure

```text
Java/
├── lib/
│   └── mysql-connector-j-26.7.0.jar
├── Account.java
├── AccountDAO.java
├── BankOperations.java
├── CurrentAccount.java
├── DBConnection.java
├── InsufficientFundsException.java
├── Main.java
├── SavingsAccount.java
├── TransactionDAO.java
└── README.md
```

## Important Note

This is an educational project demonstrating Java, JDBC, and MySQL concepts. The yearly interest option applies interest whenever selected; it does not automatically track annual interest periods. This application is not intended for real banking use.

## Author

Developed as a Java and MySQL learning project.
