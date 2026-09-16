package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import visitors.ViewNotificationsVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class ViewNotifications extends Command {

    public ViewNotifications(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.viewNotifications);
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        ObjectNode out = MAPPER.createObjectNode();
        out.put("command", "viewNotifications");
        out.put("username", username);
        out.put("timestamp", getTs().toString());

        ViewNotificationsVisitor visitor = new ViewNotificationsVisitor();
        visitor.setOutput(out);
        getUser().accept(visitor, system);

        return out;
    }

    @Override
    public boolean canUndo() {
        return false;
    }
}
