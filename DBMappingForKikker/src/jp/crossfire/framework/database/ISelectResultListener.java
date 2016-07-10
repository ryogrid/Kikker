package jp.crossfire.framework.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


/**
 * 
 * @author iizuka
 */
public interface ISelectResultListener{
	public void getResult(ResultSet result, ResultSetMetaData meta) throws Exception;
}
