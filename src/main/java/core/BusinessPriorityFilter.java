package core;

public class BusinessPriorityFilter implements SearchInterface<Ticket>{

    private Ticket.BusinessPriority businessPriority;

    public BusinessPriorityFilter(Ticket.BusinessPriority businessPriority) {
        this.businessPriority = businessPriority;
    }

    public boolean found(Ticket ticket, SearchContext ctx) {
        return ticket.getBusinessPriority() == businessPriority;
    }
}
