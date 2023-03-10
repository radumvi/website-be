package repository.item;

import java.sql.*;
import java.util.ArrayList;

import entities.Item;

/**
 * Implementation of the ItemRepositoryInterface. 
 * Makes possible operations on the items in the database.
 */
public class DBItemRepository implements ItemRepositoryInterface {
	static final String DB_URL = "x";
	static final String USER = "y";
	static final String PASS = "z";
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean create(Item itemObject) {
		try {
			String insertString = "INSERT INTO Item(Name, Brand, Quantity, Price, CategoryID) "
					+ "VALUES(\"" + itemObject.getName() + "\", \"" + itemObject.getBrand() + "\", \""  + itemObject.getQuantity() + "\", "
					+ itemObject.getPrice() + ", " + "(SELECT CategoryID FROM Category WHERE Name = \"" + itemObject.getCategory()
					+ "\"));";
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(insertString);
			
			return true;
		}
		catch (SQLException | ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Item> read() {
		ArrayList<Item> result = new ArrayList<>();
		Item buffer;
		
		try {
			// getting all the items from the database
			String query = "SELECT ItemID, Name, Brand, Quantity, Price, Rating "
							+ "FROM Item;";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				// there are no items in the resulting table
				return null;
			} 
			else {
				do {
					// adding each item in the result list
					buffer = new Item();
					buffer.setId(rs.getInt("ItemID"));
					buffer.setName(rs.getString("Name"));
					buffer.setBrand(rs.getString("Brand"));
					buffer.setQuantity(rs.getString("Quantity"));
					buffer.setPrice(rs.getFloat("Price"));
					buffer.setRating(rs.getFloat("Rating"));
					
					result.add(buffer);
				}
				while (rs.next() != false);
			}
		}
		catch (SQLException | ClassNotFoundException e) {
			return null;
		}
		
		return result;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Item> read(String category) {
		ArrayList<Item> result = new ArrayList<>();
		Item buffer;
		
		try {
			// getting all the items that belong to the given category
			String query = "SELECT ItemID, Item.Name, Brand, Quantity, Price, Rating\r\n"
					+ "FROM Item JOIN Category ON (Item.CategoryID = Category.CategoryID)\r\n"
					+ "WHERE Categorii.Nume = \"" + category + "\";";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				// there are no items in the category, or the category does not exist
				return null;
			} 
			else {
				do {
					// going through each row and setting a new object
					buffer = new Item();
					
					buffer.setId(rs.getInt("ItemID"));
					buffer.setName(rs.getString("Name"));
					buffer.setBrand(rs.getString("Brand"));
					buffer.setQuantity(rs.getString("Quantity"));
					buffer.setPrice(rs.getFloat("Price"));
					buffer.setRating(rs.getFloat("Rating"));
					
					result.add(buffer);
				}
				while (rs.next() != false);
			}
		}
		catch (SQLException | ClassNotFoundException e) {
			return null;
		}
		
		return result;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public Item read(int id) {
		Item item = new Item();
		
		try {
			
			String query = "SELECT Name, Brand, Quantity, Price, Rating "
							+ "FROM Item "
							+ "WHERE ItemID = " + id + ";";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			// creating a connection and a statement
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				// the item does not exist in the database
				return null;
			} 
			else {
				// setting the product object with the information from the result
				item.setName(rs.getString("Nume"));
				item.setBrand(rs.getString("Brand"));
				item.setQuantity(rs.getString("Cantitate"));
				item.setPrice(rs.getInt("Pret"));
				item.setRating(rs.getInt("Rating"));
				
				return item;
			}
		}
		catch (SQLException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean update(Item itemObject) {
		try {
			
			// updating the item information based on its id
			String updateString = "UPDATE Item "
					+ "SET Name = \"" + itemObject.getName() 
					+ "\", Brand = \"" + itemObject.getBrand() 
					+ "\", Price=" + itemObject.getPrice()
					+ " WHERE ProdusID = " + itemObject.getId() + ";";

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(updateString);
			
			// no exceptions
			return true;
		}
		catch (SQLException | ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean delete(int id) {
		try {
			String deleteString = "DELETE FROM Item WHERE ItemID = " + id + ";";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(deleteString);
			
			// there were no exceptions thrown
			return true;
		}
		catch (SQLException | ClassNotFoundException e) {
			return false;
		}
	}
}
