package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.*;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class StartTestingPhase extends Command {
    private int ticketId;

    @Getter
    @Setter
    private boolean undo;


    public StartTestingPhase(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.startTestingPhase);
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        StartTestingPhaseVisitor visitor = new StartTestingPhaseVisitor(this);
        getUser().accept(visitor, system);

        return visitor.getNode();
    }



    @Override
    public boolean canUndo() {
        return false;
    }

    public void undo(AppSystem system) {
    }

}
