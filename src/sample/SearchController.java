package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;


public class SearchController {

    private boolean isEditor;

    @FXML
    private Button edit_countries_btn;
    @FXML
    private Button del_selected_btn;
    @FXML
    private TextField country_search_name;
    @FXML
    private TextField initial_year;
    @FXML
    private TextField final_year;

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
