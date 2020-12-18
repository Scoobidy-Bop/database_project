package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class SearchController {

    private static Stage window;
    private final int MIN_YEAR = 1965;
    private final int MAX_YEAR = 2015;


    public Button edit_countries_btn;
    public Button del_selected_btn;
    public Button view_editors_btn;
    public TextField country_search_name;
    public TextField initial_year;
    public TextField final_year;


    public void initialize(Boolean editor_val) {
        try {
            window = new Stage();
            Parent loader = FXMLLoader.load(getClass().getResource("Search.fxml"));
            Scene search_scene = new Scene(loader, 1000, 800);
            window.setScene(search_scene);
            Main.stage_settings(window);
            initItems();
            if (!editor_val) {
                edit_countries_btn.setVisible(false);
                del_selected_btn.setVisible(false);
                view_editors_btn.setVisible(false);
            }
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initItems() {
        edit_countries_btn = (Button) window.getScene().lookup("#edit_countries_btn");
        del_selected_btn = (Button) window.getScene().lookup("#del_selected_btn");
        view_editors_btn = (Button) window.getScene().lookup("#view_editors_btn");
        country_search_name = (TextField) window.getScene().lookup("country_search_name");
        initial_year = (TextField) window.getScene().lookup("#initial_year");
        initial_year.setText(MIN_YEAR + "");
        final_year = (TextField) window.getScene().lookup("#final_year");
        final_year.setText(MAX_YEAR + "");
    }

    public void editCountries(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Edit.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Edit Country");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void selectAtts() {
        System.out.println("Displaying possible search attributes");
    }


    public void performSearch() {
        String country = country_search_name.getText();
        int init_year = Integer.parseInt(initial_year.getText());
        int fin_year = Integer.parseInt(final_year.getText());
        validateSearch();
    }


    private boolean validateSearch() {
        return false;
    }


    public void compareSelected() {
        System.out.println("Showing all selected countries");
    }


    public void showEditors() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("editors.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 300);
            Stage stage = new Stage();
            stage.setTitle("Edit Editors");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void delSelected() {
        boolean ans = PopUp.init_confirm("WARNING", "Are you sure you want delete the selected Country entries?");
        if (ans)
            System.out.println("Deleted selected countries");
    }
}
