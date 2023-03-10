package repository.user;

import java.util.ArrayList;

import entities.User;

public interface UserRepositoryInterface {
	/**
	 * Add an user.
	 * @param 	userObject	the user to be registered
	 * @return				true, if the operation was successful
	 */
	public boolean create(User userObject);
	
	/**
	 * Returns all the users in the system.
	 * @return	the list with all the users.
	 */
	public ArrayList<User> read();
	
	/**
	 * Returns details about an user.
	 * @param 	id	the id of the user
	 * @return		the user object
	 */
	public User read(int id);
	
	/**
	 * Modifies an user.
	 * @param userObject	the user to be updated	
	 * @return				true, if the operation was sucessful
	 */
	public boolean update(User userObject);
	
	/**
	 * Deletes an user
	 * @param 	id	the id of the user to be deleted	
	 * @return		true, if the operation was sucessful
	 */
	public boolean delete(int id);
}
