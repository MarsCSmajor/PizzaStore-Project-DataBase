/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


 import java.sql.DriverManager;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.io.File;
 import java.io.FileReader;
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.util.List;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.lang.Math;
 import java.util.Scanner;
 import java.util.concurrent.ExecutionException;
 
 import javax.jws.soap.SOAPBinding.Use;
 
 /**
  * This class defines a simple embedded SQL utility class that is designed to
  * work with PostgreSQL JDBC drivers.
  *
  */
 public class PizzaStore {
 
    // reference to physical database connection.
    private Connection _connection = null;
 
    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
                                 new InputStreamReader(System.in));
 
    /**
     * Creates a new instance of PizzaStore
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {
 
       System.out.print("Connecting to database...");
       try{
          // constructs the connection URL
          String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
          System.out.println ("Connection URL: " + url + "\n");
 
          // obtain a physical connection
          this._connection = DriverManager.getConnection(url, user, passwd);
          System.out.println("Done");
       }catch (Exception e){
          System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
          System.out.println("Make sure you started postgres on this machine");
          System.exit(-1);
       }//end catch
    }//end PizzaStore
 
    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate (String sql) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the update instruction
       stmt.executeUpdate (sql);
 
       // close the instruction
       stmt.close ();
    }//end executeUpdate
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and output them to standard out.
       boolean outputHeader = true;
       while (rs.next()){
        if(outputHeader){
          for(int i = 1; i <= numCol; i++){
          System.out.print(rsmd.getColumnName(i) + "\t");
          }
          System.out.println();
          outputHeader = false;
        }
          for (int i=1; i<=numCol; ++i)
             System.out.print (rs.getString (i) + "\t");
          System.out.println ();
          ++rowCount;
       }//end while
       stmt.close();
       return rowCount;
    }//end executeQuery
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the results as
     * a list of records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;
 
       // iterates through the result set and saves the data returned by the query.
       boolean outputHeader = false;
       List<List<String>> result  = new ArrayList<List<String>>();
       while (rs.next()){
         List<String> record = new ArrayList<String>();
       for (int i=1; i<=numCol; ++i)
          record.add(rs.getString (i));
         result.add(record);
       }//end while
       stmt.close ();
       return result;
    }//end executeQueryAndReturnResult
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();
 
        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);
 
        int rowCount = 0;
 
        // iterates through the result set and count nuber of results.
        while (rs.next()){
           rowCount++;
        }//end while
        stmt.close ();
        return rowCount;
    }
 
    /**
     * Method to fetch the last value from sequence. This
     * method issues the query to the DBMS and returns the current
     * value of sequence used for autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
    Statement stmt = this._connection.createStatement ();
 
    ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
    if (rs.next())
       return rs.getInt(1);
    return -1;
    }
 
    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup(){
       try{
          if (this._connection != null){
             this._connection.close ();
          }//end if
       }catch (SQLException e){
          // ignored.
       }//end try
    }//end cleanup
 
    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    public static void main (String[] args) {
       if (args.length != 3) {
          System.err.println (
             "Usage: " +
             "java [-classpath <classpath>] " +
             PizzaStore.class.getName () +
             " <dbname> <port> <user>");
          return;
       }//end if
 
       Greeting();
       PizzaStore esql = null;
       try{
          // use postgres JDBC driver.
          Class.forName ("org.postgresql.Driver").newInstance ();
          // instantiate the PizzaStore object and creates a physical
          // connection.
          String dbname = args[0];
          String dbport = args[1];
          String user = args[2];
          esql = new PizzaStore (dbname, dbport, user, "");
 
          boolean keepon = true;
          while(keepon) {
             // These are sample SQL statements
             System.out.println("MAIN MENU");
             System.out.println("---------");
             System.out.println("1. Create user");
             System.out.println("2. Log in");
             System.out.println("9. < EXIT");
             String authorisedUser = null;
             switch (readChoice()){
                case 1: CreateUser(esql); break;
                case 2: authorisedUser = LogIn(esql); break;
                case 9: keepon = false; break;
                default : System.out.println("Unrecognized choice!"); break;
             }//end switch
             if (authorisedUser != null) {
               boolean usermenu = true;
               while(usermenu) {
                 System.out.println("MAIN MENU");
                 System.out.println("---------");
                 System.out.println("1. View Profile");
                 System.out.println("2. Update Profile");
                 System.out.println("3. View Menu");
                 System.out.println("4. Place Order"); //make sure user specifies which store
                 System.out.println("5. View Full Order ID History");
                 System.out.println("6. View Past 5 Order IDs");
                 System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                 System.out.println("8. View Stores"); 
 
                 //**the following functionalities should only be able to be used by drivers & managers**
                 System.out.println("9. Update Order Status");
 
                 //**the following functionalities should ony be able to be used by managers**
                 System.out.println("10. Update Menu");
                 System.out.println("11. Update User");
 
                 System.out.println(".........................");
                 System.out.println("20. Log out");
                 switch (readChoice()){
                    case 1: viewProfile(esql,authorisedUser); break;
                    case 2: updateProfile(esql,authorisedUser); break;
                    case 3: viewMenu(esql); break;
                    case 4: placeOrder(esql, authorisedUser); break;
                    case 5: viewAllOrders(esql, authorisedUser); break;
                    case 6: viewRecentOrders(esql, authorisedUser); break;
                    case 7: viewOrderInfo(esql); break;
                    case 8: viewStores(esql); break;
                    case 9: updateOrderStatus(esql, authorisedUser); break;
                    case 10: updateMenu(esql, authorisedUser); break;
                    case 11: updateUser(esql,authorisedUser); break;
 
 
 
                    case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                 }
               }
             }
          }//end while
       }catch(Exception e) {
          System.err.println (e.getMessage ());
       }finally{
          // make sure to cleanup the created table and close the connection.
          try{
             if(esql != null) {
                System.out.print("Disconnecting from database...");
                esql.cleanup ();
                System.out.println("Done\n\nBye !");
             }//end if
          }catch (Exception e) {
             // ignored.
          }//end try
       }//end try
    }//end main
 
    public static void Greeting(){
       System.out.println(
          "\n\n*******************************************************\n" +
          "              User Interface      	               \n" +
          "*******************************************************\n");
    }//end Greeting
 
    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
       int input;
       // returns only if a correct value is given.
       do {
          System.out.print("Please make your choice: ");
          try { // read the integer, parse it and break.
             input = Integer.parseInt(in.readLine());
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }//end try
       }while (true);
       return input;
    }//end readChoice
 
    /*
     * Creates a new user
     **/
    public static void CreateUser(PizzaStore esql){
       
 
       try {
          System.out.print("Enter user Login: ");
          String login = in.readLine();
          String Query = "SELECT U.login FROM Users U WHERE U.login = '" + login + "';";
          int rowCount = esql.executeQuery(Query);
          if (rowCount > 0) {
             System.out.print("entered login already exists");
             Thread.sleep(1500);
             Greeting();
          }
          else {
             System.out.print("provide phone number:");
             String phone_num = in.readLine();
 
             System.out.print("Provide a password:");
             String password = in.readLine();
 
             String role = "customer";
             String user_data_query = String.format("INSERT INTO Users (login, password, role, phoneNum) VALUES ('%s', '%s', '%s', '%s');", login,password,role,phone_num);
 
             esql.executeUpdate(user_data_query);
             System.out.println("Added info to data base. Going back to main menu");
             Thread.sleep(1500);
             Greeting();
          }
 
       }catch (Exception e) {
          System.err.println(e.getMessage());
       }
 
    }//end CreateUser
 
    /*
     * Check log in credentials for an existing user
     * @return User login or null is the user does not exist
     **/
    public static String LogIn(PizzaStore esql){ //Login_works
       try {
          System.out.print("Enter Login: ");
          String login = in.readLine();
 
          String Query = "SELECT U.login FROM Users U WHERE U.login = '" + login + "';";
          int rowCount = esql.executeQuery(Query);
 
          if (rowCount == 1){//Meaning if round one
             System.out.print("Enter Password: ");
             String password = in.readLine();
             String password_Query = String.format("SELECT U.password FROM Users U WHERE U.login = '%s' AND U.password = '%s';",login,password);
 
 
             if (esql.executeQuery(password_Query) == 1) {// password matches login
                System.out.println("login Success");
                Thread.sleep(1500);
                return login;
             }
             System.out.println("Incorrect password");
             return null;
             }
          else
          {
             System.out.println("Login was not found");
             return null;
          }
       }catch(Exception e) {
          System.err.println(e.getMessage());
          return null;
       }
       // return null;
    }//end
 
 
 
 // Rest of the functions definition go in here
 
 
   // View Profile
   public static void viewProfile(PizzaStore esql, String User) {
      try {
          
         System.out.println("---USER Profile----");
          
         String Query = "SELECT * FROM Users U WHERE U.login = '" + User + "';";
         List<List<String>> User_profile = esql.executeQueryAndReturnResult(Query);
         System.out.println("User: "+ User_profile.get(0).get(0));
         System.out.println("password: "+ User_profile.get(0).get(1));
         System.out.println("role: "+ User_profile.get(0).get(2));
         System.out.println("favorite Item: "+ User_profile.get(0).get(3));
         System.out.println("phone_number: "+ User_profile.get(0).get(4));
 
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }
 
   // Update Profile
   public static void updateProfile(PizzaStore esql, String User) {
      try {
         System.out.println("Hello "+User+ " what do you want to update");
 
         String u = "SELECT * FROM Users U WHERE U.login = '" + User + "';";
          
         List<List<String>> user_data = esql.executeQueryAndReturnResult(u);
                    
         System.out.println("1. Change password");
         System.out.println("2. Change Phone Number");
         System.out.println("3. Update favorite item");
 
         // System.out.println("0. Done");
 
         int read_input = readChoice();
 
         if (read_input == 1 ) {
            System.out.print("Enter current password: ");
            String input_password = in.readLine();
             
            // Check if the password matches the current password
            if (input_password.equals(user_data.get(0).get(1))) { 
               System.out.print("Enter new password: ");
               input_password = in.readLine();
               String change_password = "UPDATE Users SET password = '" + input_password + "' WHERE login = '"+User+ "';";
               esql.executeUpdate(change_password);
             }
          }
          else if (read_input == 2) {
             System.out.print("Enter current phone Number ie(123-567-9979): ");
             String check_phonenum = in.readLine();
             if (check_phonenum.equals(user_data.get(0).get(4))) {
                System.out.print("Enter new number: ");
                String update = in.readLine();
                String change_phonenum = "UPDATE Users SET phoneNum = '" + update + "' WHERE login = '"+User+ "';";
                esql.executeUpdate(change_phonenum);
             }
          }
          else if (read_input == 3) {
             System.out.print("Enter new favorite item: ");
             String new_favorite_item = in.readLine();
 
             String update_favorite_item = "UPDATE Users SET favoriteItems = '" + new_favorite_item + "' WHERE login = '"+User+ "';";
             esql.executeUpdate(update_favorite_item);
             System.out.println("Update successful");
          }
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }
 
   // View Menu
    public static void viewMenu(PizzaStore esql) {
       try {
          
          System.out.println("[---Menu---]\n");
          List<String> type = Arrays.asList("entree", "sides", "drinks");
 
          for (String i : type){
             System.out.println("[---"+i+"---]");
             String item_type = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%" + i + "%';";
             List<List<String>> execute_query = esql.executeQueryAndReturnResult(item_type);
             for (int k = 0; k < execute_query.size();++k) {
                System.out.println(execute_query.get(k).get(0)+"------------------------$"+execute_query.get(k).get(1));
             }
             System.out.print("\n");
          }
 
          while(true){
             System.out.println("Filter Search By: ");
             System.out.println("1. Drinks");
             System.out.println("2. Sides");
             System.out.println("3. Entree");
             System.out.println("4. Food Items under a Certain Price");
             System.out.println("5. Sort Menu Highest to Lowest Price");
             System.out.println("6. Sort Menu Lowest to Highest Price \n");
             System.out.println("Back To Menu: ");
             System.out.println("8. Back to Menu");
             System.out.println("9. Main Menu\n");
             
             int read_input = readChoice();
             List<List<String>> Output;
             switch (read_input) {
                case 1:
                   System.out.flush();
                   String drinks_query = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%drinks%';";
                   Output = esql.executeQueryAndReturnResult(drinks_query);
                   for(int k = 0; k < Output.size(); ++k) {
                      System.out.println(Output.get(k).get(0)+"------------------------$"+Output.get(k).get(1));
                   }
                   break;
                
                case 2:
                   String sides_query = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%sides%';";
                   Output = esql.executeQueryAndReturnResult(sides_query);
                   for(int k = 0; k < Output.size(); ++k) {
                      System.out.println(Output.get(k).get(0)+"------------------------$"+Output.get(k).get(1));
                   }
                   break;
                
                case 3:
                   String entree_query = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%entree%';";
                   Output = esql.executeQueryAndReturnResult(entree_query);
                   for(int k = 0; k < Output.size(); ++k) {
                      System.out.println(Output.get(k).get(0)+"------------------------$"+Output.get(k).get(1));
                   }                 
                   break;
                
                case 4:
                   System.out.print("Enter a price $ ");
                   String price_in = in.readLine();
                   // in.close();
                   for (String i : type){
                      System.out.println("[---"+i+"---]");
                      String item_type = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%" + i + "%' AND I.price <="+price_in+" ;";
                      List<List<String>> execute_query = esql.executeQueryAndReturnResult(item_type);
                      for (int k = 0; k < execute_query.size();++k) {
                         System.out.println(execute_query.get(k).get(0)+"------------------------$"+execute_query.get(k).get(1));
                      }
                      System.out.print("\n");        
                   }              
                   break;
                
                case 5:
                   for (String i : type){
                      System.out.println("[---"+i+"---]");
                      String Highest_lowest = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%" + i + "%' ORDER BY I.price DESC ;"; // this will return prices from highes to lowest in different types
                      List<List<String>> execute_query = esql.executeQueryAndReturnResult(Highest_lowest);
                      for (int k = 0; k < execute_query.size();++k) {
                         System.out.println(execute_query.get(k).get(0)+"------------------------$"+execute_query.get(k).get(1));
                      }
                      System.out.print("\n");        
                   }
                   break;
                
                case 6: 
                   for (String i : type){
                      System.out.println("[---"+i+"---]");
                      String Lowest_highest = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%" + i + "%' ORDER BY I.price ASC ;";
                      List<List<String>> execute_query = esql.executeQueryAndReturnResult(Lowest_highest);
                      for (int k = 0; k < execute_query.size();++k) {
                         System.out.println(execute_query.get(k).get(0)+"------------------------$"+execute_query.get(k).get(1));
                      }
                      System.out.print("\n");       
                   }
                   break;
                
                case 8:
                   for (String i : type){
                      System.out.println("[---"+i+"---]");
                      String item_type = "SELECT I.itemName, I.price FROM Items I WHERE I.typeOfItem LIKE '%" + i + "%';";
                      List<List<String>> execute_query = esql.executeQueryAndReturnResult(item_type);
                      for (int k = 0; k < execute_query.size();++k) {
                         System.out.println(execute_query.get(k).get(0)+"------------------------$"+execute_query.get(k).get(1));
                      }
                      System.out.print("\n");        
                   }
                   break;
                
                case 9:
                   return;
          
                default:
                   break;
             }
          }
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
    }
 
   // All Roles can place order
    public static void placeOrder(PizzaStore esql, String login) {
    try {
       System.out.print("Enter store ID: ");
       int storeID = Integer.parseInt(in.readLine());
       
       String storeStatusQuery = "SELECT isOpen FROM Store WHERE storeID = " + storeID;
       List<List<String>> storeStatusResult = esql.executeQueryAndReturnResult(storeStatusQuery);
          
       // Check if store is open
       if (storeStatusResult.isEmpty() || !storeStatusResult.get(0).get(0).equalsIgnoreCase("yes")) {
          System.out.println("Cannot place order. The selected store is closed.");
          return;
       }
       
       List<String> items = new ArrayList<>();
       List<Integer> quantities = new ArrayList<>();
       double totalPrice = 0;
       
       while (true) {
          System.out.print("Enter item name (or type 'done' to finish): ");
          String itemName = in.readLine();
          if (itemName.equalsIgnoreCase("done")) break;
          
          System.out.print("Enter quantity: ");
          int quantity = Integer.parseInt(in.readLine());
          
          // Fetch item price
          String priceQuery = "SELECT price FROM Items WHERE itemName = '" + itemName + "'";
          List<List<String>> priceResult = esql.executeQueryAndReturnResult(priceQuery);
          
          if (priceResult.isEmpty()) {
             System.out.println("Item not found. Please enter a valid item.");
             continue;
          }
          
          // Calculate total price
          double price = Double.parseDouble(priceResult.get(0).get(0));
          totalPrice += price * quantity;
          
          items.add(itemName);
          quantities.add(quantity);
       }
       
       // Check if any items were selected
       if (items.isEmpty()) {
          System.out.println("No items selected. Order cancelled.");
          return;
       }
 
       // Generate a new orderID manually
       String getMaxOrderIDQuery = "SELECT COALESCE(MAX(orderID), 0) + 1 FROM FoodOrder";
       List<List<String>> maxOrderIDResult = esql.executeQueryAndReturnResult(getMaxOrderIDQuery);
       int orderID = Integer.parseInt(maxOrderIDResult.get(0).get(0));
 
       // Insert order into FoodOrder table
       String insertOrderQuery = "INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) " +
                                 "VALUES (" + orderID + ", '" + login + "', " + storeID + ", " + totalPrice + ", NOW(), 'incomplete')";
       esql.executeUpdate(insertOrderQuery);
       
       // Insert each item into ItemsInOrder table
       for (int i = 0; i < items.size(); i++) {
          String insertItemQuery = "INSERT INTO ItemsInOrder (orderID, itemName, quantity) VALUES (" + orderID + ", '" + items.get(i) + "', " + quantities.get(i) + ")";
          esql.executeUpdate(insertItemQuery);
       }
       
       System.out.println("Order placed successfully. Total Price: $" + totalPrice + " Order ID: " + orderID);
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
    }
 
    // View all orders for a user
    public static void viewAllOrders(PizzaStore esql, String login) {
       try {
          String query = "SELECT * FROM FoodOrder WHERE login = '" + login + "'";
          List<List<String>> result = esql.executeQueryAndReturnResult(query);
          
          if (result.isEmpty()) {
             System.out.println("No orders found.");
          } else {
             System.out.println("All Orders:");
             for (List<String> row : result) {
                System.out.println(row);
             }
          }
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
    }
 
    // View the 5 most recent orders for a user
    public static void viewRecentOrders(PizzaStore esql, String login) {
       try {
          String query = "SELECT * FROM FoodOrder WHERE login = '" + login + "' ORDER BY orderTimestamp DESC LIMIT 5";
          List<List<String>> result = esql.executeQueryAndReturnResult(query);
          
          if (result.isEmpty()) {
             System.out.println("No recent orders found.");
          } else {
             System.out.println("Recent Orders:");
             for (List<String> row : result) {
                System.out.println(row);
             }
          }
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
    }
 
    // View detailed information about an order
    public static void viewOrderInfo(PizzaStore esql) {
       try {
          System.out.print("Enter Order ID: ");
          int orderID = Integer.parseInt(in.readLine());
          
          // Fetch order details
          String orderQuery = "SELECT orderTimestamp, totalPrice, orderStatus FROM FoodOrder WHERE orderID = " + orderID;
          List<List<String>> orderResult = esql.executeQueryAndReturnResult(orderQuery);
          
          if (orderResult.isEmpty()) {
             System.out.println("Order not found.");
             return;
          }
          
          System.out.println("Order Details:");
          System.out.println("Timestamp: " + orderResult.get(0).get(0));
          System.out.println("Total Price: $" + orderResult.get(0).get(1));
          System.out.println("Status: " + orderResult.get(0).get(2));
          
          // Fetch order items
          String itemsQuery = "SELECT itemName, quantity FROM ItemsInOrder WHERE orderID = " + orderID;
          List<List<String>> itemsResult = esql.executeQueryAndReturnResult(itemsQuery);
          
          System.out.println("Items in Order:");
          for (List<String> item : itemsResult) {
             System.out.println("Item: " + item.get(0) + ", Quantity: " + item.get(1));
          }
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
    }
 
    // View all stores
    public static void viewStores(PizzaStore esql) {
       try {
          // Display all stores with full information
          String storeQuery = "SELECT storeID, address, city, state, isOpen, reviewScore FROM Store";
          List<List<String>> stores = esql.executeQueryAndReturnResult(storeQuery);
          
          if (stores.isEmpty()) {
             System.out.println("No stores available.");
             return;
          }
          
          System.out.println("All Stores:");
          for (List<String> store : stores) {
             System.out.println("Store ID: " + store.get(0) + ", Address: " + store.get(1) + ", City: " + store.get(2) + ", State: " + store.get(3) + ", Open Status: " + store.get(4) + ", Review Score: " + store.get(5));
          }
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
    }
 
    // Update order status (only driver and manager)
    public static void updateOrderStatus(PizzaStore esql, String login) {
       try {
          // Check if the user is a driver or manager
          String roleQuery = "SELECT role FROM Users WHERE login = '" + login + "'";
          List<List<String>> roleResult = esql.executeQueryAndReturnResult(roleQuery);
          
          if (roleResult.isEmpty()) {
             System.out.println("User not found.");
             return;
          }
          
          String role = roleResult.get(0).get(0).trim();
          if (!role.equalsIgnoreCase("driver") && !role.equalsIgnoreCase("manager")) {
             System.out.println("Access denied. Only drivers and managers can update order status.");
             return;
          }
          
          System.out.print("Enter Order ID: ");
          int orderID = Integer.parseInt(in.readLine());
          
          // Check if the order exists
          String orderQuery = "SELECT orderStatus FROM FoodOrder WHERE orderID = " + orderID;
          List<List<String>> orderResult = esql.executeQueryAndReturnResult(orderQuery);
          
          if (orderResult.isEmpty()) {
             System.out.println("Order not found.");
             return;
          }
          
          System.out.print("Enter new status (incomplete or complete): ");
          String newStatus = in.readLine().trim();
          
          if (!newStatus.equalsIgnoreCase("incomplete") && !newStatus.equalsIgnoreCase("complete")) {
             System.out.println("Invalid status. Status must be 'incomplete' or 'complete'.");
             return;
          }
          
          String updateQuery = "UPDATE FoodOrder SET orderStatus = '" + newStatus + "' WHERE orderID = " + orderID;
          esql.executeUpdate(updateQuery);
          
          System.out.println("Order status updated successfully.");
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
    }
 
    public static void updateMenu(PizzaStore esql, String User) {
       try {
          String u = "SELECT U.role FROM USERS U WHERE U.login = '" + User + "' AND U.role = 'manager';";
          if(esql.executeQuery(u) == 1) {
             System.out.println("Select the following options");
             System.out.println("1. Update Item");
             System.out.println("2. Add new item to menu");
             System.out.println("3. Go to Main Menu");
 
             int read_choice = readChoice();
 
             switch (read_choice) {
                case 1:
                   System.out.println("Enter an item name");
                   String select_item = in.readLine();
                   String item_query = "SELECT * FROM Items I WHERE I.itemName ='" +select_item+ "';";
 
                   if(esql.executeQuery(item_query) == 1) {
                      List<List<String>> item_info = esql.executeQueryAndReturnResult(item_query);
                      System.out.println("Item: "+item_info.get(0).get(0));
                      System.out.println("ingredients: "+item_info.get(0).get(1));
                      System.out.println("Type of item: "+item_info.get(0).get(2));
                      System.out.println("price: "+item_info.get(0).get(3));
                      System.out.println("Description: "+item_info.get(0).get(4));
 
                      System.out.println("Selection option: Item (1), ingredients (2), Type (3), Price (4), Description (5)");
 
                      int update_item = readChoice();
                      String query;
                      String input_type;
                      switch (update_item) {
                         case 1:
                            System.out.print("Rename Item: "); 
                            input_type = in.readLine();
                           
                           String values = String.format("INSERT INTO Items (itemName, ingredients, typeOfItem, price, description) " + "VALUES ('%s', '%s', '%s', %s, '%s');", input_type, item_info.get(0).get(1), item_info.get(0).get(2), item_info.get(0).get(3), item_info.get(0).get(4) ); 
                           String DeleteOldItem = "DELETE FROM Items WHERE itemName = '"+item_info.get(0).get(0) + "';";
                           
                            try {
                              
                                 esql.executeUpdate(values);
                                 esql.executeUpdate(DeleteOldItem);
                            } catch (Exception e) {
                               System.err.println(e.getMessage());
                            }
                            break;
 
                         case 2:
                            System.out.print("ingredients: ");
                            input_type = in.readLine();
                            query = "UPDATE Items SET ingredients = '" + input_type + "' WHERE itemName = '"+select_item+ "';";
                            try {
                               esql.executeUpdate(query);
                            } catch (Exception e) {
                               System.err.println(e.getMessage());
                            }
                            break;
                         
                         case 3:
                            System.out.print("Type of Item: "); 
                            input_type = in.readLine();
                            query = "UPDATE Items SET typeOfItem = '" + input_type + "' WHERE itemName = '"+select_item+ "';";
                            try {
                               esql.executeUpdate(query);
                            } catch (Exception e) {
                               System.err.println(e.getMessage());
                            }
                            break;
                         
                         case 4:
                            System.out.print("Edit Price: ");
                            input_type = in.readLine();
                            query = "UPDATE Items SET price = " + input_type + " WHERE itemName = '"+select_item+ "';";
                            try {
                               esql.executeUpdate(query);
                            } catch (Exception e) {
                               System.err.println(e.getMessage());
                            }
                            break;
                         
                         case 5:
                            System.out.print("Enter Description of item: ");
                            input_type = in.readLine();
                            query = "UPDATE Items SET description = '" + input_type + "' WHERE itemName = '"+select_item+ "';";
                            try {
                               esql.executeUpdate(query);
                            } catch (Exception e) {
                               System.err.println(e.getMessage());
                            }
                            break;
        
                         default:
                            break;
                      }
                   }
                   else {
                      throw new Exception("Item Not Found");
                   }        
                   break;
                
                case 2:
                   System.out.print("Name of Item: ");
                   String new_item = in.readLine();
                   String check_item_unique = "SELECT * FROM Items WHERE itemName = '"+ new_item +"';";
                   if (esql.executeQuery(check_item_unique) == 0) { // is unique and not found
                      
                      System.out.print("Enter ingrediants: ");
                      String ingredients = in.readLine();
                      System.out.print("Enter type of item: ");
                      String type = in.readLine();
                      if (type.equals("entree") || type.equals("drinks") || type.equals("sides") ) {
                         System.out.print("Enter Price: ");
                         String price = in.readLine();
                         System.out.print("Enter Description: ");
                         String description = in.readLine();
                         String insert_Item_Query = "INSERT INTO Items (itemName, \"ingredients\", typeOfItem, price, \"description\") " + "VALUES ('" + new_item + "', '" + ingredients + "', '" + type + "', " + price + ", '" + description + "');";
                         esql.executeUpdate(insert_Item_Query);
                         System.out.println("Added new item to data base");
                      }
                      else {
                         throw new Exception("Invalid type of Item");
                      }
                   }
                   else {
                      throw new Exception("Item exists in data base");
                   }
                   break;
                
                case 3:
                   return;
             
                default:
                   break;
             }
 
 
 
 
          }
          else {
             throw new Exception("Access Denied. Only managers can update the Menu.");
          }
 
       } catch (Exception e) {
          System.err.println(e.getMessage());
       }
 
    }
 
   public static void updateUser(PizzaStore esql, String User) {
    try {
        // Ensure only manager can update users
        String managerCheck = "SELECT * FROM Users WHERE login = '" + User + "' AND role = 'manager';";
        if (esql.executeQuery(managerCheck) != 1) {
            throw new Exception("Access Denied. Only managers can update a user.");
        }

        System.out.println("Hello Manager, select an option:");
        System.out.println("1. Edit a User's login");
        System.out.println("2. Edit a User's Role");
        int choice = readChoice();

        if (choice == 1) {
            System.out.print("Enter the current login name: ");
            String oldLogin = in.readLine();

            // Check if user exists
            String userCheck = "SELECT * FROM Users WHERE login = '" + oldLogin + "'";
            if (esql.executeQuery(userCheck) == 0) {
                throw new Exception("User '" + oldLogin + "' does not exist.");
            }

            System.out.print("Enter the new login name: ");
            String newLogin = in.readLine();

            // Check if new login already exists
            String checkNewLogin = "SELECT * FROM Users WHERE login = '" + newLogin + "'";
            if (esql.executeQuery(checkNewLogin) != 0) {
                throw new Exception("New login '" + newLogin + "' already exists. Choose another.");
            }

            try {
                // Disable foreign key constraint
                esql.executeUpdate("ALTER TABLE FoodOrder DROP CONSTRAINT foodorder_login_fkey;");

                // Update Users.login and FoodOrder.login
                String updateUserQuery = "UPDATE Users SET login = '" + newLogin + "' WHERE login = '" + oldLogin + "'";
                esql.executeUpdate(updateUserQuery);

                String updateOrdersQuery = "UPDATE FoodOrder SET login = '" + newLogin + "' WHERE login = '" + oldLogin + "'";
                esql.executeUpdate(updateOrdersQuery);

                // Re-add foreign key constraint
                esql.executeUpdate("ALTER TABLE FoodOrder ADD CONSTRAINT foodorder_login_fkey FOREIGN KEY (login) REFERENCES Users(login) ON DELETE CASCADE;");

                System.out.println("User login updated successfully.");
            } catch (Exception e) {
                throw new Exception("Error updating login: " + e.getMessage());
            }
        }

        else if (choice == 2) {
            System.out.print("Enter the user login name: ");
            String userLogin = in.readLine();
            String userCheck = "SELECT login FROM Users WHERE login = '" + userLogin + "'";
            
            if (esql.executeQuery(userCheck) == 0) {
                throw new Exception("User '" + userLogin + "' does not exist.");
            }

            System.out.print("Enter new role: ");
            String newRole = in.readLine();
            if (newRole.equals("manager") || newRole.equals("driver") || newRole.equals("customer")) {
                String updateRoleQuery = "UPDATE Users SET role = '" + newRole + "' WHERE login = '" + userLogin + "'";
                esql.executeUpdate(updateRoleQuery);
                System.out.println("User role updated successfully.");
            } else {
                throw new Exception("Invalid role. Choose from: 'manager', 'driver', or 'customer'.");
            }
        }

        else {
            throw new Exception("Invalid selection. Returning to main menu.");
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
}

 }//end PizzaStore