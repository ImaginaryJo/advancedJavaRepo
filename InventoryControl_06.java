//imports
import java.util.Scanner;
import java.util.logging.Level;

public class InventoryControl_06 {

    //construct logger class object for use
    static LoggerClass loggerClass = new LoggerClass();
    //construct database reader class object for use
    static DatabaseReader databaseReader = new DatabaseReader();
    
    //main method
    public static void main(String[] args) {

        //login information: username, password, user's name
        String[][] managerLogins = {{"manager", "managerpass", "John Stamos"},
                                    {"rondon2000", "rondonrules", "Ronald McDonald"}};

        String[][] employeeLogins = {{"employee", "employeepass", "Dwayne Johnson"},
                                     {"numberOneEmployee","bestPassword","Usain Bolt"}};

        String[][] hrLogins = {{"HR", "hrpass", "Barack Obama"},
                               {"HR2", "hr2pass", "Joe Biden"}};

        //introduce program
        System.out.println("Inventory Manager v0.6");

        //call login method
        login(managerLogins, employeeLogins, hrLogins);
    }

    //LOGIN
    public static void login(String[][] managerLogins, String[][] employeeLogins, String[][] hrLogins) {
        Scanner scanner = new Scanner(System.in);
        boolean validLogin = false;
        boolean inManagement = false;
        boolean inHR = false;
        String userName = "";

        do {
            //empty username/password fields in case of repeat login attempt(s)
            String username = "";
            String password = "";

            //input username and password
            System.out.println("\n\nUsername: ");
            username = scanner.next();
            System.out.println("Password: ");
            password = scanner.next();

            //check if login is valid and if manager
            for (int i = 0; i < managerLogins.length; i++) {
                if (username.equals(managerLogins[i][0]) && password.equals(managerLogins[i][1])) {
                    validLogin = true;
                    inManagement = true;
                    inHR = false;
                    userName = managerLogins[i][2];
                    break;
                }
            }
            //check if login is valid and if employee
            for (int i = 0; i < employeeLogins.length; i++) {
                if (username.equals(employeeLogins[i][0]) && password.equals(employeeLogins[i][1])) {
                    validLogin = true;
                    inManagement = false;
                    inHR = false;
                    userName = employeeLogins[i][2];
                    break;
                }
            }
            //check if login is valid and if HR
            for (int i = 0; i < hrLogins.length; i++) {
                if (username.equals(hrLogins[i][0]) && password.equals(hrLogins[i][1])) {
                    validLogin = true;
                    inManagement = false;
                    inHR = true;
                    userName = hrLogins[i][2];
                    break;
                }
            }

            //if login is invalid
            if (!validLogin) {
                System.out.println("\nInvalid Login");
                LoggerClass.log(Level.INFO, "Failed Login Attempt: " + username);
            }
            else {
                //log successful login
                LoggerClass.log(Level.INFO, "User " + userName + " logged in successfully.");
            }
        //repeat login screen if invalid login
        } while (!validLogin);

        //launch interface
        if (inManagement) {
            managerInterface(userName);
        } else if (inHR) {
        	hrInterface(userName);
        } else {
            employeeInterface(userName);
        }

        //close scanner
        scanner.close();

    //end of login interface
    }	
	
//MANAGER Interface
	public static void managerInterface(String userName) {
		//declare variables
		Scanner scanner = new Scanner (System.in);
		String input;
		boolean repeat = false;
		
		System.out.println("Manager interface\nWelcome, " + userName);
		
		//manager class functionality (will continue to add features)
		do {
			repeat = false;
			//access log?
			System.out.println("Would you like to access the logs? (y or n):");
			input = scanner.nextLine().toUpperCase();
			
			if (input.equals("Y")) {
				LoggerClass.readLogFile();
			} else if (input.equals("N")) {
				System.out.println("Okay, that's all the functionality I have so far, please come back for more later");
				break;
			} else {
				System.out.println("Invalid Input. Try Again.");
				repeat = true;
			}
		} while (repeat);
		
		//close scanner
		scanner.close();
	}	
//EMPLOYEE Interface
	public static void employeeInterface(String userName) {
		System.out.println("Employee interface\nWelcome, " + userName);
	}
//HR Interface
	public static void hrInterface(String userName){
		System.out.println("HR Interface\nWelcome, " + userName);
	}
}	