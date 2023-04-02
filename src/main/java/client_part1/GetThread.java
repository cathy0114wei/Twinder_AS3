package client_part1;

import constants.Constants;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;
import io.swagger.client.model.SwipeDetails;
import utils.RandomMsgGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static constants.Constants.REQUESTS_PER_SECOND;
import static constants.Constants.processGetFilePath;

public class GetThread  implements Runnable {
    ApiClient apiClient;
    RandomMsgGenerator randomMsgGenerator;
    File writeFile;
    MatchesApi matchesApi;
    StatsApi statsApi;

    public GetThread() {
        apiClient = new ApiClient();
        apiClient.setBasePath(Constants.basePath);
        randomMsgGenerator = new RandomMsgGenerator();
        writeFile = new File(processGetFilePath);
        matchesApi = new MatchesApi(apiClient);
        statsApi = new StatsApi(apiClient);
    }

    /**
     * In this method, we will do GET, and add the start time stamp and the latency into a file.
     */
    @Override
    public void run() {
        for(int i = 0; i < REQUESTS_PER_SECOND; i++){
            dotask();
        }
    }

    private void dotask(){
        SwipeDetails swipeExample = randomMsgGenerator.generateSwipeDetails();
        System.out.println("[x] GET method.");
        try{
            BufferedWriter bf = new BufferedWriter(new FileWriter(writeFile,true));

            long startTimeStamp = System.currentTimeMillis();
            if(randomMsgGenerator.generateBoolean()){
                doMatch(swipeExample.getSwipee());
            } else {
                doStats(swipeExample.getSwiper());
            }
            long latency = System.currentTimeMillis() - startTimeStamp;
            String out = startTimeStamp + "," + "GET" + "," + latency + "," + "200";

            bf.write(out);
            bf.newLine();
            bf.flush();
            bf.close();
        } catch (ApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doMatch(String userID) throws ApiException {
        Matches matches = matchesApi.matches(userID);
        System.out.println("user #" + userID + " potential matches are: \n" + matches.getMatchList());
    }

    private void doStats(String userID) throws ApiException {
        MatchStats matchStats = statsApi.matchStats(userID);
        System.out.println("user #" + userID + " stats are: \n" + "like number: " + matchStats.getNumLlikes() + "\n" + "dislike number: " + matchStats.getNumDislikes());
    }
}
