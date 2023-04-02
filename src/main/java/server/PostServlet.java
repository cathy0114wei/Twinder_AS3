package server;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import dto.Swipe;
import utils.ParameterGetter;
import conn.RMQChannelFactory;
import conn.RMQChannelPool;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static constants.Constants.*;

@WebServlet(name = "server.PostServlet", urlPatterns = {"/swipe/left/","/swipe/right/"})
public class PostServlet extends HttpServlet {
    RMQChannelFactory rmqChannelFactory;
    RMQChannelPool rmqChannelPool;
    Connection connection;
    int poolCapacity;
    @Override
    public void init() throws ServletException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOSTNAME);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
//        factory.setVirtualHost(VIRTUALHOST);

        poolCapacity = CHANNEL_POOL_CAPACITY;
        try {
            //initialize the factory and the connection
            connection = factory.newConnection();
            rmqChannelFactory = new RMQChannelFactory(connection);
            rmqChannelPool = new RMQChannelPool(poolCapacity, rmqChannelFactory);

            Channel channel = rmqChannelPool.borrowObject();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);

            //AS3: declare a quorum queue
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-queue-type", "quorum");
//            channel.queueDeclare("TempStore", true, false, false, arguments);
//            channel.queueBind("test-quorum-queue", EXCHANGE_NAME, "");


            for(int i = 0; i < QUEUE_NUM; i++){
                channel.queueDeclare(QUEUE_NAME + i, true, false, false, arguments);
                channel.queueBind(QUEUE_NAME + i, EXCHANGE_NAME, "");
            }
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        super.init();
    }

    @Override
    public void destroy() {
        try {
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.destroy();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Channel channel = rmqChannelPool.borrowObject();
            processRequest(request, response, channel);
            if(channel != null){
                rmqChannelPool.returnObject(channel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, Channel channel) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        try {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = request.getReader().readLine()) != null) {
                sb.append(s);
            }
            Swipe swipe = gson.fromJson(sb.toString(), Swipe.class);
            ParameterGetter parameterGetter = new ParameterGetter(request);
            swipe.setRightOrNot(parameterGetter.getLastParameter().equals("right"));
            // see if it's not a valid URL and JSON
            if (Objects.isNull(swipe.getSwiper()) || Objects.isNull(swipe.getSwipee())
                    || swipe.getComment() == null || swipe.getComment().length() == 0 || swipe.getComment().length() > 256) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Bad Request!");
            } else {
                String swipeJsonString = gson.toJson(swipe);
                response.getWriter().print(swipeJsonString);
                channel.basicPublish("", QUEUE_NAME + 0, null, swipeJsonString.getBytes(StandardCharsets.UTF_8));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Bad Request!");
        } finally {
            response.getWriter().flush();
        }
    }
}
