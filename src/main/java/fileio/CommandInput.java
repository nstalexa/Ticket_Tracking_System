package fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import core.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class CommandInput {

    private String command;
    private String username;
    private String timestamp;
    private TicketInput params;
    private String name;
    private String dueDate;
    private String[] blockingFor;
    private List<Integer> tickets;
    private List<String> assignedDevs;
    private int ticketID;
    private String comment;
    private SearchInput filters;
}
