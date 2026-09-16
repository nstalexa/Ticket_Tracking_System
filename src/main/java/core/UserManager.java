package core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class UserManager extends User {
    @Getter
    @Setter
    private String hireDate;
    @Getter
    @Setter
    private List<String> subordinates;

    public UserManager(String username, String email, User.Role role,
                       String hireDate, List<String> subordinates) {
        super(username, email, role);
        this.hireDate = hireDate;
        this.subordinates = subordinates;
    }
    public void accept(CommandVisitor cmdVisitor, AppSystem system) {
        cmdVisitor.visit(this, system);
    }
}