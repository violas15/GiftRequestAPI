package edu.wpi.cs3733.TeamD;

import edu.wpi.cs3733.TeamD.Entities.GR;
import edu.wpi.cs3733.TeamD.Entities.Gift;
import edu.wpi.cs3733.TeamD.Managers.GiftDirectory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    private Database(){
    }

    private static class SingletoneHelper{
        private final static Database INSTANCE = new Database();
    }

    public static Database getInstance(){
        return SingletoneHelper.INSTANCE;
    }


    // the manager managing this database
    static private Connection connection = null; // the database connection

    /** Connect and set up the database
     *
     */
    public static void initDatabase(){

        // Make sure the Java DB driver is installed as a dependency (prints instructions to set up if not installed properly)
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Java DB Driver not found. Add the classpath to your module.");
            System.out.println("For IntelliJ do the following:");
            System.out.println("File | Project Structure, Modules, Dependency tab");
            System.out.println("Add by clicking on the green plus icon on the right of the window");
            System.out.println("Select JARs or directories. Go to the folder where the Java JDK is installed");
            System.out.println("Select the folder java/jdk1.8.xxx/edu.wpi.cs3733.TeamD.Database/lib where xxx is the version.");
            System.out.println("Click OK, compile the code and run it.");
            e.printStackTrace();
            return;
        }

        // Make a connection to the database
        try {
            // substitute your database name for myDB
            connection = DriverManager.getConnection("jdbc:derby:mydb;create=true");
        } catch (SQLException e) {
            System.out.println("Connection failed. Check output console.");
            e.printStackTrace();
            return;
        }

        // UNCOMMENT THE BELOW LINE TO RESET THE DATABASE
        //dropTables();
        createTables();

        // Connection successful!
        System.out.println("Java DB connection established!");
    }

    private static void createTables(){

        // Gift table
        try{
            Statement s = connection.createStatement();
            s.execute("CREATE TABLE gifts (name VARCHAR(200) PRIMARY KEY, cost FLOAT, isFood VARCHAR(5))");
            s.close();
        } catch(SQLException e){
            System.out.println("Could not create gifts table.");
            e.printStackTrace();
        }

        // Employee table
        try{
            Statement s = connection.createStatement();
            s.execute("CREATE TABLE employees (name VARCHAR(100) PRIMARY KEY)");
            s.close();
        } catch(SQLException e){
            System.out.println("Could not create employees table.");
            e.printStackTrace();
        }

        // Gift Request table
        try{
            Statement s = connection.createStatement();
            s.execute("CREATE TABLE giftrequests (grID VARCHAR(50) PRIMARY KEY," +
                    " giftName VARCHAR(200)," +
                    " assignee VARCHAR(100)," +
                    " date DATE," +
                    " time TIME," +
                    "constraint fk_giftName foreign key(giftName) references gifts(name)," +
                    "constraint fk_assignee foreign key(assignee) references employees(name))");
            s.close();
        } catch(SQLException e){
            System.out.println("Could not create gift requests table.");
            e.printStackTrace();
        }
    }

    public static void dropTables(){
        // GR table
        try{
            Statement s = connection.createStatement();
            s.execute("DROP TABLE giftrequests");
            s.close();
        } catch(SQLException e){
            System.out.println("Could not drop gift requests table.");
            //e.printStackTrace();
        }

        // Gift table
        try{
            Statement s = connection.createStatement();
            s.execute("DROP TABLE gifts");
            s.close();
        } catch(SQLException e){
            System.out.println("Could not drop gifts table.");
            //e.printStackTrace();
        }

        // employees table
        try{
            Statement s = connection.createStatement();
            s.execute("DROP TABLE employees");
            s.close();
        } catch(SQLException e){
            System.out.println("Could not drop employees table.");
            //e.printStackTrace();
        }
    }

    public static void close(){
        try{
            connection.close();
        } catch (SQLException e){
            System.out.println("Could not close DB connection.");
            e.printStackTrace();
        }

    }

    public static void insertEmployee(String employee){
        try{
            PreparedStatement ps = connection.prepareStatement("INSERT INTO employees VALUES(?)");
            ps.setString(1, employee);
            ps.execute();
            ps.close();
        } catch(SQLIntegrityConstraintViolationException e){
            System.out.println("Could not insert employee " + employee + " because the employee already exists.");
        } catch(SQLException e){
            System.out.println("Could not insert employee " + employee);
            e.printStackTrace();
        }
    }

    public static void insertGift(Gift g){
        try{
            PreparedStatement ps = connection.prepareStatement("INSERT INTO gifts VALUES(?, ?, ?)");
            ps.setString(1, g.getName());
            ps.setFloat(2, g.getCost());
            ps.setString(3, g.isFood()? "t": "f");
            ps.execute();
            ps.close();
        } catch(SQLIntegrityConstraintViolationException e){
            System.out.println("Could not insert gift " + g.getName() + " because the gift already exists.");
        } catch(SQLException e){
            System.out.println("Could not insert gift " + g.getName());
            e.printStackTrace();
        }
    }

    public static void insertGR(GR gr){
        try{
            PreparedStatement ps = connection.prepareStatement("INSERT INTO giftrequests VALUES(?, ?, ?, ?, ?)");
            ps.setString(1, gr.getGrID());
            ps.setString(2, gr.getGift().getName());
            ps.setString(3, gr.getAssignee());
            ps.setDate(4, gr.getDate());
            ps.setTime(5, gr.getTime());
            ps.execute();
            ps.close();
        } catch(SQLIntegrityConstraintViolationException e){
            System.out.println("Could not insert gift request " + gr.getGrID() + " because the gift request already exists.");
        } catch(SQLException e){
            System.out.println("Could not insert gift request " + gr.getGrID());
            e.printStackTrace();
        }
    }

    public static HashMap<String, Gift> loadGiftDirectory(){
        HashMap<String, Gift> gifts = new HashMap<>();

        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM gifts");
            ResultSet rs = ps.executeQuery();

            String name;
            Float cost;
            boolean isFood;

            while(rs.next()){
                name = rs.getString("name");
                cost = rs.getFloat("cost");
                isFood = rs.getString("isFood").equals("t");

                Gift g = new Gift(name, cost, isFood);
                gifts.put(name, g);
            }

            ps.close();
            rs.close();
        } catch(SQLException e){
            System.out.println("Failed to load gift directory");
            e.printStackTrace();
        }

        return gifts;
    }

    public static List<String> loadEmployees(){
        List<String> employees = new ArrayList<String>();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM employees");
            ResultSet rs = ps.executeQuery();

            String name;

            while(rs.next()){
                name = rs.getString("name");
                employees.add(name);
            }

            ps.close();
            rs.close();
        } catch(SQLException e){
            System.out.println("Failed to load employees list");
            e.printStackTrace();
        }

        return employees;
    }

    public static HashMap<String, GR> loadGRs(GiftDirectory giftDirectory){
        HashMap<String, GR> grs = new HashMap<>();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM giftrequests");
            ResultSet rs = ps.executeQuery();

            String grID, giftName, assignee;
            Date date;
            Time time;

            while(rs.next()){
                grID = rs.getString("grID");
                giftName = rs.getString("giftName");
                assignee = rs.getString("assignee");
                date = rs.getDate("date");
                time = rs.getTime("time");

                GR gr = new GR(grID, giftDirectory.getGift(giftName), assignee, date, time);
                grs.put(grID, gr);
            }

            ps.close();
            rs.close();
        } catch(SQLException e){
            System.out.println("Failed to load gift requests");
            e.printStackTrace();
        }

        return grs;
    }

}
