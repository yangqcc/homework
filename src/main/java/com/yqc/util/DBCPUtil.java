package com.yqc.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * <p>title:</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-24
 * @modified By yangqc
 */
public class DBCPUtil {

  private static Properties properties = new Properties();
  private static DataSource dataSource;

  private final static DBCPUtil INSTANCE = new DBCPUtil();

  /**
   *  加载DBCP配置文件
   */
  static {
    try {
      InputStream is = DBCPUtil.class.getResourceAsStream("/dbcp.properties");
      properties.load(is);
      dataSource = BasicDataSourceFactory.createDataSource(properties);
    } catch (Exception e) {
      throw new RuntimeException(e.getCause());
    }
  }

  private DBCPUtil() {
  }

  public static DBCPUtil getInstance() {
    return INSTANCE;
  }

  /**
   * 从连接池中获取一个连接
   */
  public Connection getConnection() {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

  /**
   * 关闭{@link ResultSet},{@link PreparedStatement},{@link Connection}
   * @param rs
   * @param ps
   * @param connection
   */
  public void close(ResultSet rs, PreparedStatement ps, Connection connection) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    if (ps != null) {
      try {
        ps.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
