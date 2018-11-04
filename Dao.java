package com.heh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DAO base class for all extending DAOs. Note that this class is abstract,
 * which means it cannot itself be independently constructed. It does, however, have
 * a constructor so that inheriting DAO classes can initialize the data source
 * from their own constructors through the one here.
 * @author twinfeats
 */
public abstract class Dao {
	/**
	 * The data source for this DAO. This is created from the JNDI name provided to the constructor.
	 * Notice that this variable is protected, meaning that subclasses can access it.
	 */
	protected DataSource ds;

	/**
	 * Initializes the data source from the provided JNDI name. Throws NamingException if the JNDI name
	 * cannot be found.
	 * @param jndiName
	 * @throws NamingException 
	 */
	public Dao(String jndiName) throws NamingException {
		/**
		 * The below code is how a data source is initialized for Tomcat. Other servers, like Glassfish,
		 * do this differently. Tomcat uses line 37, all other java application servers use line 39.
		 */
		InitialContext ctx = new InitialContext();
		try {
			ds = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
		} catch (NamingException e) {
			ds = (DataSource) ctx.lookup(jndiName);
		}
	}

	/**
	 * Utility method to clean up all database related resources. Note that this method is protected so that
	 * subclasses can use it.
	 * @param rs
	 * @param stmt
	 * @param connection 
	 */
	protected void closeResources(ResultSet rs, PreparedStatement stmt, Connection connection) {
		//clean up resources here
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			}
		}
	}
}