package repository.itemorder;

import java.sql.*;

public class DBItemOrderRepository implements ItemOrderRepositoryInterface {
	
	static final String DB_URL = "x";
	static final String USER = "y";
	static final String PASS = "z";
	
	/**
	 * {@inheritDoc}
	 */
	public boolean add(int orderId, int itemId, int count) {
		try {
			// inserting into the connection table
			String insertString = "INSERT INTO "
					+ "ItemOrder(ItemID, OrderID, Quantity) "
					+ "VALUES(" + itemId + ", " + orderId + ", " + count + ");";
		
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(insertString);
			
			// no exceptions
			return true;
		}
		catch (SQLException | ClassNotFoundException e) {
			return false;
			
		}
	}
}
