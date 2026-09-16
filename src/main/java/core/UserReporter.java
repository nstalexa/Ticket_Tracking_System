package core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class UserReporter extends User {

    public UserReporter(String username, String email, User.Role role) {
        super(username, email, role);
    }
    public void accept(CommandVisitor cmdVisitor, AppSystem system) {
        cmdVisitor.visit(this, system);
    }
}