package core;

import lombok.Getter;
import lombok.Setter;

public class TicketBug extends Ticket {

    private String expectedBehavior;
    private String actualBehavior;
    @Getter
    @Setter
    private Frequency frequency;
    @Getter
    @Setter
    private Severity severity;
    private String environment;
    private int errorCode;
    public enum Frequency {
        RARE,
        OCCASIONAL,
        FREQUENT,
        ALWAYS
    }

    public enum Severity {
        MINOR,
        MODERATE,
        SEVERE
    }
    private TicketBug(BuilderBug builder) {
        super(builder);
        this.expectedBehavior = builder.expectedBehavior;
        this.actualBehavior = builder.actualBehavior;
        this.frequency = builder.frequency;
        this.severity = builder.severity;
        this.environment = builder.environment;
        this.errorCode = builder.errorCode;
    }

    public static class BuilderBug extends Ticket.BuilderTicket<BuilderBug> {

        private String expectedBehavior;
        private String actualBehavior;
        private Frequency frequency;
        private Severity severity;
        private String environment;
        private int errorCode;

        @Override
        public BuilderBug typeBuilder() {
            return this;
        }

        public BuilderBug expectedBehavior(String expectedBehavior) {
            this.expectedBehavior = expectedBehavior;
            return this;
        }

        public BuilderBug actualBehavior(String actualBehavior) {
            this.actualBehavior = actualBehavior;
            return this;
        }

        public BuilderBug frequency(Frequency frequency) {
            this.frequency = frequency;
            return this;
        }

        public BuilderBug severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public BuilderBug environment(String environment) {
            this.environment = environment;
            return this;
        }

        public BuilderBug errorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        @Override
        public TicketBug build() {
            return new TicketBug(this);
        }
    }


    @Override
    public double calculateCustomerImpact() {
        int freq = getFrequencyScore(this.getFrequency());
        int priority = getPriorityScore(this.getBusinessPriority());
        int severity = getSeverityScore(this.getSeverity());

        double initial = freq * priority * severity;
        double max = 48.0;
        return (initial * 100.0) / max;
    }

    private int getFrequencyScore(Frequency freq) {
        if (freq == null) return 1;
        switch (freq) {
            case ALWAYS: return 4;
            case FREQUENT: return 3;
            case OCCASIONAL: return 2;
            case RARE : return 1;
            default: return 1;
        }
    }
    private int getSeverityScore(Severity severity) {
        if (severity == null) return 1;
        switch (severity) {
            case SEVERE: return 3;
            case MODERATE: return 2;
            case MINOR: return 1;
            default: return 1;
        }
    }

    private int getPriorityScore(BusinessPriority priority) {
        if (priority == null) return 1;
        switch (priority) {
            case CRITICAL: return 4;
            case HIGH: return 3;
            case MEDIUM: return 2;
            default: return 1;
        }
    }

    @Override
    public double getTicketRisk() {
        int freq = getFrequencyScore(this.getFrequency());
        int severity = getSeverityScore(this.getSeverity());

        double initial = freq * severity;
        double max = 12.0;
        return (initial * 100.0) / max;

    }

    @Override
    public double getEfficiency() {
        int freq = getFrequencyScore(this.getFrequency());
        int severity = getSeverityScore(this.getSeverity());
        double daysToResolve = AppSystem.daysBetween(getAssignedAt(), getSolvedAt());

        double initial = (double) (freq + severity)*10/daysToResolve;
        double max = 70.0;
        return (initial * 100.0) / max;

    }
}