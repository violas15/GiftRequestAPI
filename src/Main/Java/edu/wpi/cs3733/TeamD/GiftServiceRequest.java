package edu.wpi.cs3733.TeamD;

import edu.wpi.cs3733.TeamD.Managers.GiftRequestManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GiftServiceRequest {

    private static GiftRequestManager GRM;

    private static int windowWidth;
    private static int windowLength;

    private static boolean adminEnabled = true;
    private static String destNode = "";

    private static List<String> locations = new ArrayList<>();


    public GiftServiceRequest(){
        Database.getInstance().initDatabase();
        GRM = new GiftRequestManager();
    }

    public static GiftRequestManager getGRM(){
        return GRM;
    }

    public static void disableAdmin(){
        GiftServiceRequest.adminEnabled = false;
    }

    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) throws ServiceException{

        this.windowWidth = windowWidth;
        this.windowLength = windowLength;
        destNode = destNodeID;

        /*
        GRM.getGiftDirectory().addGift("Snake", (float)19.99, false);
        GRM.getGiftDirectory().addGift("Plane", (float)10.00, false);
        GRM.getGiftDirectory().addGift("Chocolates", (float)1.00, true);
        GRM.getEmployeeList().addEmployee("T1", "Trevor");
        GRM.getEmployeeList().addEmployee("D1","Damon");
        GRM.getEmployeeList().addEmployee("J1","Jess");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        GRM.addGRWDATE(GRM.getGiftDirectory().getGifts().get(1), "Here", new Date(c.getTime().getTime()));
        c = Calendar.getInstance();
        c.add(Calendar.DATE, -2);
        GRM.addGRWDATE(GRM.getGiftDirectory().getGifts().get(2), "Here", new Date(c.getTime().getTime()));
        c = Calendar.getInstance();
        c.add(Calendar.DATE, -3);
        GRM.addGRWDATE(GRM.getGiftDirectory().getGifts().get(1), "Here", new Date(c.getTime().getTime()));
        c = Calendar.getInstance();
        c.add(Calendar.DATE, -4);
        GRM.addGRWDATE(GRM.getGiftDirectory().getGifts().get(0), "Here", new Date(c.getTime().getTime()));
        */


        try{
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GiftRequestScreen.fxml"));

            Scene scene = new Scene(root, windowWidth, windowLength);
            if(cssPath != null){
                scene.getStylesheets().add(cssPath);
            }

            stage.setTitle("Gift Request");
            stage.setX(xcoord);
            stage.setY(ycoord);
            stage.setScene(scene);
            stage.show();

        }
        catch(IOException e){
            System.out.println("Could not load Gift Request screen.");
            e.printStackTrace();
            throw new ServiceException("Couldnt load main screen.");
        }

    }

    public void importLocations(List<String> locations){
        GiftServiceRequest.locations = locations;
    }

    public static List<String> getLocations(){
        return locations;
    }

    public static boolean getAdmin(){
        return adminEnabled;
    }

    public static String getDestNode(){
        return destNode;
    }

    public static int getWindowWidth(){
        return windowWidth;
    }

    public static int getWindowLength(){
        return windowLength;
    }
}
