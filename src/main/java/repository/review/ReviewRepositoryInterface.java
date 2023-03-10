package repository.review;

import java.util.ArrayList;

import entities.Review;

public interface ReviewRepositoryInterface {
	/**
	 * Adds a review.
	 * @param review	the review to be added
	 * @return			true, if the operation was successful
	 */
	public boolean create(Review review);
	
	/**
	 * Returns all the reviews of an item.
	 * @param 	itemId	the id of the item
	 * @return			the list of reviews
	 */
	public ArrayList<Review> readByItem(int itemId);
	
	/**
	 * Returns all the reviews that a user has posted.
	 * @param 	userId	the id of the user
	 * @return			the list of reviews
	 */
	public ArrayList<Review> readByUser(int userId);
	
	/**
	 * Returns the review of a user on an item
	 * @param 	userId	the id of the user
	 * @param 	itemId	the id of the item
	 * @return			the review object
	 */
	public Review read(int userId, int itemId);
}
