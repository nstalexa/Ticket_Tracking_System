package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static core.AppSystem.MAPPER;

public class ViewMilestonesVisitor implements CommandVisitor {

    private final ArrayNode output;

    @Getter
    @Setter
    private ObjectNode ret;

    public ViewMilestonesVisitor(ArrayNode output) {
        this.output = output;
    }

    @Override
    public void visit(UserManager userManager, AppSystem system) {
        addMilestonesManager(system, userManager);
    }

    @Override
    public void visit(UserDeveloper userDeveloper, AppSystem system) {
        addMilestonesDev(system, userDeveloper);
    }

    @Override
    public void visit(UserReporter userReporter, AppSystem system) {
    }

    private void addMilestonesDev(AppSystem system, User user) {
        List<Milestone> listMilestones = system.getMilestones();
        listMilestones.sort(new Comparator<Milestone>() {
            @Override
            public int compare(Milestone o1, Milestone o2) {
                if(o1.getDue().isAfter(o2.getDue())) {
                    return 1;
                } else if(o1.getDue().isBefore(o2.getDue())) {
                    return -1;
                } else {
                    return(o1.getName().compareTo(o2.getName()));
                }
            }
        });
        for (Milestone m : listMilestones) {
            if(m.getAssignedDevs().contains(user.getUsername())) {
                ObjectNode node = MAPPER.createObjectNode();
                node.put("name", m.getName());
                ArrayNode list = MAPPER.createArrayNode();
                for(String i : m.getBlockingFor()) {
                    list.add(i);
                }
                node.put("blockingFor", list);
                node.put("dueDate", m.getDue().toString());
                node.put("createdAt", m.getTimeCreated().toString());
                ArrayNode ids = MAPPER.createArrayNode();
                for (Ticket i : m.getTickets()) {
                    ids.add(i.getId());
                }
                node.put("tickets", ids);

                ArrayNode devs = MAPPER.createArrayNode();
                for(String i : m.getAssignedDevs()) {
                    devs.add(i);
                }
                node.put("assignedDevs", devs);
                node.put("createdBy", m.getUserCreator());
                node.put("status", m.getStatus().toString());
                node.put("isBlocked", m.isBlocked());

                if(AppSystem.daysBetween(system.getNow(), m.getDue()) > 0) {
                    node.put("daysUntilDue", AppSystem.daysBetween(system.getNow(), m.getDue()));
                } else {
                    node.put("daysUntilDue", 0);
                }

                LocalDate endDate;

                if (m.getStatus() == Milestone.Status.COMPLETED) {
                    endDate = m.getCompletedNow();
                } else {
                    endDate = system.getNow();
                }

                int overdueBy = 0;
                if (endDate.isAfter(m.getDue())) {
                    overdueBy = AppSystem.daysBetween(
                            m.getDue(),
                            endDate
                    );
                }

                node.put("overdueBy", overdueBy);

                ArrayNode tickets = MAPPER.createArrayNode();
                for (Ticket i : m.getTickets()) {
                    if(!i.getStatus().equals(Ticket.Status.CLOSED)) {
                        tickets.add(i.getId());
                    }
                }
                node.put("openTickets", tickets);
                tickets = MAPPER.createArrayNode();
                for (Ticket i : m.getTickets()) {
                    if(i.getStatus().equals(Ticket.Status.CLOSED)) {
                        tickets.add(i.getId());
                    }
                }
                node.put("closedTickets", tickets);
                node.put("completionPercentage", m.getCompletionPercentage());
                node.put("repartition", m.repartition(system));

                output.add(node);

            }
        }
        ret.put("milestones", output);
    }

    private void addMilestonesManager(AppSystem system, User user) {
        List<Milestone> listMilestones = system.getMilestones();
        listMilestones.sort(new Comparator<Milestone>() {
            @Override
            public int compare(Milestone o1, Milestone o2) {
                if(o1.getDue().isAfter(o2.getDue())) {
                    return 1;
                } else if(o1.getDue().isBefore(o2.getDue())) {
                    return -1;
                } else {
                    return(o1.getName().compareTo(o2.getName()));
                }
            }
        });
        for (Milestone m : listMilestones) {
            if(m.getUserCreator().equals(user.getUsername())) {
                ObjectNode node = MAPPER.createObjectNode();
                node.put("name", m.getName());
                ArrayNode list = MAPPER.createArrayNode();
                for(String i : m.getBlockingFor()) {
                    list.add(i);
                }
                node.put("blockingFor", list);
                node.put("dueDate", m.getDue().toString());
                node.put("createdAt", m.getTimeCreated().toString());
                ArrayNode ids = MAPPER.createArrayNode();
                for(Ticket i : m.getTickets()) {
                    ids.add(i.getId());
                }
                node.put("tickets", ids);
                ArrayNode devs = MAPPER.createArrayNode();
                for(String i : m.getAssignedDevs()) {
                    devs.add(i);
                }
                node.put("assignedDevs", devs);
                node.put("createdBy", m.getUserCreator());
                node.put("status", m.getStatus().toString());
                node.put("isBlocked", m.isBlocked());

                if(AppSystem.daysBetween(system.getNow(), m.getDue()) > 0 && !m.getStatus().equals(Milestone.Status.COMPLETED)) {
                    node.put("daysUntilDue", AppSystem.daysBetween(system.getNow(), m.getDue()));
                } else if(AppSystem.daysBetween(system.getNow(), m.getDue()) <= 0 && !m.getStatus().equals(Milestone.Status.COMPLETED)){
                    node.put("daysUntilDue", 0);
                } else if(AppSystem.daysBetween(m.getCompletedNow(), m.getDue()) > 0 && m.getStatus().equals(Milestone.Status.COMPLETED)) {
                    node.put("daysUntilDue", AppSystem.daysBetween(m.getCompletedNow(), m.getDue()));
                } else {
                    node.put("daysUntilDue", 0);
                }
                LocalDate endDate;

                if (m.getStatus() == Milestone.Status.COMPLETED) {
                    endDate = m.getCompletedNow();
                } else {
                    endDate = system.getNow();
                }

                int overdueBy = 0;
                if (endDate.isAfter(m.getDue())) {
                    overdueBy = AppSystem.daysBetween(
                            m.getDue(),
                            endDate
                    );
                }

                node.put("overdueBy", overdueBy);

                ArrayNode tickets = MAPPER.createArrayNode();
                for (Ticket i : m.getTickets()) {
                    if(!i.getStatus().equals(Ticket.Status.CLOSED)) {
                        tickets.add(i.getId());
                    }
                }
                node.put("openTickets", tickets);
                tickets = MAPPER.createArrayNode();
                for (Ticket i : m.getTickets()) {
                    if(i.getStatus().equals(Ticket.Status.CLOSED)) {
                        tickets.add(i.getId());
                    }
                }
                node.put("closedTickets", tickets);
                node.put("completionPercentage", m.getCompletionPercentage());
                node.put("repartition", m.repartition(system));

                output.add(node);

            }
        }
        ret.put("milestones", output);
    }
}
