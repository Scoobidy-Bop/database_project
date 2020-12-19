/*
    This file is the controller for the edit country window.
 */

package sample;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
/*
This class is the main driver for the window
 */
public class EditController {

    // all these textfields in the window to get the user entry
    public TextField abbr;
    public TextField country_name;
    public TextField year;
    public TextField pop_city;
    public TextField capital;
    public TextField landmass;
    public TextField population;
    public TextField language;
    public TextField pop_density;
    public TextField gdp;
    public TextField currency;
    public TextField pwr_consumption;
    public TextField transport;

    // the buttons for confirm and cancel
    public Button confirmButton;
    public Button cancelButton;

    private static Stage window;

    // the variables needed for the connection and statements
    Connection conn;
    Statement st;
    PreparedStatement pst;

    // called when the user presses the confirm button
    public void confirmPressed(){
        if(checkFilled()) { // checks all textfields filled in
            getConnection();
            if (countryExists()) { // checks if the country exists
                editCountry(); // if it does then it updates the country
            } else {
                addNewCountry(); // if it doesn't it adds it
            }
            try { // closes connection
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            cancelPressed(); // calls this method to close the window
        }
        else {
            PopUp.init_error("ERROR: Please enter all values");
        }
    }

    // this function is for when the user presses the cancel button and the window closes
    public void cancelPressed(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // if country doesn't exists, adds new country
    public void addNewCountry(){
        // all insert statements
        String insert = "INSERT INTO country VALUES (?, ?, ?, ?, ?, ?)";
        String insertPop = "INSERT INTO country_population VALUES (?, ?, ?, ?, ?)";
        String insertEcon = "INSERT INTO economy VALUES (?, ?, ?, ? ,?, ?)";
        // get prepared statement ready and enter it with the values the user has entered
        try{
            pst = conn.prepareStatement(insert);
            pst.setString(1, getAbbr());
            pst.setInt(2, getYear());
            pst.setString(3, getCountryName());
            pst.setString(4, getPop_City());
            pst.setString(5, getCapital());
            pst.setInt(6, getLandMass());
            pst.executeUpdate();
            pst.close();

            pst = conn.prepareStatement(insertPop);
            pst.setString(1, getAbbr());
            pst.setInt(2, getYear());
            pst.setInt(3, getPopulation());
            pst.setString(4, getLanguage());
            pst.setDouble(5, getPop_Density());
            pst.executeUpdate();
            pst.close();

            pst = conn.prepareStatement(insertEcon);
            pst.setString(1, getAbbr());
            pst.setInt(2, getYear());
            pst.setDouble(3, getGDP());
            pst.setString(4, getCurrency());
            pst.setDouble(5, getPWR());
            pst.setInt(6, getTransport());
            pst.executeUpdate();
            pst.close();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    // if country does exist, update the country's data
    public void editCountry(){
        String update = "UPDATE country SET country_name = ?, most_populous_city = ?, capital = ?, land_mass = ? WHERE abbr = ? AND year = ?";
        String updatePop = "UPDATE country_population SET pop_size = ?, country_language = ?, pop_density = ? WHERE abbr = ? AND year = ?";
        String updateEcon = "UPDATE economy SET gdp = ?, currency = ?, power_consumption_per_capita = ?, passenger_air_transport = ? WHERE abbr = ? AND year = ?";
        try {
            pst = conn.prepareStatement(update);
            pst.setString(1, getCountryName());
            pst.setString(2, getPop_City());
            pst.setString(3, getCapital());
            pst.setInt(4, getLandMass());
            pst.setString(5, getAbbr());
            pst.setInt(6, getYear());
            pst.executeUpdate();
            pst.close();

            pst = conn.prepareStatement(updatePop);
            pst.setInt(1, getPopulation());
            pst.setString(2, getLanguage());
            pst.setDouble(3, getPop_Density());
            pst.setString(4, getAbbr());
            pst.setInt(5, getYear());
            pst.executeUpdate();
            pst.close();

            pst = conn.prepareStatement(updateEcon);
            pst.setDouble(1, getGDP());
            pst.setString(2, getCurrency());
            pst.setDouble(3, getPWR());
            pst.setInt(4, getTransport());
            pst.setString(5, getAbbr());
            pst.setInt(6, getYear());
            pst.executeUpdate();
            pst.close();


        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // checks if country already exists in database
    public boolean countryExists(){
        boolean doesExist = false;
        try{
            String preparedStatement = "SELECT abbr, year FROM country WHERE abbr = ? AND year = ?";
            pst = conn.prepareStatement(preparedStatement);
            pst.setString(1, getAbbr());
            pst.setInt(2, getYear());
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                doesExist = true;
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doesExist;
    }

    // this function gets the connection to the database with the credentials
    public void getConnection(){
        try {
            String[] creds = GetSQLInfo.getCredentials();
            String usr = creds[0];
            String pwd = creds[1];
            String url = GetSQLInfo.getUrlConnect();
            conn = DriverManager.getConnection(url, usr, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this function checks if the textboxes are filled in by user
    public boolean checkFilled(){
        if(getAbbr().length() > 0 && getCountryName().length() > 0 && year.getText().length() > 0 &&
                getPop_City().length() > 0 && getCapital().length() > 0 && landmass.getText().length() > 0 &&
                population.getText().length() > 0 && getLanguage().length() > 0 && pop_density.getText().length() > 0
                && gdp.getText().length() > 0 && getCurrency().length() > 0 && pwr_consumption.getText().length() > 0 && transport.getText().length() > 0){
            return  true;
        }
        return  false;
    }

    // getter functions to get the values to add to database
    public String getAbbr(){
        return abbr.getText();
    }

    public String getCountryName(){
        return country_name.getText();
    }

    public int getYear() {
        int intYear = Integer.parseInt(year.getText());
        return intYear;
    }

    public String getPop_City() {
        return pop_city.getText();
    }

    public String getCapital() {
        return capital.getText();
    }

    public int getLandMass() {
        int landMassInt = Integer.parseInt(landmass.getText());
        return landMassInt;
    }

    public int getPopulation() {
        return Integer.parseInt(population.getText());
    }
    public String getLanguage() {
        return language.getText();
    }
    public double getPop_Density() {
        return Double.parseDouble(pop_density.getText());
    }
    public double getGDP() {
        return Double.parseDouble(gdp.getText());
    }
    public String getCurrency() {
        return currency.getText();
    }
    public double getPWR(){
        return Double.parseDouble(pwr_consumption.getText());
    }
    public int getTransport() {
        return Integer.parseInt(transport.getText());
    }
}
