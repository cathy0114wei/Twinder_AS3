package utils;

import constants.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperator {
    int count;
    private void writeFile(String method, long startTimeStamp, int code) throws IOException {
        //set cvs path
        String filePath = Constants.processFilePath + count + ".csv";
        File writeFile = new File(filePath);
        BufferedWriter bf = new BufferedWriter(new FileWriter(writeFile));
        long latency = System.currentTimeMillis() - startTimeStamp;
        String out = startTimeStamp + "," + method + "," + latency + "," + (code == 0 ? 200 : code);
        bf.write(out);
        bf.newLine();
        bf.flush();
        bf.close();
    }
}
