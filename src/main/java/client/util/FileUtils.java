package client.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lxg
 * @create 2018-06-21 14:40
 * @desc
 */
public class FileUtils {
    private static LLogger lLogger = LLoggerFactory.getLogger(FileUtils.class);
    public static void main(String[] args) {

        LinkedBlockingQueue<String> records = null;
        try {
            records = FileUtils.readFile("E:\\res\\snapshot.csv");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BigDecimal d = new BigDecimal("0");
        int i = 0;
        for(String record : records) {
            String[] r = record.split(",");
            double o_balance = Utils.matchDouble(r[3]);
            d = d.add(new BigDecimal(r[3]));
            lLogger.info("total: {}, balance: {}, index: {}", d, o_balance, i);
        }
        System.out.println("balance: " + d);
    }

    public static LinkedBlockingQueue<String> readFile(String fileName) throws InterruptedException {
        File file = new File(fileName);
        BufferedReader reader = null;
        LinkedBlockingQueue<String> data = new LinkedBlockingQueue<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                data.put(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return data;
    }

    public static void execFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }


}
