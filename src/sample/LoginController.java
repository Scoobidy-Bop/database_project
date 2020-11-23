package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;

public class LoginController {

    public TextField first_name;
    public TextField last_name;

    public void e_log_in() {
        boolean ret = isName(first_name, last_name);
        if (ret) {
            String resource = "Search.fxml";
            set_search_stage(resource);
        } else {
            PopUp.init_error("ERROR: Illegal name entered");
        }
    }

    public void log_in() {
        boolean ret = isName(first_name, last_name);
        if (ret) {
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


    /*
     * Function to check that t
     */
    private boolean isName(TextField first, TextField last) {
        String first_name = first.getText();
        String last_name = last.getText();
        return first_name.matches("[a-zA-Z]+") && last_name.matches("[a-zA-Z]+");
    }

}
