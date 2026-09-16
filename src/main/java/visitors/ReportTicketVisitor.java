package visitors;

import core.*;

public class ReportTicketVisitor implements CommandVisitor {
    private Ticket ticket;
    private Command command;

    public ReportTicketVisitor(Command command) {
        this.command = command;
    }
    @Override
    public void visit(UserManager userManager, AppSystem system) {

    }

    @Override
    public void visit(UserDeveloper userDeveloper, AppSystem system) {

    }

    @Override
    public void visit(UserReporter userReporter, AppSystem system) {
        system.getTickets().add(ticket);
    }

}