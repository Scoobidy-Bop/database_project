package sample;

import com.mysql.cj.xdevapi.Table;
import javafx.beans.property.*;
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
    private final int MIN_YEAR = 1965;
    private final int MAX_YEAR = 2015;


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

    public List<Country> compared = new ArrayList<>();
    public List<Country> selected;

    private List<String> search_atts = new ArrayList<>();


    public void initialize(Boolean editor_val) {
        try {
            window = new Stage();
            Parent loader = FXMLLoader.load(getClass().getResource("Search.fxml"));
            Scene search_scene = new Scene(loader, 1000, 900);
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
        final_year = (TextField) window.getScene().lookup("#final_year");
        results_pane = (ScrollPane) window.getScene().lookup("#results_pane");
        results_pane.setVisible(false);
        container = (GridPane) window.getScene().lookup("#container");

        initial_year.setText(MIN_YEAR + "");
        final_year.setText(MAX_YEAR + "");
        cleanCompare();
    }

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
        AttSelectController asc = new AttSelectController();
        asc.initialize(search_atts, this);
    }

    public void storeSelected() {
        if (search_atts.size() < 1) {
            atts_label.setText("All Attributes (Default)");
        } else {
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

    public void performSearch() {
        selected = new ArrayList<>();
        TableView<Country> table_view = new TableView<>();

        table_view.setMaxHeight(450);
        String country = country_search_name.getText();
        String init_year = initial_year.getText();
        String fin_year = final_year.getText();
        boolean ret = validateSearch(country, init_year, fin_year);
        if (ret) {
            String sql_select = attToSQL();
            String q = "SELECT " + sql_select + " FROM country c" +
                    " JOIN country_population cp USING (year, abbr)" +
                    " JOIN economy e USING (year, abbr)" + " WHERE  c.year >= " + init_year +
                    " AND c.year <= " + fin_year + " AND c.country_name = ?" +
                    " ORDER BY year ASC;";
            getStoreData(q, country, table_view);
            Parent root = table_view;
            results_pane.setContent(root);
            results_pane.setVisible(true);
        }
    }


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
            boolean flag = false;

            ArrayList<Country> data = new ArrayList<>();
            while (rs.next()) {
                Country country = new Country();

                // year, abbr, and country_name will always be searched
                CheckBox cb = new CheckBox();
                country.setSelect(cb);
                country.year.set(rs.getInt("year"));
                country.abbr.set(rs.getString("abbr"));
                country.country_name.set(rs.getString("country_name"));

                if (checkCompare(country)) {
                    country.select.setSelected(true);
                    selected.add(country);
                    System.out.println(selected.toString());
                }

                cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        String[] creds = GetSQLInfo.getCredentials();
                        String usr = creds[0];
                        String pwd = creds[1];
                        String url = GetSQLInfo.getUrlConnect();

                        try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
                            if (newValue) {
                                if (!compared.contains(country)) {
                                    compared.add(country);
                                    addToCompare(country, cn);
                                    selected.add(country);
                                    System.out.println(selected.toString());
                                }
                            } else {
                                if (compared.contains(country)) {
                                    compared.remove(country);
                                    removeFromCompare(country, cn);
                                    selected.remove(country);
                                    System.out.println(selected.toString());
                                }
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });

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

                data.add(country);
            }

            ObservableList dbData = FXCollections.observableArrayList(data);


            TableColumn check_col = new TableColumn<>();
            check_col.setText("Selected");
            check_col.setCellValueFactory(new PropertyValueFactory<>("select"));
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
                col.setCellValueFactory(new PropertyValueFactory<>(rs.getMetaData().getColumnName(i+1)));
                table_view.getColumns().add(col);
            }

            // Fill table_view with the data
            table_view.setItems(dbData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String attToSQL() {
        if (search_atts.size() < 1) {
            return "*";
        }
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
            } if (intFY > 2015) {
                PopUp.init_error("INVALID ENTRY:\nFinal year cannot be greater than 2015");
                return false;
            } if (intIY < 1965) {
                PopUp.init_error("INVALID ENTRY:\nInitial year cannot be less than 1965");
                return false;
            }
            if (co.matches("[a-zA-Z ]+")) {
                return true;
            }
        }
        return false;
    }


    public void compareSelected() {
        System.out.println("Creating Table of compared countries");
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