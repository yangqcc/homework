package com.yqc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * <p>title:</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-23
 * @modified By yangqc
 */
public class KCYDBCPUtil {

  private static Properties properties = new Properties();
  private static DataSource dataSource;

  //加载DBCP配置文件
  static {
    try {
      InputStream is = KCYDBCPUtil.class.getResourceAsStream("/dbcp.properties");
      properties.load(is);
      dataSource = BasicDataSourceFactory.createDataSource(properties);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //从连接池中获取一个连接
  public static Connection getConnection() {
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

}
