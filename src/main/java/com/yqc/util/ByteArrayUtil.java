package com.yqc.util;

import com.yqc.config.CustomConfig;
import com.yqc.entity.PersistenceData;
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
public class StringUtil {

  private final static StringUtil instance = new StringUtil();

  private StringUtil() {
  }

  public static StringUtil getInstance() {
    return instance;
  }

  public PersistenceData String2Data(String records) {
    String[] array = records.split(",");
    SimpleDateFormat formatter = new SimpleDateFormat(CustomConfig.DATE_FORMAT);
    PersistenceData persistenceData = null;
    try {
      persistenceData = new PersistenceData(array[0], formatter.parse(array[1]),
          array[2],
          Double.valueOf(array[3]));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return persistenceData;
  }
}
