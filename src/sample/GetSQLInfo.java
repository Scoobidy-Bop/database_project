package sample;

public class GetSQLInfo {

    private final static String domain = "cps-database.gonzaga.edu";
    private final static String username = "nkirsch";
    private final static String password = "nkirsch21767533";
    private final static String database = "nkirsch_DB";
    private final static String settings = "?serverTimezone=UTC";


    public static String[] getCredentials() {
        String[] creds = new String[2];
        creds[0] = getUsername();
        creds[1] = getPassword();
        return creds;
    }

    public static String getUrlConnect() {
        String domain = getDomain(), db = getDatabase(), settings = getSettings();
        return "jdbc:mysql://" + domain + "/" + db + settings;
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
