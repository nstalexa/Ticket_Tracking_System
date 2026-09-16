package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public abstract class Command {

    @Getter
    private final CmdType type;
    public enum CmdType {
        lostInvestors,
        startTestingPhase,
        viewTickets,
        reportTicket,
        addComment,
        undoAddComment,
        createMilestone,
        viewMilestones,
        assignTicket,
        undoAssignTicket,
        viewAssignedTickets,
        changeStatus,
        undoChangeStatus,
        viewTicketHistory,
        viewNotifications,
        search,
        generatePerformanceReport,
        generateTicketRiskReport,
        generateResolutionEfficiencyReport,
        generateCustomerImpactReport,
        appStabilityReport
    }

    @Getter
    private final User user;
    @Getter
    private final LocalDate ts;
    @Getter
    protected final String username;

    @Getter
    @Setter
    private String stringComment;

    public Command(final User user, final String username, final LocalDate ts, final CmdType type) {
        this.user = user;
        this.username = username;
        this.ts = ts;
        this.type = type;
    }

    public abstract ObjectNode execute(AppSystem system);

    public boolean canUndo() {
        return false;
    }

    public void undo() {
    }
}
