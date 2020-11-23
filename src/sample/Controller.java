package sample;

import com.sun.jndi.toolkit.ctx.StringHeadTail;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Controller {

    public Button login_but;
    public TextField first_name;
    public TextField last_name;

    public void log_in() {
        boolean ret = isName(first_name, last_name);
        if (ret) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("search.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage new_stage = new Stage();
                new_stage.setScene(new Scene(root1));
                Main.changeStage(new_stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            PopUp.init_error("ERROR: Illegal name entered");
        }
    }



    private boolean isName(TextField first, TextField last) {
        String first_name = first.getText();
        String last_name = last.getText();
        if (first_name.matches("[a-zA-Z]+") && last_name.matches("[a-zA-Z]+")) {
            return true;
        } else {
            return false;
        }
    }

}
