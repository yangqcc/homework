package com.yqc.config;

import com.yqc.util.DBCPUtil;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>title:</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-24
 * @modified By yangqc
 */
public class CustomConfig {

  private static Properties properties = new Properties();

  public static final String FILE_ENCODING;

  public static final String FILE_PATH;

  public static final String DATE_FORMAT;

  static {
    try {
      InputStream is = DBCPUtil.class.getResourceAsStream("/config.properties");
      properties.load(is);
      FILE_ENCODING = properties.getProperty("fileEncoding");
      FILE_PATH = properties.getProperty("filePath");
      DATE_FORMAT = properties.getProperty("dateFormat");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
