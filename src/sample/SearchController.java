package sample;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class SearchController {

    public void edit_countries() {

    }


    public void select_atts() {

    }


    public void perform_search() {

    }


    public void show_editors() {

    }


    public void del_selected() {
        boolean ans = PopUp.init_confirm("WARNING", "Are you sure you want delete the selected Country entries?");
        if (ans)
            System.out.println("Deleted selected countries");
    }
}
