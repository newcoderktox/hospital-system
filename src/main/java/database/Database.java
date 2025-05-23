package database;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    //db file
    private static final String DB_URL = "jdbc:sqlite:hospital_appointment_system.db";
    //make db connection
    public static Connection connect() {
        Connection myCon = null; //connection object
        try {
            //load sqlite driver
            Class.forName("org.sqlite.JDBC");
            //this part is to find the db file where program runs
            String currentFolder = Paths.get(".").toAbsolutePath().normalize().toString();
            String fullDbPath = Paths.get(currentFolder, "hospital_appointment_system.db").normalize().toString();
            System.out.println("DEBUG: trying to connect db at " + fullDbPath);
            myCon = DriverManager.getConnection(DB_URL); //connect to db
            System.out.println("db connected ok");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: sqlite driver not found " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("ERROR: db connection prob " + e.getMessage());
            e.printStackTrace();
        }
        return myCon; //return the connection
    }
    //make or check tables
    public static void setupTables() {
        try (Connection myCon = connect(); //get conn
             Statement myStmt = myCon.createStatement()) { //sql query
            //users table sql
            String usersTableSql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "surname TEXT NOT NULL," +
                    "phoneNumber TEXT," +
                    "email TEXT," +
                    "userType TEXT NOT NULL" +
                    ");";
            myStmt.execute(usersTableSql); //run sql
            System.out.println("users table done or already exists");
            //doctors table sql
            String doctorsTableSql = "CREATE TABLE IF NOT EXISTS doctors (" +
                    "id INTEGER PRIMARY KEY," +
                    "specialization TEXT NOT NULL," +
                    "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";
            myStmt.execute(doctorsTableSql); //run sql
            System.out.println("doctors table done or already exists");
            //patients table sql
            String patientsTableSql = "CREATE TABLE IF NOT EXISTS patients (" +
                    "id INTEGER PRIMARY KEY," +
                    "dateOfBirth TEXT," +
                    "bloodGroup TEXT," +
                    "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";
            myStmt.execute(patientsTableSql); // run sql
            System.out.println("patients table done or already exists");
            //appointments table sql
            String apptsTableSql = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "patient_id INTEGER NOT NULL," +
                    "doctor_id INTEGER NOT NULL," +
                    "appointment_date TEXT NOT NULL," +
                    "appointment_time TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";
            myStmt.execute(apptsTableSql); //run sql
            System.out.println("appts tablealready exists");
            System.out.println("all table updated");
        } catch (SQLException e) {
            System.err.println("ERROR: db table problem " + e.getMessage());
        }
    }
    //close connection
    public static void closeMyCon(Connection dbCon) {
        if (dbCon != null) {
            try {
                dbCon.close(); //close connection
                System.out.println("db connection closed");
            } catch (SQLException e) {
                System.err.println("ERROR: db close problem " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // program starts
        setupTables();
        // finished yeyyy
    }
}
