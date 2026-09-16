package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputLoader {
    public static List<UserInput> UserLoader (String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserInput[] usersNode = mapper.readValue(new File(path), UserInput[].class);
        return Arrays.asList(usersNode);
    }

    public static List<User> getListUsers(List<UserInput> users, AppSystem system) {
        List<User> getUsers = new ArrayList<>();
        for(UserInput i : users) {
            User aux = null;
            switch(i.getRole()) {
                case "DEVELOPER" -> {
                    UserDeveloper auxDev = new UserDeveloper(i.getUsername(), i.getEmail(),
                        User.Role.valueOf(i.getRole()), i.getHireDate(),
                        UserDeveloper.ExpertiseArea.valueOf(i.getExpertiseArea()),
                        UserDeveloper.Seniority.valueOf(i.getSeniority()));

                    system.getDeveloperList().add(auxDev);
                    aux = auxDev;
                }
                case "MANAGER" -> {
                    UserManager auxMan = new UserManager(i.getUsername(), i.getEmail(),
                        User.Role.valueOf(i.getRole()), i.getHireDate(), i.getSubordinates());
                    system.getManagerList().add(auxMan);
                    aux = auxMan;
                }
                case "REPORTER" -> {
                    UserReporter auxRep = new UserReporter(i.getUsername(), i.getEmail(),
                        User.Role.valueOf(i.getRole()));
                    system.getReporterList().add(auxRep);
                    aux = auxRep;
                }
            }
            getUsers.add(aux);
        }

        return getUsers;
    }

    public static List<CommandInput> CommandLoader (String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CommandInput[] commandNode = mapper.readValue(new File(path), CommandInput[].class);
        for(CommandInput i: commandNode) {
            if(i.getParams() != null) {
                i.getParams().setCreatedAt(i.getTimestamp());
            }
        }
        return Arrays.asList(commandNode);
    }

    public static List<Command> getListCommands(List<CommandInput> commands, List<User> users, AppSystem system) {
        List<Command> getCommands = new ArrayList<>();
        for(CommandInput i : commands) {
            User curr = null;
            for(User j : users) {
                if(j.getUsername().equals(i.getUsername())) {
                    curr = j;
                }
            }

            Command cmd = null;
            switch(i.getCommand()) {
                case "lostInvestors":
                    cmd = new LostInvestors(curr, i.getUsername(),
                            LocalDate.parse(i.getTimestamp()));
                    getCommands.add(cmd);
                    break;
                case "reportTicket":
                    cmd = new ReportTicket(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()), i.getParams());
                    break;
                case "viewTickets":
                    cmd = new ViewTickets(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;

                case "createMilestone":
                    CreateMilestone aux;
                    aux = new CreateMilestone(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()), i.getTickets());
                    Milestone milestone = new Milestone(i.getName(), i.getBlockingFor(), LocalDate.parse(i.getTimestamp()), LocalDate.parse(i.getDueDate()), i.getAssignedDevs(), i.getUsername());
                    List<Ticket> list = new ArrayList<>();
                    for(Integer id : i.getTickets()) {
                        Ticket currTicket = system.getTicketById(id);
                        if(currTicket != null) {
                            list.add(currTicket);
                        }
                    }
                    milestone.setTickets(list);
                    aux.setMilestone(milestone);
                    cmd = aux;
                    break;

                case "viewMilestones":
                    cmd = new ViewMilestones(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "assignTicket":
                    cmd = new AssignTicket(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()),
                            i.getTicketID(), system.getDevById(i.getUsername()), false);
                    break;
                case "undoAssignTicket":
                    cmd = new AssignTicket(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()),
                            i.getTicketID(), system.getDevById(i.getUsername()), true);
                    break;
                case "viewAssignedTickets":
                    cmd = new ViewAssignedTickets(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "addComment":
                    cmd = new AddComment(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()),
                            i.getTicketID(), false);
                    cmd.setStringComment(i.getComment());
                    break;
                case "undoAddComment":
                    cmd = new AddComment(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()),
                            i.getTicketID(), true);
                    break;
                case "changeStatus":
                    cmd = new ChangeStatus(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()), i.getTicketID(), false);
                    break;
                case "undoChangeStatus":
                    cmd = new ChangeStatus(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()), i.getTicketID(), true);
                    break;
                case "viewTicketHistory":
                    cmd = new ViewTicketHistory(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "search":
                    cmd = new Search(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()), i.getTicketID(), i.getFilters());
                    break;
                case "viewNotifications":
                    cmd = new ViewNotifications(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "generateCustomerImpactReport":
                    cmd = new GenerateCustomerImpactReport(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "generateTicketRiskReport":
                    cmd = new GenerateTicketRiskReport(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "generateResolutionEfficiencyReport":
                    cmd = new GenerateResolutionEfficiencyReport(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "appStabilityReport":
                    cmd = new AppStabilityReport(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "generatePerformanceReport":
                    cmd = new GeneratePerformanceReport(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
                case "startTestingPhase":
                    cmd = new StartTestingPhase(curr, i.getUsername(), LocalDate.parse(i.getTimestamp()));
                    break;
            }
            if (cmd != null) {
                getCommands.add(cmd);
            }
        }

        return getCommands;
    }
}