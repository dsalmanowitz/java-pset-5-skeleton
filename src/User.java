import java.util.Arrays;

public class User {
	
	public final static String[] STATES = {
		"alabama", "alaska", "arizona", "arkansas", "california", "colorado", "connecticut", "delaware", "florida", "georgia", "hawaii", "idaho",
		"illinois", "indiana", "iowa", "kansas", "kentucky", "louisiana", "maine", "maryland", "massachusetts", "michigan", "minnesota",
		"mississippi", "missouri", "montana", "nebraska", "nevada", "new hampshire", "new jersey", "new mexico", "new york", "north carolina",
		"north dakota", "ohio", "oklahoma", "oregon", "pennsylvania", "rhode island", "south carolina", "south dakota", "tennessee", "texas",
		"utah", "vermont", "virginia", "washington", "west virginia", "wisconsin", "wyoming", "district of columbia"
	};
	
	public final static String[] STATE_ABBREVIATIONS = {
		"al", "ak", "az", "ar", "ca", "co", "ct", "de", "fl", "ga", "hi", "id", "il", "in", "ia", "ks", "ky", "la", "me", "md", "ma", "mi", "mn",
		"ms", "mo", "mt", "ne", "nv", "nh", "nj", "nm", "ny", "nc", "nd", "oh", "ok", "or", "pa", "ri", "sc", "sd", "tn", "tx", "ut", "vt", "va",
		"wa", "wv", "wi", "wy", "dc"
	};
	
	private int pin;
	private int dob;
	private long phone;
	private String firstName;
	private String lastName;
	private String streetAddress;
	private String city;
	private String state;
	private String zip;
	
	/**
	 * Constructor for User.
	 * 
	 * @param pin
	 * @param dob
	 * @param phone
	 * @param first
	 * @param last
	 * @param address
	 * @param city
	 * @param state
	 * @param zip
	 */
	
	public User(int pin, int dob, long phone, String firstName, String lastName, String streetAddress, String city, String state, String zip) {
		this.pin = pin;
		this.dob = dob;
		this.phone = phone;
		this.firstName = firstName;
		this.lastName = lastName;
		this.streetAddress = streetAddress;
		this.city = city;
		this.state = abbreviateState(state);
		this.zip = zip;
	}
	
	/////////////////////////////////// GETTERS AND SETTERS ///////////////////////////////////
	
	/**
	 * Retrieves the user's PIN.
	 * 
	 * @return pin
	 */
	
	public int getPIN() {
		return pin;
	}
	
	/**
	 * Retrieves the user's date of birth.
	 * 
	 * @return dob
	 */
	
	public int getDOB() {
		return dob;
	}
	
	/**
	 * Retrieves the user's phone number.
	 * 
	 * @return phone
	 */
	
	public long getPhone() {
		return phone;
	}
	
	/**
	 * Retrieves the user's first name.
	 * 
	 * @return firstName
	 */
	
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * Retrieves the user's last name.
	 * 
	 * @return lastName
	 */
	
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * Retrieves the user's street address.
	 * 
	 * @return streetAddress
	 */
	
	public String getStreetAddress() {
		return streetAddress;
	}
	
	/**
	 * Retrieves the user's city of residence.
	 * 
	 * @return city
	 */
	
	public String getCity() {
		return city;
	}
	
	/**
	 * Retrieves the user's state of residence.
	 * 
	 * @return state
	 */
	
	public String getState() {
		return state;
	}
	
	/**
	 * Retrieves the user's postal code.
	 * 
	 * @return zip
	 */
	
	public String getZip() {
		return zip;
	}
	
	/**
	 * Updates the user's PIN.
	 * 
	 * @param current the user's current PIN
	 * @param pin the new PIN
	 */
	
	public void setPIN(int current, int pin) {
		if (pin < 0 || pin > 9999) return;
		if (this.pin != current) return;
		
		this.pin = pin;
	}
		
	/**
	 * Updates the user's phone number.
	 * 
	 * @param phone the new phone number
	 */
	
	public void setPhone(long phone) {
		this.phone = phone;
	}
	
	/**
	 * Updates the user's street address.
	 * 
	 * @param streetAddress the new street address
	 */
	
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	/**
	 * Updates the user's city of residence.
	 * 
	 * @param city the new city
	 */
	
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Updates the user's state of residence.
	 * 
	 * @param state the new state
	 */
	
	public void setState(String state) {
		if (state.length() == 2) {
			this.state = state;
		} else {
			this.state = abbreviateState(state);
		}
	}
	
	/**
	 * Updates the user's postal code.
	 * 
	 * @param zip the new postal code
	 */
	
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	/////////////////////////////////// INSTANCE METHODS ///////////////////////////////////
	
	/**
	 * Formats first and last names as a full name.
	 *  
	 * @param firstName
	 * @param lastLast
	 * @return the full name
	 */
	
	public String getName() {
		return firstName + " " + lastName;
	}
	
	/**
	 * Formats raw phone numbers as (555) 555-5555.
	 * 
	 * @param phone
	 * @return the formatted phone number
	 */
	
	public String getFormattedPhone() {
		return "(" + Integer.parseInt(String.valueOf(phone).substring(0, 3)) + ") " +
			Integer.parseInt(String.valueOf(phone).substring(3, 6)) + "-" +
			Integer.parseInt(String.valueOf(phone).substring(6, 10));
	}
	
	/**
	 * Formats raw date values as string representations.
	 * 
	 * @param dob
	 * @return the formatted dob
	 */
	
	public String getFormattedDOB() {
		int year = Integer.parseInt(String.valueOf(dob).substring(0, 4));
		int month = Integer.parseInt(String.valueOf(dob).substring(4, 6));
		int day = Integer.parseInt(String.valueOf(dob).substring(6, 8));
		
		return getMonth(month) + " " + day + ", " + year;
	}
	
	/**
	 * Formats the city, state, and postal code portion of an address.
	 * 
	 * @return the formatted address components
	 */
	
	public String getFormattedAddress() {
		return city + ", " + state + " " + zip;
	}
	
	/////////////////////////////////// PRIVATE METHODS ///////////////////////////////////
	
	/*
	 * Converts a numeric month to its string equivalent.
	 * 
	 * @param month the numeric month
	 * @return the string equivalent
	 */
	
	private String getMonth(int month) {
		switch (month) {
			case  1: return "January";
			case  2: return "February";
			case  3: return "March";
			case  4: return "April";
			case  5: return "May";
			case  6: return "June";
			case  7: return "July";
			case  8: return "August";
			case  9: return "September";
			case 10: return "October";
			case 11: return "November";
			case 12: return "December";
		}
		
		return null;
	}
	
	/*
	 * Converts a state to its two-character abbreviation.
	 * 
	 * @param state the state to be abbreviated
	 * @return the abbreviation
	 */
	
	private String abbreviateState(String state) {
		if (Arrays.asList(STATE_ABBREVIATIONS).contains(state.toLowerCase())) {
			return state.toUpperCase();
		} else {
			int index = Arrays.asList(STATES).indexOf(state.toLowerCase());
		
			if (index == -1) {
				return "";
			} else {
				return STATE_ABBREVIATIONS[index].toUpperCase();
			}
		}
	}
}