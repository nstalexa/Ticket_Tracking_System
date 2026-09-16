package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.AssignTicketVisitor;
import visitors.ChangeStatusVisitor;
import visitors.UndoChangeStatusVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class ChangeStatus extends Command {
    private int ticketId;

    @Getter
    @Setter
    private boolean undo;

    public ChangeStatus(User user, String username, LocalDate ts, int ticketId, boolean undo) {
        super(user, username, ts, CmdType.assignTicket);
        this.ticketId = ticketId;
        this.undo = undo;
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        if(!undo) {
            Ticket ticket = system.getTicketById(ticketId);

            ChangeStatusVisitor visitor = new ChangeStatusVisitor(this, ticket);
            getUser().accept(visitor, system);

            if(ticket == null) {
                return null;
            }
            if (visitor.getErrorNode() != null) {
                return visitor.getErrorNode();
            }

        } else {
            return undo(system);
        }
        return null;
    }



    @Override
    public boolean canUndo() {
        return true;
    }

    public ObjectNode undo(AppSystem system) {
        Ticket ticket = system.getTicketById(ticketId);

        UndoChangeStatusVisitor visitor = new UndoChangeStatusVisitor(this, ticket);
        getUser().accept(visitor, system);

        if(ticket == null) {
            return null;
        }
        if (visitor.getErrorNode() != null) {
            return visitor.getErrorNode();
        }
        return null;
    }

}