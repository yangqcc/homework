package com.yqc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

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

    private String itemId;
    private Date tradingDate;
    private String stockCode;
    private double itemValue;

}
