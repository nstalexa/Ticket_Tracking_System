package core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import visitors.ViewTicketsVisitor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static core.AppSystem.MAPPER;

public class ViewTickets extends Command {
    @Getter
    @Setter
    private AppSystem system;
    public ViewTickets(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.viewTickets);
    }

    public ObjectNode execute(AppSystem system) {
        ObjectNode ret = MAPPER.createObjectNode();
        ret.put("command", "viewTickets");
        ret.put("username", this.username);
        ret.put("timestamp", getTs().toString());

        ViewTicketsVisitor visitor = new ViewTicketsVisitor(this);
        getUser().accept(visitor, system);
        List<Ticket> tickets = new ArrayList<>(visitor.getRes());

        tickets.sort(
                Comparator
                        .comparing(Ticket::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Ticket::getId)
        );

        ArrayNode ticketsArray = MAPPER.createArrayNode();

        for (Ticket t : tickets) {
            if(t != null) {
                ObjectNode ticketNode = MAPPER.createObjectNode();

                ticketNode.put("id", t.getId());
                ticketNode.put("type", t.getType().name());
                ticketNode.put("title", t.getTitle());
                ticketNode.put("businessPriority", t.getBusinessPriority().toString());
                ticketNode.put("status", t.getStatus().toString());
                ticketNode.put("createdAt", t.getCreatedAt().toString());
                ticketNode.put("assignedAt", t.getAssignedAt() == null ? "" : t.getAssignedAt().toString());
                ticketNode.put("solvedAt", t.getSolvedAt() == null ? "" : t.getSolvedAt().toString());
                ticketNode.put("assignedTo", t.getDeveloper() == null ? "" : t.getDeveloper().getUsername());
                ticketNode.put("reportedBy", t.getReportedBy() != null ? t.getReportedBy() : "");

                ArrayNode commentsArray = MAPPER.createArrayNode();
                for (Comment c : t.getComments()) {
                    ObjectNode commentNode = MAPPER.createObjectNode();
                    commentNode.put("author", c.getAuthor().getUsername());
                    commentNode.put("content", c.getContent());
                    commentNode.put("createdAt", c.getCreatedAt().toString());
                    commentsArray.add(commentNode);
                }
                ticketNode.set("comments", commentsArray);

                ticketsArray.add(ticketNode);
            }
        }

        ret.set("tickets", ticketsArray);

        return ret;
    }


    @Override
    public boolean canUndo() {
        return false;
    }
}