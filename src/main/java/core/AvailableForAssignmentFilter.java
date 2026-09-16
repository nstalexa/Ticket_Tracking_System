package core;

public class AvailableForAssignmentFilter implements SearchInterface<Ticket> {

    @Override
    public boolean found(Ticket ticket, SearchContext ctx) {

        UserDeveloper dev = null;
        for(UserDeveloper i : ctx.getSystem().getDeveloperList()) {
            if(i.getUsername().equals(ctx.getCurrentUser().getUsername())) {
                dev = i;
            }
        }

        if (ticket.getStatus() != Ticket.Status.OPEN) {
            return false;
        }

        if (dev == null || !ticket.canAssign(dev.getExpertiseArea())) {
            return false;
        }

        if (!ticket.getRequiredSeniorities().contains(dev.getSeniority())) {
            return false;
        }

        Milestone m = ticket.getMilestone();
        if (m != null) {
            if (!m.getAssignedDevs().contains(dev.getUsername())) {
                return false;
            }
            return true;
        }

        return true;
    }
}


