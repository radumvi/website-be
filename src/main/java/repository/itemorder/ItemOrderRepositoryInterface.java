package repository.itemorder;

public interface ItemOrderRepositoryInterface {
	/**
	 * Add an items to an order.
	 * @param 	orderId		the id of the order in which to add the item
	 * @param 	itemId		the item's id
	 * @param 	count		the amount of items in the order
	 * @return				true, if the operation was successful
	 */
	public boolean add(int orderId, int itemId, int count);
}
