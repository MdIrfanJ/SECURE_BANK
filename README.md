# Smart Bank Management System

A console-based banking application developed using **Java and MySQL**. The project demonstrates object-oriented programming, database connectivity using JDBC, transaction management, and secure PIN authentication.

## Features

- **PIN Authentication:** Verify account PINs before allowing access.
- **Secure PIN Hashing:** Store PINs as hashes using `PBKDF2WithHmacSHA256` with a random salt instead of storing plain-text PINs.
- **Account Locking:** Lock accounts after three incorrect valid-format PIN attempts.
- **Balance Inquiry:** View account details and current balance.
- **Deposit and Withdrawal:** Perform banking transactions with validation.
- **Fund Transfer:** Transfer money between bank accounts.
- **Transaction History:** Display the latest five transactions.
- **Savings Interest:** Apply 4% yearly interest through a manual Super Admin operation.
- **Account Rules:** Maintain a minimum savings balance of ₹1,000 and enforce a current-account overdraft limit of ₹5,000.
- **Exception Handling:** Use a custom `InsufficientFundsException`.
- **Admin Management:** Support administrative roles such as Super Admin, Account Manager, and Auditor.

## Technologies Used

- Java
- MySQL
- JDBC
- MySQL Connector/J
- Visual Studio Code
- MySQL Workbench

## Object-Oriented Programming Concepts

- **Abstraction:** Use an abstract `Account` class to define common account features.
- **Inheritance:** `SavingsAccount` and `CurrentAccount` extend the `Account` class.
- **Method Overriding:** Account types provide their own specific account rules.
- **Polymorphism:** Account objects can be handled through their common parent class.
- **Encapsulation:** Account data and operations are organized within classes.

## Database

The application uses a MySQL database named `securebank`.

The database contains three main tables:

- **`accounts`** — Stores account details, balances, PIN hashes, account-lock status, and failed login attempts.
- **`transactions`** — Stores transaction records, including transaction type, amount, and transaction time.
- **`admins`** — Stores administrator details, password hashes, and assigned roles.

### Admin Roles

- `SUPER_ADMIN`
- `ACCOUNT_MANAGER`
- `AUDITOR`

## Security Features

- PINs are stored using `PBKDF2WithHmacSHA256` hashing with a random salt.
- The original PIN is not stored in the database.
- Accounts are locked after three incorrect valid-format PIN attempts.
- JDBC `PreparedStatement` is used for parameterized SQL operations.

## How to Run

### 1. Prerequisites

Install the following:

- Java JDK
- MySQL Server
- MySQL Workbench
- MySQL Connector/J

Ensure the MySQL Connector/J JAR file is available in the project's `lib` folder.

### 2. Configure the Database

- Create the `securebank` database.
- Create the required tables using the project's SQL setup.
- Configure the database connection details in `DBConnection.java`.
- Keep database passwords out of public source code. Use environment variables or a local configuration file excluded from Git.

### 3. Compile

Open a terminal in the project directory and run:

```powershell
javac -cp ".;lib/*" *.java
```

### 4. Run

Execute:

```powershell
java -cp ".;lib/*" Main
```

### 5. Use the Application

Log in using an account number and PIN. After successful authentication, select the required operation from the banking menu.

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

This is an educational project developed to demonstrate Java, JDBC, MySQL, object-oriented programming, and basic banking security concepts.

The 4% yearly interest operation is triggered manually by a Super Admin. It does not run automatically on a yearly schedule or maintain duplicate annual-interest records.

This application is not intended for real-world banking use.

## Project Team

This project was collaboratively developed by:

1. **Mohamed Irfan J**
2. **Mohammed Reabai J**
3. **Muralikarthick V**
4. **Devanathan C S**

---

**Project:** Smart Bank Management System  
**Domain:** Java Application Development and Database Management  
**Technologies:** Java, MySQL and JDBC