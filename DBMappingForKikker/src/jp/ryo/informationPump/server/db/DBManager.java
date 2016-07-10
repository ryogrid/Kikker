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
//          jocl�t�@�C�����g�����p
            Class.forName("org.apache.commons.dbcp.PoolingDriver");
            // MySQL�̂��߂�JDBC�h���C�o�����[�h���܂�
            Class.forName("com.mysql.jdbc.Driver");

            // ObjectPool�C���X�^���X�𐶐����܂�
//            ObjectPool pool = new StackObjectPool();
            GenericObjectPool pool = new GenericObjectPool(null,100,(byte)2,2000l,10,false,false,10000l,5,5000l,true);
//            new GenericObjectPool()

            // Connection�I�u�W�F�N�g�𐶐����邽�߂�ConnectionFactory
            // �C���X�^���X�𐶐����܂�
            ConnectionFactory conFactory = new DriverManagerConnectionFactory(
                    "jdbc:mysql://localhost/������DB��������?&autoReconnect=true&useUnicode=true&characterEncoding=utf8",
//                    "jdbc:mysql://ryogrid.myhome.cx/kikker?&autoReconnect=true&useUnicode=true&characterEncoding=sjis",
                    "������DB�̃��[�UID������", "������DB�̃p�X���[�h������");

            // PoolableConnectionFactory�C���X�^���X�𐶐����܂��B
            new PoolableConnectionFactory(conFactory, pool, null, null, false,
                    true);


            // �v�[�����O�@�\������DataSource�C���X�^���X�𐶐����܂�
            ds_ = new PoolingDataSource(pool);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected Connection getConnection(boolean usePool){
        try {
            if (usePool) {
                // �R�l�N�V�����v�[������I�u�W�F�N�g�����o���܂� (5)
                return ds_.getConnection();
            } else {
                return DriverManager
                        .getConnection(
                                "jdbc:mysql://localhost/kikker?&autoReconnect=true&useUnicode=true&characterEncoding=utf8",
                                "������DB�̃��[�UID������", "������DB�̃p�X���[�h������");
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
                    // �R�l�N�V�����v�[���ɃR�l�N�V������ԋp���܂� (6)
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
