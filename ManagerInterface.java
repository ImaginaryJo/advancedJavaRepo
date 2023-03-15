import java.util.NoSuchElementException;
import java.util.Scanner;
import java.sql.SQLException;

public class ManagerInterface {
	private String usersName;
	private String username;
	private DatabaseReader databaseReader;
	private static Scanner managerScanner = new Scanner(System.in);
	static String productType;
	final String[] productTypeArr = { "Electronics", "Furniture", "Decor" };
	static String menuChoice;
	static String subMenuChoice;
	static int minimumThreshold;

	public ManagerInterface(String usersName, String username) {
		this.usersName = usersName;
		this.username = username;
		this.databaseReader = new DatabaseReader();

	}

	// Manager UI
	public void run() throws SQLException, NoSuchElementException, InterruptedException {

		// Declare Variables
		boolean loop;
		boolean innerLoop;

		// Welcome Message
		System.out.println("\n\nManager interface\nWelcome, " + usersName);

		// Main loop
		do {
			loop = true;

			// Display menu choices and handle user input
			System.out.println("\nPlease select an option:");
			System.out.println("1. Create Inventory");
			System.out.println("2. Delete Inventory");
			System.out.println("3. Increment Inventory");
			System.out.println("4. Decrement Inventory");
			System.out.println("5. Create Order");
			System.out.println("6. Search Sales Order");
			System.out.println("7. Delete Order");
			System.out.println("8. Delivery Reports");
			System.out.println("9. Show Manager Inventory");
			System.out.println("10. Order Report");
			System.out.println("11. Access Logs");
			System.out.println("12. Security Settings");
			System.out.println("13. Log Out\n");
			menuChoice = managerScanner.nextLine();

			switch (menuChoice) {

			// Create Inventory
			case "1":
				System.out.println("\nCreate New Product:");
				System.out.println("\nProduct Name: ");
				String productName = managerScanner.nextLine();
				do {
					innerLoop = false;
					System.out.println("Product Type:");
					System.out.println("1. Electronics");
					System.out.println("2. Furniture");
					System.out.println("3. Decor\n");
					subMenuChoice = managerScanner.nextLine();
					switch (subMenuChoice) {
					case "1":
						productType = productTypeArr[0];
						break;
					case "2":
						productType = productTypeArr[1];
						break;
					case "3":
						productType = productTypeArr[2];
						break;
					default:
						System.out.println("\nInvalid Input");
						innerLoop = true;
					}
				} while (innerLoop);
				boolean validInput = false;

				do {
					System.out.println("\nMinimum Threshold: ");
					if (managerScanner.hasNextInt()) {
						minimumThreshold = managerScanner.nextInt();
						validInput = true;
					} else {
						System.out.println("Invalid input. Please enter a valid integer.");
						managerScanner.next(); // consume invalid input
					}
				} while (!validInput);

				managerScanner.nextLine();
				System.out.println("Price: ");
				double productPrice = managerScanner.nextDouble();
				managerScanner.nextLine();
				System.out.println("Quantity: ");
				int quantity = managerScanner.nextInt();
				managerScanner.nextLine();
				String completeProductInfo = "| Name: " + productName + " | Type: " + productType + " | Price: "
						+ productPrice + " | Qty: " + quantity + " | minimum threshold: " + minimumThreshold + " |";
				System.out.println("\nProduct = " + completeProductInfo);
				do {
					innerLoop = false;
					System.out.println("1. Submit");
					System.out.println("2. Cancel");
					subMenuChoice = managerScanner.nextLine();
					switch (subMenuChoice) {
					case "1":
						databaseReader.createInventory(productName, productType, minimumThreshold, productPrice,
								quantity, username);
						break;
					case "2":
						break;
					default:
						innerLoop = true;
						break;
					}
				} while (innerLoop);
				break;

			// Delete Inventory record
			case "2":
				System.out.println("\nRemove Product");
				int productNumber = 0;
				validInput = false;
				while (!validInput) {
					System.out.println("\nProduct Number: ");
					if (managerScanner.hasNextInt()) {
						productNumber = managerScanner.nextInt();
						validInput = true;
					} else {
						System.out.println("\nInvalid input. Please enter a number.");
						managerScanner.nextLine();
					}
					databaseReader.deleteProductFromDatabase(productNumber, username);
				}
				break;
			// Increment Inventory
			case "3":
				productNumber = 0;
				quantity = 0;
				validInput = false;
				System.out.println("\nIncrement Inventory");
				while (!validInput) {
					System.out.println("\nProduct Number: ");
					if (managerScanner.hasNextInt()) {
						productNumber = managerScanner.nextInt();
						validInput = true;
					} else {
						System.out.println("\nInvalid input. Please enter a number.");
						managerScanner.nextLine();
					}
				}
				validInput = false;
				while (!validInput) {
					System.out.println("Quantity: ");
					if (managerScanner.hasNextInt()) {
						quantity = managerScanner.nextInt();
						validInput = true;
					} else {
						System.out.println("\nInvalid input. Please enter a number.");
						managerScanner.nextLine();
					}
				}
				managerScanner.nextLine(); // Consume newline character
				databaseReader.incrementInventory(productNumber, quantity, username);
				break;

			// Decrement Inventory
			case "4":
				productNumber = 0;
				validInput = false;
				while (!validInput) {
					System.out.println("\nProduct Number of Product to Decrement: ");
					if (managerScanner.hasNextInt()) {
						productNumber = managerScanner.nextInt();
						managerScanner.nextLine(); // Consume newline character
						validInput = true;
					} else {
						System.out.println("\nInvalid input. Please enter a number.");
						managerScanner.nextLine(); // Consume invalid input
					}
				}
				DatabaseReader.decrementInventory(productNumber, 0 , username);
				break;

			// Create SO
			case "5":
				boolean salesOrderLoop = true;
				while(salesOrderLoop) {
					int customerID = 0;
					int productID = 0;
					quantity = 0;
					String deliveryDay = "";
					
					boolean searchOrCreateCxLoop;
					do {
						searchOrCreateCxLoop = false;
						//create customer or search customer (save customer id)
						System.out.println("\n1. Search Customer Profile\n2. Create new Customer\n3. Cancel");
						String menuChoice = managerScanner.nextLine();
						switch (menuChoice) {
						case "1":
							System.out.println("Search Name/Customer Number/Phone/Email: ");
							String search = managerScanner.nextLine();
							customerID = DatabaseReader.searchCustomer(search);
							System.out.println("\n1. Continue\n2. Search Again\n3. Cancel");
							menuChoice = managerScanner.nextLine();
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
							String search = managerScanner.nextLine();
							productID = DatabaseReader.searchProduct(search);
							System.out.println("\n1. Continue\n2. Search Again\n3. Cancel");
							String menuChoice = managerScanner.nextLine();
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
									quantity = Integer.parseInt(managerScanner.nextLine());
									break;
								} catch (NumberFormatException e) {
									System.out.print("Invalid input. Enter a valid quantity: ");
								}
							}

							// Prompt the user to enter a delivery day
							System.out.print("Enter a delivery day (YYYY-MM-DD): ");

							// Validate the input
							while (true) {
								deliveryDay = managerScanner.nextLine();
								if (deliveryDay.matches("\\d{4}-\\d{2}-\\d{2}")) {
									break;
								} else {
									System.out.print("Invalid input. Enter a valid delivery day (YYYY-MM-DD): ");
								}
						}  
							System.out.println("\n1. Submit Changes\n2. Start Over\n3. Exit");
							String menuchoice = managerScanner.nextLine();
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
				
			// Search SO
			case "6":
				boolean searchSO;
				do {
					searchSO = false;
					//pick inventory
					System.out.println("Search Order number/CustomerID/Delivery Date: ");
					String search = managerScanner.nextLine();
					DatabaseReader.searchSO(search);
					System.out.println("\n1. Search Again\n2. Cancel");
					String menuChoice = managerScanner.nextLine();
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
				
			// Delete SO
			case "7":
				DatabaseReader.deleteSO(username);
				break;
				
			// Delivery Reports
			case "8":
				DatabaseReader.deliveryReport();
				break;
			// Show Manager Inventory
			case "9":
				databaseReader.showManagerInventory();
				break;
			case "10":
				databaseReader.checkInventoryThresholds(username);
				break;
			// Show Logs
			case "11":
				LoggerClass.readLogFile();
				break;

			// Security Settings
			case "12":
				System.out.println("\nSelect the setting you want to update:");
				System.out.println("\n1. Password");
				System.out.println("2. Security Questions");
				subMenuChoice = managerScanner.nextLine();

				switch (subMenuChoice) {
				case "1":
					System.out.println("\nEnter the new password:");
					String newPassword = managerScanner.nextLine();
					System.out.println("\n1. Submit Changes\n2. Discard Changes");
					subMenuChoice = managerScanner.nextLine();
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

			// Log Out
			case "13":
				System.out.println("\nLogged Out");
				loop = false;
				break;

			// Default
			default:
				System.out.println("\nInvalid Input. Try Again.");
			}
		} while (loop);
	}

	public static void closeResources() {
		managerScanner.close();
	}
}