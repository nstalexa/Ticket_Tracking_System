package visitors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;
        import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class AppStabilityReportVisitor implements CommandVisitor {

    @Getter
    private ObjectNode node;
    private AppStabilityReport command;


    public AppStabilityReportVisitor(AppStabilityReport command) {
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

        double bugImpact = 0.0;
        double featureRequestImpact = 0.0;
        double uiFeedbackImpact = 0.0;

        double bugRiskSum = 0.0;
        double frRiskSum = 0.0;
        double uiRiskSum = 0.0;

        for(Ticket t : ticketList) {
            double impact = t.calculateCustomerImpact();
            double risk = t.getTicketRisk();

            if (t.getType() == Ticket.TicketType.BUG) {
                bugCount++;
                bugRiskSum += risk;
                bugImpact += impact;
            } else if (t.getType() == Ticket.TicketType.FEATURE_REQUEST) {
                featureRequestCount++;
                frRiskSum += risk;
                featureRequestImpact += impact;
            } else if (t.getType() == Ticket.TicketType.UI_FEEDBACK) {
                uiFeedbackCount++;
                uiRiskSum += risk;
                uiFeedbackImpact += impact;
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

        String riskBug = getRiskString(bugRiskSum/bugCount);
        String riskFR = getRiskString(frRiskSum/featureRequestCount);
        String riskUI = getRiskString(uiRiskSum/uiFeedbackCount);

        String stabilityStatus = getStability(ticketList.size(), riskBug, riskFR,
                riskUI, bugRiskSum/bugCount, frRiskSum/featureRequestCount,
                uiRiskSum/uiFeedbackCount);

        ObjectNode resultNode = AppSystem.MAPPER.createObjectNode();
        resultNode.put("command", "appStabilityReport");
        resultNode.put("username", command.getUser().getUsername());
        resultNode.put("timestamp", command.getTs().toString());

        ObjectNode reportNode = resultNode.putObject("report");
        reportNode.put("totalOpenTickets", ticketList.size());

        ObjectNode typeNode = reportNode.putObject("openTicketsByType");
        typeNode.put("BUG", bugCount);
        typeNode.put("FEATURE_REQUEST", featureRequestCount);
        typeNode.put("UI_FEEDBACK", uiFeedbackCount);

        ObjectNode priorityNode = reportNode.putObject("openTicketsByPriority");
        priorityNode.put("LOW", lowCount);
        priorityNode.put("MEDIUM", mediumCount);
        priorityNode.put("HIGH", highCount);
        priorityNode.put("CRITICAL", criticalCount);

        ObjectNode riskNode = reportNode.putObject("riskByType");
        riskNode.put("BUG", getRiskString(round(bugRiskSum, bugCount)));
        riskNode.put("FEATURE_REQUEST", getRiskString(round(frRiskSum, featureRequestCount)));
        riskNode.put("UI_FEEDBACK", getRiskString(round(uiRiskSum, uiFeedbackCount)));

        ObjectNode finalNode = reportNode.putObject("impactByType");
        finalNode.put("BUG", round(bugImpact, bugCount));
        finalNode.put("FEATURE_REQUEST", round(featureRequestImpact, featureRequestCount));
        finalNode.put("UI_FEEDBACK", round(uiFeedbackImpact, uiFeedbackCount));

        reportNode.put("appStability", stabilityStatus);

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

    private String getRiskString(double val) {
        if (val <= 24.0) {
            return "NEGLIGIBLE";
        } else if (val <= 49.0) {
            return "MODERATE";
        } else if (val <= 74.0) {
            return "SIGNIFICANT";
        } else {
            return "MAJOR";
        }
    }

    private String getStability(int totalTickets,
                                      String riskBug, String riskFR, String riskUI,
                                      double impactBug, double impactFR, double impactUI) {

        if (totalTickets == 0) {
            return "STABLE";
        }

        boolean allRisksNegligible = riskBug.equals("NEGLIGIBLE") &&
                riskFR.equals("NEGLIGIBLE") &&
                riskUI.equals("NEGLIGIBLE");

        boolean allImpactsLow = impactBug < 50.0 && impactFR < 50.0 && impactUI < 50.0;

        if (allRisksNegligible && allImpactsLow) {
            return "STABLE";
        }

        boolean hasSignificantOrWorse = isSignificantOrWorse(riskBug) ||
                isSignificantOrWorse(riskFR) ||
                isSignificantOrWorse(riskUI);

        if (hasSignificantOrWorse) {
            return "UNSTABLE";
        }

        return "PARTIALLY STABLE";
    }

    private boolean isSignificantOrWorse(String label) {
        return label.equals("SIGNIFICANT") || label.equals("MAJOR");
    }

}

