package core;

import java.time.LocalDate;

public class CreatedBeforeFilter implements SearchInterface<Ticket> {
    private  LocalDate timestamp;

    public CreatedBeforeFilter(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean found(Ticket t, SearchContext ctx) {
        return t.getCreatedAt().isBefore(timestamp);
    }
}

