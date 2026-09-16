package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import core.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class GeneratePerformanceReportVisitor implements CommandVisitor {

    @Getter
    private ObjectNode node;
    private GeneratePerformanceReport command;


    public GeneratePerformanceReportVisitor(GeneratePerformanceReport command) {
        this.command = command;
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
    }


    @Override
    public void visit(UserManager man, AppSystem system) {
        LocalDate date = command.getTs();
        LocalDate month = date.minusMonths(1);
        int lastMonth = month.getMonthValue();
        int lastYear = month.getYear();




        List<UserDeveloper> devList = new ArrayList<>();
        for(String i : man.getSubordinates()) {
            for(UserDeveloper j : system.getDeveloperList()) {
                if(i.equals(j.getUsername())) {
                    devList.add(j);
                }
            }
        }

        Collections.sort(devList, new Comparator<UserDeveloper>() {
            @Override
            public int compare(UserDeveloper o1, UserDeveloper o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        });


        ArrayNode reportArray = AppSystem.MAPPER.createArrayNode();
        for(UserDeveloper dev : devList) {
            List<Ticket> closedTickets = new ArrayList<>();
            for(Ticket i : dev.getTickets()) {
                if(i.getStatus() == Ticket.Status.CLOSED && i.getSolvedAt() != null) {
                    if(i.getSolvedAt().getMonthValue() == lastMonth && i.getSolvedAt().getYear() == lastYear) {
                        closedTickets.add(i);
                    }
                }
            }
            double performanceScore = 0.0;
            double averageResolutionTime = 0.0;
            int closedCount = closedTickets.size();

            if (closedCount > 0) {
                int bugCount = 0;
                int featureRequestCount = 0;
                int uiFeedbackCount = 0;
                int highPriorityCount = 0;
                double totalDays = 0.0;

                for(Ticket t : closedTickets) {
                    if (t.getType() == Ticket.TicketType.BUG) {
                        bugCount++;
                    } else if (t.getType() == Ticket.TicketType.FEATURE_REQUEST) {
                        featureRequestCount++;
                    } else if (t.getType() == Ticket.TicketType.UI_FEEDBACK) {
                        uiFeedbackCount++;
                    }

                    if(t.getBusinessPriority() == Ticket.BusinessPriority.HIGH ||
                            t.getBusinessPriority() == Ticket.BusinessPriority.CRITICAL) {
                        highPriorityCount++;
                    }


                    long days = AppSystem.daysBetween(t.getAssignedAt(), t.getSolvedAt());
                    totalDays += days;
                }

                averageResolutionTime = totalDays / closedCount;

                UserDeveloper.Seniority seniority = dev.getSeniority();


                if (seniority == UserDeveloper.Seniority.JUNIOR) {
                    double diversity = ticketDiversityFactor(bugCount, featureRequestCount, uiFeedbackCount);
                    double raw = (0.5 * closedCount) - diversity;
                    performanceScore = Math.max(0, raw) + 5;

                } else if (seniority == UserDeveloper.Seniority.MID) {
                    double raw = (0.5 * closedCount) + (0.7 * highPriorityCount) - (0.3 * averageResolutionTime);
                    performanceScore = Math.max(0, raw) + 15;

                } else if (seniority == UserDeveloper.Seniority.SENIOR) {
                    double raw = (0.5 * closedCount) + (1.0 * highPriorityCount) - (0.5 * averageResolutionTime);
                    performanceScore = Math.max(0, raw) + 30;
                }

                dev.setPerformanceScore(performanceScore);
            }

            ObjectNode devNode = AppSystem.MAPPER.createObjectNode();
            devNode.put("username", dev.getUsername());
            devNode.put("closedTickets", closedCount);
            devNode.put("averageResolutionTime", Math.round(averageResolutionTime * 100.0) / 100.0);
            devNode.put("performanceScore", Math.round(performanceScore * 100.0) / 100.0);
            devNode.put("seniority", dev.getSeniority().toString());

            reportArray.add(devNode);
        }

        ObjectNode resultNode = AppSystem.MAPPER.createObjectNode();
        resultNode.put("command", "generatePerformanceReport");
        resultNode.put("username", command.getUser().getUsername());
        resultNode.put("timestamp", command.getTs().toString());

        resultNode.set("report", reportArray);

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


    public double averageResolvedTicketType(int bug, int feature, int ui) {
        return (bug + feature + ui) / 3.0;
    }

    public double standardDeviation(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, 2) + Math.pow(feature - mean, 2) + Math.pow(ui - mean, 2)) / 3.0;
        return Math.sqrt(variance);
    }

    public double ticketDiversityFactor(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);

        if (mean == 0.0) {
            return 0.0;
        }

        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }



}

