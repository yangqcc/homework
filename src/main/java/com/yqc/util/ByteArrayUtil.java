package com.yqc.util;

import com.yqc.config.CustomConfig;
import com.yqc.entity.PersistenceData;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <p>title:</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-24
 * @modified By yangqc
 */
public class ByteArrayUtil {

  private final static ByteArrayUtil instance = new ByteArrayUtil();

  private ByteArrayUtil() {
  }

  public static ByteArrayUtil getInstance() {
    return instance;
  }

  public PersistenceData bytes2Data(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      throw new IllegalArgumentException("字节数组不能为空!");
    }
    PersistenceData persistenceData = null;
    try {
      String records = new String(bytes, CustomConfig.FILE_ENCODING);
      String[] array = records.split(",");
      SimpleDateFormat formatter = new SimpleDateFormat(CustomConfig.DATE_FORMAT);
      persistenceData = new PersistenceData(array[0], formatter.parse(array[1]),
          array[2],
          Double.valueOf(array[3]));
    } catch (ParseException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return persistenceData;
  }

}
