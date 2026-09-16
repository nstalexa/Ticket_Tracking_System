package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ChangeStatusVisitor implements CommandVisitor {

    private ObjectNode error;
    private ChangeStatus command;
    private Ticket ticket;

    public ChangeStatusVisitor(ChangeStatus command, Ticket ticket) {
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

        if (ticket.getStatus() == Ticket.Status.CLOSED) {
            return;
        }

        if(ticket.getStatus().equals(Ticket.Status.IN_PROGRESS)) {
            ticket.setStatus(Ticket.Status.RESOLVED);
            ticket.setSolvedAt(system.getNow());
            List<Action> ticketHistory = ticket.getHistory();
            ticketHistory.add(new Action(
                    null,
                    dev.getUsername(),
                    system.getNow().toString(),
                    "STATUS_CHANGED",
                    Ticket.Status.IN_PROGRESS.toString(),
                    Ticket.Status.RESOLVED.toString()
            ));
            ticket.setSolvedAt(system.getNow());
        } else if (ticket.getStatus().equals(Ticket.Status.RESOLVED)) {
            ticket.setStatus(Ticket.Status.CLOSED);
            List<Action> ticketHistory = ticket.getHistory();
            ticketHistory.add(new Action(
                    null,
                    dev.getUsername(),
                    system.getNow().toString(),
                    "STATUS_CHANGED",
                    Ticket.Status.RESOLVED.toString(),
                    Ticket.Status.CLOSED.toString()
            ));
            Milestone m = ticket.getMilestone();
            if(m!= null && ticket.getMilestone().isAllClosed()) {
                for(String i : ticket.getMilestone().getBlockingFor()) {
                    Milestone milestone = system.getMilestoneByName(i);
                    milestone.allClosed(command.getTs(), system, ticket.getId());
                }
                ticket.getMilestone().setStatus(Milestone.Status.COMPLETED);
                ticket.getMilestone().setCompletedNow(command.getTs());
            }
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
        node.put("command", "changeStatus");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        node.put("error", msg);
        return node;
    }
}

