package repository.item;

import java.util.ArrayList;

import entities.Item;

/**
 * Interface for basic operations on items.
 */
public interface ItemRepositoryInterface {
	/**
	 * Adds an item.
	 * @param itemObject	the user to be added
	 * @return				true, if the operation was successful
	 */
	public boolean create(Item itemObject);
	
	/**
	 * Returns all the items in the sistem.
	 * @return	all the items
	 */
	public ArrayList<Item> read();
	
	/**
	 * Returns all the items in the category given.
	 * @param 	category	the category which items belong to
	 * @return				all the items belonging to the category
	 */
	public ArrayList<Item> read(String category);
	
	/**
	 * Reads an item.
	 * @param  id	the id of the item
	 * @return		the item
	 */
	public Item read(int id);
	
	/**
	 * Updates an item.
	 * @param 	itemObject	the item to be modified
	 * @return				true, if the operation was successful
	 */
	public boolean update(Item itemObject);
	
	/**
	 * Deletes an item
	 * @param 	id	the id of the item to be deleted
	 * @return		true, if the operation was successful
	 */
	public boolean delete(int id);
}
