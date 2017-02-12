package dao;
/**
 * 08-672 Assignment 3.
 * @author Jiayi Xie
 * @id jiayix
 * 11/25/2016
 */ 

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import databean.FavoriteBean;

public class FavoriteDAO {
	private List<Connection> connectionPool = new ArrayList<Connection>();

	private String jdbcDriver;
	private String jdbcURL;
	private String tableName;
	
	public FavoriteDAO(String jdbcDriver, String jdbcURL, String tableName) throws MyDAOException {
		this.jdbcDriver = jdbcDriver;
		this.jdbcURL = jdbcURL;
		this.tableName = tableName;
		
		if (!tableExists()) {
		    createTable();
		}
	}
	
	private synchronized Connection getConnection() throws MyDAOException {
		if (connectionPool.size() > 0) {
			return connectionPool.remove(connectionPool.size()-1);
		}
		
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new MyDAOException(e);
        }

        try {
            return DriverManager.getConnection(jdbcURL);
        } catch (SQLException e) {
            throw new MyDAOException(e);
        }
	}
	
	private synchronized void releaseConnection(Connection con) {
		connectionPool.add(con);
	}

	public void create(FavoriteBean item) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();
        	con.setAutoCommit(false);

            PreparedStatement pstmt = con.prepareStatement(
            		        "INSERT INTO " 
            		        + tableName 
            		        + " (userId,url,commentText,clickCount) VALUES (?,?,?,?)");
            pstmt.setInt(1, item.getUserId());
            pstmt.setString(2, item.getUrl());
            pstmt.setString(3, item.getComments());
            pstmt.setInt(4, item.getClicks());
            pstmt.executeUpdate();
            pstmt.close();
            
            con.commit();
            con.setAutoCommit(true);
            releaseConnection(con);
    	} catch (SQLException e) {
            try { 
                if (con != null) {
                    con.close(); 
                }
            } catch (SQLException e2) { /* ignore */ 
            }
        	throw new MyDAOException(e);
		}
	}
	
	public FavoriteBean[] getUserFavorites(int id) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE userId=" + id);
            
            List<FavoriteBean> list = new ArrayList<FavoriteBean>();
            while (rs.next()) {
            	FavoriteBean bean = new FavoriteBean();
            	bean.setFavoriteId(rs.getInt("favoriteId"));
            	bean.setUserId(rs.getInt("userId"));
            	bean.setUrl(rs.getString("url"));
            	bean.setComments(rs.getString("commentText"));
            	bean.setClicks(rs.getInt("clickCount"));          	
            	list.add(bean);
            }
            stmt.close();
            releaseConnection(con);
            return list.toArray(new FavoriteBean[list.size()]);
    	} catch (SQLException e) {
            try { 
                if (con != null) {
                    con.close(); 
                }
            } catch (SQLException e2) { /* ignore */ 
            }
        	throw new MyDAOException(e);
		}
	}
	
	public int count(int favoriteId) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT clickCount FROM " + tableName + " WHERE favoriteId = " + favoriteId);
            
            rs.next();
            int clickCount = rs.getInt("clickCount");
            ++clickCount;
   
            PreparedStatement pstmt = con.prepareStatement(
            		"UPDATE " + tableName + " SET clickCount = " + clickCount + " WHERE favoriteId = ?");
            pstmt.setInt(1, favoriteId);
            pstmt.executeUpdate();
            pstmt.close();
            stmt.close();
            releaseConnection(con);
            return clickCount;
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}
	
	private boolean tableExists() throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();
        	DatabaseMetaData metaData = con.getMetaData();
        	ResultSet rs = metaData.getTables(null, null, tableName, null);
        	
        	boolean answer = rs.next();
        	
        	rs.close();
        	releaseConnection(con);
        	
        	return answer;
        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }

	private void createTable() throws MyDAOException {
    	Connection con = getConnection();
    	try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(
            		    "CREATE  TABLE " 
        		        + tableName 
        		        + " (favoriteId INT NOT NULL AUTO_INCREMENT, "
        		        + " userId INT NULL, " 
        		        + " url TEXT NULL, " 
        		        + " commentText TEXT NULL, " 
        		        + " clickCount INT NULL, " 
        		        + " PRIMARY KEY (favoriteId), " 
        		        + " FOREIGN KEY (userId )" 
        		        + " REFERENCES fav_site_user(userId));");
            stmt.close();
            releaseConnection(con);
        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }
}
