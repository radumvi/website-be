package repository.review;

import java.sql.*;
import java.util.ArrayList;

import entities.*;

public class DBReviewRepository implements ReviewRepositoryInterface {
	
	static final String DB_URL = "x";
	static final String USER = "y";
	static final String PASS = "z";
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean create(Review review) {
		float finalRating = 0;
		
		try {
			// inserting the review into the Reviews table
			String insertString = "INSERT INTO Review(ItemID, UserID, Rating, Title) "
					+ "VALUES(" + review.getItem().getId() + ", " + review.getUser().getId() + ", "
					+ review.getRating() + ", \"" + review.getTitle() + "\");";
			
			// getting the total number of reviews
			// to recalculate the average
			String queryString1 = "SELECT COUNT(*) AS c "
					+ "FROM Review "
					+ "WHERE ItemID=" + review.getItem().getId() + ";";
			
			// getting the old rating of the item
			String queryString2 = "SELECT Rating "
					+ "FROM Item "
					+ "WHERE ItemID=" + review.getItem().getId() + ";";
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			Statement stmt1 = conn.createStatement();
			Statement stmt2 = conn.createStatement();
			Statement stmt3 = conn.createStatement();
			
			stmt1.executeUpdate(insertString);
			ResultSet rs1 = stmt2.executeQuery(queryString1);
			ResultSet rs2 = stmt3.executeQuery(queryString2);
			
			rs1.next();
			int count = rs1.getInt("c"); // no of reviews
			
			rs2.next(); 
			float rating = rs2.getFloat("Rating"); // current rating
			
			if (count == 1) {
				// there were no reviews
				finalRating = review.getRating();				
			}
			else {
				// recalculated review
				finalRating = (count - 1) * rating;
				finalRating += review.getRating();
				finalRating /= count;
			}
			
			// upating the item's rating
			String updateString = "UPDATE Produse "
					+ "SET Rating = " + finalRating
					+" WHERE ItemID=" + review.getItem().getId() + ";";
			
			Statement stmt4 = conn.createStatement();
			stmt4.executeUpdate(updateString);
			
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
	public ArrayList<Review> readByItem(int itemId) {
		ArrayList<Review> result = new ArrayList<>();
		Review buffer;
		
		try {
			// getting all the reviews associated with an item
			String query = "SELECT ReviewID, Rating, Title, LastName, FirstName "
					+ "FROM Review JOIN User ON (Review.UserID = User.UserID) "
					+ "WHERE ItemID = " + itemId + ";";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				return null;
			} 
			else {
				do {
					// adding each review to the returned list
					buffer = new Review();
					
					buffer.setId(rs.getInt("ReviewID"));
					buffer.setRating(rs.getInt("Rating"));
					buffer.setTitle(rs.getString("Titlu"));
					buffer.setUser(new User());
					buffer.getUser().setLastName(rs.getString("Nume"));
					buffer.getUser().setFirstName(rs.getString("Prenume"));
					
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
	public ArrayList<Review> readByUser(int userId) {
		ArrayList<Review> result = new ArrayList<>();
		Review buffer;
		
		try {
			// getting all the reviews of an user given their id
			String query = "SELECT I.Name, I.Brand, R.ReviewID, R.Rating, R.Title "
					+ "FROM Item I JOIN Review R ON (I.ItemID = R.ItemID) "
					+ "WHERE R.UserID = " + userId;
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if (rs.next() == false) {
				return null;
			} 
			else {
				do {
					// adding each one to the result
					buffer = new Review();
					
					buffer.setId(rs.getInt("ReviewID"));
					buffer.setRating(rs.getInt("Rating"));
					buffer.setTitle(rs.getString("Title"));
					buffer.setItem(new Item());
					buffer.getItem().setName(rs.getString("Name"));
					buffer.getItem().setBrand(rs.getString("Brand"));
					
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
	public Review read(int userId, int itemId) {
		try {
			
			// getting a review's information based on the poster's id and product id
			String query = "SELECT Rating, Title from Review where UserID = " + userId
					+ " AND ItemID = " + itemId + ";";
			
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if(rs.next() == false) {
				return null;
			}
			else {
				Review x = new Review();
				x.setRating(rs.getInt("Rating"));
				x.setTitle(rs.getString("Title"));
				return x;
			}

		}
		catch (SQLException | ClassNotFoundException e) {
			return null;
		}
	}
}
