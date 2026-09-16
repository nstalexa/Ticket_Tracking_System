package core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.net.UnknownServiceException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static core.AppSystem.MAPPER;
import static core.AppSystem.daysBetween;
import static core.Ticket.BusinessPriority.CRITICAL;

public class Milestone implements Subject {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String[] blockingFor;
    @Getter
    @Setter
    private LocalDate timeCreated;
    @Getter
    @Setter
    private LocalDate due;

    private LocalDate checkUpdate;

    @Getter
    @Setter
    private boolean isBlocked;
    @Getter
    @Setter
    private List<Ticket> tickets = new ArrayList<>();
    @Getter
    @Setter
    private List<Observer> observers = new ArrayList<>();

    @Getter
    @Setter
    private List<String> assignedDevs;

    @Getter
    @Setter
    private String userCreator;

    @Getter
    @Setter
    private LocalDate completedNow;

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    private LocalDate unblockedAt = null;


    public enum Status {
        ACTIVE,
        COMPLETED
    }

    @Getter
    @Setter
    private List<UserDeveloper> devs = new ArrayList<>();

    public Milestone(String name, String[] blockingFor, LocalDate timeCreated, LocalDate due, List<String> assignedDevs, String userCreator) {
        this.name = name;
        this.blockingFor = blockingFor;
        this.timeCreated = timeCreated;
        this.due = due;
        this.isBlocked = false;
        this.assignedDevs = assignedDevs;
        this.userCreator = userCreator;
        status = Status.ACTIVE;

        this.checkUpdate = timeCreated;
    }

    @Override
    public void notifyObservers(Notification n) {
        for (Observer o : observers) {
            o.update(n);

        }
    }

    public void addPriority(LocalDate now, AppSystem system) {
        if(!isBlocked) {
            long diff = ChronoUnit.DAYS.between(checkUpdate, now) + 1;
            if(diff > 3) {
                checkUpdate = now;
                for(Ticket i : tickets) {
                    if (i.getStatus() != Ticket.Status.CLOSED) {
                        i.increasePriority();

                        if (!i.getStatus().equals(Ticket.Status.OPEN)) {
                            checkAndKickDev(i, system);
                        }
                    }
                }
            }
        }
    }

    public void allClosed(LocalDate now, AppSystem system, int lastClosedTicketId) {
        boolean late = now.isAfter(due);

        isBlocked = false;

        if (late) {
            notifyObservers(Notification.unblockLateNotif(name));
            for (String u : assignedDevs) {
                system.getDevById(u)
                        .getNotifications()
                        .add(Notification.unblockLateNotif(name));
            }

        } else {
            notifyObservers(Notification.allClosedNotif(name, lastClosedTicketId));
            for (String u : assignedDevs) {
                system.getDevById(u)
                        .getNotifications()
                        .add(Notification.allClosedNotif(name, lastClosedTicketId));
            }

            if(ChronoUnit.DAYS.between(now, due) == 1) {
                for (String u : assignedDevs) {
                    system.getDevById(u)
                            .getNotifications()
                            .add(Notification.becomeCriticalNotif(name, now));
                }
            }

        }

        checkUpdate = now;

        if(late || daysBetween(now, due) <= 2) {
            for (Ticket t : tickets) {
                t.criticalPriority();
            }
        }

    }





    public boolean isAllClosed() {
        for (Ticket t : tickets) {
            if (t.getStatus() != Ticket.Status.CLOSED) {
                return false;
            }
        }
        return true;
    }


    public void becomeCritical(LocalDate now, AppSystem system) {
        if(isBlocked) {
            return;
        }

        if(now.equals(due.minusDays(1))) {
            notifyObservers(Notification.becomeCriticalNotif(name, due));
            for(String i : assignedDevs) {
                UserDeveloper dev = system.getDevById(i);
                dev.getNotifications().add(Notification.becomeCriticalNotif(name, due));
            }
            for (Ticket i : tickets) {
                if (i.getStatus() != Ticket.Status.CLOSED) {
                    i.criticalPriority();
                    if (!i.getStatus().equals(CRITICAL)) {
                        checkAndKickDev(i, system);
                    }
                }
            }

        }
    }

    public float getCompletionPercentage() {
        float closed = 0;
        float probability;
        for(Ticket i : tickets) {
            if(i.getStatus().equals(Ticket.Status.CLOSED)) {
                closed++;
            }
        }
        probability = (float) Math.round(closed/tickets.size() * 100)/100;
        return probability;
    }


    public ArrayNode repartition(AppSystem system) {
        ArrayNode ret = MAPPER.createArrayNode();
        List<String> assignedDevsAux = new ArrayList<>(assignedDevs);
        assignedDevsAux.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                UserDeveloper ud1 = system.getDevById(o1);
                UserDeveloper ud2 = system.getDevById(o2);

                if(ud1.getTickets().size() < ud2.getTickets().size()) {
                    return -1;
                } else if (ud1.getTickets().size() > ud2.getTickets().size()) {
                    return 1;
                }
                return 0;
            }
        });
        for(String i : assignedDevsAux) {
            List<Integer> listIds = new ArrayList<>();
            ObjectNode curr = MAPPER.createObjectNode();
            curr.put("developer", i);
            UserDeveloper currDev = system.getDevById(i);
            ArrayNode ticketList = MAPPER.createArrayNode();
            for(Ticket j : currDev.getTickets()) {
                if(getTickets().contains(j)) {
                    listIds.add(j.getId());
                }
            }

            listIds.sort(Integer :: compareTo);

            for(int id : listIds) {
                ticketList.add(id);
            }
            curr.put("assignedTickets", ticketList);
            ret.add(curr);
        }
        return ret;
    }

    private void checkAndKickDev(Ticket ticket, AppSystem system) {
        UserDeveloper dev = ticket.getDeveloper();
        if (dev == null) {
            return;
        }

        boolean shouldKick = false;
        Ticket.BusinessPriority priority = ticket.getBusinessPriority();
        UserDeveloper.Seniority seniority = dev.getSeniority();

        if (seniority == UserDeveloper.Seniority.JUNIOR) {
            if (priority == Ticket.BusinessPriority.HIGH || priority == CRITICAL) {
                shouldKick = true;
            }
        }


        if (seniority == UserDeveloper.Seniority.MID) {
            if (priority == CRITICAL) {
                shouldKick = true;
            }
        }

        if (shouldKick) {
            String devUsername = dev.getUsername();
            ticket.setAssignedAt(null);
            dev.getTickets().remove(ticket);

            ticket.getHistory().add(new Action(
                    null,
                    "system",
                    system.getNow().toString(),
                    "REMOVED_FROM_DEV",
                    devUsername,
                    null
            ));

            ticket.setDeveloper(null);
            ticket.setStatus(Ticket.Status.OPEN);

        }

    }

}
