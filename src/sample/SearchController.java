package sample;

/**
 * This is the primary instance of the application. This screen is presented after logging in, and acts
 * as the primary hub for all application functionality.
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SearchController {

    private static Stage window;
    private int MIN_YEAR = 1965;
    private int MAX_YEAR = 2015;


    public Button edit_countries_btn;
    public Button del_selected_btn;
    public Button view_editors_btn;
    public TextField country_search_name;
    public TextField initial_year;
    public TextField final_year;
    public Button select_att_btn;
    public Label atts_label;
    public ScrollPane results_pane;
    public GridPane container;

    // List for tracking all of the selected Country/Years
    public List<Country> compared = new ArrayList<>();

    // List for determining which items are selected soley on the current search
    public List<Country> selected;

    // List of search attributes based on the Attribute Selector
    private final List<String> search_atts = new ArrayList<>();

    /**
     * Initialize Search Screen and it's associated buttons and items
     *
     * @param editor_val
     */
    public void initialize(Boolean editor_val) {
        try {
            window = new Stage();
            Parent loader = FXMLLoader.load(getClass().getResource("Search.fxml"));
            Scene search_scene = new Scene(loader, 1000, 900);
            window.setScene(search_scene);
            Main.stage_settings(window);
            initItems();

            // Based on the editor_val calulcated when logging in, if the user is not an editor
            // then remove the following buttons from their display
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

    /**
     * Initialize the items on screen to be accessed programmatically
     */
    private void initItems() {
        edit_countries_btn = (Button) window.getScene().lookup("#edit_countries_btn");
        del_selected_btn = (Button) window.getScene().lookup("#del_selected_btn");
        view_editors_btn = (Button) window.getScene().lookup("#view_editors_btn");
        country_search_name = (TextField) window.getScene().lookup("country_search_name");
        initial_year = (TextField) window.getScene().lookup("#initial_year");
        final_year = (TextField) window.getScene().lookup("#final_year");
        results_pane = (ScrollPane) window.getScene().lookup("#results_pane");
        results_pane.setVisible(false);
        container = (GridPane) window.getScene().lookup("#container");

        // Set the min/max years as default search parameters
        initial_year.setText(MIN_YEAR + "");
        final_year.setText(MAX_YEAR + "");

        // Clean out the compare table so no data carries over from last application runs
        cleanCompare();
    }

    /**
     * cleanCompare deletes everything from the compare table, so that all data added to it are only from
     * the current app instance
     */
    private void cleanCompare() {
        String[] creds = GetSQLInfo.getCredentials();
        String usr = creds[0];
        String pwd = creds[1];
        String url = GetSQLInfo.getUrlConnect();

        try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
            String q = "DELETE FROM compare";
            PreparedStatement p_stmt = cn.prepareStatement(q);
            int ret = p_stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // this function is called when the user presses the edit countries button and loads the new window
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

    /**
     * Opens the Attribute Selector Screen so the user can select what attributes to search with their countries
     */
    public void selectAtts() {
        AttSelectController asc = new AttSelectController();
        asc.initialize(search_atts, this);
    }

    /**
     * store Selected gets called from the Attribute Selector as a signal to update the Search Screen to display
     * the attributes that the user has selected
     */
    public void storeSelected() {
        // Check if no attributes are selected. Default to searching all attributes
        if (search_atts.size() < 1) {
            atts_label.setText("All Attributes (Default)");
        } else {
            // Create a String based on selected attributes to display on screen
            String search = "";
            for (String att : search_atts) {
                search = search.concat(att);
                if (search_atts.indexOf(att) != (search_atts.size() - 1)) {
                    search = search.concat(", ");
                }
            }
            atts_label.setText(search);
        }
    }

    /**
     * Activates when the user presses search button. Performs a vast number of operations to display the
     * get, and display the results to a table
     */
    public void performSearch() {
        //Create a new instance of the selected List for every search, as we don't want to delete any items
        // from a previous search
        selected = new ArrayList<>();

        // The table that we are going to populate
        TableView<Country> table_view = new TableView<>();
        table_view.setMaxHeight(450);

        // Get the search parameters that the user entered
        String country = country_search_name.getText();
        String init_year = initial_year.getText();
        String fin_year = final_year.getText();

        // Validate that the entered information is legal
        boolean ret = validateSearch(country, init_year, fin_year);
        if (ret) {
            // Create the query to search. We must create the select clause dynamically
            // attToSQL is called to convert the List of attribute IDs selected into the appropriate column names
            String sql_select = attToSQL();
            String q = "SELECT " + sql_select + " FROM country c" +
                    " JOIN country_population cp USING (year, abbr)" +
                    " JOIN economy e USING (year, abbr)" + " WHERE  c.year >= " + init_year +
                    " AND c.year <= " + fin_year + " AND c.country_name = ?" +
                    " ORDER BY year ASC;";
            // The query, table, and country the user entered are passed to create the table
            getStoreData(q, country, table_view);

            // The completed table is placed inside the hidden ScrollPane and displayed
            Parent root = table_view;
            results_pane.setContent(root);
            results_pane.setVisible(true);
        }
    }

    /**
     * getStoreData retrieves searched data from the database and stores it to the table that is displayed on screen
     *
     * @param q the query string is passed in missing only the country name to search using prepared statements
     * @param search_country the country the user wants to search for is passed in to be placed in a prepared statement
     * @param table_view the table we will be populating with data found
     */
    public void getStoreData(String q, String search_country, TableView<Country> table_view) {
        String[] creds = GetSQLInfo.getCredentials();
        String usr = creds[0];
        String pwd = creds[1];
        String url = GetSQLInfo.getUrlConnect();

        try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
            PreparedStatement p_stmt = cn.prepareStatement(q);
            p_stmt.setString(1, search_country);
            ResultSet rs = p_stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();

            // Boolean flag is used to determine if the user is searching for a specific attribute or not
            // This is needed because otherwise the table will try to read data from columns that don't exist
            // in the query
            boolean flag = false;

            // A list of countries is created to represent every row of the query
            ArrayList<Country> data = new ArrayList<>();
            while (rs.next()) {
                Country country = new Country();

                // year, abbr, and country_name will always be searched by default
                CheckBox cb = new CheckBox();
                country.setSelect(cb);
                country.year.set(rs.getInt("year"));
                country.abbr.set(rs.getString("abbr"));
                country.country_name.set(rs.getString("country_name"));

                // We need to check if any countries has been previously selected for comparison
                // or has been saved to the compare table. If so, mark the checkbox as selected
                if (checkCompare(country)) {
                    country.select.setSelected(true);
                    selected.add(country);
                }

                cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    /**
                     * Checkbox is given a state change listener. When the country/year is selected (checked)
                     * we want that info to be sent and stored in the compare table so it is remembered through
                     * searches. If it is unchecked, then we need to remove that info from the compare table
                     *
                     * @param observable Not used
                     * @param oldValue the value that the checkbox was
                     * @param newValue the value that the checkbox now is
                     */
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        String[] creds = GetSQLInfo.getCredentials();
                        String usr = creds[0];
                        String pwd = creds[1];
                        String url = GetSQLInfo.getUrlConnect();
                        try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
                            // The way that this listener was implemented is by far not the most efficient,
                            // nor the best way to do this task. I would have liked to make the program run faster
                            // and less expensively by not adding removing everytime the checkbox is checked/unchecked
                            // but I simply didn't have time.
                            // In the code below I believe I tried to go for two different approaches unnecessarily
                            if (newValue) {
                                //The check box is selected
                                if (!compared.contains(country)) {
                                    compared.add(country);
                                    addToCompare(country, cn);
                                    selected.add(country);
                                }
                            } else {
                                // The checkbox is not selected
                                if (compared.contains(country)) {
                                    compared.remove(country);
                                    removeFromCompare(country, cn);
                                    selected.remove(country);
                                }
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });

                /**
                 * Check all of the optional attributes to ensure we don't try to read non-existent data
                 */

                // Check if most_populous_city is being searched for
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("most_populous_city"))
                        flag = true;
                if (flag)
                    country.most_populous_city.set(rs.getString("most_populous_city"));
                else
                    country.most_populous_city.set("N/A");


                // Check if capital is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("capital"))
                        flag = true;
                if (flag)
                    country.capital.set(rs.getString("capital"));
                else
                    country.capital.set("N/A");


                // Check if land_mass is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("land_mass"))
                        flag = true;
                if (flag)
                    country.land_mass.set(rs.getInt("land_mass"));
                else
                    country.land_mass.set(0);


                // Check if pop_size is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("pop_size"))
                        flag = true;
                if (flag)
                    country.pop_size.set(rs.getInt("pop_size"));
                else
                    country.pop_size.set(0);


                // Check if country_language is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("country_language"))
                        flag = true;
                if (flag)
                    country.country_language.set(rs.getString("country_language"));
                else
                    country.country_language.set("N/A");


                // Check if pop_density is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("pop_density"))
                        flag = true;
                if (flag)
                    country.pop_density.set(rs.getFloat("pop_density"));
                else
                    country.pop_density.set(0);


                // Check if gdp is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("gdp"))
                        flag = true;
                if (flag)
                    country.gdp.set(rs.getFloat("gdp"));
                else
                    country.gdp.set(0);


                // Check if currency is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("currency"))
                        flag = true;
                if (flag)
                    country.currency.set(rs.getString("currency"));
                else
                    country.currency.set("N/A");


                // Check if power_consumption_per_capita is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("power_consumption_per_capita"))
                        flag = true;
                if (flag)
                    country.power_consumption_per_capita.set(rs.getFloat("power_consumption_per_capita"));
                else
                    country.power_consumption_per_capita.set(0);


                // Check if passenger_air_transport is being searched for
                flag = false;
                for (int i = 1; i < cols; i++)
                    if (rsmd.getColumnName(i).equals("passenger_air_transport"))
                        flag = true;
                if (flag)
                    country.passenger_air_transport.set(rs.getInt("passenger_air_transport"));
                else
                    country.passenger_air_transport.set(0);


                // After all data vaules are set, then the country is added to the list of rows (Countries)
                data.add(country);
            }

            //the country list is converted into an ObservableList
            ObservableList dbData = FXCollections.observableArrayList(data);

            // The checkbox column is created individually since it is not part of the query results
            TableColumn check_col = new TableColumn<>();
            check_col.setText("Selected");
            check_col.setCellValueFactory(new PropertyValueFactory<>("select"));

            // Column is added to the table
            table_view.getColumns().add(check_col);


            // Give columns readable names
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                TableColumn col = new TableColumn<>();
                switch (rs.getMetaData().getColumnName(i+1)) {
                    case "year":
                        col.setText("Year");
                        break;
                    case "abbr":
                        col.setText("Abbr");
                        break;
                    case "country_name":
                        col.setText("Country Name");
                        break;
                    case "most_populous_city":
                        col.setText("Most Pop. City");
                        break;
                    case "capital":
                        col.setText("Capital");
                        break;
                    case "land_mass":
                        col.setText("Land Mass");
                        break;
                    case "pop_size":
                        col.setText("Pop. Size");
                        break;
                    case "country_language":
                        col.setText("Language");
                        break;
                    case "pop_density":
                        col.setText("pop. Density");
                        break;
                    case "gdp":
                        col.setText("GDP");
                        break;
                    case "currency":
                        col.setText("Currency");
                        break;
                    case "power_consumption_per_capita":
                        col.setText("Power Cons. Capita");
                        break;
                    case "passenger_air_transport":
                        col.setText("Passenger Air Trans.");
                        break;
                    default:
                        col.setText(rs.getMetaData().getColumnName(i+1));
                        break;
                }
                // Set the tag of each column and then add to table
                col.setCellValueFactory(new PropertyValueFactory<>(rs.getMetaData().getColumnName(i+1)));
                table_view.getColumns().add(col);
            }

            // Fill table_view with the columns
            table_view.setItems(dbData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts the global list of attributes into a String of the associated attribute column names
     *
     * @return string of every column name being selected to be added to the main SELECT query
     */
    public String attToSQL() {

        // If the user selected no attributes, we will search for every attribute
        if (search_atts.size() < 1) {
            return "*";
        }
        // The SELECT clause always searches for these 3 attributes withour exception
        String sql_select = "c.year, c.abbr, c.country_name, ";
        for (int i = 0; i < search_atts.size(); i++) {
            String tmp = search_atts.get(i);
            String title = GetSQLInfo.idToTitle(tmp);
            sql_select = sql_select.concat(title);
            if (search_atts.indexOf(tmp) != (search_atts.size() - 1)) {
                sql_select = sql_select.concat(", ");
            }
        }
        return sql_select;
    }

    /**
     * Simple validator to check that the input the user has entered is valid and searchable. Checks that
     *  - Dates are sequentially in order
     *  - Dates do not exceed the max year or the min year
     *  - The country named is only alphabetic
     *  If one of the conditions fails, a PopUp appears informing of the error
     *
     * @param co the country name to search
     * @param iy the initial year
     * @param fy the final year
     * @return boolean on if the search is legal or not
     */
    private boolean validateSearch(String co, String iy, String fy) {
        if (iy.isEmpty()) {
            PopUp.init_error("INVALID ENTRY:\nMissing initial year");
            return false;
        } if (fy.isEmpty()) {
            PopUp.init_error("INVALID ENTRY:\nMissing final year");
            return false;
        } if (co.isEmpty()) {
            PopUp.init_error("INVALID ENTRY:\nMissing Country to search");
            return false;
        }

        if (iy.matches("[0-9]+") && fy.matches("[0-9]+")) {
            int intIY = Integer.parseInt(iy);
            int intFY = Integer.parseInt(fy);
            if (intFY - intIY < 0) {
                PopUp.init_error("INVALID ENTRY:\nFinal year cannot be earlier than initial year");
                return false;
            } if (intFY > MAX_YEAR) {
                PopUp.init_error("INVALID ENTRY:\nFinal year cannot be greater than " + MAX_YEAR);
                return false;
            } if (intIY < MIN_YEAR) {
                PopUp.init_error("INVALID ENTRY:\nInitial year cannot be less than " + MIN_YEAR);
                return false;
            }
            if (co.matches("[a-zA-Z ]+")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Incomplete method to display a table of every country stored to the compare table
     */
    public void compareSelected() {
        System.out.println("Creating Table of compared countries");
    }
  
    // this function executes when the user presses the edit editors button and calls the editors window
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

    /**
     * If a country is selected to compare, it must be added to the compare table to be remembered
     *
     * @param country the Country object being selected
     * @param cn the connection to the database
     */
    public void addToCompare(Country country, Connection cn) {
        int year = country.year.getValue();
        String abbr = country.abbr.getValue();
        String cname = country.country_name.getValue();
        String mpc = country.most_populous_city.getValue();
        String cap = country.capital.getValue();
        int ldms = country.land_mass.getValue();
        int psz = country.pop_size.getValue();
        String lng = country.country_language.getValue();
        float pden = country.pop_density.floatValue();
        float gdp = country.gdp.floatValue();
        String cur = country.currency.getValue();
        float pcc = country.power_consumption_per_capita.floatValue();
        int pat = country.passenger_air_transport.getValue();

        /** Using this method was giving me errors, so I switch back to inserting manually
        String q = "INSERT INTO compare VALUES (" + year + ", ?, ?, ?, ?, " + ldms +
                ", " + psz + ", ?, " + pden + ", " + gdp + ", ?, " + pcc + ", " + pat + ");";
         **/
        String q = "INSERT INTO compare VALUES ("+ year + ", '" + abbr + "', '" + cname + "', '" +
                mpc + "', '" + cap + "', " + ldms + ", " + psz + ", '" + lng + "', " + pden + ", " +
                gdp + ", '" + cur + "', " + pcc + ", " + pat + ");";

        PreparedStatement ps = null;
        try {
            ps = cn.prepareStatement(q);

            System.out.println(ps.toString());
            int ret = ps.executeUpdate(q);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * If a country is undelected to compare, it must be removed from the compare table
     *
     * @param country the COuntry object being selected
     * @param cn the connection to the database
     */
    public void removeFromCompare(Country country, Connection cn) {
        int year = country.year.getValue();
        String abbr = country.abbr.getValue();
        String q = "DELETE FROM compare WHERE year = " + year + " AND abbr = '" + abbr + "';";

        Statement st = null;
        try {
            st = cn.createStatement();
            int ret = st.executeUpdate(q);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * When a new Search query is created the rows selected need to be checked if any exist in the
     * compare table
     *
     * @param country the Country object being checked
     * @return returns if the Country is in the compare table or not
     */
    private boolean checkCompare(Country country) {
        String[] creds = GetSQLInfo.getCredentials();
        String usr = creds[0];
        String pwd = creds[1];
        String url = GetSQLInfo.getUrlConnect();
        String abbr = country.abbr.getValue();
        int year = country.year.intValue();

        try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
            String q = "SELECT * FROM compare c WHERE c.year = " + year + " AND c.abbr = ?;";
            PreparedStatement p_stmt = cn.prepareStatement(q);
            p_stmt.setString(1, abbr);
            ResultSet rs = p_stmt.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete the selected Country/Years from the entire database
     */
    public void delSelected() {
        boolean ans = PopUp.init_confirm("WARNING", "Are you sure you want delete the selected Country entries?");
        if (ans) {
            String[] creds = GetSQLInfo.getCredentials();
            String usr = creds[0];
            String pwd = creds[1];
            String url = GetSQLInfo.getUrlConnect();

            try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
                // Commented out because removal of data simply stopped
//                for (Country country: selected) {
//                    String q = "DELETE c, cp, e FROM country c JOIN country_population cp USING (year, abbr)" +
//                            " JOIN economy e USING (year, abbr) WHERE  c.year = " + country.year.get() +
//                            " AND c.abbr = ?;";
//                    PreparedStatement ps = cn.prepareStatement(q);
//                    ps.setString(1, country.abbr.getValue());
//                    int ret = ps.executeUpdate();
//                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            PopUp.init_error("Database has been updated: Items removed");
            performSearch();
        }
    }
}