package sample;
/**
 * Primary Controller for the Attribute selector screen. Allows the user to specify which Country
 * attributes that they want to search for. If none are selected, then all attributes will be searched.
 */

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AttSelectController {

    public Stage window;

    public ToggleButton mpc;
    public ToggleButton cap;
    public ToggleButton lnd;
    public ToggleButton pop;
    public ToggleButton lng;
    public ToggleButton pde;
    public ToggleButton gdp;
    public ToggleButton cur;
    public ToggleButton ppc;
    public ToggleButton pat;

    // List of all buttons on screen
    public List<String> btn_names = new ArrayList<>();

    // Button to confirm selected atts
    public Button confirm;

    /**
     * Set up the Screen, and all necessary functions
     *
     * @param atts A list of attributes that the caller Screen uses to check what to search for
     * @param sc the caller Screen passes itself so that the Attribute Selector can pass info back
     */
    public void initialize(List<String> atts, SearchController sc) {
        try {
            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Select Search Attributes");
            window.setResizable(false);
            Parent loader = FXMLLoader.load(getClass().getResource("AttSelect.fxml"));
            Scene attributes = new Scene(loader, 400, 850);
            window.setScene(attributes);
            window.setOnCloseRequest(e -> {
                e.consume();
                window.close();
            });
            initItems();
            window.show();

            // Check that there are not already any attributes selected
            if (atts.size() > 0) {
                // Itterate through all IDs and toggle selected attribute buttons to true
                for (String att : atts) {
                    String tmp = "#" + att;
                    ToggleButton btn = (ToggleButton) attributes.lookup(tmp);
                    btn.setSelected(true);
                }
            }

            confirm.setOnAction(e -> {
                getSelected(atts);

                // we were given the caller screen, so run a storeSelected to update atts searched for
                sc.storeSelected();
                window.close();

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the list of selected attributes based on what toggleButtons are pressed upon confirm
     * button being pressed.
     *
     * @param atts the list of attributes passed to initializer is passed again for access
     */
    public void getSelected(List<String> atts) {
        for (String btn_name : btn_names) {

            // Create a string of the button id
            String tmp = "#" + btn_name;
            ToggleButton btn = (ToggleButton) window.getScene().lookup(tmp);

            // Check toggle
            if (btn.isSelected() && !atts.contains(btn.getId())) {
                atts.add(btn.getId());
            } else if (atts.contains(btn.getId()) && !btn.isSelected()) {
                atts.remove(btn.getId());
            }
        }
    }

    /**
     * Initialize all buttons and items on screen so they can be accessed programmatically.
     */
    public void initItems() {
        confirm = (Button) window.getScene().lookup("#confirm");

        mpc = (ToggleButton) window.getScene().lookup("#mpc");
        btn_names.add(mpc.getId());
        cap = (ToggleButton) window.getScene().lookup("#cap");
        btn_names.add(cap.getId());
        lnd = (ToggleButton) window.getScene().lookup("#lnd");
        btn_names.add(lnd.getId());
        pop = (ToggleButton) window.getScene().lookup("#pop");
        btn_names.add(pop.getId());
        lng = (ToggleButton) window.getScene().lookup("#lng");
        btn_names.add(lng.getId());
        pde = (ToggleButton) window.getScene().lookup("#pde");
        btn_names.add(pde.getId());
        gdp = (ToggleButton) window.getScene().lookup("#gdp");
        btn_names.add(gdp.getId());
        cur = (ToggleButton) window.getScene().lookup("#cur");
        btn_names.add(cur.getId());
        ppc = (ToggleButton) window.getScene().lookup("#ppc");
        btn_names.add(ppc.getId());
        pat = (ToggleButton) window.getScene().lookup("#pat");
        btn_names.add(pat.getId());
    }
}
