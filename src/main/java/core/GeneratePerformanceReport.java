package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.*;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class GeneratePerformanceReport extends Command {
    @Getter
    @Setter
    private boolean undo;


    public GeneratePerformanceReport(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.generatePerformanceReport);
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        GeneratePerformanceReportVisitor visitor = new GeneratePerformanceReportVisitor(this);
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