package sample;

/**
 * File for getting access to the database to access the sql data stored remotely
 */

public class GetSQLInfo {

    private final static String domain = "cps-database.gonzaga.edu";
    private final static String username = "nkirsch";
    private final static String password = "nkirsch21767533";
    private final static String database = "nkirsch_DB";
    private final static String settings = "?serverTimezone=UTC";

    /**
     * Get the username and password for logging into the database
     *
     * @return - String array of the username in 0, and the password in 1
     */
    public static String[] getCredentials() {
        String[] creds = new String[2];
        creds[0] = getUsername();
        creds[1] = getPassword();
        return creds;
    }

    /**
     * Concatenates a url string based on the settings info, database being used, and the domain the server
     * exists on.
     *
     * @return - url String to create a Connection
     */
    public static String getUrlConnect() {
        String domain = getDomain(), db = getDatabase(), settings = getSettings();
        return "jdbc:mysql://" + domain + "/" + db + settings;
    }

    /**
     * A button's id is passed in, and the appropriate column name is returned
     *
     * @param id the button id being requeted
     * @return the column name associated with that id
     */
    public static String idToTitle(String id) {
        String name;
        switch (id) {
            case "mpc":
                name = "c.most_populous_city";
                break;
            case "cap":
                name = "c.capital";
                break;
            case "lnd":
                name = "c.land_mass";
                break;
            case "pop":
                name = "cp.pop_size";
                break;
            case "lng":
                name = "cp.country_language";
                break;
            case "pde":
                name = "cp.pop_density";
                break;
            case "gdp":
                name = "e.gdp";
                break;
            case "cur":
                name = "e.currency";
                break;
            case "ppc":
                name = "e.power_consumption_per_capita";
                break;
            case "pat":
                name = "e.passenger_air_transport";
                break;
            default:
                PopUp.init_error("ERROR: Invalid attribute ID passed to search!\nSearch aborted!");
                name = null;
        }
        return name;
    }


    private static String getDomain() {
        return domain;
    }

    private static String getUsername() {
        return username;
    }

    private static String getPassword() {
        return password;
    }

    private static String getDatabase() {
        return database;
    }

    private static String getSettings() {
        return settings;
    }
}