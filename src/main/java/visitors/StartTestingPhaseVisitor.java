package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class StartTestingPhaseVisitor implements CommandVisitor {

    @Getter
    private ObjectNode node;
    private StartTestingPhase command;


    public StartTestingPhaseVisitor(StartTestingPhase command) {
        this.command = command;
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
    }


    @Override
    public void visit(UserManager man, AppSystem system) {
        boolean hasActiveMilestones = false;

        if (system.getMilestones() != null) {
            for (Milestone m : system.getMilestones()) {
                if (m.getTickets().stream().anyMatch(ticket -> ticket.getStatus().equals(Ticket.Status.OPEN) || ticket.getStatus().equals(Ticket.Status.IN_PROGRESS))) {
                    hasActiveMilestones = true;
                    break;
                }
            }
        }

        if (hasActiveMilestones) {
            this.node = error("Cannot start a new testing phase.");
        } else {
            system.setTestingPhase(true);
            system.setStartDay(command.getTs());
            this.node = null;
        }
    }

    @Override
    public void visit(UserReporter u, AppSystem system) {
    }

    private ObjectNode error(String msg) {
        ObjectNode node = AppSystem.MAPPER.createObjectNode();
        node.put("command", "startTestingPhase");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        node.put("error", msg);
        return node;
    }
}
