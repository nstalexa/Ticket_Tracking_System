package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import core.*;

import java.util.List;

public class UndoAddCommentVisitor implements CommandVisitor {

    private AddComment command;
    private Ticket ticket;
    private ObjectNode error;

    public UndoAddCommentVisitor(AddComment command, Ticket ticket) {
        this.command = command;
        this.ticket = ticket;
    }

    public ObjectNode getErrorNode() {
        return error;
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
        if(ticket == null) return;

        if(ticket.getReportedBy().isEmpty()) {
            error = error("Comments are not allowed on anonymous tickets.");
            return;
        }

        List<Comment> userComments = ticket.getComments().stream()
                .filter(c -> c.getAuthor().getUsername().equals(dev.getUsername()))
                .toList();

        if(userComments.isEmpty()) return;

        Comment last = userComments.getLast();
        ticket.removeComment(last);
    }

    @Override
    public void visit(UserReporter reporter, AppSystem system) {
        if(ticket == null) return;

        if(ticket.getReportedBy().isEmpty()) {
            error = error("Comments are not allowed on anonymous tickets.");
            return;
        }

        List<Comment> userComments = ticket.getComments().stream()
                .filter(c -> c.getAuthor().getUsername().equals(reporter.getUsername()))
                .toList();

        if(userComments.isEmpty()) return;

        Comment last = userComments.getLast();
        ticket.removeComment(last);
    }

    @Override
    public void visit(UserManager u, AppSystem system) {
        error = error("Only developers or reporters can undo comments.");
    }

    private ObjectNode error(String msg) {
        ObjectNode node = AppSystem.MAPPER.createObjectNode();
        node.put("command", "undoAddComment");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        node.put("error", msg);
        return node;
    }
}
