import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

public class InventoryControl_1 {

	// Instantiate the LoggerClass and DatabaseReader objects
	private static DatabaseReader databaseReader = new DatabaseReader();
	private static Scanner inventoryControlScanner = new Scanner(System.in);

	// The main method for the program
	public void run() throws SQLException, InterruptedException {

		// Declare variables
		boolean loop;

		// Display program name
		System.out.println("\n\nInventory Manager v1.0");

		// Main Loop
		do {
			loop = false;
			// Authenticate user login and retrieve employee info from the database
			String[] employeeInfo = databaseReader.authenticateUser();
			String username;
			String usersName;

			// If the user is authenticated successfully
			if (employeeInfo != null) {
				username = employeeInfo[3];
				usersName = employeeInfo[1] + " " + employeeInfo[2];
				LoggerClass.log(Level.INFO, "User " + username + " logged in successfully."); // Log successful login

				// Checks that user doesn't have default password/asks them to change it and
				// their challenge question/answer
				databaseReader.checkAccountIsSetup(username);

				// Launch the appropriate interface for the employee type
				switch (employeeInfo[0]) {
				case "Manager":
					ManagerInterface manager = new ManagerInterface(usersName, username);
					manager.run();
					break;
				case "HR":
					HrInterface hr = new HrInterface(usersName, username);
					hr.run();
					break;
				default:
					EmployeeInterface employee = new EmployeeInterface(usersName, username);
					employee.run();
					break;
				}

				// If the user's login is invalid
			} else {
				System.out.println("\nInvalid Login");
				LoggerClass.log(Level.INFO, "Failed Login Attempt");

				// Try again? Reset Password?
				System.out.println("\n\n1. Try Again\n2. Forgot Password\n3. Return to Main Menu");
				String yOrN = inventoryControlScanner.nextLine();
				switch (yOrN) {

				// Try to login again without resetting password
				case "1":
					loop = true;
					break;
				// Reset Password
				case "2":
					// Enter username
					System.out.println("\n1. Enter Username: ");
					username = inventoryControlScanner.nextLine();

					// Security Question Check
					boolean passedSecurity = databaseReader.securityCheck(username);

					// If username incorrect or didn't pass security check
					if (!passedSecurity) {
						System.out.println("\nThat is not correct");
						break;

						// If user passed security check, prompt for new password
					} else {
						System.out.println("\nEnter New Password: ");
						String newPassword = inventoryControlScanner.nextLine();

						// Prompt user to confirm the change
						System.out.println("\nAre You Sure? (y or n): ");
						yOrN = inventoryControlScanner.nextLine();

						// If user confirms change, update password in database
						if (yOrN.equalsIgnoreCase("Y")) {
							databaseReader.updatePassword(username, newPassword);
						}
					}
				}
			}
		} while (loop);
	}
	public static int createprofile(String username) throws SQLException {
		boolean loop;
		boolean innerloop;
		String[] recordPrompt = {"\nFirst Name: ","Last Name: ","Phone Number: ","Email: ", "Address: "};
		do {
			loop = false;
			String[] recordInput = {"","","","",""};
			do {
				innerloop = false;
				Arrays.fill(recordInput, ""); // empty the array
				
				// Loop that presents menu options, takes input for them, and validates the input at the same time
				for (int i = 0; i < recordPrompt.length; i++) {
					System.out.println(recordPrompt[i]);
					String input = inventoryControlScanner.nextLine();
			        
					// prevent empty input
					if (input.isEmpty()) {
						System.out.println("\nInput cannot be empty. Please try again.");
						innerloop = true;
						break; // break out of the for loop
					}
						
					// Formats and validates phone number
					if (i == 2) {
						String digitsOnly = input.replaceAll("\\D", ""); // remove non-digit characters
						if (digitsOnly.length() == 10) {
							String formattedNumber = digitsOnly.substring(0, 3) + "-" + digitsOnly.substring(3, 6) + "-" + digitsOnly.substring(6);
							input = formattedNumber;
						} else {
							System.out.println("\nInvalid phone number");
							innerloop = true;
							break;
						}
					}
						
					// Checks if email is formatted correctly
					if (i == 3) {
						if (!input.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
							System.out.println("Invalid email");
							innerloop = true;
							break;
						}
					}
						
					// Save input if it passes all checks
					recordInput[i] = input;
					if (i == recordPrompt.length - 1) {
					}
				}
			} while(innerloop); // loop until valid input
			System.out.println("\n\nReview Changes:");
			for (int i = 0; i < recordInput.length; i++) {
				System.out.println(recordPrompt[i] + recordInput[i]);
			}
			if (tryAgain()) {
				System.out.println("Changes Submitted");
				int customerID = DatabaseReader.newCustomer(recordInput[0], recordInput[1], recordInput[2], recordInput[3], recordInput[4], username);
				return customerID;
			} else {
				System.out.println("\nChanges Discarded");
				loop = tryAgain();
			}
			return 0;
		} while (loop); //Loops until user submits or exits
	}
	
	//worker method
	public static boolean tryAgain() {
		boolean loop;
		boolean loopProgram = false;
		String menuChoice;
		do {
			loop = false;
			System.out.println("\n1. Continue\n2. Exit");
			menuChoice = inventoryControlScanner.nextLine();
			switch(menuChoice) {
			// If loop main program
			case "1":
				loopProgram = true;
				break;
			// If exit program
			case "2":
				System.out.println("\n\nExited...");
				break;
			// Invalid Input
			default:
				System.out.println("\nInvalid Input");
				loop = true;
				break;
			}
			return loopProgram;
		} while (loop); // Loops until valid input    	
	}
	// close resources
	public static void closeResources() {
		inventoryControlScanner.close();
	}
}
