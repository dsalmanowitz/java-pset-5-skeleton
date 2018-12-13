import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The ATM class is primarily responsible for user interaction.
 * 
 * @author Ryan Wilson
 */

public class ATM {
	
	public final static int INVALID_AMOUNT = 0;
	public final static int EXCEEDS_MAXIMUM = 1;
	public final static int INSUFFICIENT_FUNDS = 2;
	public final static int ACCOUNT_NOT_FOUND = 3;
	public final static int SUCCESS = 4;
	
	private Scanner in;
	private BankAccount account;
	private BankAccount destination;
	private Database db;
		
	/**
	 * Constructs an instance of the ATM class.
	 * 
	 * @param account the account to be linked
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	
	public ATM() throws FileNotFoundException, IOException {
		this.account = null;
		this.destination = null;
		this.db = new Database("accounts-db.txt");
	}

	/////////////////////////////////// INSTANCE METHODS ///////////////////////////////////

	/**
	 * Starts the ATM and handles all user interaction and routing.
	 * 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

	public void run() throws InterruptedException, FileNotFoundException, IOException {
		in = new Scanner(System.in);
		
		boolean active = true;
		boolean validated = false;
		
		System.out.println("Welcome to APCSA Enhanced ATM, the ATM of choice for the UCVTS community.");
		System.out.println("To begin, create a new account or access an existing one.");
		
		while (active) {
			try {
				if (!validated) {
					showMenu();
					int selection = in.nextInt();
					
					switch (selection) {
						case 1: validated = openAccount(); break;
						case 2: validated = login(); break;
						case 3: active = false; break;
					}
				} else {
					showSubmenu();
					int selection = in.nextInt();
					
					switch (selection) {
						case 1: deposit(); break;
						case 2: withdraw(); break;
						case 3: transfer(); break;
						case 4: viewBalance(); break;
						case 5: viewPersonalInfo(); break;
						case 6: updatePersonalInfo(); break;
						case 7: validated = closeAccount(); break;
						case 8: validated = logout(); break;
					}
				}
			} catch (InputMismatchException e) {
				in.nextLine();
			}
		}
		
		ATM.showCountdown("Powering off", "Shutdown complete.");
		in.close();
	}
	
	/**
	 * Creates a new account.
	 * 
	 * @return true for successful account creations, false otherwise
	 * @throws InterruptedException 
	 */
	
	public boolean openAccount() throws InterruptedException {
		System.out.println("\nAlright, so you're opening a new account. Enter your information, or -1 to cancel.\n");
		in.nextLine();
		
		String firstName = null;
		while (!isValidText(firstName)) {
			System.out.print("                First Name : ");
			firstName = in.nextLine();
			if (firstName.equals("-1")) return false;	
		}
		
		String lastName = null;
		while (!isValidText(lastName)) {
			System.out.print("                 Last Name : ");
			lastName = in.nextLine();
			if (lastName.equals("-1")) return false;	
		}
		
		String dob = null;
		while (!isValidDOB(dob)) {
			System.out.print("Date of Birth (MM/DD/YYYY) : ");
			dob = in.nextLine();
			if (dob.equals("-1")) return false;
		}
		
		String phone = null;
		while (!isValidPhone(phone)) {
			System.out.print("Phone Number (digits only) : ");
			phone = in.nextLine();
			if (phone.equals("-1")) return false;
		}
		
		String streetAddress = null;
		while (!isValidText(streetAddress)) {
			System.out.print("            Street Address : ");
			streetAddress = in.nextLine();
			if (streetAddress.equals("-1")) return false;	
		}
		
		String city = null;
		while (!isValidText(city)) {
			System.out.print("                      City : ");
			city = in.nextLine();
			if (city.equals("-1")) return false;	
		}
		
		String state = null;
		while (!isValidState(state)) {
			System.out.print("                     State : ");
			state = in.nextLine();
			if (state.equals("-1")) return false;	
		}
		
		String zip = null;
		while (!isValidZip(zip)) {
			System.out.print("               Postal Code : ");
			zip = in.nextLine();
			if (zip.equals("-1")) return false;	
		}
		
		String pin = null;
		while (!isValidPIN(pin)) {
			System.out.print("                     PIN # : ");
			pin = in.nextLine();
			if (pin.equals("-1")) return false;	
		}
		
		this.account = new BankAccount(
			'Y',
			db.getMaxAccountNumber() + 1,
			0.0,
			new User(
				Integer.parseInt(pin),
				format(dob),
				Long.parseLong(phone),
				firstName,
				lastName,
				streetAddress,
				city,
				state,
				zip
			)
		);
		
		ATM.showCountdown("Creating account", "Account successfully created.");
		System.out.println("\nHi, " + account.getUser().getName() + "! What can I help you with?");
		
		return true;
	}
	
	public boolean closeAccount() throws IOException, InterruptedException {
		System.out.println("\nAw, we're sorry to see you go... Are you sure?\n");
		in.nextLine();
		
		boolean validated = false;
		while (!validated) {
			System.out.print("Confirm (Y/N): ");
			String response = in.nextLine();
			
			if (response.toLowerCase().equals("y")) {
				account.setStatus('N');
				db.updateAccount(account, null);
				account = null;
				ATM.showCountdown("Closing account", "Account closed.");
				
				return false;
			} else if (response.toLowerCase().equals("n")) {
				System.out.println("\nPhew! Thought we lost you for a minute...");
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Performs account login.
	 * 
	 * @return true for successful logins, false otherwise
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	
	public boolean login() throws FileNotFoundException, IOException {
		boolean validated = false;
		long accountNumber = 0;
		
		System.out.println("\nPlease enter your account credentials, or -1 to cancel.\n");
		while (!validated) {
			try {
				if (accountNumber == 0) {
					System.out.print("Account # : ");
					accountNumber = in.nextLong();
				} else if (accountNumber == -1) {
					System.out.println("\nCanceling and returning to the previous menu.");
					break;
				} else {
					System.out.print("    PIN # : ");
					int pin = in.nextInt();
					
					if (pin == -1) {
						System.out.println("\nCanceling and returning to the previous menu.");
						break;
					} else if (isValidAccount(accountNumber, pin)) {
						validated = true;
						System.out.println("\nHi, " + account.getUser().getName() + "! What can I help you with?");
					}
				}
			} catch (InputMismatchException e) {
				in.nextLine();
			}
		}
		
		return validated;
	}
	
	/**
	 * Performs account logout.
	 * 
	 * @return false to invalidate the user's session
	 * @throws IOException 
	 */
	
	public boolean logout() throws IOException {
		System.out.println("\nSee you later, " + account.getUser().getName() + "!");
		
		db.updateAccount(account, destination);
		account = null;
		destination = null;
		
		return false;
	}

	/**
	 * Deposits a user-specified amount of money into the account.
	 */
	
	public void deposit() {
		if (account.getBalance() == BankAccount.ACCOUNT_MAXIMUM) {
			System.out.println("\nYour account already holds the maximum amount. Try withdrawing or transfering some money first.");
		} else {
			System.out.println("\nTell me how much money you want to deposit, or enter -1 to cancel.");
			boolean valid = false;

			while (!valid) {
				try {
					System.out.print("\nEnter Amount : ");
					double amount = in.nextDouble();
					
					if (amount == -1) {
						System.out.println("\nCanceling and returning to previous menu."); break;
					} else if (amount < 0.01) {
						throw new IllegalArgumentException();
					}
					
					switch (account.deposit(amount)) {
						case ATM.INVALID_AMOUNT: System.out.println("\nAmount must be greater than $0.00."); break;
						case ATM.EXCEEDS_MAXIMUM: System.out.println("\nAccount balance would exceed maximum. Deposit rejected."); break;
						case ATM.SUCCESS:
							System.out.println("\nDeposited " + format(amount) + ". Your updated balance is " + format(account.getBalance()) + ".");
							valid = true;
							break;
					}
				} catch (InputMismatchException e) {
					in.nextLine();
					System.out.println("\nAmount must be a numeric value.");
				} catch (IllegalArgumentException e) {
					System.out.println("\nAmount must be greater than or equal to $0.01.");
				}
			}	
		}
	}

	/**
	 * Withdraws a user-specified amount of money from the account.
	 */

	public void withdraw() {
		if (account.getBalance() == 0) {
			System.out.println("\nYou don't have any money to withdraw. Try depositing money first.");
		} else {
			System.out.println("\nTell me how much money you want to withdraw, or enter -1 to cancel.");
			boolean valid = false;
			
			while (!valid) {
				try {
					System.out.print("\nEnter Amount : ");
					double amount = in.nextDouble();
					
					if (amount == -1) {
						System.out.println("\nCanceling and returning to previous menu."); break;
					} else if (amount < 0.01) {
						throw new IllegalArgumentException();
					}

					switch (account.withdraw(amount)) {
						case ATM.INVALID_AMOUNT: System.out.println("\nAmount must be greater than $0.00."); break;
						case ATM.INSUFFICIENT_FUNDS: System.out.println("\nInsufficient funds."); break;
						case ATM.SUCCESS:
							System.out.println("\nWithdrew " + format(amount) + ". Your updated balance is " + format(account.getBalance()) + ".");
							valid = true;
							break;
					}	
				} catch (InputMismatchException e) {
					in.nextLine();
					System.out.println("\nAmount must be a numeric value.");
				} catch (IllegalArgumentException e) {
					System.out.println("\nAmount must be greater than or equal to $0.01.");
				}
			}	
		}
	}
	
	/**
	 * Transfers a user-specified amount of money from the account to a user-specified destination account.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public void transfer() throws FileNotFoundException, IOException {
		if (account.getBalance() == 0) {
			System.out.println("\nYou don't have any money to transfer. Try depositing money first.");
		} else {
			System.out.println("\nTell me where and how much you want to transfer, or enter -1 to cancel.");
			boolean accountValid = false;
			boolean amountValid = false;
			long accountNumber = -1;
			
			System.out.println();
			while (!accountValid) {
				amountValid = false;
				try {
					System.out.print("Enter Destination Account # : ");
					accountNumber = in.nextLong();
					
					if (accountNumber == -1) {
						System.out.println("\nCanceling and returning to previous menu."); break;
					} else if (String.valueOf(accountNumber).length() != 9) {
						amountValid = true;
					} else {
						destination = db.getAccount(accountNumber);
					}
				} catch (InputMismatchException e) {
					in.nextLine();
				}
			
				int attempts = 0;
				while (!amountValid) {
					try {
						if (attempts++ == 0) {
							System.out.print("               Enter Amount : ");
						} else {
							System.out.print("\n               Enter Amount : ");
						}
						double amount = in.nextDouble();
						
						if (amount == -1) {
							System.out.println("\nCanceling and returning to previous menu."); break;
						} else if (amount < 0.01) {
							throw new IllegalArgumentException();
						}
						
						switch(account.transfer(destination, amount)) {
							case ATM.INVALID_AMOUNT: System.out.println("\nAmount must be greater than $0.00."); break;
							case ATM.EXCEEDS_MAXIMUM: System.out.println("\nAccount balance would exceed maximum. Transfer rejected."); break;
							case ATM.INSUFFICIENT_FUNDS: System.out.println("\nInsufficient funds."); break;
							case ATM.ACCOUNT_NOT_FOUND:
								System.out.println("\nAccount not found.");
								
								accountValid = false;
								amountValid = true;
								break;
							case ATM.SUCCESS:
								System.out.println("\nTransferred " + format(amount) + " to " + accountNumber + ". Your updated balance is " +
									format(account.getBalance()) + ".");
								
								accountValid = true;
								amountValid = true;
								break;
						}
					} catch (InputMismatchException e) {
						in.nextLine();
						System.out.println("\nAmount must be a numeric value.");
					} catch (IllegalArgumentException e) {
						System.out.println("\nAmount must be greater than or equal to $0.01.");
					}
				}
			}
		}
	}

	/**
	 * Displays the current account balance.
	 */
	
	public void viewBalance() {
		System.out.println("\nCurrent balance is " + format(account.getBalance()) + ".");
	}
	
	/**
	 * Displays the user's personal information.
	 */
	
	public void viewPersonalInfo() {
		System.out.println("\n     Account # : " + account.getAccountNumber());
		System.out.println("Account Holder : " + account.getUser().getName());
		System.out.println("       Address : " + account.getUser().getStreetAddress());
		System.out.println("                 " + account.getUser().getFormattedAddress());
		System.out.println(" Date of Birth : " + account.getUser().getFormattedDOB());
		System.out.println("     Telephone : " + account.getUser().getFormattedPhone());
	}
	
	/**
	 * Updates the user's personal information.
	 */
	
	public void updatePersonalInfo() {
		System.out.println("\nSelect the personal information you wish to update, or -1 to cancel.");
		boolean valid = false;
		
		while (!valid) {
			try {
				showUpdateMenu();
				int selection = in.nextInt();
				
				switch (selection) {
					case -1: valid = true; break;
					case  1: valid = updatePIN(); break;
					case  2: valid = updatePhone(); break;
					case  3: valid = updateAddress(); break;
				}
			} catch (InputMismatchException e) {
				in.nextLine();
			}
		}
	}
	
	/**
	 * Displays a countdown sequence with custom messages.
	 * 
	 * @param first the opening message
	 * @param second the closing message
	 * @param isShutdown whether or not the Scanner needs to be closed
	 * @throws InterruptedException
	 */
	
	public static void showCountdown(String first, String second) throws InterruptedException {
		System.out.print("\n" + first);
		Thread.sleep(750);
		System.out.print(".");
		Thread.sleep(750);
		System.out.print(".");
		Thread.sleep(750);
		System.out.print(".");
		Thread.sleep(1000);
		System.out.println("\n" + second);
	}
	
	/////////////////////////////////// PRIVATE METHODS ///////////////////////////////////

	/*
	 * Displays a menu of options.
	 */
	
	public void showMenu() {
		System.out.println("\n   [1] Open Account");
		System.out.println("   [2] Login");
		System.out.println("   [3] Quit");
		
		System.out.print("\nMake a selection: ");
	}

	/*
	 * Displays a submenu of options.
	 */
	
	public void showSubmenu() {
		System.out.println("\n   [1] Deposit");
		System.out.println("   [2] Withdraw");
		System.out.println("   [3] Transfer");
		System.out.println("   [4] View Balance");
		System.out.println("   [5] View Personal Information");
		System.out.println("   [6] Update Personal Information");
		System.out.println("   [7] Close Account");
		System.out.println("   [8] Logout");
		
		System.out.print("\nMake a selection: ");
	}
	
	/*
	 * Displays personal information update menu.
	 */
	
	private void showUpdateMenu() {
		System.out.println("\n   [1] PIN");
		System.out.println("   [2] Telephone");
		System.out.println("   [3] Address");
		
		System.out.print("\nMake a selection: ");
	}
	
	/*
	* Validates an account number and PIN.
	* 
	* @param accountNumber the account number to validate
	* @param pin the PIN to validate
	* @return true for valid account number/PIN pairs, false otherwise
	* @throws FileNotFoundException
	* @throws IOException
	*/

	private boolean isValidAccount(long accountNumber, int pin) throws FileNotFoundException, IOException {
		account = db.getAccount(accountNumber);
		
		if (account != null) {
			if (account.getAccountNumber() == accountNumber && account.getUser().getPIN() == pin) {
				return true;
			}
		}
		account = null;
		
		return false;
	}
	
	/*
	 * Updates the user's PIN.
	 *  
	 * @return true if the update is successful, false otherwise
	 */
	
	private boolean updatePIN() {
		String current = null;
		boolean valid = false;
		
		System.out.println();
		while (!valid) {
			try {
				System.out.print("     Enter current PIN # : ");
				current = in.next();
				
				if (current.equals("-1")) {
					System.out.println("\nCanceling and returning to the previous menu.");
					
					return false;
				} else if (Integer.parseInt(current) == account.getUser().getPIN()) {
					valid = true;
				}
			} catch (NumberFormatException e) {
				in.nextLine();
			}
		}
		valid = false;
		
		while (!valid) {
			try {
				System.out.print("Enter new, 4-digit PIN # : ");
				String pin = in.next();
					
				if (pin.equals("-1")) {
					System.out.println("\nCanceling and returning to the previous menu.");
						
					return false;
				} else if (pin.length() != 4) {
					// ignore entered PIN so prompt will repeat
				} else {
					account.getUser().setPIN(Integer.parseInt(current), Integer.parseInt(pin));
					if (Integer.parseInt(current) != Integer.parseInt(pin)) {
						System.out.println("\nSuccessfully changed PIN from " + current + " to " + pin + ".");
					} else {
						System.out.println("\nThe new PIN did not differ from the current PIN. No changes made.");
					}
						
					return true;	
				}
			} catch (InputMismatchException e) {
				in.nextLine();
			}
		}
		
		return false;
	}
	
	/*
	 * Updates the user's phone number.
	 * 
	 * @return true if the update is successful, false otherwise
	 */
	
	private boolean updatePhone() {
		long phone = -1;
		boolean valid = false;
		
		while (!valid) {
			try {
				System.out.print("\nEnter new phone number : ");
				phone = in.nextLong();
				
				if (phone == -1 ) {
					System.out.println("\nCanceling and returning to the previous menu.");
					
					return false;
				} else if (Long.toString(phone).length() != 10) {
					System.out.println("\nPhone numbers should be exactly 10 digits and cannot start with 0. Try again.");
				} else {
					String current = account.getUser().getFormattedPhone();
					account.getUser().setPhone(phone);
					
					if (!current.equals(account.getUser().getFormattedPhone())) {
						System.out.println("\nSuccessfully changed phone number from " + current + " to " + account.getUser().getFormattedPhone() + ".");
					} else {
						System.out.println("\nThe new phone number did not differ from the current phone number. No changes made.");
					}
					
					return true;
				}
			} catch (InputMismatchException e) {
				in.nextLine();
				System.out.println("\nPhone numbers should contain digits only. Try again.");
			}
		}
		
		return false;
	}
	
	/*
	 * Updates the user's address.
	 * 
	 * @return true if the update is successful, false otherwise
	 */
	
	private boolean updateAddress() {
		String streetAddress = null;
		String city = null;
		String state = null;
		String zip = null;
		boolean valid = false;
		in.nextLine();
				
		System.out.println();
		while (!valid) {
			System.out.print("Enter new street address : ");
			streetAddress = in.nextLine();
			if (streetAddress == null || streetAddress.equals("-1")) {
				System.out.println("\nCanceling and returning to the previous menu.");
						
				return false;
			} else if (streetAddress.length() < 1) {
				valid = false;
			} else {
				valid = true;
			}
		}
		valid = false;
				
		while (!valid) {
			System.out.print("          Enter new city : ");
			city = in.nextLine();
			if (city == null || city.equals("-1")) {
				System.out.println("\nCanceling and returning to the previous menu.");
				
				return false;
			} else if (city.length() < 1) {
				valid = false;
			} else {
				valid = true;
			}
		}
		valid = false;
				
		while (!valid) {
			System.out.print("         Enter new state : ");
			state = in.nextLine();
			if (state == null || state.equals("-1")) {
				System.out.println("\nCanceling and returning to the previous menu.");
				
				return false;
			} else if (state.length() < 2) {
				valid = false;
			} else if (!isValidState(state)) {
				valid = false;
			} else {
				valid = true;
			}
		}
		valid = false;
				
		while (!valid) {
			System.out.print("   Enter new postal code : ");
			zip = in.nextLine();
			if (zip == null || zip.equals("-1")) {
				System.out.println("\nCanceling and returning to the previous menu.");
				
				return false;
			} else if (zip.length() != 5) {
				valid = false;
			} else {
				valid = true;
			}
		}
				
		if (valid) {
			String previous = account.getUser().getStreetAddress() + "\n" + account.getUser().getFormattedAddress();					
			account.getUser().setStreetAddress(streetAddress);
			account.getUser().setCity(city);
			account.getUser().setState(state);
			account.getUser().setZip(zip);
			String current = account.getUser().getStreetAddress() + "\n" + account.getUser().getFormattedAddress();
			
			if (!previous.equals(current)) {
				System.out.println("\nSuccessfully changed address from :\n\n" + previous + "\n\nto :\n\n" + current);						
			} else {
				System.out.println("\nThe new address did not differ from the current address. No changes made.");
			}
			
			return true;
		}
		
		return false;
	}
	
	/*
	 * Provides limited validation for generic text.
	 * 
	 * @param text the text to be validated
	 * @return true if the text passes validation, false otherwise
	 */
	
	private boolean isValidText(String text) {
		if (text == null) return false;
		if (text.length() == 0) return false;
		
		return true;
	}
	
	/*
	 * Provides limited validation for a date of birth.
	 * 
	 * @param dob the date of birth
	 * @return true if the date passes validation, false otherwise
	 */
	
	private boolean isValidDOB(String dob) {
		if (dob == null) return false;
		if (dob.length() != 10) return false;
		if (dob.charAt(2) != '/' || dob.charAt(5) != '/') return false;
		
		try {
			Integer.parseInt(dob.substring(0, 2));		// try to parse the month,
			Integer.parseInt(dob.substring(3, 5));		// day, and year. if there's
			Integer.parseInt(dob.substring(6, 10));		// an error, return false
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	/*
	 * Provides limited validation for a phone number.
	 * 
	 * @param phone the phone number to validate
	 * @return true if the phone number passes validation, false otherwise
	 */
	
	private boolean isValidPhone(String phone) {
		if (phone == null) return false;
		if (phone.length() != 10) return false;
		if (phone.charAt(0) == '0') return false;
		
		for (int i = 0; i < phone.length(); i++) {
			if (phone.charAt(i) < '0' || phone.charAt(i) > '9') {	// verify all characters are digits
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Validates the state entered by the user.
	 *  
	 * @param state the state entered by the user
	 * @return true if the value is a valid U.S. state, false otherwise
	 */
	
	private boolean isValidState(String state) {
		if (state == null) return false;
		if (state.length() < 2) return false;
		
		if (Arrays.asList(User.STATES).contains(state.toLowerCase())) {
			return true;
		} else if (Arrays.asList(User.STATE_ABBREVIATIONS).contains(state.toLowerCase())) {
			return true;
		}
		
		return false;
	}
	
	/*
	 * Provides limited validation for a postal code.
	 * 
	 * @param zip the postal code to be validated
	 * @return true if the postal code passes validation, false otherwise
	 */
	
	private boolean isValidZip(String zip) {
		if (zip == null) return false;
		if (zip.length() != 5) return false;
		
		for (int i = 0; i < zip.length(); i++) {
			if (zip.charAt(i) < '0' || zip.charAt(i) > '9') {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean isValidPIN(String pin) {
		if (pin == null) return false;
		if (pin.length() != 4) return false;
		
		for (int i = 0; i < pin.length(); i++) {
			if (pin.charAt(i) < '0' || pin.charAt(i) > '9') {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Formats raw dollar amounts as $1,234.56.
	 * 
	 * @param amount the amount to format
	 * @return the formatted dollar amount
	 */
	
	private static String format(double amount) {
		return "$" + String.format("%,.2f", amount);
	}
	
	/*
	 * Formats dates as YYYYMMDD.
	 * 
	 * @param dob the date to format
	 * @return the formatted date
	 */
	
	private static int format(String dob) {
		try {
			return Integer.valueOf(String.format("%4d%02d%02d",
				Integer.parseInt(dob.substring(6, 10)),
				Integer.parseInt(dob.substring(0, 2)),
				Integer.parseInt(dob.substring(3, 5)))
			);			
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}