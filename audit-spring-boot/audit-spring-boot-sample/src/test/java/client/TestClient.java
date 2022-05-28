package client;

import org.springframework.web.client.RestTemplate;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject("http://localhost:8080/clear", String.class);
        long l = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++) {
            try {
                String forObject = restTemplate.getForObject("http://localhost:8080/test", String.class);
                System.out.println(forObject);
            }catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("耗时: " + (System.currentTimeMillis() - l));
    }
}
