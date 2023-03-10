package repository.address;

import java.util.ArrayList;

import entities.Address;

/**
 * Interface for basic operations on addresses.
 */
public interface AddressRepositoryInterface {
	/**
	 * Inserts an address.
	 * @param address	the address to be added
	 * @return 			true if the operation was successful
	 */
	public boolean create(Address address);
	
	/**
	 * Reads all the addresses of an user.
	 * @param userId	the id of the user which requested the addresses
	 * @return			a list of all the addresses of the user
	 */
	public ArrayList<Address> read(int userId);
	
	/**
	 * Deletes an address from the database.
	 * @param addressId		the id of the address to be deleted
	 * @param userId		the id of the user that wants the address deleted
	 * @return				true if the deletion was possible
	 */
	public boolean delete(int addressId, int userId);
}
