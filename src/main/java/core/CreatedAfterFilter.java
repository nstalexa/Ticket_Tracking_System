package core;

import java.time.LocalDate;

public class CreatedAfterFilter implements SearchInterface<Ticket> {
    private  LocalDate timestamp;

    public CreatedAfterFilter(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean found(Ticket t, SearchContext ctx) {
        return t.getCreatedAt().isAfter(timestamp);
    }
}

