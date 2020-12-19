package sample;
/**
 * This class acts as a helper Object for TableViews and Lists. Contains all attributes and a Checkbox
 */


import javafx.beans.property.*;
import javafx.scene.control.CheckBox;

public class Country {
    CheckBox select;
    IntegerProperty year = new SimpleIntegerProperty();
    StringProperty abbr = new SimpleStringProperty();
    StringProperty country_name = new SimpleStringProperty();
    StringProperty most_populous_city = new SimpleStringProperty();
    StringProperty capital = new SimpleStringProperty();
    IntegerProperty land_mass = new SimpleIntegerProperty();
    IntegerProperty pop_size = new SimpleIntegerProperty();
    StringProperty country_language = new SimpleStringProperty();
    FloatProperty pop_density = new SimpleFloatProperty();
    FloatProperty gdp = new SimpleFloatProperty();
    StringProperty currency = new SimpleStringProperty();
    FloatProperty power_consumption_per_capita = new SimpleFloatProperty();
    IntegerProperty passenger_air_transport = new SimpleIntegerProperty();

    @Override
    public String toString() {
        return "Country{" + year.get() +
                ", " + abbr.getValue() + "}";
    }

    public Country(int year, String abbr, String country_name, String most_populous_city,
                   String capital, int land_mass, int pop_size, String country_language,
                   float pop_density, float gdp, String currency,
                   float power_consumption_per_capita, int passenger_air_transport) {
        this.select = new CheckBox();
        this.year.set(year);
        this.year.set(year);
        this.abbr.set(abbr);
        this.country_name.set(country_name);
        this.most_populous_city.set(most_populous_city);
        this.capital.set(capital);
        this.land_mass.set(land_mass);
        this.pop_size.set(pop_size);
        this.country_language.set(country_language);
        this.pop_density.set(pop_density);
        this.gdp.set(gdp);
        this.currency.set(currency);
        this.power_consumption_per_capita.set(power_consumption_per_capita);
        this.passenger_air_transport.set(passenger_air_transport);
    }

    public CheckBox getSelect() {
        return select;
    }

    public void setSelect(CheckBox select) {
        this.select = select;
    }

    public void setBool(boolean val) {
        this.select.setSelected(val);
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public StringProperty abbrProperty() {
        return abbr;
    }

    public StringProperty country_nameProperty() {
        return country_name;
    }

    public StringProperty most_populous_cityProperty() {
        return most_populous_city;
    }

    public StringProperty capitalProperty() {
        return capital;
    }

    public IntegerProperty land_massProperty() {
        return land_mass;
    }

    public IntegerProperty pop_sizeProperty() {
        return pop_size;
    }

    public StringProperty country_languageProperty() {
        return country_language;
    }

    public FloatProperty pop_densityProperty() {
        return pop_density;
    }

    public FloatProperty gdpProperty() {
        return gdp;
    }

    public StringProperty currencyProperty() {
        return currency;
    }

    public FloatProperty power_consumption_per_capitaProperty() {
        return power_consumption_per_capita;
    }

    public IntegerProperty passenger_air_transportProperty() {
        return passenger_air_transport;
    }

    Country(){}
}
