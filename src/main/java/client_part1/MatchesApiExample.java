package client_part1;

import io.swagger.client.ApiException;
import io.swagger.client.model.*;
import io.swagger.client.api.MatchesApi;

public class MatchesApiExample {

    public static void main(String[] args) {

        MatchesApi apiInstance = new MatchesApi();
        String userID = "userID_example"; // String | user to return matches for
        try {
            Matches result = apiInstance.matches(userID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling MatchesApi#matches");
            e.printStackTrace();
        }
    }
}
//import io.swagger.client.*;
//import io.swagger.client.auth.*;
//import io.swagger.client.model.*;
//import io.swagger.client.api.SwipeApi;
//
//import java.io.File;
//import java.utils.*;
//
//public class SwipeApiExample {
//
//    public static void main(String[] args) {
//
//        SwipeApi apiInstance = new SwipeApi();
//        SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details
//        String leftorright = "left"; // String | Ilike or dislike user
//        try {
//            apiInstance.swipe(body, leftorright);
//        } catch (ApiException e) {
//            System.err.println("Exception when calling SwipeApi#swipe");
//            e.printStackTrace();
//        }
//    }
//}