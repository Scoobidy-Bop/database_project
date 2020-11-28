package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


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
                System.out.println(first + " " + last + " is not an editor :(");
            }
            String resource = "Search.fxml";
            set_search_stage(resource);
        } else {
            PopUp.init_error("ERROR: Illegal name entered");
        }
    }

    private void set_search_stage(String ref) {
        try {
            Parent root1 = FXMLLoader.load(getClass().getResource(ref));
            Stage new_stage = new Stage();
            new_stage.setScene(new Scene(root1, 1000, 800));
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
//        Properties prop = new Properties();
//        FileInputStream in = new FileInputStream("./config.properties");
//
//        String dab = "nkirsch_DB";
//        String host = prop.getProperty("host");
//        String usr = prop.getProperty("user");
//        String pwd = prop.getProperty("password");
//        String url = "jdbc:mysql://" + host + "/" + dab;

        String url = "jdbc:mysql://cps-database.gonzaga.edu/nkirsch_DB";
        String usr = "nkirsch";
        String pwd = "nkirsch21767533";

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
