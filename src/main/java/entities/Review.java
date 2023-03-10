package entities;

/**
 * Class that models a review = item + user.
 */
public class Review {
	
	private int id;
	private int rating;
	private String title;
	private Item item;
	private User user;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
