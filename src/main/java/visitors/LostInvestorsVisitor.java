package visitors;

import core.*;

public class LostInvestorsVisitor implements CommandVisitor {

    @Override
    public void visit(UserManager userManager, AppSystem system) {
        system.setTestingPhase(false);
    }

    @Override
    public void visit(UserDeveloper userDeveloper, AppSystem system) {

    }

    @Override
    public void visit(UserReporter userReporter, AppSystem system) {

    }
}