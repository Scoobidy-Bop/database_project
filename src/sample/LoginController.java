package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;


public class LoginController {

    public TextField first_name;
    public TextField last_name;

    @FXML
    private Button login_button;

    public void log_in() throws Exception {
        boolean ret = isName(first_name, last_name);
        if (ret) {
            String first = first_name.getText();
            String last = last_name.getText();
            boolean val = isEditor(first, last);
            if (val) {
                System.out.println(first + " " + last + " is an editor!");
            } else {
                System.out.println(first + " " + last + " is not an editor");
            }
            String resource = "Search.fxml";
            set_search_stage(resource, val);
        } else {
            PopUp.init_error("ERROR: Illegal name entered");
        }
    }

    private void set_search_stage(String ref, boolean val) {
        try {
            Parent root1 = FXMLLoader.load(getClass().getResource(ref));
            Stage new_stage = new Stage();
            Scene searchScene = new Scene(root1, 1000, 800);
            searchScene.setUserData(val);
            new_stage.setScene(searchScene);
            Main.changeStage(new_stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isName(TextField first, TextField last) {
        String first_name = first.getText();
        String last_name = last.getText();
        return first_name.matches("[a-zA-Z]+") && last_name.matches("[a-zA-Z]+");
    }


    private boolean isEditor(String first, String last) throws Exception{
        /*
            We could not figure out config file at this point in time, so we had to hard code lof in info into the file
            (yes this is very dumb). If you want to access the database that we're pulling from, running the source file
            "CPSC_project4.sql" on the project github will create the tables that this application will pull from. We
            want to create a better way to do this, but we're not sure if there is a better way.
         */

        String db = "";
        String sett = "?serverTimezone=UTC";
        String domain = "cps-database.gonzaga.edu";

        String url = "jdbc:mysql://" + domain + "/" + db + sett;
        String usr = "username";
        String pwd = "password";

        try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
            String q = "SELECT * FROM editor WHERE first_name = ? AND last_name = ?";
            PreparedStatement p_stmt = cn.prepareStatement(q);
            p_stmt.setString(1, first);
            p_stmt.setString(2, last);
            ResultSet rs = p_stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
