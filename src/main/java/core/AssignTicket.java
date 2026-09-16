package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.AssignTicketVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class AssignTicket extends Command {
    private int ticketId;
    private UserDeveloper developer;

    @Getter
    @Setter
    private boolean undo;

    public AssignTicket(User user, String username, LocalDate ts, int ticketId, UserDeveloper developer, boolean undo) {
        super(user, username, ts, CmdType.assignTicket);
        this.ticketId = ticketId;
        this.developer = developer;
        this.undo = undo;
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        if(!undo) {
            Ticket ticket = system.getTicketById(ticketId);

            if(ticket == null) {
                return null;
            }
            AssignTicketVisitor visitor = new AssignTicketVisitor(this, ticket);
            getUser().accept(visitor, system);
            if (visitor.getErrorNode() != null) {
                return visitor.getErrorNode();
            } else {
                return null;
            }

        } else {
            return undo(system);
        }
    }



    @Override
    public boolean canUndo() {
        return true;
    }

    public ObjectNode undo(AppSystem system) {
        Ticket ticket = system.getTicketById(ticketId);
        if(!ticket.getStatus().equals(Ticket.Status.IN_PROGRESS)) {
            ObjectNode error = MAPPER.createObjectNode();
            error.put("command", "undoAssignTicket");
            error.put("username", this.getUsername());
            error.put("timestamp", this.getTs().toString());
            error.put("error", "Only IN_PROGRESS tickets can be unassigned.");
            return error;
        }
        if (ticket != null) {
            ticket.unassign(developer, system);
        }
        return null;
    }

}