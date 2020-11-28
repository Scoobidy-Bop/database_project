package sample;

import javafx.scene.control.TextField;
import java.sql.*;

public class EditController {

    public TextField abbr;
    public TextField country_name;

    Connection conn;
    Statement st;
    PreparedStatement pst;

    public void confirmPressed(){
        if(countryExists()){
            editCountry();
        }
        else{
            addNewCountry();
        }
    }

    // if country doesn't exists, adds new country
    public void addNewCountry(){
        String insert = "INSERT INTO country VALUES (?, ?, ?, ?, ?, ?)";
        // get prepared statement ready and enter it with the values the user has entered
    }

    // if country does exist, update the country's data
    public void editCountry(){

    }

    // checks if country already exists in database
    public boolean countryExists(){

        return true;
    }

    public void getConnection(){

    }

    public String getAbbr(){
        return abbr.getText();
    }

    public String getCountryName(){
        return country_name.getText();
    }
}
