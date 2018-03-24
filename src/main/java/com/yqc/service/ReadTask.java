package com.yqc.service;

import com.yqc.config.CustomConfig;
import com.yqc.entity.PersistenceData;
import com.yqc.util.ByteArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>title:读取文件任务类</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-24
 * @modified By yangqc
 */
public class ReadTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(PersistenceTask.class);

    private final LinkedBlockingQueue<PersistenceData> taskQueue;
    private ByteArrayUtil byteArrayUtil = ByteArrayUtil.getInstance();
    private AtomicBoolean isComplete;

    public ReadTask(LinkedBlockingQueue<PersistenceData> taskQueue, AtomicBoolean isComplete) {
        if (taskQueue == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        this.taskQueue = taskQueue;
        this.isComplete = isComplete;
    }

    private void read(String filePath) {
        File file = null;
        FileChannel fileChannel = null;
        try {
            if (filePath == null || "".equals(filePath)) {
                throw new IllegalArgumentException("文件路径不能为空!");
            }
            file = new File(CustomConfig.FILE_PATH);
            fileChannel = new RandomAccessFile(file, "r").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 1024 * 1024);
            //使用temp字节数组用于存储不完整的行的内容
            byte[] last = new byte[0];
            while (fileChannel.read(byteBuffer) != -1 && !isComplete.get()) {
                //将buffer数据放入bs
                byte[] dest = new byte[byteBuffer.position()];
                byteBuffer.flip();
                byteBuffer.get(dest);
                byteBuffer.clear();
                last = operateByteArray(last, dest);
            }
        } catch (Exception e) {
            logger.error("读取失败!");
            throw new RuntimeException(e);
        } finally {
            try {
                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isComplete.set(true);
        }
    }

    private byte[] operateByteArray(byte[] lastArray, byte[] byteArray) {
        int lastEndNum = 0;
        boolean isNewLine = true;
        byte[] toTemp = null;
        byte[] returnByte = null;
        int i = 0;
        boolean commit = false;
        for (; i < byteArray.length; i++) {
            if (byteArray[i] == '\n') {
                if (isNewLine && lastArray != null && lastArray.length > 0) {
                    //将temp中的内容与换行符之前的内容拼接
                    toTemp = new byte[lastArray.length + i];
                    System.arraycopy(lastArray, 0, toTemp, 0, lastArray.length);
                    System.arraycopy(byteArray, 0, toTemp, lastArray.length, i - lastEndNum - 1);
                    lastEndNum = i;
                    isNewLine = false;
                } else {
                    if (isNewLine) {
                        isNewLine = false;
                    }
                    toTemp = new byte[i - lastEndNum];
                    System.arraycopy(byteArray, lastEndNum, toTemp, 0, i - lastEndNum - 1);
                    lastEndNum = i;
                }
                while (!isComplete.get() && !commit) {
                    commit = taskQueue.offer(byteArrayUtil.bytes2Data(toTemp));
                }
                commit = false;
            }
        }
        if (i != lastEndNum)

        {
            returnByte = new byte[i - lastEndNum];
            System.arraycopy(byteArray, lastEndNum, returnByte, 0, returnByte.length);
        }
        return returnByte;
    }

    @Override
    public void run() {
        read(CustomConfig.FILE_PATH);
    }
}
