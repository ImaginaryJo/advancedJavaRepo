import java.sql.*;

public class DatabaseReader {
    private String url;
    private String username;
    private String password;
    private Connection conn;
    
    public DatabaseReader() {
        this.url = "jdbc:mysql://127.0.0.1:3306/ims";
        this.username = "root";
        this.password = "Jmulletis22!!";
    }
    
    public void connect() throws SQLException {
        conn = DriverManager.getConnection(url, username, password);
    }
    
    public void disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
    
    public void initializeInventoryList() throws SQLException {
        String query = "SELECT * FROM inventory_list";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int productNumber = resultSet.getInt("product_number");
            String productName = resultSet.getString("product_name");
            String productType = resultSet.getString("product_type");
            int minimumThreshold = resultSet.getInt("minimum_threshold");
            double productPrice = resultSet.getDouble("product_price");
            // Initialize variables here using retrieved data
        }
        resultSet.close();
        statement.close();
    }
    
    public void initializeEmployees() throws SQLException {
        String query = "SELECT * FROM employees";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int employeeId = resultSet.getInt("employee_id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String employeeType = resultSet.getString("employee_type");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            String challengeQuestion = resultSet.getString("challenge_question");
            String challengeAnswer = resultSet.getString("challenge_answer");
            // Initialize variables here using retrieved data
        }
        resultSet.close();
        statement.close();
    }
}
