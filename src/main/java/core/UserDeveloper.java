package core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class UserDeveloper extends User{
    @Getter
    private String hireDate;
    @Getter
    private ExpertiseArea expertiseArea;

    @Getter
    @Setter
    private double performanceScore = 0;

    @Getter
    @Setter
    private List<Notification> notifications = new ArrayList<>();



    public enum ExpertiseArea {
        FRONTEND,
        BACKEND,
        DEVOPS,
        DESIGN,
        DB,
        FULLSTACK
    }

    @Getter
    private Seniority seniority;
    public enum Seniority {
        JUNIOR,
        MID,
        SENIOR
    }
    public UserDeveloper(String username, String email, User.Role role, String hireDate,
                         ExpertiseArea expertiseArea, Seniority seniority) {
        super(username, email, role);
        this.hireDate = hireDate;
        this.expertiseArea = expertiseArea;
        this.seniority = seniority;
    }

    @Getter
    @Setter
    private List<Ticket> tickets = new ArrayList<>();

    @Getter
    @Setter
    private List<Ticket> allTickets = new ArrayList<>();

    public void addTicket(Ticket t) {
        tickets.add(t);
    }

    public void addTicketToAll(Ticket t) {
        allTickets.add(t);
    }

    public void removeTicket(Ticket t) {
        tickets.remove(t);
    }


    public void accept(CommandVisitor cmdVisitor, AppSystem system) {
        cmdVisitor.visit(this, system);
    }

}