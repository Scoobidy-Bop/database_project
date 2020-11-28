package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;

import javax.swing.*;

public class PopUp {

    static boolean answer;

    public static boolean init_confirm(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        label.setWrapText(true);

        //Two Buttons
        Button yesButton = new Button("Yes");
        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        Button noButton = new Button("No");
        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        HBox internal = new HBox(10);
        internal.setAlignment(Pos.CENTER);
        internal.getChildren().addAll(yesButton, noButton);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, internal);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();


        return answer;
    }

    public static void init_error(String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("ERROR");
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        label.setWrapText(true);
        label.setPadding(new Insets(15,15,15,15));

        //Two Buttons
        Button ok_button = new Button("ok");

        ok_button.setOnAction(e -> {
            answer = true;
            window.close();
        });


        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, ok_button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}