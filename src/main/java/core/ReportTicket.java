package core;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.TicketInput;
import lombok.Getter;
import lombok.Setter;
import visitors.ReportTicketVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;


public class ReportTicket extends Command {

    @Getter
    @Setter
    private AppSystem system;
    private Ticket ticket;
    private TicketInput params;
    public ReportTicket(User user, String username, LocalDate ts, TicketInput params) {
        super(user, username, ts, CmdType.reportTicket);
        this.params = params;
    }

    public ObjectNode execute(AppSystem system) {
        ObjectNode ret = MAPPER.createObjectNode();

        if (getUser() == null) {
            ret.put("command", "reportTicket");
            ret.put("username", this.username);
            ret.put("timestamp", getTs().toString());
            ret.put("error", "The user " + this.username + " does not exist.");
            return ret;
        }

        ticket = TicketParser.convertToTicket(params);

        if (ticket.getReportedBy().isEmpty() &&
                ticket.getType() != Ticket.TicketType.BUG) {
            ret.put("command", "reportTicket");
            ret.put("username", this.username);
            ret.put("timestamp", getTs().toString());
            ret.put("error", "Anonymous reports are only allowed for tickets of type BUG.");
            system.subtractId();
            return ret;
        }

        if (!system.isTestingPhase()) {
            ret.put("command", "reportTicket");
            ret.put("username", this.username);
            ret.put("timestamp", getTs().toString());
            ret.put("error", "Tickets can only be reported during testing phases.");
            system.subtractId();
            return ret;
        }



        if (ticket.getReportedBy().isEmpty()) {
            ticket.setBusinessPriority(Ticket.BusinessPriority.LOW);
        }

        system.addTicket(ticket);

        return null;
    }


    @Override
    public boolean canUndo() {
        return false;
    }
}