import java.io.FileNotFoundException;
import java.io.IOException;

public class Tester {
	
	/**
	 * Main method. Execution starts here.
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	
	public static void main(String[] args) throws InterruptedException {		
		try {
			ATM atm = new ATM();
			atm.run();
		} catch (InterruptedException e) {
			System.out.println("Uh, something went wrong.");
			ATM.showCountdown("Powering off", "Shutdown complete.");
		} catch (FileNotFoundException e) {
			System.out.println("Uh, I can't seem to find the database file.");
			ATM.showCountdown("Powering off", "Shutdown complete.");
		} catch (IOException e) {
			System.out.println("Uh, I can't seem to access the database file.");
			ATM.showCountdown("Powering off", "Shutdown complete.");
		}
	}
}