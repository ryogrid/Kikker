package jp.crossfire.framework.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * A class to execute sql 
 * @author iizuka
 *
 */
public class SQLExecutor{
	protected Connection con;
	protected String driverName;

	/**
	 * @return Returns the con.
	 */
	public Connection getCon() {
		return con;
	}
	/**
	 * @param con The con to set.
	 */
	public void setCon(Connection con) {
		this.con = con;
	}
	/**
	 * 
	 * @throws HyperDbException
	 */
	public void configConnection(String driver, String url, String user, String pass)
												throws HyperDbException{
		// set property
		this.driverName = driver;
		
		// load driver
		try {
			Class.forName(this.driverName);
		} catch (ClassNotFoundException e) {
			throw new HyperDbException(e);
		}
		
		// create connection
		Properties prop = new Properties();
		if(user != null){
			prop.setProperty("user", user);
		}
		if(pass != null){
			prop.setProperty("password", pass);
		}
		
		try {
			this.con = DriverManager.getConnection(url, prop);
		} catch (SQLException e) {
			throw new HyperDbException(e);
		}
	}
	
	/**
	 * Dispose connection
	 *
	 */
	public void dispose(){
		if(this.con != null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * A Method to updating SQL
	 * @param sql
	 * @throws HyperDbException
	 */
	public void execUpdateSQL(String sql) throws HyperDbException{
		//DebugUtil.out("EXECUTE : " + sql);
		Statement stmt = null;
		try{
			stmt = this.con.createStatement();
			stmt.executeUpdate(sql);
			
			
		}catch(Exception e){
			throw new HyperDbException(e);
		}finally{
			try {
				stmt.close();
			} catch (SQLException ignore) {
			}
		}
	}
	
	/**
	 * 
	 * @param sql
	 * @throws HyperDbException
	 */
	public void executeSelectSQL(String sql, ISelectResultListener listner)
														throws HyperDbException{
		Statement stmt = null;
		ResultSet result = null;
		try{
			stmt = this.con.createStatement();
			
//			System.out.println("[FW] execute select SQL -> " + sql);
			
			stmt.executeQuery(sql);
			result = stmt.getResultSet();
			ResultSetMetaData meta = result.getMetaData();
            //フェッチサイズを変更 by Ryo
            result.setFetchSize(1000);
            
			while(result.next()){
				listner.getResult(result, meta);
			}
			
			
			
			
		}catch(Exception e){
			throw new HyperDbException(e);
		}finally{
			try {
				result.close();
			} catch (SQLException ignore) {
			}
			try {
				stmt.close();
			} catch (SQLException ignore) {
			}
		}
	}
	/**
	 * 
	 * @param sql
	 * @param listners
	 * @throws HyperDbException
	 */
	public void executeSelectSQL(String sql, List listners)
											throws HyperDbException{
		try{
			Statement stmt = this.con.createStatement();
			
//			System.out.println("[FW] execute select SQL -> " + sql);
			
			stmt.executeQuery(sql);
			ResultSet result = stmt.getResultSet();
            //フェッチサイズを変更 by Ryo
            result.setFetchSize(1000);
			ResultSetMetaData meta = result.getMetaData();
                        
			while(result.next()){
				Iterator it = listners.iterator();
				while(it.hasNext()){
					ISelectResultListener listner = (ISelectResultListener)it.next();
					listner.getResult(result, meta);
				}
			}
			
			result.close();
			
			stmt.close();
		}catch(Exception e){
			throw new HyperDbException(e);
		}		
	}
}