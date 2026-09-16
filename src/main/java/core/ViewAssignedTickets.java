package core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import visitors.ViewAssignedTicketsVisitor;

import java.time.LocalDate;

import static core.AppSystem.MAPPER;

public class ViewAssignedTickets extends Command {

    public ViewAssignedTickets(User user, String username, LocalDate ts) {
        super(user, username, ts, CmdType.viewAssignedTickets);
    }

    @Override
    public ObjectNode execute(AppSystem system) {

        ViewAssignedTicketsVisitor visitor =
                new ViewAssignedTicketsVisitor(this);

        getUser().accept(visitor, system);

        if (visitor.getErrorNode() != null) {
            return visitor.getErrorNode();
        }

        ObjectNode out = MAPPER.createObjectNode();
        out.put("command", "viewAssignedTickets");
        out.put("username", getUser().getUsername());
        out.put("timestamp", getTs().toString());

        ArrayNode arr = out.putArray("assignedTickets");
        for (Ticket t : visitor.getTickets()) {
            ObjectNode ticketJson = AppSystem.MAPPER.createObjectNode();

            ticketJson.put("id", t.getId());
            ticketJson.put("type", t.getType().toString());
            ticketJson.put("title", t.getTitle());
            ticketJson.put("businessPriority", t.getBusinessPriority().toString());
            ticketJson.put("status", t.getStatus().toString());
            ticketJson.put("createdAt", t.getCreatedAt().toString());
            ticketJson.put("assignedAt", t.getAssignedAt() != null ? t.getAssignedAt().toString() : "");
            ticketJson.put("reportedBy", t.getReportedBy());

            ArrayNode commentsArray = MAPPER.createArrayNode();
            for (Comment c : t.getComments()) {
                ObjectNode commentNode = MAPPER.createObjectNode();
                commentNode.put("author", c.getAuthor().getUsername());
                commentNode.put("content", c.getContent());
                commentNode.put("createdAt", c.getCreatedAt().toString());
                commentsArray.add(commentNode);
            }

            ticketJson.set("comments", commentsArray);
            arr.add(ticketJson);
        }


        return out;
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public void undo() {
    }
}
