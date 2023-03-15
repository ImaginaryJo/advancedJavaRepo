import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
	// Declare class variables
	private static final Scanner scanner = new Scanner(System.in);
	private static final InventoryControl_1 session = new InventoryControl_1();
	static String menuChoice;

	public static void main(String[] args) throws SQLException, InterruptedException, NoSuchElementException {
		// Call the displayMenu() method to start the program
		menu();
	}

	// Method to display the main menu and handle user input
	static void menu() throws InterruptedException, SQLException {

		// Declare Variables
		boolean loop;
		// Main Loop
		do {
			loop = true;
			// Display menu options and get user input
			System.out.println("Main Menu\n");
			System.out.println("1. Inventory Control Program v1.0");
			System.out.println("2. Report Bugs/Make Suggestions");
			System.out.println("3. Exit program\n");
			menuChoice = scanner.nextLine();
			switch (menuChoice) {

			// If the user selects option 1, run the program
			case "1":
				session.run();
				break;
			// Report bugs/ make suggestions for improvements
			case "2":
				DatabaseReader.helpAManOut();
				break;

			// If the user selects option 2, stop loop, close resources and exit program
			case "3":
				System.out.println("\nClosing...");
				Thread.sleep(2000);
				System.out.println("\nProgram closed");
				closeResources();
				loop = false;
				break;
			default:
				// If the user enters an invalid option, display an error message and repeat the
				// loop
				System.out.println("\nInvalid input. Please try again.");
				break;
			}
			System.out.println("\n\n"); // UI improvement
		} while (loop);
	}

	// UI improvement that is used throughout all the interfaces
	public static void process() throws InterruptedException {
		for (int i = 0; i < 6; i++) {
			System.out.print(".");
			if (i == 5) {
				System.out.println("\n\n");
			}
		}
	}

	// Close Resources
	private static void closeResources() throws SQLException {
		InventoryControl_1.closeResources();
		ManagerInterface.closeResources();
		HrInterface.closeResources();
		EmployeeInterface.closeResources();
		DatabaseReader.closeResources();

	}
}
