package com.yqc.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>title:</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-24
 * @modified By yangqc
 */
@Data
@AllArgsConstructor
public class PersistenceData {

  public PersistenceData() {
  }

  private String itemId;
  private Date tradingDate;
  private String stockCode;
  private double itemValue;

}
