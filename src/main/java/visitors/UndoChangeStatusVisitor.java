package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;

import java.util.List;

public class UndoChangeStatusVisitor implements CommandVisitor {

    private ObjectNode error;
    private ChangeStatus command;
    private Ticket ticket;

    public UndoChangeStatusVisitor(ChangeStatus command, Ticket ticket) {
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

        if(!dev.getTickets().contains(ticket)) {
            error = error("Ticket " + ticket.getId() + " is not assigned to developer " + dev.getUsername() + ".");
        }

        if (ticket.getStatus() == Ticket.Status.IN_PROGRESS) {
            return;
        }

        if(ticket.getStatus().equals(Ticket.Status.CLOSED)) {
            ticket.setStatus(Ticket.Status.RESOLVED);
            List<Action> ticketHistory = ticket.getHistory();
            ticketHistory.add(new Action(
                    null,
                    dev.getUsername(),
                    system.getNow().toString(),
                    "STATUS_CHANGED",
                    Ticket.Status.CLOSED.toString(),
                    Ticket.Status.RESOLVED.toString()
            ));
        } else if (ticket.getStatus().equals(Ticket.Status.RESOLVED)) {
            ticket.setStatus(Ticket.Status.IN_PROGRESS);
            List<Action> ticketHistory = ticket.getHistory();
            ticketHistory.add(new Action(
                    null,
                    dev.getUsername(),
                    system.getNow().toString(),
                    "STATUS_CHANGED",
                    Ticket.Status.RESOLVED.toString(),
                    Ticket.Status.IN_PROGRESS.toString()
            ));
        }

    }


    @Override
    public void visit(UserManager u, AppSystem system) {
        error = error("Only developers can assign tickets.");
    }

    @Override
    public void visit(UserReporter u, AppSystem system) {
        error = error("Only developers can assign tickets.");
    }

    private ObjectNode error(String msg) {
        ObjectNode node = AppSystem.MAPPER.createObjectNode();
        node.put("command", "undoChangeStatus");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        node.put("error", msg);
        return node;
    }
}

