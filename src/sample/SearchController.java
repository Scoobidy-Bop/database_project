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
        initial_year = (TextField) window.getScene().lookup("#country_search_name");
        final_year = (TextField) window.getScene().lookup("#final_year");
    }

    public void edit_countries() {
        System.out.println("Showing country attribute editor");
    }


    public void select_atts() {
        System.out.println("Displaying possible search attributes");
    }


    public void perform_search() {
        String country = country_search_name.getText();
        int init_year = Integer.parseInt(initial_year.getText());
        int fin_year = Integer.parseInt(final_year.getText());
        if (init_year - fin_year < 0) {
            System.out.println("Searching for " + country + " from: " + init_year + " - " + fin_year);
        } else {
            PopUp.init_error("Illegal year entry. Final year must be after Initial year.");
        }
    }

    public void compare_selected() {
        System.out.println("Showing all selected countries");
    }


    public void show_editors() {
        System.out.println("Showing table of editors");

    }


    public void del_selected() {
        boolean ans = PopUp.init_confirm("WARNING", "Are you sure you want delete the selected Country entries?");
        if (ans)
            System.out.println("Deleted selected countries");
    }
}
