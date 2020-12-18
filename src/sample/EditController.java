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

public class EditController {

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

    public Button confirmButton;
    public Button cancelButton;


    private static Stage window;

    Connection conn;
    Statement st;
    PreparedStatement pst;

    // don't use
    private void initItems() {
        abbr = (TextField) window.getScene().lookup("#abbr");
        country_name = (TextField) window.getScene().lookup("#country_name");
        year = (TextField) window.getScene().lookup("#year");
        pop_city = (TextField) window.getScene().lookup("#pop_city");
        capital = (TextField) window.getScene().lookup("#capital");
        landmass = (TextField) window.getScene().lookup("#landmass");
        population = (TextField) window.getScene().lookup("#population");
        language = (TextField) window.getScene().lookup("#language");
        pop_density = (TextField) window.getScene().lookup("#po_density");
        gdp = (TextField) window.getScene().lookup("#gdp");
        currency = (TextField) window.getScene().lookup("#currency");
        pwr_consumption = (TextField) window.getScene().lookup("#pwr_consumption");
        transport = (TextField) window.getScene().lookup("#transport");
        confirmButton = (Button) window.getScene().lookup("#confirmButton");
        cancelButton = (Button) window.getScene().lookup("#cancelButton");
    }

    public void confirmPressed(){
        getConnection();
        if (countryExists()) {
            editCountry();
        } else {
            addNewCountry();
        }
        try{
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        cancelPressed();

    }

    public void cancelPressed(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    // if country doesn't exists, adds new country
    public void addNewCountry(){
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
