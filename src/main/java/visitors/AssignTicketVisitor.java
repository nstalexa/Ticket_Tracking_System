package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AssignTicketVisitor implements CommandVisitor {

    private ObjectNode error;
    private AssignTicket command;
    private Ticket ticket;

    public AssignTicketVisitor(AssignTicket command, Ticket ticket) {
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


        if (!ticket.canAssign(dev.getExpertiseArea())) {
            List<UserDeveloper.ExpertiseArea> validAreas = new ArrayList<>();
            switch(ticket.getExpertiseArea()) {
                case FRONTEND, DESIGN -> {
                    validAreas.add(UserDeveloper.ExpertiseArea.DESIGN);
                    validAreas.add(UserDeveloper.ExpertiseArea.FRONTEND);
                    validAreas.add(UserDeveloper.ExpertiseArea.FULLSTACK);
                }
                case BACKEND -> {
                    validAreas.add(UserDeveloper.ExpertiseArea.BACKEND);
                    validAreas.add(UserDeveloper.ExpertiseArea.FULLSTACK);
                }
                case DEVOPS -> {
                    validAreas.add(UserDeveloper.ExpertiseArea.DEVOPS);
                    validAreas.add(UserDeveloper.ExpertiseArea.FULLSTACK);

                }
                case DB -> {
                    validAreas.add(UserDeveloper.ExpertiseArea.BACKEND);
                    validAreas.add(UserDeveloper.ExpertiseArea.DB);
                    validAreas.add(UserDeveloper.ExpertiseArea.FULLSTACK);
                }
            }


            error = error(
                    "Developer " + dev.getUsername() +
                            " cannot assign ticket " + ticket.getId() +
                            " due to expertise area. Required: " + validAreas.stream().map(Enum::name).collect(Collectors.joining(", ")) +
                            "; Current: " + dev.getExpertiseArea() + "."
            );
            return;
        }

        if (ticket.getStatus() != Ticket.Status.OPEN) {
            error = error("Only OPEN tickets can be assigned.");
            return;
        }

        Milestone m = ticket.getMilestone();
        if (m != null) {
            if (!m.getAssignedDevs().contains(dev.getUsername())) {
                error = error(
                        "Developer " + dev.getUsername() +
                                " is not assigned to milestone " + m.getName() + "."
                );
                return;
            }

            if (m.isBlocked()) {
                error = error(
                        "Cannot assign ticket " + ticket.getId() +
                                " from blocked milestone " + m.getName() + "."
                );
                return;
            }
        }
        Ticket.BusinessPriority p = ticket.getBusinessPriority();
        UserDeveloper.Seniority s = dev.getSeniority();

        if (p == Ticket.BusinessPriority.CRITICAL && s == UserDeveloper.Seniority.MID) {
            error = error(
                    "Developer " + dev.getUsername() +
                            " cannot assign ticket " + ticket.getId() +
                            " due to seniority level. Required: SENIOR; Current: MID."
            );
            return;
        }

        if (p == Ticket.BusinessPriority.HIGH
                && s == UserDeveloper.Seniority.JUNIOR) {
            error = error(
                    "Developer " + dev.getUsername() +
                            " cannot assign ticket " + ticket.getId() +
                            " due to seniority level. Required: MID, SENIOR; Current: JUNIOR."
            );
            return;
        }

        if (p == Ticket.BusinessPriority.CRITICAL
                && s == UserDeveloper.Seniority.JUNIOR) {
            error = error(
                    "Developer " + dev.getUsername() +
                            " cannot assign ticket " + ticket.getId() +
                            " due to seniority level. Required: SENIOR; Current: JUNIOR."
            );
            return;
        }

        ticket.assign(dev, system);
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
        node.put("command", "assignTicket");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        node.put("error", msg);
        return node;
    }

}

