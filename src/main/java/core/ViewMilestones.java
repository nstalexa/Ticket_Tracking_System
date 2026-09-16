package core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import visitors.ViewMilestonesVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class ViewMilestones extends Command {

    public ViewMilestones(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.viewMilestones);
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        ObjectNode ret = MAPPER.createObjectNode();

        ret.put("command", "viewMilestones");
        ret.put("username", getUser().getUsername());
        ret.put("timestamp", getTs().toString());

        ArrayNode milestonesArray = MAPPER.createArrayNode();

        ViewMilestonesVisitor visitor =
                new ViewMilestonesVisitor(milestonesArray);

        visitor.setRet(ret);
        getUser().accept(visitor, system);
        return ret;
    }

    @Override
    public boolean canUndo() {
        return false;
    }
}
