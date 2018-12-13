public class BankAccount {
	
	public static final double ACCOUNT_MAXIMUM = 999999999999.99;
	
	private char status;
	private long accountNumber;
	private double balance;
	private User user;
	
	/**
	 * Constructor for newly created accounts.
	 * 
	 * @param status
	 * @param accountNumber
	 * @param balance
	 * @param user
	 */
	
	public BankAccount(char status, long accountNumber, double balance, User user) {
		this.status = status;
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.user = user;
	}
	
	/**
	 * Constructor for existing accounts retrieved from the database.
	 * 
	 * @param details
	 */
	
	public BankAccount(String details) {
		this(BankAccount.parseStatus(details), BankAccount.parseAccountNumber(details), BankAccount.parseBalance(details), BankAccount.parseUser(details));
	}
	
	/////////////////////////////////// GETTERS AND SETTERS ///////////////////////////////////
	
	/**
	 * Retrieves the account status.
	 * 
	 * @return status
	 */
	
	public char getStatus() {
		return status;
	}
	
	/**
	 * Retrieves the account number.
	 * 
	 * @return accountNumber
	 */
	
	public long getAccountNumber() {
		return accountNumber;
	}
	
	/**
	 * Retrieves the account balance.
	 * 
	 * @return balance
	 */
	
	public double getBalance() {
		return balance;
	}
	
	/**
	 * Retrieves the user associated with this account.
	 * 
	 * @return user
	 */
	
	public User getUser() {
		return user;
	}
	
	/**
	 * Updates the account status.
	 * 
	 * @param status the new status
	 */
	
	public void setStatus(char status) {
		this.status = status;
	}
	
	/**
	 * Updates the user associated with this account.
	 * 
	 * @param user the new user
	 */
	
	public void setUser(User user) {
		this.user = user;
	}
	
	/////////////////////////////////// INSTANCE METHODS ///////////////////////////////////
	
	/**
	 * Deposits money into this account.
	 * 
	 * @param amount the money to deposit
	 * @return a status code (0: invalid amount, 1: exceeds max, 4: success)
	 */
	
	public int deposit(double amount) {
		if (amount <= 0) {
			return ATM.INVALID_AMOUNT;
		} else if ((amount + balance) > BankAccount.ACCOUNT_MAXIMUM) {
			return ATM.EXCEEDS_MAXIMUM;
		} else {
			balance = balance + amount;
			
			return ATM.SUCCESS;
		}
	}
	
	/**
	 * Withdraws money from this account.
	 * 
	 * @param amount the money to withdraw
	 * @return a status code (0: invalid amount, 2: insufficient funds, 4: success)
	 */
	
	public int withdraw(double amount) {
		if (amount <= 0) {
			return ATM.INVALID_AMOUNT;
		} else if (amount > balance) {
			return ATM.INSUFFICIENT_FUNDS;
		} else {
			balance = balance - amount;
			
			return ATM.SUCCESS;
		}
	}
	
	/**
	 * Transfers money from this account to another account.
	 * 
	 * @param destination the account to which to transfer the money
	 * @param amount the amount of money to transfer
	 * @return a status code (0: invalid amount, 2: insufficient funds, 3: account not found, 4: success)
	 */
	
	public int transfer(BankAccount destination, double amount) {
		if (destination == null) {
			return ATM.ACCOUNT_NOT_FOUND;
		} else {
			int status = this.withdraw(amount);
			
			if (status == ATM.SUCCESS) {
				status = destination.deposit(amount);
			}
			
			return status;
		}
	}
	
	/////////////////////////////////// OVERRIDDEN METHODS ///////////////////////////////////

	/*
	 * Generates a String representation of the BankAccount
	 * 
	 * @return a String representation of the BankAccount
	 */
	
	@Override
	public String toString() {			
		return String.format("%09d%04d%-15.2f%-20s%-15s%8d%10d%-30s%-30s%2s%5s%s",
			accountNumber,
			user.getPIN(),
			balance,
			user.getLastName(),
			user.getFirstName(),
			user.getDOB(),
			user.getPhone(),
			user.getStreetAddress(),
			user.getCity(),
			user.getState(),
			user.getZip(),
			status
		);
	}
	
	/////////////////////////////////// PRIVATE METHODS ///////////////////////////////////
	
	/*
	 * Parses the details of a User from the account string.
	 * 
	 * @param account the account string
	 * @return a User
	 */
	
	private static User parseUser(String account) {
		return new User(
			BankAccount.parsePIN(account),
			BankAccount.parseDOB(account),
			BankAccount.parsePhone(account),
			BankAccount.parseFirstName(account),
			BankAccount.parseLastName(account),
			BankAccount.parseStreetAddress(account),
			BankAccount.parseCity(account),
			BankAccount.parseState(account),
			BankAccount.parseZip(account)
		);
	}
	
	/*
	 * Parses the account number from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed account number
	 */
	
	private static long parseAccountNumber(String account) {
		return Long.parseLong(account.substring(0, 9));
	}
	
	/*
	 * Parses the PIN from the account string.
	 * 
	 * @param account the account stirng
	 * @return the parsed PIN
	 */
	
	private static int parsePIN(String account) {
		return Integer.parseInt(account.substring(9, 13));
	}
	
	/*
	 * Parses the balance from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed balance
	 */
	
	private static double parseBalance(String account) {
		return Double.parseDouble(account.substring(13, 28));
	}
	
	/*
	 * Parses the last name from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed last name
	 */
	
	private static String parseLastName(String account) {
		return account.substring(28, 48).trim();
	}
	
	/*
	 * Parses the first name from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed first name
	 */
	
	private static String parseFirstName(String account) {
		return account.substring(48, 63).trim();
	}
	
	/*
	 * Parses the date of birth from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed date of birth
	 */
	
	private static int parseDOB(String account) {
		return Integer.parseInt(account.substring(63, 71));
	}
	
	/*
	 * Parses the phone number from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed phone number
	 */
	
	private static long parsePhone(String account) {
		return Long.parseLong(account.substring(71, 81));
	}
	
	/*
	 * Parses the street address from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed street address
	 */
	
	private static String parseStreetAddress(String account) {
		return account.substring(81, 111).trim();
	}
	
	/*
	 * Parses the city from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed city
	 */
	
	private static String parseCity(String account) {
		return account.substring(111, 141).trim();
	}
	
	/*
	 * Parses the state from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed state
	 */
	
	private static String parseState(String account) {
		return account.substring(141, 143);
	}
	
	/*
	 * Parses the zip code from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed zip code
	 */
	
	private static String parseZip(String account) {
		return account.substring(143, 148);
	}
	
	/*
	 * Parses the account status from the account string.
	 * 
	 * @param account the account string
	 * @return the parsed account status
	 */
	
	private static char parseStatus(String account) {
		return account.charAt(148);
	}
}