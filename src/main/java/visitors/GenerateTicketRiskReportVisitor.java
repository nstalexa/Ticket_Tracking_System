package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GenerateTicketRiskReportVisitor implements CommandVisitor {

    @Getter
    private ObjectNode node;
    private GenerateTicketRiskReport command;


    public GenerateTicketRiskReportVisitor(GenerateTicketRiskReport command) {
        this.command = command;
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
    }


    @Override
    public void visit(UserManager man, AppSystem system) {
        List<Ticket> ticketList = new ArrayList<>();
        for(Ticket j : system.getTickets()) {
            if(j.getStatus().equals(Ticket.Status.OPEN) || j.getStatus().equals(Ticket.Status.IN_PROGRESS)) {
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

        double bugRiskSum = 0.0;
        double frRiskSum = 0.0;
        double uiRiskSum = 0.0;

        for(Ticket t : ticketList) {
            double risk = t.getTicketRisk();

            if (t.getType() == Ticket.TicketType.BUG) {
                bugCount++;
                bugRiskSum += risk;
            } else if (t.getType() == Ticket.TicketType.FEATURE_REQUEST) {
                featureRequestCount++;
                frRiskSum += risk;
            } else if (t.getType() == Ticket.TicketType.UI_FEEDBACK) {
                uiFeedbackCount++;
                uiRiskSum += risk;
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
        resultNode.put("command", "generateTicketRiskReport");
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
        ObjectNode finalNode = reportNode.putObject("riskByType");
        finalNode.put("BUG", getRiskString(round(bugRiskSum, bugCount)));
        finalNode.put("FEATURE_REQUEST", getRiskString(round(frRiskSum, featureRequestCount)));
        finalNode.put("UI_FEEDBACK", getRiskString(round(uiRiskSum, uiFeedbackCount)));
        this.node = resultNode;




    }

    @Override
    public void visit(UserReporter u, AppSystem system) {
    }


    private double round(double sum, int count) {
        if (count == 0) return 0.00;
        double avg = sum / count;
        return Math.round(avg * 100.0) / 100.0;
    }

    private String getRiskString(double val) {
        if (val >= 0 && val <= 24.0) {
            return "NEGLIGIBLE";
        } else if (val >= 25 && val <= 49.0) {
            return "MODERATE";
        } else if (val >= 50 && val <= 74.0) {
            return "SIGNIFICANT";
        } else {
            return "MAJOR";
        }
    }

}

