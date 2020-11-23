package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static Stage window;

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));
        Parent root = loader.load();
        window.setScene(new Scene(root, 300, 150));
        changeStage(primaryStage);
    }

    public static void changeStage(Stage new_stage) {
        window.close();
        window = new_stage;
        window.setTitle("Country Comparator");
        window.setResizable(false);
        window.show();

        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });
    }

    private static void closeProgram() {
        boolean ans = PopUp.init_confirm("Confirm Close", "Are you sure you want to quit?");
        if (ans)
            window.close();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
