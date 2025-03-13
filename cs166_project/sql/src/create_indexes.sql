-- Index for FoodOrder.login (it speeds up user-related queries)
CREATE INDEX idx_foodorder_login ON FoodOrder(login);

-- Index for FoodOrder.storeID (it speeds up store lookups since there are 1000 stores)
CREATE INDEX idx_foodorder_storeID ON FoodOrder(storeID);

-- Index for ItemsInOrder.orderID (it speeds up order-item lookups)
CREATE INDEX idx_itemsinorder_orderID ON ItemsInOrder(orderID);
