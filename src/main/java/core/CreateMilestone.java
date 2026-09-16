package core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;
import lombok.Getter;
import lombok.Setter;
import visitors.CreateMilestoneVisitor;

import static core.AppSystem.MAPPER;

public class CreateMilestone extends Command {

    @Getter
    AppSystem system;
    @Getter
    @Setter
    List<Integer> Ids;
    @Getter
    @Setter
    Milestone milestone;

    public CreateMilestone(User user, String username, LocalDate ts, List<Integer> Ids) {
        super(user, username, ts, CmdType.createMilestone);
        this.Ids = Ids;
    }

    public ObjectNode execute(AppSystem system) {
        ObjectNode ret = MAPPER.createObjectNode();

        if(!system.hasManager(username)) {
            ret.put("command", "createMilestone");
            ret.put("username", getUser().getUsername());
            ret.put("timestamp", getTs().toString());
            ret.put("error", "The user does not have permission to execute this command: required " +
                    "role MANAGER; user role " + getUser().getRole().toString().toUpperCase() + ".");
            return ret;
        }
        if (system.isTestingPhase()) {
            ret.put("command", "createMilestone");
            ret.put("username", getUser().getUsername());
            ret.put("timestamp", getTs().toString());
            ret.put("error", "Cannot create milestone during testing phase.");
            CreateMilestoneVisitor visitor = new CreateMilestoneVisitor(milestone);
            getUser().accept(visitor, system);
            return ret;
        }

        for (String milestoneName : milestone.getBlockingFor()) {
            Milestone curr = system.getMilestoneByName(milestoneName);

            if (curr != null) {
                curr.setBlocked(true);
            }
        }

        for (Integer id : Ids) {
            Ticket t = system.getTicketById(id);

            if(t != null) {
                if (t.getMilestone() != null) {
                    ret.put("command", "createMilestone");
                    ret.put("username", getUser().getUsername());
                    ret.put("timestamp", getTs().toString());
                    ret.put(
                            "error",
                            "Tickets " + id + " already assigned to milestone " +
                                    t.getMilestone().getName() + "."
                    );

                    return ret;
                }
            }
        }

        String[] blockingFor = new String[Ids.size()];

        for (int i = 0; i < Ids.size(); i++) {
            blockingFor[i] = String.valueOf(Ids.get(i));
        }

        List<Ticket> tickets = new ArrayList<>();
        for (Integer id : Ids) {
            Ticket t = system.getTicketById(id);
            if (t != null) {
                t.setMilestone(milestone);
                tickets.add(t);
                t.getHistory().add(new Action(
                        milestone.getName(),
                        username,
                        system.getNow().toString(),
                        "ADDED_TO_MILESTONE",
                        null,
                        null
                ));
            }
        }

        milestone.setTickets(tickets);
        CreateMilestoneVisitor visitor = new CreateMilestoneVisitor(milestone);
        getUser().accept(visitor, system);
        return null;
    }

    @Override
    public boolean canUndo() {
        return false;
    }
}