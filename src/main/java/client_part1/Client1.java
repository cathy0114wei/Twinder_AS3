package client_part1;

import constants.Constants;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

import static constants.Constants.*;

public class Client1 {
    DescriptiveStatistics get_descriptiveStatistics;
    DescriptiveStatistics post_descriptiveStatistics;

    long timeStampStart;
    long postWallTime;
    long postThroughput;
    int postFailure;

    public Client1() {
        this.get_descriptiveStatistics = new DescriptiveStatistics();
        this.post_descriptiveStatistics = new DescriptiveStatistics();
        this.timeStampStart = -1;
        this.postWallTime = -1;
        this.postThroughput = -1;
        this.postFailure = 0;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        Client1 client1 = new Client1();
        client1.timeStampStart = System.currentTimeMillis();
        Queue<FutureTask<Integer>> queue = new LinkedList<FutureTask<Integer>>();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        //start doPost
        System.out.println("----------------------Post Start----------------------");
        client1.clearFile(processPostFilePath);
        for (int i = 0; i < Constants.NUM_POST_THREAD; i++) {
            PostThread callable = new PostThread(i);
            FutureTask<Integer> futuretask = new FutureTask<Integer>(callable);
            new Thread(futuretask).start();
            queue.offer(futuretask);
        }

        //start doGet
        System.out.println("----------------------Get Start----------------------");
        client1.clearFile(processGetFilePath);
        GetThread runnable = new GetThread();
        executorService.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.MILLISECONDS);

        //wait for doPost finish
        while (!queue.isEmpty()) {
            client1.postFailure += queue.poll().get();
        }
        System.out.println("----------------------Post Finished----------------------");

        // Print the massage about doGet
        client1.readFile(processGetFilePath, client1.get_descriptiveStatistics);
        client1.printDoGetMsg();
        //end doGet
        executorService.shutdown();
        System.out.println("----------------------Get Finished----------------------");

        // Print the massage about doPost
        client1.readFile(processPostFilePath, client1.post_descriptiveStatistics);
        client1.postWallTime = System.currentTimeMillis() - client1.timeStampStart;
        client1.postThroughput = Constants.TOTAL_POST_REQUEST * 1000L / client1.postWallTime;
        client1.printDoPostMsg();
    }

    private void printDoGetMsg(){
        System.out.println("************************GET RESULT************************");
        System.out.println("min response time: " + get_descriptiveStatistics.getMin() + '\n'
                +"mean response time: " + get_descriptiveStatistics.getMean() + '\n'
                + "max response time: " + get_descriptiveStatistics.getMax());
        System.out.println("*********************GET RESULT END*************************");
    }
    private void printDoPostMsg(){
        System.out.println("************************POST RESULT************************");
        System.out.println("Total success post requests number: " + (TOTAL_POST_REQUEST - postFailure) + '\n'
                + "Total failures number: " + postFailure + '\n'
                + "Wall time (ms): " + postWallTime + '\n'
                + "postThroughput (/s): " + postThroughput + '\n'
                + "Threads number: " + Constants.NUM_POST_THREAD + '\n'
                + "Expect Throughput: " + (int)(Constants.NUM_POST_THREAD / 0.035) + '\n'
                + "min response time: " + post_descriptiveStatistics.getMin() + '\n'
                + "mean response time: " + post_descriptiveStatistics.getMean() + '\n'
                + "max response time: " + post_descriptiveStatistics.getMax());
        System.out.println("*********************POST RESULT END*************************");
    }
    private void readFile(String filePath, DescriptiveStatistics descriptiveStatistics) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while((line = reader.readLine()) != null){
            String[] arr = line.split(",");
            int responseTime = Integer.parseInt(arr[2]);
            descriptiveStatistics.addValue(responseTime);
        }
    }

    private void clearFile(String filePath){
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print("");
            printWriter.close();
            String[] file_path = filePath.split("/");
            System.out.println("File *" + file_path[file_path.length - 1] + "* prepared successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
