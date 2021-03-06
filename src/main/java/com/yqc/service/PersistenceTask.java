package com.yqc.service;

import com.yqc.entity.PersistenceData;
import com.yqc.util.DBCPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>title:持久化任务类</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-24
 * @modified By yangqc
 */
public class PersistenceTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(PersistenceTask.class);

    private DBCPUtil dbcpUtil = DBCPUtil.getInstance();
    private Connection connection = dbcpUtil.getConnection();
    private static final int BATCH_COUNT = 2000;
    private LinkedBlockingQueue<PersistenceData> queue;
    private AtomicBoolean isComplete;

    public PersistenceTask(LinkedBlockingQueue<PersistenceData> queue, AtomicBoolean isComplete) {
        this.queue = queue;
        this.isComplete = isComplete;
    }

    private void insertRecords() {
        String sql = "insert into time_series_data( item_id,trading_date,stock_code"
                + ", item_value) values (?,?,?,?)";
        int i = 0;
        PreparedStatement ps = null;
        try {
            Thread.sleep(1000);
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(sql);
            int j=0;
            while (!isComplete.get()) {
                PersistenceData persistenceData = queue.poll(1, TimeUnit.SECONDS);
                if (persistenceData != null) {
                    ps.setString(1, persistenceData.getItemId());
                    ps.setDate(2, new java.sql.Date(persistenceData.getTradingDate().getTime()));
                    ps.setString(3, persistenceData.getStockCode());
                    ps.setDouble(4, persistenceData.getItemValue());
                    ps.addBatch();
                    i++;
                    if (i % BATCH_COUNT == 0) {
                        ps.executeBatch();
                    }
                }
            }
            if (i % BATCH_COUNT != 0) {
                ps.executeBatch();
            }
            connection.commit();
        } catch (SQLException | InterruptedException e) {
            isComplete.set(true);
            logger.error("持久化失败!");
            throw new RuntimeException(e);
        } finally {
            dbcpUtil.close(null, ps, connection);
        }
    }


    @Override
    public void run() {
        insertRecords();
    }
}
