package core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class User implements Observer {
    @Getter
    protected String username;
    protected String email;
    @Getter
    protected Role role;
    public enum Role {
        REPORTER,
        DEVELOPER,
        MANAGER
    }

    public User(String username, String email, Role role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }
    protected List<Notification> notifications = new ArrayList<>();

    @Override
    public void update(Notification n) {
        notifications.add(n);
    }

    public abstract void accept(CommandVisitor cmdVisitor, AppSystem system);

}