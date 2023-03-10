import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerClass {
    //logger
    private static final Logger logger = Logger.getLogger(LoggerClass.class.getName());

    //logger method
    public static void log(Level level, String message) throws SQLException {
        logger.log(level, message);
        // Upload log to database
        DatabaseReader.uploadLog(level.getName(), message);
    }

    //method to read log file
    public static void readLogFile() throws SQLException {
        DatabaseReader.readLogFromDb();
    }
}
