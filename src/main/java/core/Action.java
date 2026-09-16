package core;

import lombok.Getter;
import lombok.Setter;

public class Action {
    @Getter
    @Setter
    private String milestone;
    @Getter
    @Setter
    private String by;
    @Getter
    @Setter
    private String timestamp;
    @Getter
    @Setter
    private String action;

    @Getter
    @Setter
    private String from;

    @Getter
    @Setter
    private String to;

    public Action(String milestone, String by, String timestamp, String action, String from, String to) {
        this.milestone = milestone;
        this.by = by;
        this.timestamp = timestamp;
        this.action = action;
        this.from = from;
        this.to = to;
    }
}
