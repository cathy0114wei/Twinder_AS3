package client_part1;

import constants.Constants;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import utils.RandomMsgGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Callable;

import static constants.Constants.processGetFilePath;
import static constants.Constants.processPostFilePath;

class PostThread implements Callable {
    int postFailure;
    ApiClient apiClient;
    RandomMsgGenerator randomMsgGenerator;

    SwipeApi swipeApi;
    int count;
    File writeFile;
    public PostThread(int count) {
        apiClient = new ApiClient();
        apiClient.setBasePath(Constants.basePath);
        swipeApi = new SwipeApi();
        swipeApi.setApiClient(apiClient);
        writeFile = new File(processPostFilePath);
        this.count = count;
    }

    @Override
    public Object call() throws Exception {
        BufferedWriter bf = new BufferedWriter(new FileWriter(writeFile,true));
        //initialize the failure to 0 as start
        postFailure = 0;
        for(int i = 0; i < Constants.TOTAL_POST_REQUEST / Constants.NUM_POST_THREAD; i++){
            long startTimeStamp = System.currentTimeMillis();
            int code = doPost();
            long latency = System.currentTimeMillis() - startTimeStamp;
            postFailure += code == 0 ? 0 : 1;
            String out = startTimeStamp + "," + "POST" + "," + latency + "," + (code == 0 ? 200 : code);
            bf.write(out);
            bf.newLine();
        }
        bf.flush();
        bf.close();
        return postFailure;
    }

    private int doPost(){
        RandomMsgGenerator randomMsgGenerator = new RandomMsgGenerator();
        SwipeDetails body = randomMsgGenerator.generateSwipeDetails();
        String leftOrRight = randomMsgGenerator.generateBoolean() ? "right" : "left"; // String | I like or dislike user
        System.out.println("[*] POST method.");

        try {
            swipeApi.swipe(body, leftOrRight);
        } catch (ApiException e) {
            System.err.println("Exception when calling SwipeApi#swipe");
            e.printStackTrace();
            postFailure++;
            return e.getCode();
        }
        return 0;
    }
}