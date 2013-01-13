package sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SqlClass implements SqlInterface {

	protected Connection conn = null;

	public String prefix = "";

	protected String current_query = "";
	
	public PreparedStatement prepare;

	public ResultSet results;
	

	public void build(String sql) {
		current_query += sql;
	}
	
	public ResultSet executePreparedQuery(String[] parameters) {
		ResultSet query = executeRawPreparedQuery(current_query, parameters);
		current_query = "";
		return query;
	}
	
	public int executePreparedUpdate(String[] parameters) {
		int query = executeRawPreparedUpdate(current_query, parameters);
		current_query = "";
		return query;
	}

	public ResultSet executeQuery() {
		ResultSet query = executeRawQuery(current_query);
		current_query = "";
		return query;
	}
	
	public int executeUpdate() {
		int query = executeRawUpdate(current_query);
		current_query = "";
		return query;
	}

	public boolean isConnected() {
		try {
			return conn.isValid(10) ? true : false;
		} catch (SQLException e) {
			return false;
		}
	}

}