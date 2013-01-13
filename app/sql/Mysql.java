package sql;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Mysql extends SqlClass {

    @Override
    public boolean connect( String host, String port, String database, String username, String password ) {
        try {
            DriverManager.registerDriver( new com.mysql.jdbc.Driver() );
            conn = DriverManager.getConnection( "jdbc:mysql://" + host + ":" + port + "/" + database, username, password );
        } catch ( SQLException ex ) {
            return false;
        }
        return true;
    }

    @Override
    public void disconnect() {
        try {
            conn.close();
        } catch ( SQLException e ) {
            // If it had problems, it was already disconnected
        }
    }

    @Override
    public ResultSet executeRawPreparedQuery( String sql, String[] parameters ) {
        PreparedStatement preparedStatement;
        ResultSet results = null;
        try {
            preparedStatement = conn.prepareStatement( sql );
            int i = 1;
            for ( String parameter : parameters ) {
                preparedStatement.setString( i, parameter );
                i++;
            }
            results = preparedStatement.executeQuery();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public int executeRawPreparedUpdate( String sql, String[] parameters ) {
        PreparedStatement preparedStatement;
        int results = 0;
        try {
            preparedStatement = conn.prepareStatement( sql );
            int i = 1;
            for ( String parameter : parameters ) {
                preparedStatement.setString( i, parameter );
                i++;
            }
            results = preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public ResultSet executeRawQuery( String sql ) {
        PreparedStatement preparedStatement;
        ResultSet results = null;
        try {
            preparedStatement = conn.prepareStatement( sql );
            results = preparedStatement.executeQuery();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public int executeRawUpdate( String sql ) {
        PreparedStatement preparedStatement;
        int results = 0;
        try {
            preparedStatement = conn.prepareStatement( sql );
            results = preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return results;
    }
}
