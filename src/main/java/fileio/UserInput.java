package fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class UserInput {
    private String username;
    private String email;
    private String role;
    private String hireDate;
    private String expertiseArea;
    private String seniority;
    private List<String> subordinates;
}
