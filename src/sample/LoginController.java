package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;

public class LoginController {

    public Button login_but;
    public TextField first_name;
    public TextField last_name;

    public void log_in() {
        boolean ret = isName(first_name, last_name);
        if (ret) {
            try {
//                window.setScene(new Scene(root, 300, 150));
                Parent root1 = FXMLLoader.load(getClass().getResource("Search.fxml"));
                Stage new_stage = new Stage();
                new_stage.setScene(new Scene(root1, 1000, 800));
                Main.changeStage(new_stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            PopUp.init_error("ERROR: Illegal name entered");
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
