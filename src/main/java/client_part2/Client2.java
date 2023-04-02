package client_part2;

import io.swagger.client.ApiException;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import constants.Constants;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import static constants.Constants.filePath;

public class Client2 {

    static DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();

    static void mergeFileAndCalculate() throws IOException {
        File writeFile = new File(filePath);
        BufferedWriter bf = new BufferedWriter(new FileWriter(writeFile));
        bf.write("start time, type, latency, respond code");
        bf.newLine();
        for(int i = 0; i < Constants.NUM_POST_THREAD; i++){
            String curFile = Constants.processFilePath + i + ".csv";
            BufferedReader reader = new BufferedReader(new FileReader(curFile));
            String line;
            while((line = reader.readLine()) != null){
                String[] arr = line.split(",");
                int responseTime = Integer.parseInt(arr[2]);
                descriptiveStatistics.addValue(responseTime);
                bf.write(line);
                bf.newLine();
            }
        }
        bf.flush();
        bf.close();
    }
    public static void main(String[] args) throws ApiException, InterruptedException, ExecutionException, IOException {
        int failure = 0;
        long timeStampStart = System.currentTimeMillis();
        Queue<FutureTask<Integer>> queue = new LinkedList<FutureTask<Integer>>();

        for(int i = 0; i < Constants.NUM_POST_THREAD; i++){
            ClientThread2 callable = new ClientThread2(i);
            FutureTask<Integer> futuretask = new FutureTask<Integer>(callable);
            new Thread(futuretask).start();
            queue.offer(futuretask);
        }

        System.out.println("Main thread finished");
        while(!queue.isEmpty()){
            failure += queue.poll().get();
        }

        mergeFileAndCalculate();

        long wallTime = System.currentTimeMillis() - timeStampStart;
        long throughput = Constants.TOTAL_POST_REQUEST * 1000L / wallTime;

        System.out.println("************************part1************************");
        System.out.println("Total success requests number: " + (Constants.TOTAL_POST_REQUEST - failure) + '\n'
                + "Total failures number: " + failure + '\n'
                + "Wall time (ms): " + wallTime + '\n'
                + "throughput (/s): " + throughput + '\n'
                + "Threads number: " + Constants.NUM_POST_THREAD + '\n'
                + "Expect Throughput: " + (Constants.NUM_POST_THREAD / 0.04));

        double medianifEven = (descriptiveStatistics.getElement(Constants.TOTAL_POST_REQUEST / 2 - 1) +
                descriptiveStatistics.getElement(Constants.TOTAL_POST_REQUEST / 2 - 1)) / 2;
        double medianifOdd = (descriptiveStatistics.getElement(Constants.TOTAL_POST_REQUEST / 2));

        System.out.println("************************part2************************");
        System.out.println("mean response time: " + descriptiveStatistics.getMean() + '\n'
                + "median response time: " + (Constants.TOTAL_POST_REQUEST % 2 == 0 ? medianifEven : medianifOdd) + '\n'
                + "throughput: " + throughput + '\n'
                + "p99 response time: " + descriptiveStatistics.getPercentile(99) + '\n'
                + "min and max response time: " + descriptiveStatistics.getMin() + " and " + descriptiveStatistics.getMax());
    }

}