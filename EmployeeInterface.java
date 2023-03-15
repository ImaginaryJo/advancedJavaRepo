import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeInterface {
	// declare variables
	private String usersName;
	private static String username;
	private DatabaseReader databaseReader;
	static String[] recordInput = new String [5];
	private static Scanner employeeScanner = new Scanner(System.in);

	// constructor
	public EmployeeInterface(String userName, String username) {
		this.usersName = userName;
		EmployeeInterface.username = username;
	}

	// Employee UI
	public void run() throws SQLException, InterruptedException {

		// Declare Variables
		String input;
		boolean mainloop;

		System.out.println("\n\nEmployee interface\nWelcome, " + usersName);

		// Main loop
		do {
			mainloop = true;

			// Display menu choices and handle user input
			System.out.println("\nWhat would you like to do?");
			System.out.println("1. Create Order");
			System.out.println("2. Show Store Inventory");
			System.out.println("3. Search Sales Orders");
			System.out.println("4. Security Settings");
			System.out.println("5. Log out\n");
			input = employeeScanner.nextLine();
			
			switch (input) {

			// Create a sales order
			case "1":
				boolean salesOrderLoop = true;
				while(salesOrderLoop) {
					int customerID = 0;
					int productID = 0;
					int quantity = 0;
					String deliveryDay = "";
					
					boolean searchOrCreateCxLoop;
					do {
						searchOrCreateCxLoop = false;
						//create customer or search customer (save customer id)
						System.out.println("\n1. Search Customer Profile\n2. Create new Customer\n3. Cancel");
						String menuChoice = employeeScanner.nextLine();
						switch (menuChoice) {
						case "1":
							System.out.println("Search Name/Customer Number/Phone/Email: ");
							String search = employeeScanner.nextLine();
							customerID = DatabaseReader.searchCustomer(search);
							System.out.println("\n1. Continue\n2. Search Again\n3. Cancel");
							menuChoice = employeeScanner.nextLine();
							switch (menuChoice) {
							case "1":
								// Continue with the process and exit searchOrCreateCxLoop
								break;
							case "2":
								// Continue with searchOrCreateCxLoop
								searchOrCreateCxLoop = true;
								break;
							case "3":
								salesOrderLoop = false;
								// Cancel
								break;
							default:
								System.out.println("Invalid choice!");
								// Continue with the inner loop
								break;
							}
							break;
						case "2":
							customerID = InventoryControl_1.createprofile(username);
							break;
						case "3":
							break; // Exit the method if "Cancel" is chosen
						default:
							System.out.println("Invalid choice!");
							searchOrCreateCxLoop = true;
							// Continue with the outer loop
							break;
						}
					} while (searchOrCreateCxLoop);
					
					// If user didn't choose to exit the process
					if (salesOrderLoop) {
						
						boolean addProductLoop;
						do {
							addProductLoop = false;
							//pick inventory
							System.out.println("Search Product Name/Product Number/Product Type: ");
							String search = employeeScanner.nextLine();
							productID = DatabaseReader.searchProduct(search);
							System.out.println("\n1. Continue\n2. Search Again\n3. Cancel");
							String menuChoice = employeeScanner.nextLine();
							switch (menuChoice) {
							case "1":
								// Continue with the process
								break;
							case "2":
								addProductLoop = true;
								// Continue with the inner loop
								break;
							case "3":
								salesOrderLoop = false;
								break;
							default:
								System.out.println("Invalid choice!");
								addProductLoop = true;
								// Continue with the loop
								break;
							}
						} while (addProductLoop);
						
						// If user didn't choose to exit in last loop
						if(salesOrderLoop) {
						
							// Prompt the user to enter a quantity
							System.out.print("Enter a quantity: ");

							// Validate the input
							while (true) {
								try {
									quantity = Integer.parseInt(employeeScanner.nextLine());
									break;
								} catch (NumberFormatException e) {
									System.out.print("Invalid input. Enter a valid quantity: ");
								}
							}

							// Prompt the user to enter a delivery day
							System.out.print("Enter a delivery day (YYYY-MM-DD): ");

							// Validate the input
							while (true) {
								deliveryDay = employeeScanner.nextLine();
								if (deliveryDay.matches("\\d{4}-\\d{2}-\\d{2}")) {
									break;
								} else {
									System.out.print("Invalid input. Enter a valid delivery day (YYYY-MM-DD): ");
								}
						}  
							System.out.println("\n1. Submit Changes\n2. Start Over\n3. Exit");
							String menuchoice = employeeScanner.nextLine();
							if (menuchoice.equals("1")) {
								if (DatabaseReader.createSalesOrder(customerID, productID, quantity, deliveryDay, username)) {
									System.out.println("\nOperation Cancelled");
									salesOrderLoop = true;
								} else {
								System.out.println("\nOrder Created Successfully\n");
								salesOrderLoop = false;
								}
							} else if (menuchoice.equals("2")) {
								salesOrderLoop = true;
							}
						}
					} 
				}
				break;
			
			// Show inventory
			case "2":
				DatabaseReader.showEmployeeInventory();
				break;
			// Search Sales Order
			case "3":
				boolean searchSO;
				do {
					searchSO = false;
					//pick inventory
					System.out.println("Search Order number/CustomerID/Delivery Date: ");
					String search = employeeScanner.nextLine();
					DatabaseReader.searchSO(search);
					System.out.println("\n1. Search Again\n2. Cancel");
					String menuChoice = employeeScanner.nextLine();
					switch (menuChoice) {
					case "1":
						searchSO = true;
						// Continue with the inner loop
						break;
					default:
						// Exit search loop
						break;
					}
				} while (searchSO);
				break;
				
			// Security Settings
			case "4":
				System.out.println("\nSelect the setting you want to update:");
				System.out.println("1. Password");
				System.out.println("2. Security Questions");
				String setting = employeeScanner.nextLine();
				switch (setting) {
				case "1":
					System.out.println("\nEnter the new password:");
					String newPassword = employeeScanner.nextLine();
					System.out.println("\n1. Submit Changes\n2. Discard Changes");
					String subMenuChoice = employeeScanner.nextLine();
					switch (subMenuChoice) {
					case "1":
						databaseReader.updatePassword(username, newPassword);
						break;
					default:
						System.out.println("\nChanges Discarded");
						break;
					}
					break;
				case "2":
					databaseReader.updateSecurity(username);
					break;
				default:
					System.out.println("\nInvalid Input. Try Again.");
					break;
				}
				break;

			// Log out
			case "5":
				System.out.println("\nLogged out");
				mainloop = false;
				break;
			// Invalid Input
			default:
				System.out.println("\nInvalid input. Please try again.");
				break;
			}
		} while (mainloop);
	}


	// Close resources
	public static void closeResources() {
		employeeScanner.close();
	}
}
