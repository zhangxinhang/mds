package et.mds.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public final class JdbcUtil {
	private static ResourceBundle bundle = ResourceBundle.getBundle("mysql");
	private static String dbUrl = "jdbc:mysql://";
	public static String host = bundle.getString("mysql.host");
	public static String port = bundle.getString("mysql.port");
	public static String username = bundle.getString("mysql.username");
	public static String password = bundle.getString("mysql.password");
	public static String databaseName = bundle.getString("mysql.databaseName");

	private JdbcUtil() {
	}

	static {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Connection getConnection() throws SQLException {
		String connName = dbUrl + host + ":" + port + "/" + databaseName;
		return DriverManager.getConnection(connName, username, password);
	}

	public static void close(Connection conn, Statement st, ResultSet rs) {
		try {
			if (st != null) {

				st.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new ExceptionInInitializerError();
			}
		}
	}
}