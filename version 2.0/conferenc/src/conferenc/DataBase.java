package conferenc;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Interface to use MySQL database.
 * 
 * <p> Using singelton design pattern (non-lazy initialization).
 * <code>DATA_BASE — singe object of this class.</code></p>
 * 
 * @since conferenc 0.0.1
 */
public class DataBase {
    /**
     * MySQL connection logger.
     */
    static final Logger logger = Logger.getLogger(DataBase.class.getName());
    
    
    /**
     * Sigle <code>DataBase</code> object.
     */
    public static DataBase DATA_BASE = new DataBase();
    
    /**
     * MySQL connection.
     */
    private Connection connection;
        
    /**
     * MySQL server host.
     */
    private final String dbHost = "127.0.0.1";
    
    /**
     * MySQL server port.
     */
    private final String dbPort = "3306";
    
    /**
     * Conferenc database name.
     */
    private final String dbName = "conferenc";
    
    
    /**
     * MySQL server user.
     */
    private final String dbUsername = "root";
    
    /**
     * MySQL user password.
     */
    private final String dbPassword = "aH2uf0mKm";
    
    
    /**
     * Cinferenc users table name.
     */
    private final String userTable = "user_test";
    
    
    private DataBase() {
        // Adding logging to file
        try {
            
            FileHandler fh = new FileHandler("%tdb_log.log", 1000000, 10, true);
            logger.addHandler(fh);
            
        } catch (SecurityException ex) {
            
            logger.log(Level.SEVERE, "Can't create log file: forbidden", ex);
            System.exit(0xDEADBEEF);
            
        } catch (IOException ex) {
            
            logger.log(Level.SEVERE, "Can't create log file: IO error", ex);
            System.exit(0xDEADBEEF);
            
        }
        
        // Including MySQL driver
        try {
            
            Class.forName("com.mysql.jdbc.Driver");
            
        } catch (ClassNotFoundException ex) {
            
            logger.log(Level.SEVERE, "Can't load MySQL driver", ex);
            System.exit(0xDEADBEEF);
            
        }
        
        // Connecting to MySQL server
        try {
            
            connection = DriverManager.getConnection(
                "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName,
                dbUsername,
                dbPassword
            );
            
        } catch (SQLException ex) {
            
            logger.log(Level.SEVERE, "Can't connect to MySQL server", ex);
            System.exit(0xDEADBEEF);
            
        }
    }
    
    
    /**
     * Checks if user exists and password is correct.
     * 
     * @param login user login.
     * @param password user password.
     * 
     * @return if authorization correct.
     */
    public boolean authorize(String login, String password) {
        try {
            PreparedStatement pst = connection.prepareStatement(
                "SELECT * FROM " + userTable +
                " WHERE " +
                " login LIKE ? AND " +
                " password LIKE ?;"
            );
            
            pst.setString(1, login);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            
            return  rs.next();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Bad query", ex);
        }
        
        return false;
    }
    
    /**
     * Checks if username already used.
     * 
     * @param login username to check.
     * 
     * @return if username used.
     */
    public boolean userExists(String login) {
        try {
            
            PreparedStatement pst = connection.prepareStatement(
                "SELECT * FROM " + userTable +
                " WHERE " +
                " login LIKE ?;"
            );
            
            pst.setString(1, login);
            
            ResultSet rs = pst.executeQuery();
            
            return  rs.next();
            
        } catch (SQLException ex) {
            
            logger.log(Level.WARNING, "Bad query", ex);
            
        }
        
        return false;
    }
    
    /**
     * Adds new user to confeenc users table.
     * 
     * @param login user login.
     * @param password user password.
     */
    public void addUser(String login, String password) {
        logger.log(Level.INFO, "Adding new user {0}", login);
        
        try {
            
            PreparedStatement pst = connection.prepareStatement(
                "INSERT INTO " + userTable + 
                " VALUES  ( ? , ? );"
            );
            
            pst.setString(1, login);
            pst.setString(2, password);
            
            pst.executeUpdate();
            
        } catch (SQLException ex) {
            
            logger.log(Level.WARNING, "Can't add new user", ex);
            
        }
    }
    
}