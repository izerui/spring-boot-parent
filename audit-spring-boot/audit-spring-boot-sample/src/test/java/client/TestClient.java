package client;

import org.springframework.web.client.RestTemplate;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        long l = System.currentTimeMillis();
        for (int i = 0; i < 400; i++) {
            String forObject = restTemplate.getForObject("http://localhost:8080/test", String.class);
            System.out.println(forObject);
        }
        System.out.println("耗时: " + (System.currentTimeMillis() - l));
    }
}
