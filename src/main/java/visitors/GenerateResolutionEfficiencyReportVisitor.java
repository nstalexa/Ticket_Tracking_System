package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;
        import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GenerateResolutionEfficiencyReportVisitor implements CommandVisitor {

    @Getter
    private ObjectNode node;
    private GenerateResolutionEfficiencyReport command;


    public GenerateResolutionEfficiencyReportVisitor(GenerateResolutionEfficiencyReport command) {
        this.command = command;
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
    }


    @Override
    public void visit(UserManager man, AppSystem system) {
        List<Ticket> ticketList = new ArrayList<>();
        for(Ticket j : system.getTickets()) {
            if(j.getStatus().equals(Ticket.Status.CLOSED) || j.getStatus().equals(Ticket.Status.RESOLVED)) {
                ticketList.add(j);
            }
        }

        int bugCount = 0;
        int featureRequestCount = 0;
        int uiFeedbackCount = 0;

        int lowCount = 0;
        int mediumCount = 0;
        int highCount = 0;
        int criticalCount = 0;


        double bugEfficiency = 0.0;
        double frEfficiency = 0.0;
        double uiEfficiency = 0.0;

        for(Ticket t : ticketList) {
            if (t.getType() == Ticket.TicketType.BUG) {
                bugCount++;
                bugEfficiency += t.getEfficiency();

            } else if (t.getType() == Ticket.TicketType.FEATURE_REQUEST) {
                featureRequestCount++;
                frEfficiency += t.getEfficiency();
            } else if (t.getType() == Ticket.TicketType.UI_FEEDBACK) {
                uiFeedbackCount++;
                uiEfficiency += t.getEfficiency();
            }

            if (t.getBusinessPriority() == Ticket.BusinessPriority.LOW) {
                lowCount++;
            } else if (t.getBusinessPriority() == Ticket.BusinessPriority.MEDIUM) {
                mediumCount++;
            } else if (t.getBusinessPriority() == Ticket.BusinessPriority.HIGH) {
                highCount++;
            } else if (t.getBusinessPriority() == Ticket.BusinessPriority.CRITICAL) {
                criticalCount++;
            }

        }


        ObjectNode resultNode = AppSystem.MAPPER.createObjectNode();
        resultNode.put("command", "generateResolutionEfficiencyReport");
        resultNode.put("username", command.getUser().getUsername());
        resultNode.put("timestamp", command.getTs().toString());

        ObjectNode reportNode = resultNode.putObject("report");
        reportNode.put("totalTickets", ticketList.size());

        ObjectNode typeNode = reportNode.putObject("ticketsByType");
        typeNode.put("BUG", bugCount);
        typeNode.put("FEATURE_REQUEST", featureRequestCount);
        typeNode.put("UI_FEEDBACK", uiFeedbackCount);

        ObjectNode priorityNode = reportNode.putObject("ticketsByPriority");
        priorityNode.put("LOW", lowCount);
        priorityNode.put("MEDIUM", mediumCount);
        priorityNode.put("HIGH", highCount);
        priorityNode.put("CRITICAL", criticalCount);
        ObjectNode finalNode = reportNode.putObject("efficiencyByType");
        finalNode.put("BUG", round(bugEfficiency, bugCount));
        finalNode.put("FEATURE_REQUEST", round(frEfficiency, featureRequestCount));
        finalNode.put("UI_FEEDBACK", round(uiEfficiency, uiFeedbackCount));
        this.node = resultNode;


    }

    @Override
    public void visit(UserReporter u, AppSystem system) {
    }

    private ObjectNode node(String msg) {
        ObjectNode node = AppSystem.MAPPER.createObjectNode();
        node.put("command", "changeStatus");
        node.put("username", command.getUser().getUsername());
        node.put("timestamp", command.getTs().toString());
        return node;
    }

    private double round(double sum, int count) {
        if (count == 0) return 0.00;
        double avg = sum / count;
        return Math.round(avg * 100.0) / 100.0;
    }


}

