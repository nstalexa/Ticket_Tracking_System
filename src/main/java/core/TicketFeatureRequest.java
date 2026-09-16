package core;

import lombok.Getter;

public class TicketFeatureRequest extends Ticket {

    @Getter
    private BusinessValue businessValue;
    public enum BusinessValue {
        S,
        M,
        L,
        XL
    }

    @Getter
    private CustomerDemand customerDemand;
    public enum CustomerDemand {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }

    private TicketFeatureRequest(TicketFeatureRequest.BuilderFeature builder) {
        super(builder);
        this.customerDemand = builder.customerDemand;
        this.businessValue = builder.businessValue;
    }

    public static class BuilderFeature extends Ticket.BuilderTicket<BuilderFeature> {
        private CustomerDemand customerDemand;
        private BusinessValue businessValue;

        @Override
        public TicketFeatureRequest.BuilderFeature typeBuilder() {
            return this;
        }

        public TicketFeatureRequest.BuilderFeature customerDemand(CustomerDemand customerDemand) {
            this.customerDemand = customerDemand;
            return this;
        }
        public TicketFeatureRequest.BuilderFeature businessValue(BusinessValue businessValue) {
            this.businessValue = businessValue;
            return this;
        }

        @Override
        public TicketFeatureRequest build() {
            return new TicketFeatureRequest(this);
        }
    }

    @Override
    public double calculateCustomerImpact() {
        int val = getBusinessValueScore(this.getBusinessValue());
        int demand = getCustomerDemandScore(this.getCustomerDemand());

        double initial = val * demand;
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

    private int getCustomerDemandScore(CustomerDemand demand) {
        if (demand == null) return 1;
        switch (demand) {
            case VERY_HIGH: return 10;
            case HIGH: return 6;
            case MEDIUM: return 3;
            case LOW: return 1;
            default: return 1;
        }
    }

    @Override
    public double getTicketRisk() {
        int val = getBusinessValueScore(this.getBusinessValue());
        int demand = getCustomerDemandScore(this.getCustomerDemand());

        double initial = val + demand;
        double max = 20.0;
        return (initial * 100.0) / max;
    }

    @Override
    public double getEfficiency() {
        int val = getBusinessValueScore(this.getBusinessValue());
        int demand = getCustomerDemandScore(this.getCustomerDemand());
        double daysToResolve = AppSystem.daysBetween(getAssignedAt(), getSolvedAt());

        double initial = (double) (val + demand)/daysToResolve;
        double max = 20.0;
        return (initial * 100.0) / max;

    }
}