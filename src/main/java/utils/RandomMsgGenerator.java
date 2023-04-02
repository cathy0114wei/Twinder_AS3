package utils;

import io.swagger.client.model.SwipeDetails;

import java.util.Random;

public class RandomMsgGenerator {
    Random random;

    public RandomMsgGenerator() {
        this.random = new Random();
    }

    public String generateString(){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        while (salt.length() < 256) {
            int index = (int) (random.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public SwipeDetails generateSwipeDetails(){
        SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details
        body.setSwiper("" + (random.nextInt(50000) + 1));
        body.setSwipee("" + (random.nextInt(50000) + 1));
        body.setComment(generateString());
        return body;
    }

    public boolean generateBoolean(){
        int randomNumber = random.nextInt(2);
        return randomNumber % 2 == 0;
    }
}
