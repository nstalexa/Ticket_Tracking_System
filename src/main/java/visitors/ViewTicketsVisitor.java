package visitors;

import core.*;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

public class ViewTicketsVisitor implements CommandVisitor {
    @Getter
    private Set<Ticket> res = new LinkedHashSet<>();
    private ViewTickets command;

    public ViewTicketsVisitor(ViewTickets command) {
        this.command = command;
    }

    @Override
    public void visit(UserManager userManager, AppSystem system) {
        res.addAll(system.getTickets());
    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
        for(Ticket t : system.getTickets()) {
            if(t.getStatus() == Ticket.Status.OPEN && t.getMilestone().getAssignedDevs().contains(dev.getUsername())) {
                res.add(t);
            }
        }
    }

    @Override
    public void visit(UserReporter userReporter, AppSystem system) {
        for(Ticket i : system.getTickets()) {
            if (i != null && i.getReportedBy() != null &&
                    !i.getReportedBy().isEmpty() &&
                    i.getReportedBy().equals(userReporter.getUsername())) {
                res.add(i);
            }

        }
    }

}