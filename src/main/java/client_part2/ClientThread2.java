package client_part2;

import constants.Constants;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.concurrent.Callable;

class   ClientThread2 implements Callable {
    int failure;
    SwipeApi swipeApi = new SwipeApi();
    int count;
    public ClientThread2(int count) {
        this.count = count;
    }

    @Override
    public Object call() throws Exception {
        //initialize the failure to 0 as start
        failure = 0;
//        System.out.println("**************Start calculating!!!" + count);
        //set cvs path
        String filePath = Constants.processFilePath + count + ".csv";
        File writeFile = new File(filePath);
        BufferedWriter bf = new BufferedWriter(new FileWriter(writeFile));
        for(int i = 0; i < Constants.TOTAL_POST_REQUEST / Constants.NUM_POST_THREAD; i++){
            if(i % 7 == 0){
                System.out.println("I'm doing one of this" + i);
            }
            long startTimeStamp = System.currentTimeMillis();
            int code = doTask();
            long latency = System.currentTimeMillis() - startTimeStamp;
            String out = startTimeStamp + "," + "POST" + "," + latency + "," + (code == 0 ? 200 : code);
            bf.write(out);
            bf.newLine();
        }
        bf.flush();
        bf.close();
        System.out.println("**************Finish calculating!!!");
        return failure;
    }

    private String stringGenerator() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 256) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    private int doTask(){
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(Constants.basePath);
        swipeApi.setApiClient(apiClient);

        SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details
        Random random = new Random();
        body.setSwiper("" + (random.nextInt(5000) + 1));
        body.setSwipee("" + (random.nextInt(1000000) + 1));
        body.setComment(stringGenerator());
        String leftorright = random.nextInt(2) == 0 ? "right" : "left"; // String | I like or dislike user

        try {
            swipeApi.swipe(body, leftorright);
        } catch (ApiException e) {
            System.err.println("Exception when calling SwipeApi#swipe");
            e.printStackTrace();
            failure++;
            return e.getCode();
        }
        return 0;
    }
}