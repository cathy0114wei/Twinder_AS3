package constants;

public class Constants {
    public static int NUM_POST_THREAD = 50;
    public static int TOTAL_POST_REQUEST = 1000;
    public static int TOTAL_GET_REQUEST = -1;

    public static int REQUESTS_PER_SECOND = 5;

    public static int CHANNEL_POOL_CAPACITY = 50;
//    public static String QUEUE_NAME = "MESSAGE_QUEUE";
    public static String QUEUE_NAME = "TempStore";

    public static String EXCHANGE_NAME = "my_exchange";
    public static int QUEUE_NUM = 2;
    public static int USERINFOSIZE = 100;
    //RMQ IP, for the servlet
//    public static String HOSTNAME = "54.226.25.251";
    //private RMQ ip
    public static String HOSTNAME = "172.31.22.115";
    public static String USERNAME = "test";
    public static String PASSWORD = "test";
    //load balancer dns
//    public static String basePath = "http://balancer-616361848.us-east-1.elb.amazonaws.com:8080/Twinder_war";
    //server path, for the client
    public static String basePath ="http://localhost:8084/Twinder_war_exploded";
    public static String filePath = "/Users/cathywei/IdeaProjects/Twinder/out/hw1file/output.csv";
    public static String processFilePath = "/Users/cathywei/IdeaProjects/Twinder/out/hw1file/process";
    public static String processGetFilePath = "/Users/cathywei/IdeaProjects/Twinder/out/hw1file/process_get.csv";
    public static String processPostFilePath = "/Users/cathywei/IdeaProjects/Twinder/out/hw1file/process_post.csv";

    //DB access

    public static String DB_ER2EE = "SwipeF";
    public static String DB_ERDIS = "SwipeF_Dislike";

    public static String DB_EE2ER = "SwipeR";
    public static String accessKeyId = "ASIA3MRIZ5REKFO5YRMB";
    public static String secretAccessKey = "pfrbzB0/J6RmW9I3gPg99y0XAkBZtXOHITJvYDAc";
    public static String sessionToken = "FwoGZXIvYXdzEOv//////////wEaDHwR7ZEXNOjtTFOTGyLIAfpb8lvUtoZLxHwQXNYtGEX+eB2kapHrczDDYn/6KaG7LtwpmPlGi9IN7bjt2B7QEtxeiqmem2HQ0EP45DfMfv/KrkGSnGYAtHVnp4Y5LsXoMyO49Fq+kZnmre0Q+qmtKF2/1BGjWTQeGJrY34x/4GmHktQcxzqRw+0gAhSr9O5Li9kUhC6CENgqFG5KSU672hBBVeEpV1AHI332LndG2jsOvVTvyqG6glm0dTUgLsoWet4tgfRM1JthKsuA+5m5L0fGK0MduSwVKPiyo6EGMi2BJTkVlPL+B3YTZ1WiDsKCp29adGmBxsWsbkQWkB2EnRpY4yfPg1N1ER0wWOI=";
}
    //    public static String throuput = "/Users/cathywei/IdeaProjects/Twinder/out/graph/throuput.csv";
//    public static String time = "/Users/cathywei/IdeaProjects/Twinder/out/graph/time.csv";

