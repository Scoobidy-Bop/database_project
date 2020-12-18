package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    public TableView result_table;
    public ScrollPane results_pane;
    public GridPane container;

    private List<String> search_atts = new ArrayList<>();


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
        final_year = (TextField) window.getScene().lookup("#final_year");
        results_pane = (ScrollPane) window.getScene().lookup("#results_pane");
        results_pane.setVisible(false);
        container = (GridPane) window.getScene().lookup("#container");

        initial_year.setText(MIN_YEAR + "");
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
        String sql_select = attToSQL();
        String country = country_search_name.getText();
        String init_year = initial_year.getText();
        String fin_year = final_year.getText();
        boolean ret = validateSearch(country, init_year, fin_year);
        if (ret) {
            String[] creds = GetSQLInfo.getCredentials();
            String usr = creds[0];
            String pwd = creds[1];
            String url = GetSQLInfo.getUrlConnect();

            try (Connection cn = DriverManager.getConnection(url, usr, pwd)) {
                String q = "SELECT ? FROM country c" +
                        " JOIN country_population cp USING (year, abbr)" +
                        " JOIN economy e USING (year, abbr)" +
                        " WHERE  c.year >= ? AND c.year <= ? AND c.country_name = ?" +
                        " ORDER BY year ASC";
                PreparedStatement p_stmt = cn.prepareStatement(q);
                p_stmt.setString(1, sql_select);
                p_stmt.setString(2, init_year);
                p_stmt.setString(3, fin_year);
                p_stmt.setString(4, country);
                System.out.println("\n\n" + p_stmt.toString() + "\n\n");
                ResultSet rs = p_stmt.executeQuery();
                System.out.println(rs.toString());
                populateTable(rs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String attToSQL() {
        if (search_atts.size() < 1) {
            return "*";
        }
        String sql_select = "";
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

    public void populateTable(ResultSet rs) {
        try {
            if (rs.getMetaData().getColumnCount() == 0) {
                System.out.println("No results");
                results_pane.setVisible(false);
            } else {
                results_pane.setVisible(true);
                System.out.println("There are results!");
//                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
//                    final int j = i;
//                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
//                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
//                        @Override
//                        public ObservableValue call(TableColumn.CellDataFeatures param) {
//                            return new SimpleStringProperty(param.getValue().toString());
//                        }
//                    });
//
//                    result_table.getColumns().addAll(col);
//                    System.out.println("Column [" + i +"] ");
//                }
//
//                while(rs.next()) {
//                    ObservableList<String> row = FXCollections.observableArrayList();
//                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
//                        row.add(rs.getString(i));
//                    }
//                    System.out.println("Row [1] added" + row);
//
//                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

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
            if (co.matches("[a-zA-Z]+")) {
                return true;
            }
        }
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