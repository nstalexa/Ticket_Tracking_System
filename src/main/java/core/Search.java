package core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.SearchInput;
import lombok.Getter;
import lombok.Setter;
import visitors.AssignTicketVisitor;
import visitors.ChangeStatusVisitor;
import visitors.SearchVisitor;
import visitors.UndoChangeStatusVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class Search extends Command {
    private int ticketId;

    @Getter
    @Setter
    private boolean undo;

    private SearchInput searchInput;

    public Search(User user, String username, LocalDate ts, int ticketId, SearchInput searchInput) {
        super(user, username, ts, CmdType.search);
        this.ticketId = ticketId;
        this.searchInput = searchInput;
    }

    @Override
    public ObjectNode execute(AppSystem system) {
        Ticket ticket = system.getTicketById(ticketId);

        SearchVisitor visitor = new SearchVisitor(this, ticket, searchInput);
        ObjectNode out = MAPPER.createObjectNode();
        out.put("command", "search");
        out.put("username", username);
        out.put("timestamp", getTs().toString());

        visitor.setOutput(out);
        getUser().accept(visitor, system);

        return out;
    }



    @Override
    public boolean canUndo() {
        return false;
    }

    public void undo(AppSystem system) {
    }

}