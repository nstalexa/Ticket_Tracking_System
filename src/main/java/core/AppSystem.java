package core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppSystem {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Getter
    @Setter
    private boolean testingPhase = true;

    @Getter
    @Setter
    private boolean investorsLost = false;

    @Getter
    @Setter
    private List<Ticket> tickets = new ArrayList<>();

    @Getter
    @Setter
    private static int ticketId = 0;

    @Getter
    @Setter
    private List<User> users = new ArrayList<>();

    @Getter
    private List<Milestone> milestones = new ArrayList<>();
    @Getter
    private List<ObjectNode> output = new ArrayList<>();

    @Getter
    @Setter
    private LocalDate startDay;
    private List<Command> commands = new ArrayList<>();

    @Getter
    private LocalDate now;

    @Getter
    @Setter
    private List<UserDeveloper> developerList = new ArrayList<>();

    @Getter
    @Setter
    private List<UserManager> managerList = new ArrayList<>();

    @Getter
    @Setter
    private List<UserReporter> reporterList = new ArrayList<>();


    @Getter
    @Setter
    private List<Invoker> invokerList = new ArrayList<>();

    public void setCommands(List<Command> commands) {
        this.commands = commands;
        startDay = commands.getFirst().getTs();
        now = startDay;
    }

    public AppSystem() {
        this.testingPhase = true;
        this.investorsLost = false;
    }

    public static int nextTicketId() {
        return ticketId++;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }


    public List<ObjectNode> iterate() {
        List<ObjectNode> list = new ArrayList<>();
        int curr = 0;

        while(!investorsLost && curr < commands.size()) {
            testingPhaseDone();
            update(now);
            while (commands.get(curr).getTs().isBefore(now)) {
                curr++;
            }
            while(curr < commands.size() && commands.get(curr).getTs().equals(now)) {


                ObjectNode aux = commands.get(curr).execute(this);
                if(aux != null && !aux.isEmpty()) {
                    list.add(aux);
                }
                curr++;
            }
            now = now.plusDays(1);
        }
        return list;
    }

    public static int daysBetween(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

   public void testingPhaseDone() {
        if(investorsLost == false && daysBetween(startDay, now) >= 12) {
            testingPhase = false;
        }
    }

    public Ticket getTicketById(int id) {
        for(Ticket i : tickets) {
            if(i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public UserDeveloper getDevById(String username) {
        for(UserDeveloper i : developerList) {
            if(i.getUsername().equals(username)) {
                return i;
            }
        }
        return null;
    }

    public Milestone getMilestoneByName(String name) {
        for(Milestone milestone : milestones) {
            if (milestone.getName().equals(name)) {
                return milestone;
            }
        }

        return null;

     }

     public boolean hasManager(String username) {
        for(UserManager i : managerList) {
            if(i.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
     }


    public void update(LocalDate now) {
        for(Milestone i : milestones) {
            i.addPriority(now, this);
            i.becomeCritical(now, this);
        }
    }

    public void subtractId(){
        ticketId--;
    }
}
