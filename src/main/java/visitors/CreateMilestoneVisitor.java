package visitors;

import core.*;

public class CreateMilestoneVisitor implements CommandVisitor {
    private Milestone milestone;
    private Command command;
    public CreateMilestoneVisitor(Milestone milestone) {
        this.milestone = milestone;
    }
    @Override
    public void visit(UserManager userManager, AppSystem system) {
        system.getMilestones().add(milestone);
        milestone.notifyObservers(Notification.createdMilestone(milestone.getName(), milestone.getDue()));
        for(String i : milestone.getAssignedDevs()) {
            UserDeveloper dev = system.getDevById(i);
            dev.getNotifications().add(Notification.createdMilestone(milestone.getName(), milestone.getDue()));
        }
    }

    @Override
    public void visit(UserDeveloper userDeveloper, AppSystem system) {

    }

    @Override
    public void visit(UserReporter userReporter, AppSystem system) {

    }

}
