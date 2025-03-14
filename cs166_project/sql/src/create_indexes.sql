-- Index on FoodOrder.login to speed up lookups when retrieving user orders
-- Helps when the user wants to view their order history
CREATE INDEX idx_foodorder_login ON FoodOrder(login);

-- Helps when the user wants to view their latest order
-- avoids full table scan
CREATE INDEX idx_foodorder_timestamp ON FoodOrder(orderTimestamp DESC);

-- Speed up lookups when retrieving items in an order
CREATE INDEX idx_itemsinorder_orderID ON ItemsInOrder(orderID);

-- Speeds up store lookups when filtering by storeID.
-- Reduces the number of rows scanned when checking available stores
CREATE INDEX idx_store_storeID ON Store(storeID);

