package helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class with static methods for password hashing and validation.
 */
public class StringOperations {
	
	/**
	 * Hashes the input.
	 * @paraminput 	String to be hashed.
	 * @return			The hashed input.
	 */
	public static String hashPassword(String input) {
		
		String output = null;
		
		try {
			// i get the instance that corresponds to the algorithm that I want to use
			MessageDigest messageDigestObject = MessageDigest.getInstance("MD5");
			
			messageDigestObject.update(input.getBytes());
			
			// the output in bytes
			byte[] outputByte = messageDigestObject.digest();
			
			StringBuilder stringBuilderObject= new StringBuilder(); 
			
			for (int i = 0; i < outputByte.length; i++) {
				stringBuilderObject.append(Integer.toString((outputByte[i] & 0xff) + 0x100, 16).substring(1));
			}
			
			output = stringBuilderObject.toString();
			
		} catch (NoSuchAlgorithmException e) {
			output = null;
		}
		
		return output;
	}
	
	/**
	 * Check a set of criteria for passwords.
	 * @param 	input	The String to be checked.
	 * @return			True if it valid.
	 */
	public static boolean checkPassword(String input) {
		// returns true if it contains:
		// - at least 8 characters
		// - at least one uppercase letter
		// - at least one digit
		// - at least one special character {!, @, *, #}
		
		boolean upper = false, digit = false, special = false;
		final char specialCharacters[] = {'!', '@', '#', '$', '*'}; 
		
		// firstly, I check the 1st condition
		if (input.length() < 8) {
			return false;
		}
		else {
			// here I check the other ones
			for (int i = 0; i < input.length(); i++) {
				
				if (Character.isUpperCase(input.charAt(i))) {
					upper = true;
				}
					
				if (Character.isDigit(input.charAt(i))) {
					digit = true;
				}
				
				for (int j = 0; j < specialCharacters.length; j++) {
					if (input.charAt(i) == specialCharacters[j]) {
						special = true;
						break;
					}
				}
			}
		}
		
		return upper && digit && special;
	}
}
