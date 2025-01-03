package banquemisr.challenge05.tms;

import banquemisr.challenge05.tms.model.AuthorizationRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class TaskManagementSystemApplication {

    public static void main(String[] args) {
        AuthorizationRequest authRequest = new AuthorizationRequest();
        SpringApplication.run(TaskManagementSystemApplication.class, args);
        System.out.println(authRequest.getUserName());
    }

}
