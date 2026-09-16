package core;

import java.util.ArrayList;
import java.util.List;

public class Invoker {

    private List<Command> cmdList = new ArrayList<>();

    public void execute(Command cmd, AppSystem system) {
        cmd.execute(system);
        if(cmd.canUndo()) {
            cmdList.add(cmd);
        }
    }

}
