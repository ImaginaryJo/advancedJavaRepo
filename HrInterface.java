import java.sql.SQLException;
import java.util.Scanner;

public class HrInterface {
	private String usersName;
	private String hrUsername;
	private DatabaseReader databaseReader;
	private static Scanner hrScanner = new Scanner(System.in);
	static final String[] employeeTypeArr = { "HR", "Manager", "Employee" };
	static String employeeType;
	static String username;
	static String menuChoice;
	static String subMenuChoice;
	static String empUsername;

	public HrInterface(String usersName, String hrUsername) {
		this.usersName = usersName;
		this.databaseReader = new DatabaseReader();
		this.hrUsername = hrUsername;
	}

	// HR UI
	public void run() throws InterruptedException, SQLException {

		// Declare Variables
		boolean loop;

		// Welcome Message
		System.out.println("\n\nHR interface\nWelcome, " + usersName);

		// Main Loop
		do {
			loop = true;

			// Display menu choices and handle user input
			Thread.sleep(300);
			System.out.println("\nPlease select an option:");
			Thread.sleep(500);
			System.out.println("1. Create Employee");
			Thread.sleep(500);
			System.out.println("2. Delete Employee");
			Thread.sleep(500);
			System.out.println("3. Access Logs");
			Thread.sleep(500);
			System.out.println("4. Show All Employees");
			Thread.sleep(500);
			System.out.println("5. Modify Employee Accounts");
			Thread.sleep(500);
			System.out.println("6. Log Out");
			menuChoice = hrScanner.nextLine();

			boolean innerLoop;
			boolean outerLoop;

			Thread.sleep(1000);
			switch (menuChoice) {
			case "1":
				do {
					outerLoop = false;

					// Ask for first and last name
					System.out.println("\nEnter Employee First Name: ");
					String firstName = hrScanner.nextLine();
					System.out.println("Enter Employee Last Name: ");
					String lastName = hrScanner.nextLine();

					// Automatically set password
					String password = "default";

					// Ask whether custom username or default username
					Main.process();
					System.out.println("\n1. Accept Default Username?: " + firstName.toLowerCase().charAt(0)
							+ lastName.toLowerCase());
					System.out.println("2. Custom Username");
					subMenuChoice = hrScanner.nextLine();
					switch (subMenuChoice) {

					// Accept Default username
					case "1":
						username = firstName.toLowerCase().charAt(0) + lastName.toLowerCase();
						break;

					// Create custom username
					case "2":
						// Prompt user for custom username
						System.out.println("\nEnter Custom Username");
						username = hrScanner.nextLine();
						break;

					default:
						System.out.println("\nInvalid Input");
						break;
					}

					do {
						innerLoop = false;
						// Ask for employee type
						System.out.println("\nEnter Employee Type:");
						Thread.sleep(300);
						System.out.println("1. HR");
						Thread.sleep(500);
						System.out.println("2. Manager");
						Thread.sleep(500);
						System.out.println("3. Employee");
						subMenuChoice = hrScanner.nextLine();

						switch (subMenuChoice) {
						case "1":
							employeeType = employeeTypeArr[0];
							break;
						case "2":
							employeeType = employeeTypeArr[1];
							break;
						case "3":
							employeeType = employeeTypeArr[2];
							break;
						default:
							System.out.println("\nInvalid Input. Try Again.");
							innerLoop = true;
							break;
						}
					} while (innerLoop);

					// Display employee details for review
					System.out.println("\nReview Changes:\nName: " + firstName + " " + lastName + "\nEmployee Type: "
							+ employeeType + "\nUsername: " + username + "\nPassword: " + password);

					do {
						innerLoop = false;
						Thread.sleep(300);
						System.out.println("1. Submit");
						Thread.sleep(500);
						System.out.println("2. Start Over");
						Thread.sleep(500);
						System.out.println("3. Return to Menu");
						subMenuChoice = hrScanner.nextLine();

						switch (subMenuChoice) {
						case "1":
							databaseReader.createEmployee(firstName, lastName, employeeType, username, password, "", "",
									hrUsername);
							break;
						case "2":
							outerLoop = true;
							break;
						case "3":
							break;
						default:
							System.out.println("\nInvalid Input. Try Again.");
							innerLoop = true;
							break;
						}
					} while (innerLoop);
				} while (outerLoop);
				break;

			// Delete employee
			case "2":

				System.out.println("\nEnter the username for the user you would like to delete: ");
				String userToDelete = hrScanner.nextLine();

				innerLoop = true;
				while (innerLoop) {
					innerLoop = false;
					Thread.sleep(300);
					System.out.println("\nAre you sure you want to delete " + userToDelete + "?");
					Thread.sleep(500);
					System.out.println("\n1. Submit");
					Thread.sleep(500);
					System.out.println("\n2. Start Over");
					Thread.sleep(500);
					System.out.println("\n3. Return to Menu");
					subMenuChoice = hrScanner.nextLine();
					switch (subMenuChoice) {
					case "1":
						databaseReader.deleteEmployee(userToDelete, hrUsername);
						break;
					case "2":
						outerLoop = true;
						break;
					case "3":
						run();
						break;
					default:
						System.out.println("\nInvalid Input. Try Again");
						innerLoop = true;
						break;
					}
				}
				break;

			// Show log
			case "3":
				Main.process();
				LoggerClass.readLogFile();
				break;

			// Show all employees
			case "4":
				databaseReader.showAllEmployees();
				break;

			// Modify employee accounts
			case "5":
				boolean userExists;
				do {
					Thread.sleep(300);
					System.out.println("\nEnter the username of the employee whose account you want to modify:");
					empUsername = hrScanner.nextLine();
					userExists = databaseReader.checkAccountIsSetup(empUsername);
					if (!userExists) {
						do {
							innerLoop = false;
							System.out.println("\nInvalid Username\n1. Try Again\n2. Cancel");
							String subMenuChoice = hrScanner.nextLine();
							switch (subMenuChoice) {
							case "1":
								innerLoop = false;
								break;
							case "2":
								return; // or any other appropriate action
							default:
								System.out.println("\nInvalid Input. Try Again.");
								break;
							}
						} while (innerLoop);
					}
				} while (!userExists);

				do {
					innerLoop = false;
					Thread.sleep(300);
					System.out.println("\nSelect the setting you want to update:");
					Thread.sleep(500);
					System.out.println("1. Name");
					Thread.sleep(500);
					System.out.println("2. Username");
					Thread.sleep(500);
					System.out.println("3. Password");
					Thread.sleep(500);
					System.out.println("4. Security Questions");
					subMenuChoice = hrScanner.nextLine();

					switch (subMenuChoice) {
					case "1":
						System.out.println("\nEnter the First name:");
						String fName = hrScanner.nextLine();
						System.out.println("Enter the Last name:");
						String lName = hrScanner.nextLine();
						System.out.println("\nSubmit Changes? (y or n)");
						subMenuChoice = hrScanner.nextLine();
						if (subMenuChoice.equalsIgnoreCase("y"))
							databaseReader.updateName(empUsername, fName, lName);
						else {
							System.out.println("\nChanges Discarded");
						}
						break;
					case "2":
						System.out.println("\nEnter the new username:");
						String newUsername = hrScanner.nextLine();
						System.out.println("\nSubmit Changes? (y or n)");
						subMenuChoice = hrScanner.nextLine();
						if (subMenuChoice.equalsIgnoreCase("y"))
							databaseReader.updateUsername(empUsername, newUsername);
						else {
							System.out.println("\nChanges Discarded");
						}
						break;
					case "3":
						System.out.println("\nEnter the new password:");
						String newPassword = hrScanner.nextLine();
						System.out.println("\nSubmit Changes? (y or n)");
						subMenuChoice = hrScanner.nextLine();
						if (subMenuChoice.equalsIgnoreCase("y"))
							databaseReader.updatePassword(empUsername, newPassword);
						else {
							System.out.println("\nChanges Discarded");
						}
						break;
					case "4":
						databaseReader.updateSecurity(empUsername);
						break;
					default:
						System.out.println("\nInvalid Input. Try Again.");
						innerLoop = true;
						break;
					}
				} while (innerLoop);
				break;

			// Log out
			case "6":
				System.out.println("\nLogged Out");
				loop = false;
				break;

			// Invalid input
			default:
				System.out.println("\nInvalid Input. Try Again.");
				break;
			}
		} while (loop);
	}

	// Close Resources
	public static void closeResources() {
		hrScanner.close();
	}
}
