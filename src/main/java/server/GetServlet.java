package server;

import com.google.gson.Gson;
import controller.DBController;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import utils.ParameterGetter;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

import static constants.Constants.*;
import static constants.Constants.sessionToken;

@WebServlet
//        (name = "server.GetServlet", urlPatterns = {"/matches/*"})
public class GetServlet extends HttpServlet {
    DynamoDbClient dynamoDbClient;
    DBController dbController;

    @Override
    public void init() throws ServletException {
        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretAccessKey", secretAccessKey);
        System.setProperty("aws.sessionToken", sessionToken);
        Region region = Region.US_EAST_1;
        this.dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(SystemPropertyCredentialsProvider.create())
                .region(region)
                .build();
        dbController = new DBController();
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        try{
            System.out.println("Server is processing the get method" + request.getRequestURI());
            ParameterGetter parameterGetter = new ParameterGetter(request);
            String userId = parameterGetter.getLastParameter();
            String query = parameterGetter.getLastButOneParameter();

            if(query.equals("matches")){
                List<String> list = dbController.getRecords(dynamoDbClient, DB_EE2ER, "Swipee", userId, "#ee", "Swiper");
                Matches matches = new Matches();
                matches.matchList(list);
                String json = gson.toJson(matches,Matches.class);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(json);

            } else if(query.equals("stats")){
                //likes, dislikes
                int[] stats = dbController.getStats(dynamoDbClient, DB_ER2EE, DB_ERDIS, "Swiper", userId, "#er","Swipee");
                MatchStats matchStats = new MatchStats();
                matchStats.setNumLlikes(stats[0]);
                matchStats.setNumDislikes(stats[1]);
                String json = gson.toJson(matchStats,MatchStats.class);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(json);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Bad Request!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}
