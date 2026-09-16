package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.AddCommentVisitor;
import visitors.AssignTicketVisitor;
import visitors.UndoAddCommentVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class AddComment extends Command {
    private int ticketId;

    @Getter
    @Setter
    private boolean undo;

    public AddComment(User user, String username, LocalDate ts, int ticketId, boolean undo) {
        super(user, username, ts, CmdType.addComment);
        this.ticketId = ticketId;
        this.undo = undo;
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        if(!undo) {
            Ticket ticket = system.getTicketById(ticketId);
            if(ticket == null) {
                return null;
            }

            AddCommentVisitor visitor = new AddCommentVisitor(this, ticket);
            getUser().accept(visitor, system);

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
        if(ticket == null) {
            return null;
        }
        UndoAddCommentVisitor visitor = new UndoAddCommentVisitor(this, ticket);
        getUser().accept(visitor, system);

        if (visitor.getErrorNode() != null) {
            return visitor.getErrorNode();
        }
        return null;
    }

}