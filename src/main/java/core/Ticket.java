package core;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Ticket implements Subject {
    @Getter
    protected int id;
    @Getter
    protected String title;
    @Getter
    @Setter
    protected BusinessPriority businessPriority;
    @Getter
    protected Status status = Status.OPEN;
    @Getter
    protected ExpertiseArea expertiseArea;
    @Getter
    @Setter
    protected TicketType type;
    @Getter
    private final List<Comment> comments = new ArrayList<>();

    @Getter
    private final List<Action> history = new ArrayList<>();

    @Getter
    protected Milestone milestone = null;

    @Getter
    @Setter
    private LocalDate createdAt;

    @Getter
    @Setter
    private LocalDate assignedAt;

    @Getter
    @Setter
    private LocalDate solvedAt;


    public enum TicketType {
        BUG,
        UI_FEEDBACK,
        FEATURE_REQUEST
    }

    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void notifyObservers(Notification n) {
        for (Observer o : observers) {
            o.update(n);
        }
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
        if (milestone != null && !milestone.getTickets().contains(this)) {
            milestone.getTickets().add(this);
        }
    }

    public enum BusinessPriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public enum Status {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        CLOSED
    }

    public enum ExpertiseArea {
        FRONTEND,
        BACKEND,
        DEVOPS,
        DESIGN,
        DB
    }

    @Getter
    protected String description;
    @Getter
    protected String reportedBy;


    protected Ticket(BuilderTicket builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.title = builder.title;
        this.businessPriority = builder.businessPriority;
        this.status = builder.status;
        this.expertiseArea = builder.expertiseArea;
        this.description = builder.description;
        this.reportedBy = builder.reportedBy != null ? builder.reportedBy : "";
    }

    public void setStatus(Status status) {
        if(this.status == Status.CLOSED) {
            return;
        }

        this.status = status;
    }

    public void increasePriority() {
        switch(businessPriority) {
            case LOW -> businessPriority = BusinessPriority.MEDIUM;
            case MEDIUM -> businessPriority = BusinessPriority.HIGH;
            case HIGH -> businessPriority = BusinessPriority.CRITICAL;
            case CRITICAL -> businessPriority = BusinessPriority.CRITICAL;
        }
    }

    public void criticalPriority() {
        if(status != Status.CLOSED) {
            businessPriority = BusinessPriority.CRITICAL;
        }
    }

    public static abstract class BuilderTicket<T extends BuilderTicket<T>> {
        protected int id;
        protected TicketType type;
        protected String title;
        protected BusinessPriority businessPriority;
        protected Status status = Status.OPEN;
        protected ExpertiseArea expertiseArea;
        protected String description;
        protected String reportedBy;

        public abstract T typeBuilder();

        public T id(int id) {
            this.id = id;
            return typeBuilder();
        }

        public T type(TicketType type) {
            this.type = type;
            return typeBuilder();
        }

        public T title(String title) {
            this.title = title;
            return typeBuilder();
        }

        public T businessPriority(BusinessPriority businessPriority) {
            this.businessPriority = businessPriority;
            return typeBuilder();
        }

        public T status(Status status) {
            this.status = status;
            return typeBuilder();
        }

        public T expertiseArea(ExpertiseArea expertiseArea) {
            this.expertiseArea = expertiseArea;
            return typeBuilder();
        }

        public T description(String description) {
            this.description = description;
            return typeBuilder();
        }

        public T reportedBy(String reportedBy) {
            this.reportedBy = reportedBy;
            return typeBuilder();
        }

        public abstract Ticket build();

    }

    @Getter
    @Setter
    private UserDeveloper developer;

    public void assign(UserDeveloper dev, AppSystem system) {
        this.developer = dev;
        this.status = Status.IN_PROGRESS;
        this.setAssignedAt(system.getNow());
        dev.addTicket(this);
        dev.addTicketToAll(this);
        history.add(new Action(
                null,
                dev.getUsername(),
                system.getNow().toString(),
                "ASSIGNED",
                null,
                null
        ));
        history.add(new Action(
                null,
                dev.getUsername(),
                system.getNow().toString(),
                "STATUS_CHANGED",
                Status.OPEN.toString(),
                Status.IN_PROGRESS.toString()
        ));

    }

    public void unassign(UserDeveloper dev, AppSystem system) {
        history.add(new Action(
                null,
                dev.getUsername(),
                system.getNow().toString(),
                "DE-ASSIGNED",
                null,
                null
        ));
        this.assignedAt = null;

        if (developer != null) {
            developer.removeTicket(this);
        }
        this.developer = null;
        this.status = Status.OPEN;
    }



    public void addComment(Comment comment) {
        if(comment != null) {
            comments.add(comment);
        }
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public boolean hasMilestone(Ticket ticket) {
        if(ticket.getMilestone() != null) {
            return true;
        }
        return false;
    }


    public List<UserDeveloper.Seniority> getRequiredSeniorities() {
        List<UserDeveloper.Seniority> seniorities = new ArrayList<>();
        switch(getType()) {
            case BUG, UI_FEEDBACK -> {
                seniorities.add(UserDeveloper.Seniority.JUNIOR);
                seniorities.add(UserDeveloper.Seniority.MID);
                seniorities.add(UserDeveloper.Seniority.SENIOR);
            }
            case FEATURE_REQUEST -> {
                seniorities.add(UserDeveloper.Seniority.MID);
                seniorities.add(UserDeveloper.Seniority.SENIOR);
            }
        }

        return seniorities;
    }

    public boolean canAssign(UserDeveloper.ExpertiseArea expertiseArea) {
        switch(expertiseArea) {
            case FRONTEND:
                return getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.FRONTEND.toString())
                        || getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DESIGN.toString());
            case BACKEND:
                return getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.BACKEND.toString())
                        || getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DB.toString());
            case FULLSTACK:
                return getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.BACKEND.toString())
                        || getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.FRONTEND.toString())
                        || getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DEVOPS.toString())
                        || getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DESIGN.toString())
                        || getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DB.toString());
            case DEVOPS:
                return getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DEVOPS.toString());

            case DESIGN:
                return getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DESIGN.toString())
                        || getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.FRONTEND.toString());
            case DB:
                return getExpertiseArea().toString().equals(UserDeveloper.ExpertiseArea.DB.toString());

        }
        return false;
    }

    public abstract double calculateCustomerImpact();
    public abstract double getTicketRisk();
    public abstract double getEfficiency();

}