package jp.ryo.informationPump.server.db;

import java.io.Serializable;
import java.sql.*;

import javax.swing.DefaultButtonModel;

import org.apache.commons.dbcp.*;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.*;

import jp.crossfire.framework.database.HyperDbException;
import jp.crossfire.framework.database.SQLExecutor;

public class DBManager implements Serializable{
    private static final long serialVersionUID = 1L;
    protected static transient PoolingDataSource ds_;

    public static void initMySQL() throws HyperDbException {
        try {
//          joclファイルを使う時用
            Class.forName("org.apache.commons.dbcp.PoolingDriver");
            // MySQLのためのJDBCドライバをロードします
            Class.forName("com.mysql.jdbc.Driver");

            // ObjectPoolインスタンスを生成します
//            ObjectPool pool = new StackObjectPool();
            GenericObjectPool pool = new GenericObjectPool(null,100,(byte)2,2000l,10,false,false,10000l,5,5000l,true);
//            new GenericObjectPool()

            // Connectionオブジェクトを生成するためのConnectionFactory
            // インスタンスを生成します
            ConnectionFactory conFactory = new DriverManagerConnectionFactory(
                    "jdbc:mysql://localhost/ここにDB名を入れる?&autoReconnect=true&useUnicode=true&characterEncoding=utf8",
//                    "jdbc:mysql://ryogrid.myhome.cx/kikker?&autoReconnect=true&useUnicode=true&characterEncoding=sjis",
                    "ここにDBのユーザIDを入れる", "ここにDBのパスワードを入れる");

            // PoolableConnectionFactoryインスタンスを生成します。
            new PoolableConnectionFactory(conFactory, pool, null, null, false,
                    true);


            // プーリング機能を持つDataSourceインスタンスを生成します
            ds_ = new PoolingDataSource(pool);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected Connection getConnection(boolean usePool){
        try {
            if (usePool) {
                // コネクションプールからオブジェクトを取り出します (5)
                return ds_.getConnection();
            } else {
                return DriverManager
                        .getConnection(
                                "jdbc:mysql://localhost/kikker?&autoReconnect=true&useUnicode=true&characterEncoding=utf8",
                                "ここにDBのユーザIDを入れる", "ここにDBのパスワードを入れる");
//                return DriverManager
//                .getConnection(
//                        "jdbc:apache:commons:dbcp:/kikker");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void returnConnection(Connection con, boolean usePool){
        try {
            if (con != null) {
                if (usePool) {
                    // コネクションプールにコネクションを返却します (6)
                    con.close();
                } else {
                    con.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
