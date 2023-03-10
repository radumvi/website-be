package repository.order;

import java.util.ArrayList;
import java.util.HashMap;

import entities.*;

public interface OrderRepositoryInterface {
	/**
	 * Creates an order.
	 * @param clientID		the id of the user that placed the order
	 * @param addressID		the address of the user which is associated with the order
	 * @param hm			the map of all the items in the order and their quantity
	 * @return				true, if the insert was successful
	 */
	public boolean create(int clientID, int addressID, HashMap<Integer, Integer> hm);
	
	/**
	 * Returns all the orders of an user.
	 * @param 	userId	the id of the user which requested their orders 
	 * @return			the list of all the orders of the user
	 */
	public ArrayList<Order> readByUser(int userId);
	
	/**
	 * Returns all the items in an order.
	 * @param 	id	the id of the order
	 * @return		the list of the items
	 */
	public ArrayList<Item> read(int id);
}
