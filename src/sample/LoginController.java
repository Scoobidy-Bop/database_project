package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LoginController {

    private boolean editor;
    static private Stage window;

    public TextField first_name;
    public TextField last_name;

    public void initialize(Stage primaryStage) throws Exception {
        window = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));
        Parent root = loader.load();
        window.setScene(new Scene(root, 300, 150));
        Main.stage_settings(primaryStage);
        window.show();
    }


    public void log_in() throws Exception {
        boolean ret = isName(first_name, last_name);
        if (ret) {
            String first = first_name.getText();
            String last = last_name.getText();
            editor = isEditor(first, last);
            if (editor) {
                System.out.println(first + " " + last + " is an editor!");
            } else {
                System.out.println(first + " " + last + " is not an editor");
            }
            shutdown();
        } else {
            PopUp.init_error("ERROR: Illegal name entered");
        }
    }


    private boolean isName(TextField first, TextField last) {
        String first_name = first.getText();
        String last_name = last.getText();
        return first_name.matches("[a-zA-Z]+") && last_name.matches("[a-zA-Z]+");
    }


    private boolean isEditor(String first, String last) throws Exception{
        /*
            We could not figure out config file at this point in time, so we had to hard coded the login info into a
            separate file. If you want to access the database that we're pulling from, running the source file
            "CPSC_project4.sql" on the project github will create the tables that this application will pull from. We
            want to create a better way to do this, but we're not sure how.
         */

        String[] creds = GetSQLInfo.getCredentials();
        String usr = creds[0];
        String pwd = creds[1];
        String url = GetSQLInfo.getUrlConnect();


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


    public void shutdown() {
        SearchController sc = new SearchController();
        sc.initialize(editor);
        window.close();
    }

}
