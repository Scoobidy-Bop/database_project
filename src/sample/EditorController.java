/*
this class if the controller for the edit editors window
 */

package sample;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;

// this class is the main driver for the class
public class EditorController {

    private static Stage window;

    // variables needed for the connection and statements

    Connection conn;
    PreparedStatement preparedStatement;

    // textfields for the names
    public TextField firstName;
    public TextField lastName;

    // the buttons the user presses
    public Button cancel_button;
    public Button add_button;
    public Button delete_button;

    // this function is called when the user presses the cancel button and the window closes
    public void cancelAction(){
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }

    // this function is called when the user presses the add button for adding the editor to the database
    public void addAction(){
        // gets the connection
        getConnection();
        if(!checkNamesExist()){ // checks if the name exists in database, if not then adds editor
            System.out.println("adding editor " + getFirstName() + ", " + getLastName());
            addEditor();
        } else {
            PopUp.init_error("ATTENTION: " + getFirstName() + " " + getLastName() + " is already registered as an editor");
        }
        try {
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // this function is called when the user pushes the delete button
    public void deleteAction(){
        // asks if the user really wants to delete the entry
        boolean ans = PopUp.init_confirm("WARNING", "Are you sure you want delete this editor if exists?");
        if (ans) {
            System.out.println("Deleted editor");
            getConnection(); // gets connection
            if (checkNamesExist()) { // check if it exists, if it does it deletess
                deleteEditor();
            }
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // this function creates the statement and deletes the name from the database
    public void deleteEditor(){
        String deleteStatement = "DELETE FROM editor WHERE first_name = ? AND last_name = ?";
        try {
            preparedStatement = conn.prepareStatement(deleteStatement);
            preparedStatement.setString(1, getFirstName());
            preparedStatement.setString(2, getLastName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // this function creates the statement to insert the name into the database
    public void addEditor(){
        String query = "INSERT INTO editor VALUES (?, ?)";
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, getFirstName());
            preparedStatement.setString(2, getLastName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("inserted editor");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // this function checks if the name actually exists in the database
    public boolean checkNamesExist(){
        boolean doesExist = false;
        String query = "SELECT * FROM editor WHERE first_name = ? AND last_name = ?";
        try {
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, getFirstName());
            preparedStatement.setString(2, getLastName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                doesExist = true;
            }
            preparedStatement.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        return doesExist;
    }

    // checks if the name is entered in correct characters
    private boolean isName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    // getters for the first and last name user entered
    public String getFirstName(){
        return firstName.getText();
    }

    public String getLastName(){
        return lastName.getText();
    }

    // this function gets the connection to the database
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
}
