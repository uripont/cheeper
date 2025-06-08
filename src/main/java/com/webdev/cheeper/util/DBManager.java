package com.webdev.cheeper.util;

import java.sql.*;

public class DBManager implements AutoCloseable {
	
	private Connection connection = null;
	
	public DBManager() throws Exception {
		// WITHOUT POOL
		// We moved credentials to a .emv, see template
		String user      = System.getenv("DB_USER");
		String password  = System.getenv("DB_PASS");
		String db        = System.getenv("DB_NAME");
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection=DriverManager.getConnection("jdbc:mysql://localhost/"+db+"?serverTimezone=UTC&user="+user+"&password="+password);
	}
	
	public PreparedStatement prepareStatement(String query) throws SQLException{
		// Note that this is done using https://www.arquitecturajava.com/jdbc-prepared-statement-y-su-manejo/
		return connection.prepareStatement(query);
	}
	
	@Override
	public void close() throws SQLException{
		if (connection != null && !connection.isClosed()) {
            connection.close();
        }
	}
}