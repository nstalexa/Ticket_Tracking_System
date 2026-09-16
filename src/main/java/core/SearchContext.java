package core;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchContext{

    @Getter
    private User currentUser;
    @Getter
    private LocalDate timestamp;
    @Getter
    private AppSystem system;
    @Getter
    @Setter
    private Map<Integer, List<String>> matchingWords = new HashMap<>();

    public SearchContext(User currentUser, LocalDate timestamp, AppSystem system) {
        this.currentUser = currentUser;
        this.timestamp = timestamp;
        this.system = system;
    }
}
