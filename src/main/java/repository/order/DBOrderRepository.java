package repository.order;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import entities.*;
import repository.item.DBItemRepository;
import repository.itemorder.DBItemOrderRepository;

public class DBOrderRepository implements OrderRepositoryInterface {
	
	static final String DB_URL = "x";
	static final String USER = "y";
	static final String PASS = "z";
	
	private DBItemRepository dbItemRepository;
	private DBItemOrderRepository dbItemOrderRepository;
	
	/**
	 * Contructor that sets the repositories.
	 */
	public DBOrderRepository() {
		dbItemRepository = new DBItemRepository();
		dbItemOrderRepository = new DBItemOrderRepository();
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean create(int userID, int addressID, HashMap<Integer, Integer> hm) {
		// the total to be added in the Orders table
		float total = 0;
				
		// calculating the sum of all the items in the cart
		for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
			total += dbItemRepository.read(entry.getKey()).getPrice() * entry.getValue();
		}
				
		if (total == 0) {
			// there are no items in the cart
			return false;
		}
				
		// the current date
		java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
			
		try {
			String insertString = "INSERT INTO "
					+ "Order(UserID, AddressID, Date, Total) "
					+ "VALUES(" + userID + ", " + addressID + ", \"" + date.toString() + "\", " + total + ");";
					
					
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(insertString);
					
			int orderId = this.getOrderId();
			
			if (orderId == 0) {
				return false;
			}
			
			// adding each item in the link table
			for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
				dbItemOrderRepository.add(orderId, entry.getKey(), entry.getValue());
			}
					
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
	public ArrayList<Item> read(int id) {
		
		ArrayList<Item> result = new ArrayList<>();
		Item buffer;
		
		try {
			
			// getting the items in the order based on the order ID
			String query = "SELECT Name, Brand, Item.Quantity "
					+ "FROM Item JOIN ItemOrder ON (Item.ItemID = ItemOrder.ItemID) "
					+ "WHERE ItemOrder.OrderID = " + id + ";";
			
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				// there were no items in the order
				return null;
			} 
			else {
				do {
					// adding each item in the list that is returned
					buffer = new Item();
					
					buffer.setName(rs.getString("Name"));
					buffer.setBrand(rs.getString("Brand"));
					buffer.setQuantity(rs.getString("Quantity"));
					
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
	public ArrayList<Order> readByUser(int userId) {
		ArrayList<Order> result = new ArrayList<>();
		Order buffer;

		try {
			// getting all the orders of a client 
			String query = "SELECT OrderID, Date, Total, Status "
						+ "FROM Orders "
						+ "WHERE UserID = " + userId + ";";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				// there are no orders associated with this user
				return null;
			} 
			else {
				do {
					// add each row to the result list
					buffer = new Order();
					buffer.setId(rs.getInt("OrderID"));
					buffer.setDate(rs.getString("Date"));
					buffer.setTotal(rs.getFloat("Total"));
					buffer.setStatus(rs.getString("Status"));
					
					result.add(buffer);
				}
				while (rs.next() != false);
			}
			
			return result;
		}
		catch (SQLException | ClassNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Returns the id of the last order inserted into the table.
	 */
	public int getOrderId() {
		
		try {
			// getting all the orders of a client 
			String query = "SELECT LAST_INSERT_ID();";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				return 0;
			} 
			else {
				return rs.getInt(1);
			}
		}
		catch (SQLException | ClassNotFoundException e) {
			return 0;
		}
	}

}
