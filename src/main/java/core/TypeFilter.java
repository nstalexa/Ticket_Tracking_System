package core;

public class TypeFilter implements SearchInterface<Ticket>{

    private Ticket.TicketType type;

    public TypeFilter(Ticket.TicketType type) {
        this.type = type;
    }

    public boolean found(Ticket ticket, SearchContext ctx) {
        return ticket.getType() == type;
    }
}
