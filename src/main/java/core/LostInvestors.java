package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.LostInvestorsVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class LostInvestors extends Command {
    @Getter
    @Setter
    private AppSystem system;
    public LostInvestors(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.lostInvestors);
    }

    public ObjectNode execute(AppSystem system) {
        ObjectNode ret = MAPPER.createObjectNode();
        getUser().accept(new LostInvestorsVisitor(), system);
        return ret;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

}