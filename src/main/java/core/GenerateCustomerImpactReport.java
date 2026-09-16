package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.AssignTicketVisitor;
import visitors.ChangeStatusVisitor;
import visitors.GenerateCustomerImpactReportVisitor;
import visitors.UndoChangeStatusVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class GenerateCustomerImpactReport extends Command {
    private int ticketId;

    @Getter
    @Setter
    private boolean undo;


    public GenerateCustomerImpactReport(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.generateCustomerImpactReport);
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        GenerateCustomerImpactReportVisitor visitor = new GenerateCustomerImpactReportVisitor(this);
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