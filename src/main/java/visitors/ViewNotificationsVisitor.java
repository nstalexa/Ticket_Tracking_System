package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.*;

import lombok.Setter;

import static core.AppSystem.MAPPER;

public class ViewNotificationsVisitor implements CommandVisitor {

    @Setter
    private ObjectNode output;

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
        ArrayNode arr = MAPPER.createArrayNode();

        for (Notification n : dev.getNotifications()) {
            arr.add(n.getMessage());
        }

        dev.getNotifications().clear();

        output.set("notifications", arr);
    }

    @Override
    public void visit(UserManager manager, AppSystem system) {
    }

    @Override
    public void visit(UserReporter reporter, AppSystem system) {
    }
}
