package core;

import lombok.Getter;

public class TicketUIFeedback extends Ticket {

    @Getter
    private String uiElementId;
    @Getter
    private BusinessValue businessValue;
    public enum BusinessValue {
        S,
        M,
        L,
        XL
    }

    @Getter
    private int usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    private TicketUIFeedback(TicketUIFeedback.BuilderUIFeedback builder) {
        super(builder);
        this.uiElementId = builder.uiElementId;
        this.businessValue = builder.businessValue;
        this.usabilityScore = builder.usabilityScore;
        this.screenshotUrl = builder.screenshotUrl;
        this.suggestedFix = builder.suggestedFix;
    }

    public static class BuilderUIFeedback extends Ticket.BuilderTicket<BuilderUIFeedback> {
        private String uiElementId;
        private BusinessValue businessValue;
        private int usabilityScore;
        private String screenshotUrl;
        private String suggestedFix;

        @Override
        public TicketUIFeedback.BuilderUIFeedback typeBuilder() {
            return this;
        }
        public TicketUIFeedback.BuilderUIFeedback uiElementId(String uiElementId) {
            this.uiElementId = uiElementId;
            return this;
        }
        public TicketUIFeedback.BuilderUIFeedback businessValue(BusinessValue businessValue) {
            this.businessValue = businessValue;
            return this;
        }
        public TicketUIFeedback.BuilderUIFeedback usabilityScore(int usabilityScore) {
            this.usabilityScore = usabilityScore;
            return this;
        }
        public TicketUIFeedback.BuilderUIFeedback screenshotUrl(String screenshotUrl) {
            this.screenshotUrl = screenshotUrl;
            return this;
        }
        public TicketUIFeedback.BuilderUIFeedback suggestedFix(String suggestedFix) {
            this.suggestedFix = suggestedFix;
            return this;
        }

        @Override
        public TicketUIFeedback build() {
            return new TicketUIFeedback(this);
        }
    }

    @Override
    public double calculateCustomerImpact() {
        int val = getBusinessValueScore(this.getBusinessValue());
        int usability = this.getUsabilityScore();

        double initial = val * usability;
        double max = 100.0;

        return (initial * 100.0) / max;
    }

    private int getBusinessValueScore(BusinessValue val) {
        if (val == null) return 1;
        switch (val) {
            case XL: return 10;
            case L: return 6;
            case M: return 3;
            case S: return 1;
            default: return 1;
        }
    }


    @Override
    public double getTicketRisk() {
        int val = getBusinessValueScore(this.getBusinessValue());
        int usability = this.getUsabilityScore();

        double initial = (11 - usability)*val;
        double max = 100.0;
        return(initial * 100.0) / max;

    }

    @Override
    public double getEfficiency() {
        int val = getBusinessValueScore(this.getBusinessValue());
        int usability = this.getUsabilityScore();
        double daysToResolve = AppSystem.daysBetween(getAssignedAt(), getSolvedAt());

        double initial = (double) (val + usability)/daysToResolve;
        double max = 20.0;
        return (initial * 100.0) / max;

    }
}