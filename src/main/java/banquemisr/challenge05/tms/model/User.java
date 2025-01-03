package banquemisr.challenge05.tms.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@Document(collection = "AppUsers")
public class User {
    @Id
    private String id;
    private String userName;
    private String userPassword;
    private String role;

}
