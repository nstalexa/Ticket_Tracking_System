package core;

import java.time.LocalDate;

public class Notification {

    private final String message;

    private Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static Notification createdMilestone(String name, LocalDate due) {

        return new Notification(
                "New milestone " + name +
                        " has been created with due date " + due + "."
        );

    }

    public static Notification allClosedNotif(String milestoneName, int ticketId) {
        return new Notification(
                "Milestone " + milestoneName +
                        " is now unblocked as ticket " + ticketId +
                        " has been CLOSED."
        );
    }

    public static Notification becomeCriticalNotif(String name, LocalDate due) {
        return new Notification(
                "Milestone " + name +
                        " is due tomorrow. All unresolved tickets are now CRITICAL."
        );
    }

    public static Notification unblockLateNotif(String name) {
        return new Notification(
                "Milestone " + name +
                        " was unblocked after due date. All active tickets are now CRITICAL."
        );
    }


}