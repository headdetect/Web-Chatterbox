package sql;

import java.sql.ResultSet;

public interface SqlInterface {

	public boolean connect( String host, String port, String database, String username, String password );
	public void disconnect();
	public void build( String sql );
	public ResultSet executeQuery();
	public ResultSet executeRawQuery( String sql );
	public int executeUpdate();
	public int executeRawUpdate( String sql );
	public ResultSet executeRawPreparedQuery( String sql, String[] parameters );
	public int executeRawPreparedUpdate( String sql, String[] parameters );
	
}