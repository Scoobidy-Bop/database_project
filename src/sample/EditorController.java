package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.table.TableColumn;
import java.sql.*;

public class EditorController {

    private static Stage window;

    Connection conn;
    Statement st;
    PreparedStatement preparedStatement;

    public TextField firstName;
    public TextField lastName;

    public Button cancel_button;
    public Button add_button;
    public Button delete_button;

    public void cancelAction(){
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }

    public void addAction(){
        getConnection();
        System.out.println("button pressed");
        if(!checkNamesExist()){
            System.out.println("adding editor " + getFirstName() + ", " + getLastName());
            addEditor();
        }
        try {
            conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteAction(){
        boolean ans = PopUp.init_confirm("WARNING", "Are you sure you want delete this editor if exists?");
        if (ans) {
            System.out.println("Deleted selected countries");
            getConnection();
            if (checkNamesExist()) {
                deleteEditor();
            }
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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

    private boolean isName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public String getFirstName(){

        return firstName.getText();
    }

    public String getLastName(){
        return lastName.getText();
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
}
