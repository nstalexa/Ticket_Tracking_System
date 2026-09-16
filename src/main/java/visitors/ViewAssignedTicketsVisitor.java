package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import core.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewAssignedTicketsVisitor implements CommandVisitor {

    private final ViewAssignedTickets command;
    private ObjectNode error;
    @Getter
    private final List<Ticket> tickets = new ArrayList<>();

    public ViewAssignedTicketsVisitor(ViewAssignedTickets command) {
        this.command = command;
    }

    public ObjectNode getErrorNode() {
        return error;
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {

        tickets.addAll(dev.getTickets());

        tickets.sort(
                Comparator
                        .comparing(Ticket::getBusinessPriority,
                                Comparator.reverseOrder())
                        .thenComparing(Ticket::getCreatedAt)
                        .thenComparing(Ticket::getId)
        );
    }

    @Override
    public void visit(UserManager u, AppSystem system) {
        error = error("The user does not have permission to execute this command: required role DEVELOPER; " +
                "user role MANAGER.");
    }

    @Override
    public void visit(UserReporter u, AppSystem system) {
        error = error("The user does not have permission to execute this command: required role " +
                "DEVELOPER; user role REPORTER.");
    }

    private ObjectNode error(String msg) {
        ObjectNode node = AppSystem.MAPPER.createObjectNode();
        node.put("command", "viewAssignedTickets");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        node.put("error", msg);
        return node;
    }
}
