import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

public class DatabaseReader {

	// Declare variables
	private static Connection conn;
	private static Statement stmt;
	private static Scanner dbScanner = new Scanner(System.in);
	static String subMenuChoice;
	static int questionIndex;

	// Connection
	public DatabaseReader() {
		try {
			// Establish a connection to the database
			String url = "jdbc:mysql://127.0.0.1:3306/ims";
			String user = "root";
			String password = "Jmulletis22!!";
			conn = DriverManager.getConnection(url, user, password);

			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Login method
	public String[] authenticateUser() throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(
				"SELECT employee_type, first_name, last_name, username FROM employees WHERE username = ? AND password = ?");
		System.out.print("\nEnter your username: ");
		String username = dbScanner.nextLine();
		System.out.print("Enter your password: ");
		String password = dbScanner.nextLine();
		stmt.setString(1, username);
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String[] result = new String[4];
			result[0] = rs.getString("employee_type");
			result[1] = rs.getString("first_name");
			result[2] = rs.getString("last_name");
			result[3] = rs.getString("username");
			return result;
		}
		return null;
	}

	// Logs entries uploaded to database
	public static void uploadLog(String severity, String logEntry) throws SQLException {
		String createRecord = "INSERT INTO logs (severity, logEntry) VALUES (?, ?)";
		PreparedStatement stmt = conn.prepareStatement(createRecord);
		stmt.setString(1, severity);
		stmt.setString(2, logEntry);
		stmt.executeUpdate();
	}

	// Read log entries from database
	public static void readLogFromDb() throws SQLException {
		// Select all log records from the logs table
		String selectLogs = "SELECT * FROM logs";
		ResultSet rs = stmt.executeQuery(selectLogs);

		// Loop through the result set and print each log record
		while (rs.next()) {
			int logNum = rs.getInt("idLog");
			String level = rs.getString("severity");
			String logEntry = rs.getString("logEntry");
			System.out.println("\nLog Number: " + logNum);
			System.out.println("Level: " + level);
			System.out.println("Log Entry: " + logEntry);
			System.out.println();
		}
	}

	// Suggest bug fixes/improvements
	public static void helpAManOut() throws SQLException, InterruptedException {
		String createRecord = "INSERT INTO bug_fixes_or_improvements (suggestion) VALUES (?)";
		PreparedStatement stmt = conn.prepareStatement(createRecord);
		boolean loop = true;
		while (loop) {
			// Ask user for suggestion
			System.out.println("\nWhat suggestions would you like to make, professor?: ");
			String suggestion = dbScanner.nextLine();
			System.out.println("\n1. Submit");
			System.out.println("2. Start Over");
			System.out.println("3. Cancel");
			String subMenuChoice = dbScanner.nextLine();

			switch (subMenuChoice) {
			// Submit
			case "1":
				stmt.setString(1, suggestion);
				stmt.executeUpdate();
				System.out.println("\nSuggestion Submitted");
				loop = false;
				break;
			// Start Over
			case "2":
				System.out.println("\nStarting Over");
				break;
			// Cancel Operation
			case "3":
				System.out.println("\nCancelling Operation");
				loop = false;
				break;
			default:
				System.out.println("\nInvalid input, please try again.");
				break;
			}
		}
	}

	// Create inventory record
	public void createInventory(String productName, String productType, int minimumThreshold, double productPrice,
			int quantity, String username) throws InterruptedException {
		try {
			// Check if the product name already exists in the inventory_list table
			String checkIfExists = "SELECT * FROM inventory_list WHERE product_name=?";
			PreparedStatement stmt = conn.prepareStatement(checkIfExists);
			stmt.setString(1, productName);
			ResultSet rs = stmt.executeQuery();

			// If product name already exists, warn user
			Main.process();
			if (rs.next()) {
				System.out.println("\nProduct Already Exists");
			} else {
				// If product name does not exist, create a new record
				String createRecord = "INSERT INTO inventory_list (product_name, product_type, minimum_threshold, product_price, quantity) VALUES (?, ?, ?, ?, ?)";
				PreparedStatement createPs = conn.prepareStatement(createRecord);
				createPs.setString(1, productName);
				createPs.setString(2, productType);
				createPs.setInt(3, minimumThreshold);
				createPs.setDouble(4, productPrice);
				createPs.setInt(5, quantity);
				createPs.executeUpdate();
				LoggerClass.log(Level.INFO, username + " added a new product to inventory, " + productName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Experimenting with different, more secure ways to interact with database
	// (implemented successfully in createEmployee
	// SQL statements
	private static final String CHECK_USERNAME_SQL = "SELECT COUNT(*) FROM employees WHERE username=?";
	private static final String INSERT_EMPLOYEE_SQL = "INSERT INTO employees (first_name, last_name, employee_type, username, password, challenge_question, challenge_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
	// SQL parameter indices
	private static final int FIRST_NAME_PARAM_IDX = 1;
	private static final int LAST_NAME_PARAM_IDX = 2;
	private static final int EMPLOYEE_TYPE_PARAM_IDX = 3;
	private static final int USERNAME_PARAM_IDX = 4;
	private static final int PASSWORD_PARAM_IDX = 5;
	private static final int CHALLENGE_QUESTION_PARAM_IDX = 6;
	private static final int CHALLENGE_ANSWER_PARAM_IDX = 7;

	// Create new employee record
	public void createEmployee(String firstName, String lastName, String employeeType, String username, String password,
			String challengeQuestion, String challengeAnswer, String hrUsername)
			throws InterruptedException, SQLException {
		try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_USERNAME_SQL);
				PreparedStatement insertStmt = conn.prepareStatement(INSERT_EMPLOYEE_SQL,
						Statement.RETURN_GENERATED_KEYS)) {

			// Check if the username is already taken
			checkStmt.setString(1, username);
			ResultSet checkResult = checkStmt.executeQuery();
			checkResult.next();
			int count = checkResult.getInt(1);
			if (count > 0) {
				System.out.println("\nUsername " + username + " is already taken.");
				return;
			}

			// Insert a new employee
			insertStmt.setString(FIRST_NAME_PARAM_IDX, firstName);
			insertStmt.setString(LAST_NAME_PARAM_IDX, lastName);
			insertStmt.setString(EMPLOYEE_TYPE_PARAM_IDX, employeeType);
			insertStmt.setString(USERNAME_PARAM_IDX, username);
			insertStmt.setString(PASSWORD_PARAM_IDX, password);
			insertStmt.setString(CHALLENGE_QUESTION_PARAM_IDX, challengeQuestion);
			insertStmt.setString(CHALLENGE_ANSWER_PARAM_IDX, challengeAnswer);

			int numRowsAffected = insertStmt.executeUpdate();
			Main.process();
			if (numRowsAffected == 1) {
				// Get the auto-generated employee ID
				ResultSet rs = insertStmt.getGeneratedKeys();
				if (rs.next()) {
					int employeeId = rs.getInt(1);
					System.out.println("\nEmployee " + firstName + " " + lastName + " added successfully with ID "
							+ employeeId + ".");
					LoggerClass.log(Level.INFO, hrUsername + " added an employee, " + username);
				}
			} else {
				System.out.println("\nFailed to add employee " + firstName + " " + lastName + ".");
			}
		} catch (SQLException e) {
			LoggerClass.log(Level.SEVERE, "Failed to create employee");
		}
	}

	// Print all employee records to console
	public void showAllEmployees() throws SQLException, InterruptedException {
		// Select all employee records from the employees table
		String selectEmployees = "SELECT * FROM employees";
		ResultSet rs = stmt.executeQuery(selectEmployees);

		// Loop through the result set and print each employee record
		while (rs.next()) {
			int employeeId = rs.getInt("employee_id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");
			String employeeType = rs.getString("employee_type");
			String username = rs.getString("username");
			String password = rs.getString("password");
			String challengeQuestion = rs.getString("challenge_question");
			String challengeAnswer = rs.getString("challenge_answer");
			System.out.println("\nEmployee ID: " + employeeId);
			System.out.println("Name: " + firstName + " " + lastName);
			System.out.println("Employee Type: " + employeeType);
			System.out.println("Username: " + username);
			System.out.println("Password: " + password);
			System.out.println("Challenge Question: " + challengeQuestion);
			System.out.println("Challenge Answer: " + challengeAnswer);
			System.out.println();
		}
	}

	// Show select columns for all inventory records
	public static void showEmployeeInventory() throws SQLException, InterruptedException {
		// Select the product name, product price, and quantity columns from the
		// inventory_list table
		String selectInventory = "SELECT product_number, product_name, product_price, quantity FROM inventory_list";
		ResultSet rs = stmt.executeQuery(selectInventory);

		// Loop through the result set and print the product name, product price, and
		// quantity for each record
		while (rs.next()) {
			int productNumber = rs.getInt("product_number");
			String productName = rs.getString("product_name");
			double productPrice = rs.getDouble("product_price");
			int quantity = rs.getInt("quantity");
			System.out.println("\n| Product Number: " + productNumber + " | Product Name: " + productName
					+ " | Product Price: " + productPrice + " | Available Qty: " + quantity);
		}

	}

	// Show all inventory records
	public void showManagerInventory() throws SQLException, InterruptedException {
		// Select all columns from the inventory_list table
		String selectInventory = "SELECT * FROM inventory_list";
		ResultSet rs = stmt.executeQuery(selectInventory);

		// Loop through the result set and print all columns for each record
		while (rs.next()) {
			int productNumber = rs.getInt("product_number");
			String productName = rs.getString("product_name");
			String productType = rs.getString("product_type");
			int minimumThreshold = rs.getInt("minimum_threshold");
			double productPrice = rs.getDouble("product_price");
			int quantity = rs.getInt("quantity");
			System.out.println("\nProduct Number: " + productNumber);
			System.out.println("Product Name: " + productName);
			System.out.println("Product Type: " + productType);
			System.out.println("Minimum Threshold: " + minimumThreshold);
			System.out.println("Product Price: " + productPrice);
			System.out.println("Quantity: " + quantity);
			System.out.println();
		}
	}

	public static int decrementInventory(int productNumber, int quantityToDecrement, String username) throws SQLException, InterruptedException {
	    String checkInventory = "SELECT * FROM inventory_list WHERE product_number=?";
	    PreparedStatement stmt = conn.prepareStatement(checkInventory);
	    stmt.setInt(1, productNumber);
	    ResultSet rs = stmt.executeQuery();
	    int currentQuantity = 0;
	    while (rs.next()) {
	        currentQuantity = rs.getInt("quantity");
	    }
	    if (quantityToDecrement > currentQuantity) {
	        System.out.println("Decrement amount exceeds available quantity.");
	        return 0;
	    }
	    int newQuantity = currentQuantity - quantityToDecrement;
	    String decrementInventory = "UPDATE inventory_list SET quantity=? WHERE product_number=?";
	    PreparedStatement decrementStmt = conn.prepareStatement(decrementInventory);
	    decrementStmt.setInt(1, newQuantity);
	    decrementStmt.setInt(2, productNumber);
	    decrementStmt.executeUpdate();
	    LoggerClass.log(Level.INFO, username + " decremented product number, " + productNumber + ", by " + quantityToDecrement);
	    return quantityToDecrement;
	}



	// Add quantity to an inventory record
	public void incrementInventory(int productNumber, int quantityToIncrement, String username)
			throws SQLException, InterruptedException {
		String incrementInventory = "UPDATE inventory_list SET quantity=quantity+? WHERE product_number=?";
		PreparedStatement stmt = conn.prepareStatement(incrementInventory);
		stmt.setInt(1, quantityToIncrement);
		stmt.setInt(2, productNumber);
		Main.process();
		int rowsUpdated = stmt.executeUpdate();
		LoggerClass.log(Level.INFO,
				username + " incremented product number " + productNumber + ", by " + quantityToIncrement);
		System.out.println(rowsUpdated + " row(s) updated.");
	}

	// Check inventory for products under minimum threshold and create an order
	public void checkInventoryThresholds(String username) throws SQLException, InterruptedException {
		String checkInventoryThresholds = "SELECT * FROM inventory_list WHERE quantity <= minimum_threshold";
		PreparedStatement stmt = conn.prepareStatement(checkInventoryThresholds);
		ResultSet rs = stmt.executeQuery();

		System.out.println("Order Report:");
		System.out.println("Product Number\tProduct Name\tQuantity Needed");
		Main.process();
		while (rs.next()) {
			int productNumber = rs.getInt("product_number");
			String productName = rs.getString("product_name");
			int currentQuantity = rs.getInt("quantity");
			int minimumThreshold = rs.getInt("minimum_threshold");
			int quantityNeeded = minimumThreshold - currentQuantity;
			System.out.println(productNumber + "\t" + productName + "\t" + quantityNeeded);
		}
		LoggerClass.log(Level.INFO, username + "Generated order report");
	}

	public void deleteEmployee(String username, String hrUsername) throws SQLException, InterruptedException {
		// Delete employee with the specified username from the employees table
		String deleteEmployee = "DELETE FROM employees WHERE username='" + username + "'";
		int rowsDeleted = stmt.executeUpdate(deleteEmployee);
		Main.process();
		if (rowsDeleted > 0) {
			LoggerClass.log(Level.INFO, hrUsername + " deleted user, " + username + " from employee database");
			System.out.println("\nEmployee with username '" + username + "' has been deleted.");
		} else {
			LoggerClass.log(Level.WARNING, hrUsername + " attempted to delete non-existent user, " + username);
			System.out.println("\nEmployee with username '" + username + "' does not exist.");
		}
	}

	// Delete an inventory record
	public void deleteProductFromDatabase(int productNumber, String username)
			throws SQLException, InterruptedException {
		// Delete product with the specified name from the inventory_list table
		String deleteProduct = "DELETE FROM inventory_list WHERE product_number='" + productNumber + "'";
		int rowsDeleted = stmt.executeUpdate(deleteProduct);
		Main.process();
		if (rowsDeleted > 0) {
			LoggerClass.log(Level.INFO, username + " deleted " + productNumber + " from inventory");
			System.out.println("\nProduct number: " + productNumber + "' has been deleted.");
		} else {
			LoggerClass.log(Level.WARNING, username + " attempted to delete non-existent product, " + productNumber);
			System.out.println("\nProduct number: " + productNumber + " does not exist.");
		}
	}

	// Asks new user to set up their account
	public boolean checkAccountIsSetup(String username) throws SQLException, InterruptedException {
		String checkPass = "SELECT password FROM employees WHERE username=?";
		PreparedStatement ps = conn.prepareStatement(checkPass);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			String password = rs.getString("password");
			if (password.equals("default")) {
				System.out.println("\nYour account is not yet set up. Please change your password.\n");

				// Prompt the user to change their password
				System.out.print("Enter a new password: ");
				String newPassword = dbScanner.nextLine();

				// Prompt the user to select a challenge question
				System.out.println("\nChoose a challenge question:");
				String[] challengeQuestions = { "What is your mother's maiden name?", "What is your favorite color?",
						"What city were you born in?", "What was your first pet's name?" };
				for (int i = 0; i < challengeQuestions.length; i++) {
					System.out.println((i + 1) + ". " + challengeQuestions[i]);
				}

				// Prompt the user to enter the number of the question they want to use
				int questionIndex = 0;
				boolean validInput = false;
				while (!validInput) {
					System.out.print("Enter the number of the question you want to use: ");
					if (dbScanner.hasNextInt()) {
						questionIndex = dbScanner.nextInt();
						if (questionIndex > 0 && questionIndex <= challengeQuestions.length) {
							validInput = true;
						} else {
							System.out.println("Invalid input! Please enter a valid question number.");
						}
					} else {
						dbScanner.nextLine(); // Clear scanner buffer
						System.out.println("Invalid input! Please enter an integer.");
					}
				}

				// Prompt the user to enter a challenge answer
				dbScanner.nextLine(); // Clear scanner buffer
				System.out.print("Enter a challenge answer: ");
				String challengeAnswer = dbScanner.nextLine();

				// Update the user's password and challenge information in the database
				String updateQuery = "UPDATE employees SET password=?, challenge_question=?, challenge_answer=? WHERE username=?";
				PreparedStatement updatePs = conn.prepareStatement(updateQuery);
				updatePs.setString(1, newPassword);
				updatePs.setString(2, challengeQuestions[questionIndex - 1]);
				updatePs.setString(3, challengeAnswer);
				updatePs.setString(4, username);
				int rowsUpdated = updatePs.executeUpdate();
				if (rowsUpdated > 0) {
					Main.process();
					LoggerClass.log(Level.INFO, username + " finished setting up their account");
					System.out.println("\nPassword and challenge information updated successfully.");
					return true;
				} else {
					System.out.println("\nError updating password and challenge information.");
					return false;
				}
			} else {
				return true;
			}
		} else {
			System.out.println("\nUser not found.");
			return false;
		}
	}

	// Update the name column from employee record
	public void updateName(String username, String fname, String lname) throws SQLException, InterruptedException {
		if (fname != null && lname != null) {
			String query = "UPDATE employees SET first_name=?, last_name=? WHERE username=?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, fname);
			ps.setString(2, lname);
			ps.setString(3, username);
			Main.process();
			int rowsUpdated = ps.executeUpdate();
			if (rowsUpdated == 0) {
				System.out.println("\nInvalid username.");
			} else {
				LoggerClass.log(Level.INFO, username + " updated their name to " + fname + " " + lname);
				System.out.println("\nName updated successfully.");
			}
		}
	}

	// Update the username column from employee record
	public void updateUsername(String oldUsername, String newUsername) throws SQLException, InterruptedException {
		if (newUsername != null) {
			String query = "UPDATE employees SET username=? WHERE username=?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, newUsername);
			ps.setString(2, oldUsername);
			int rowsUpdated = ps.executeUpdate();
			Main.process();
			if (rowsUpdated == 0) {
				System.out.println("\nInvalid old username.");
			} else {
				LoggerClass.log(Level.INFO, oldUsername + " updated their username to " + newUsername);
				System.out.println("\nUsername updated successfully.");
			}
		}
	}

	// Update the password column from employee record
	public void updatePassword(String username, String newPassword) throws SQLException, InterruptedException {
		String sql = "UPDATE employees SET password=? WHERE username=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, newPassword);
			ps.setString(2, username);
			int rowsUpdated = ps.executeUpdate();
			Main.process();
			if (rowsUpdated == 0) {
				System.out.println("\nInvalid username.");
			} else {
				LoggerClass.log(Level.INFO, username + " updated their password");
				System.out.println("\nPassword updated successfully.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Ask user security question and check their answer
	public boolean securityCheck(String username) throws InterruptedException {
		boolean passed = false;
		try {
			// Check if the username exists in the database
			String checkUser = "SELECT challenge_question, challenge_answer FROM employees WHERE username=?";
			PreparedStatement stmt = conn.prepareStatement(checkUser);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			// If the user exists, prompt them to answer their security question
			if (rs.next()) {
				String challengeQuestion = rs.getString("challenge_question");
				String challengeAnswer = rs.getString("challenge_answer");

				System.out.println("\nSecurity question: " + challengeQuestion);
				System.out.println("Answer: ");
				String userAnswer = dbScanner.nextLine();

				if (userAnswer.equals(challengeAnswer)) {
					System.out.println("\nSecurity check passed.");
					passed = true;
				} else {
					LoggerClass.log(Level.INFO, username + " failed their security check");
					System.out.println("\nIncorrect answer. Security check failed.");

				}
			} else {
				System.out.println("\nUser not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return passed;
	}

	// Update security question/answer columns from an employee record
	public void updateSecurity(String username) throws SQLException, InterruptedException {
		// Prompt the user to select a challenge question
		System.out.println("\nChoose a challenge question:");
		String[] challengeQuestions = { "What is your mother's maiden name?", "What is your favorite color?",
				"What city were you born in?", "What was your first pet's name?" };
		for (int i = 0; i < challengeQuestions.length; i++) {
			System.out.println((i + 1) + ". " + challengeQuestions[i]);
		}
		boolean validInput = false;
		int questionIndex = 0;
		while (!validInput) {
			System.out.println("\nEnter the number of the question you want to use: ");
			if (dbScanner.hasNextInt()) {
				questionIndex = dbScanner.nextInt();
				validInput = true;
				dbScanner.nextLine();
			} else {
				dbScanner.nextLine(); // Clear scanner buffer
				System.out.println("Invalid input! Please enter an integer.");
			}
		}
		System.out.println("Enter a challenge answer: ");
		String challengeAnswer = dbScanner.nextLine();

		// submit changes?
		System.out.println("\n1. Submit Changes\n2. Discard Changes");
		subMenuChoice = dbScanner.nextLine();
		switch (subMenuChoice){
		case "1":
			Main.process();
			// Update the user's password and challenge information in the database
			String updateQuery = "UPDATE employees SET challenge_question='" + challengeQuestions[questionIndex - 1] + "', challenge_answer='" + challengeAnswer + "' WHERE username='" + username + "'";
			stmt.executeUpdate(updateQuery);
			System.out.println("\nSecurity Settings Updated\n");
			LoggerClass.log(Level.INFO, username + " updated their security question and answer");
			break;
		default:
			System.out.println("\nChanges Discarded");
			break;
		}
	}

	// Close resources and disconnect from database
	public static void closeResources() throws SQLException {
		dbScanner.close();

		// disconnect from database
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// Inserts record into contacts table
	public static int newCustomer(String first, String last, String phone, String email, String address, String username) throws SQLException {
	    int customerID = 0; // Initialize customerID to 0
	    try {
	        // Create a prepared statement to insert a new record into the table and include the customerID column
	        String createRecord = "INSERT INTO customer (first, last, phone, email, address) VALUES (?, ?, ?, ?, ?)";
	        PreparedStatement stmt = conn.prepareStatement(createRecord, Statement.RETURN_GENERATED_KEYS);
	        stmt.setString(1, first);
	        stmt.setString(2, last);
	        stmt.setString(3, phone);
	        stmt.setString(4,  email);
	        stmt.setString(5, address);
	        stmt.executeUpdate();
	        
	        // Retrieve the customerID value of the newly inserted record
	        ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()) {
	            customerID = rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        System.out.println("Error inserting record: " + e.getMessage());
	    }
	    LoggerClass.log(Level.INFO,"User " + username + " created a new customer profile for " + first + " " + last + " Customer Num: " + customerID);
	    return customerID; // Return the customerID value
	}
		
	public static boolean createSalesOrder(int customerID, int product_number, int qty, String deliveryday, String username) throws InterruptedException {
	    boolean cancelOperation = false;
	    try {
	        // Search for the customer record based on the given ID
	        String searchCx = "SELECT * FROM customer WHERE customerID=?";
	        PreparedStatement stmt = conn.prepareStatement(searchCx);
	        stmt.setInt(1, customerID);
	        ResultSet result = stmt.executeQuery();

	        // If a customer record is found, prompt the user to enter sales information
	        if (result.next()) {
	            String firstName = result.getString("first");
	            String lastName = result.getString("last");
	            String phone = result.getString("phone");
	            String email = result.getString("email");
	            String address = result.getString("address");

	            // confirm quantity to decrement, then decrement if possible
	            if(decrementInventory(product_number, qty, username)!=0) {
	                // Insert a new sales record into the sales table
	                String createRecord = "INSERT INTO sales (product, qty, customerID, deliveryday) VALUES (?, ?, ?, ?)";
	                PreparedStatement insertStmt = conn.prepareStatement(createRecord, Statement.RETURN_GENERATED_KEYS);
	                insertStmt.setInt(1, product_number);
	                insertStmt.setInt(2, qty);
	                insertStmt.setInt(3, customerID);
	                insertStmt.setString(4, deliveryday);
	                insertStmt.executeUpdate();

	                // Retrieve the order number
	                ResultSet keys = insertStmt.getGeneratedKeys();
	                int orderNumber = keys.next() ? keys.getInt(1) : 0;

	                // Get the product name
	                String productName = "";
	                String getProduct = "SELECT product_name FROM inventory_list WHERE product_number = ?";
	                PreparedStatement getProductStmt = conn.prepareStatement(getProduct);
	                getProductStmt.setInt(1, product_number);
	                ResultSet productResult = getProductStmt.executeQuery();
	                if (productResult.next()) {
	                    productName = productResult.getString("product_name");
	                }

	                // Generate the receipt with the customer information and sales details
	                String receipt = "========== RECEIPT ==========\n"
	                        + "Order Number: " + orderNumber + "\n"
	                        + "Customer: " + firstName + " " + lastName + "\n"
	                        + "Phone: " + phone + "\n"
	                        + "Email: " + email + "\n"
	                        + "Address: " + address + "\n"
	                        + "============================\n"
	                        + "Product: " + product_number + " " + productName + "\n"
	                        + "Quantity: " + qty + "\n"
	                        + "Delivery Day: " + deliveryday + "\n"
	                        + "============================\n";
	                Main.process();
	                LoggerClass.log(Level.INFO, "User " + username + " created a sales order for customer number " + customerID);
	                System.out.println(receipt);
	            } else {
	                cancelOperation = true;
	            }
	        } else {
		        	System.out.println("\nError: No customer found with ID " + customerID);
		        }
		    } catch (SQLException e) {
		    	System.out.println("Error: " + e.getMessage());
		    }
		    return cancelOperation;
		}

		    
		public static int searchCustomer(String search) {
		    int customerID = 0;
		    try {
		        // Create a statement to select all records from the table that match the search string
		        String sql = "SELECT customerID, first, last, phone, email FROM customer WHERE first LIKE ? OR last LIKE ? OR phone LIKE ? OR email LIKE ?";
		        PreparedStatement pstmt = conn.prepareStatement(sql);
		        String searchString = "%" + search + "%";
		        pstmt.setString(1, searchString);
		        pstmt.setString(2, searchString);
		        pstmt.setString(3, searchString);
		        pstmt.setString(4, searchString);

		        // Execute the query and iterate through the result set
		        ResultSet result = pstmt.executeQuery();
		        ArrayList<Integer> customerIDs = new ArrayList<Integer>();
		        int count = 0;
		        while (result.next()) {
		            count++;
		            int custID = result.getInt("customerID");
		            String first = result.getString("first");
		            String last = result.getString("last");
		            String phone = result.getString("phone");
		            String email = result.getString("email");
		            System.out.printf("\n%d. %s %s\nCustomer #: %d | Phone: %s | Email: %s", count, first, last, custID, phone, email);
		            customerIDs.add(custID);
		        }
		        if (count == 0) {
		            System.out.println("No matching customers found.");
		        } else {
		            System.out.print("\nEnter the number of the customer you want to select (1-" + count + "): ");
		            int selection = -1;
		            while (selection == -1) {
		                try {
		                    selection = dbScanner.nextInt();
		                    if (selection < 1 || selection > count) {
		                        System.out.printf("Invalid selection. Please enter a number between 1 and %d: ", count);
		                        selection = -1;
		                    }
		                } catch (InputMismatchException e) {
		                    System.out.printf("Invalid input. Please enter a number between 1 and %d: ", count);
		                    dbScanner.nextLine(); // Consume the invalid input
		                }
		            }
		            customerID = customerIDs.get(selection-1);
		        }
		    } catch (SQLException e) {
		        System.out.println("Error searching for customer: " + e.getMessage());
		    }
		    return customerID;
		}
		
		public static int searchProduct(String search) {
		    int productNumber = 0;
		    try {
		        // Create a statement to select all records from the table that match the search string
		        String sql = "SELECT * FROM inventory_list WHERE product_number LIKE ? OR product_name LIKE ? OR product_type LIKE ?";
		        PreparedStatement stmt = conn.prepareStatement(sql);
		        String searchString = "%" + search + "%";
		        stmt.setString(1, searchString);
		        stmt.setString(2, searchString);
		        stmt.setString(3, searchString);

		        // Execute the query and store the product numbers in an array
		        ResultSet result = stmt.executeQuery();
		        List<Integer> productNumbers = new ArrayList<>();
		        while (result.next()) {
		            productNumber = result.getInt("product_number");
		            productNumbers.add(productNumber);
		            String productName = result.getString("product_name");
		            String productType = result.getString("product_type");
		            int qty = result.getInt("quantity");
		            System.out.println(productNumbers.size() + ". " + productName + " | #" + productNumber + " | Type: " + productType + " | ATP: " + qty);
		        }
		        if (productNumbers.isEmpty()) {
		            System.out.println("No matching products found.");
		        } else {
		            // Prompt the user to select a product and return the corresponding product number
		            System.out.print("Enter the number of the product you want to select (1-" + productNumbers.size() + "): ");
		            int selection = -1;
		            while (selection == -1) {
		                try {
		                    selection = dbScanner.nextInt();
		                    if (selection < 1 || selection > productNumbers.size()) {
		                        System.out.print("Invalid selection. Please enter a number between 1 and " + productNumbers.size() + ": ");
		                        selection = -1;
		                    }
		                } catch (InputMismatchException e) {
		                    System.out.print("Invalid input. Please enter a number between 1 and " + productNumbers.size() + ": ");
		                    dbScanner.nextLine(); // Consume the invalid input
		                }
		            }
		            productNumber = productNumbers.get(selection - 1);
		        }

		    } catch (SQLException e) {
		        System.out.println("Error reading table: " + e.getMessage());
		    }
		    return productNumber;
		}
		public static void searchSO(String search) {
		    try {
		        // Create a statement to select all records from the table that match the search string
		        String sql = "SELECT ordernum, customerID, deliveryday FROM sales WHERE ordernum LIKE ? OR customerID LIKE ? OR deliveryday LIKE ?";
		        PreparedStatement pstmt = conn.prepareStatement(sql);
		        String searchString = "%" + search + "%";
		        pstmt.setString(1, searchString);
		        pstmt.setString(2, searchString);
		        pstmt.setString(3, searchString);

		        // Execute the query and iterate through the result set
		        ResultSet result = pstmt.executeQuery();
		        ArrayList<Integer> orderNums = new ArrayList<Integer>();
		        int count = 0;
		        while (result.next()) {
		            count++;
		            int ordernum = result.getInt("ordernum");
		            int customerID = result.getInt("customerID");
		            String deliveryday = result.getString("deliveryday");
		            System.out.printf("\n%d. Order #: %d | Customer #: %d | Delivery Day: %s", count, ordernum, customerID, deliveryday);
		            orderNums.add(ordernum);
		        }
		        if (count == 0) {
		            System.out.println("No matching orders found.");
		        }
		    } catch (SQLException e) {
		        System.out.println("Error searching for order: " + e.getMessage());
		    }
		}
		
		public static void deliveryReport() {
		    try {
		        System.out.println("\nPlease choose a report to generate:\n");
		        System.out.println("1. Today's deliveries");
		        System.out.println("2. Tomorrow's deliveries");
		        System.out.println("3. Large upcoming deliveries");
		        int reportChoice = dbScanner.nextInt();
		        dbScanner.nextLine(); // consume newline character

		        String sql = "";
		        LocalDate startDate = LocalDate.now();
		        LocalDate endDate = LocalDate.now();
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		        switch (reportChoice) {
		            case 1:
		                sql = "SELECT * FROM sales WHERE deliveryday=?";
		                break;
		            case 2:
		                startDate = startDate.plusDays(1);
		                endDate = endDate.plusDays(1);
		                sql = "SELECT * FROM sales WHERE deliveryday=?";
		                break;
		            case 3:
		                startDate = LocalDate.now();
		                endDate = startDate.plusDays(29);
		                sql = "SELECT * FROM sales WHERE deliveryday BETWEEN ? AND ? AND qty > 10";
		                break;

		            default:
		                System.out.println("Invalid report choice.");
		                return;
		        }

		        PreparedStatement pstmt = conn.prepareStatement(sql);
		        if (reportChoice == 1 || reportChoice == 2) {
		            pstmt.setString(1, startDate.format(formatter));
		        } else {
		            pstmt.setString(1, startDate.format(formatter));
		            pstmt.setString(2, endDate.format(formatter));
		        }

		        ResultSet result = pstmt.executeQuery();
		        boolean hasResults = false; // track whether any results were returned
		        while (result.next()) {
		            hasResults = true;
		            int orderNum = result.getInt("ordernum");
		            int customerID = result.getInt("customerID");
		            LocalDate deliveryDay = LocalDate.parse(result.getString("deliveryday"), formatter);
		            int qty = result.getInt("qty");
		            int productNum = result.getInt("product");

		            System.out.printf("Order Number: %d | Customer ID: %d | Product Number: %d | Delivery Day: %s | Quantity: %d\n",
		                    orderNum, customerID, productNum, deliveryDay.format(formatter), qty);
		        }
		        if (!hasResults) {
		            System.out.println("Nothing to Report");
		        }

		    } catch (SQLException e) {
		        System.out.println("Error generating report: " + e.getMessage());
		    }
		}



		
		public static void deleteSO(String username) throws InterruptedException {
		    try {
		        // Prompt the user for the sales order number to delete
		        int salesOrderNumber = -1;
		        while (salesOrderNumber == -1) {
		            System.out.print("Enter the sales order number to delete (0 to exit): ");
		            try {
		                salesOrderNumber = dbScanner.nextInt();
		                if (salesOrderNumber == 0) {
		                    System.out.println("Exiting...");
		                    return;
		                } else if (salesOrderNumber < 0) {
		                    System.out.println("Sales order number cannot be negative. Please enter a valid number.");
		                    salesOrderNumber = -1;
		                }
		            } catch (InputMismatchException e) {
		                System.out.println("Invalid input. Please enter a valid number.");
		                dbScanner.nextLine(); // Consume the invalid input
		            }
		        }

		        String sql = "DELETE FROM sales WHERE ordernum=?";
		        PreparedStatement pstmt = conn.prepareStatement(sql);
		        pstmt.setInt(1, salesOrderNumber);
		        int rowsDeleted = pstmt.executeUpdate();
		        if (rowsDeleted == 0) {
		            System.out.println("No sales order with that number exists.");
		        } else {
		        	Main.process();
		            System.out.printf("Sales order %d deleted successfully.\n", salesOrderNumber);
		            LoggerClass.log(Level.INFO, username + "deleted sales order: " + salesOrderNumber);
		        }
		    } catch (SQLException e) {
		        System.out.println("Error deleting sales order: " + e.getMessage());
		    }
		}



		    
}
