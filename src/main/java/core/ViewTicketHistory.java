package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.ChangeStatusVisitor;
import visitors.ViewTicketHistoryVisitor;
import visitors.ViewTicketsVisitor;
import static core.AppSystem.MAPPER;


import java.time.LocalDate;

public class ViewTicketHistory extends Command {

    @Getter
    @Setter
    private AppSystem system;
    public ViewTicketHistory(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.viewTicketHistory);
    }

    @Override
    public ObjectNode execute(AppSystem system) {

        ViewTicketHistoryVisitor visitor = new ViewTicketHistoryVisitor(this, system);
        getUser().accept(visitor, system);

        return visitor.getHistory();
    }
}
