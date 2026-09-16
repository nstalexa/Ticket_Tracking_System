package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.*;
import lombok.Getter;
import lombok.Setter;

import static core.AppSystem.MAPPER;

import java.util.List;

public class ViewTicketHistoryVisitor implements CommandVisitor {

    private final ViewTicketHistory command;
    private final AppSystem system;
    @Getter
    @Setter
    private ObjectNode history = MAPPER.createObjectNode();

    public ViewTicketHistoryVisitor(ViewTicketHistory command, AppSystem system) {
        this.command = command;
        this.system = system;
    }

    @Override
    public void visit(UserManager userManager, AppSystem system) {
        ArrayNode arr = MAPPER.createArrayNode();
        for(Milestone i : system.getMilestones()) {
            if(i.getUserCreator().equals(userManager.getUsername())) {
                for(Ticket j : i.getTickets()) {
                    ObjectNode curr = MAPPER.createObjectNode();
                    curr.put("id", j.getId());
                    curr.put("title", j.getTitle());
                    curr.put("status", j.getStatus().toString());
                    curr.put("actions", printHistory(j.getHistory()));
                    ArrayNode comments = MAPPER.createArrayNode();
                    for(Comment com : j.getComments()) {
                        ObjectNode c = MAPPER.createObjectNode();
                        c.put("author", com.getAuthor().getUsername());
                        c.put("content", com.getContent());
                        c.put("createdAt", com.getCreatedAt().toString());
                        comments.add(c);
                    }
                    curr.put("comments", comments);
                    arr.add(curr);
                }
            }
        }

        history.put("command", "viewTicketHistory");
        history.put("username", userManager.getUsername());
        history.put("timestamp", command.getTs().toString());
        history.put("ticketHistory", arr);
    }

    @Override
    public void visit(UserDeveloper userDeveloper, AppSystem system) {
        ArrayNode arr = MAPPER.createArrayNode();
        for(Ticket i : userDeveloper.getAllTickets()) {
            ObjectNode curr = MAPPER.createObjectNode();
            curr.put("id", i.getId());
            curr.put("title", i.getTitle());
            curr.put("status", i.getStatus().toString());
            curr.put("actions", printHistory(i.getHistory()));
            ArrayNode comments = MAPPER.createArrayNode();
            for(Comment com : i.getComments()) {
                ObjectNode c = MAPPER.createObjectNode();
                c.put("author", com.getAuthor().getUsername());
                c.put("content", com.getContent());
                c.put("createdAt", com.getCreatedAt().toString());
                comments.add(c);
            }
            curr.put("comments", comments);
            arr.add(curr);
        }

        history.put("command", "viewTicketHistory");
        history.put("username", userDeveloper.getUsername());
        history.put("timestamp", command.getTs().toString());
        history.put("ticketHistory", arr);
    }

    @Override
    public void visit(UserReporter userReporter, AppSystem system) {
        history.put("command", "viewTicketHistory");
        history.put("username", userReporter.getUsername());
        history.put("timestamp", command.getTs().toString());
        history.put("error", "The user does not have permission to execute this command: required " +
                "role DEVELOPER, MANAGER; user role REPORTER.");
    }

    private ArrayNode printHistory(List<Action> history) {
        ArrayNode ret = MAPPER.createArrayNode();
        for (Action action : history) {
            ObjectNode out = MAPPER.createObjectNode();

            if(action.getMilestone() != null) {
                out.put("milestone", action.getMilestone());
            }
            if(action.getFrom() != null) {
                out.put("from", action.getFrom());
            }
            if(action.getTo() != null) {
                out.put("to", action.getTo());
            }

            if(action.getAction().equals("REMOVED_FROM_DEV")) {
                out.put("timestamp", action.getTimestamp());
                out.put("action", action.getAction());
                out.put("by", action.getBy());
            } else {
                out.put("by", action.getBy());
                out.put("timestamp", action.getTimestamp());
                out.put("action", action.getAction());
            }
            ret.add(out);

            if(action.getAction().equals("DE-ASSIGNED")) {
                break;
            }
        }
        return ret;
    }
}
