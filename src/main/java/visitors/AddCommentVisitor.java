package visitors;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
public class AddCommentVisitor implements CommandVisitor{
    private AddComment command;
    private Ticket ticket;
    private ObjectNode error;

    public AddCommentVisitor(AddComment command, Ticket ticket) {
        this.command = command;
        this.ticket = ticket;
    }

    public ObjectNode getErrorNode() {
        return error;
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
        if(ticket == null) {
            return;
        }
        if(command.getStringComment() != null && command.getStringComment().length() < 10) {
            error = error("Comment must be at least 10 characters long.");
            return;
        }

        if(ticket.getReportedBy().isEmpty()) {
            error = error("Comments are not allowed on anonymous tickets.");
            return;
        }

        if (!dev.getTickets().contains(ticket)) {
            error = error(
                    "Ticket " + ticket.getId() + " is not assigned to the developer " + dev.getUsername() + ".");
            return;
        }

        Comment comment = new Comment(dev, command.getStringComment(), command.getTs());
        ticket.addComment(comment);
    }

    @Override
    public void visit(UserReporter rep, AppSystem system) {
        if(ticket.getReportedBy().isEmpty()) {
            error = error("Comments are not allowed on anonymous tickets.");
            return;
        }

        if(command.getStringComment() != null && command.getStringComment().length() < 10) {
            error = error("Comment must be at least 10 characters long.");
            return;
        }

        if(ticket.getStatus() == Ticket.Status.CLOSED) {
            error = error("Reporters cannot comment on CLOSED tickets.");
            return;
        }

        if(!ticket.getReportedBy().equals(rep.getUsername())) {
            error = error("Reporter " + rep.getUsername() + " cannot comment on ticket " + ticket.getId() + ".");
            return;
        }

        Comment comment = new Comment(rep, command.getStringComment(), command.getTs());
        ticket.addComment(comment);
    }
    @Override
    public void visit(UserManager man, AppSystem system) {
    }

    private ObjectNode error(String msg) {
        ObjectNode node = AppSystem.MAPPER.createObjectNode();
        node.put("command", "addComment");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        node.put("error", msg);
        return node;
    }


}



