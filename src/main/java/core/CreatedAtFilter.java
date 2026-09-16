package core;

import java.time.LocalDate;

public class CreatedAtFilter implements SearchInterface<Ticket>{
    private LocalDate timestamp;

    public CreatedAtFilter(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean found(Ticket t, SearchContext ctx) {
        return t.getCreatedAt().equals(timestamp);
    }
}





