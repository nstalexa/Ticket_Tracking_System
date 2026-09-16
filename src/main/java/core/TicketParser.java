package core;

import fileio.TicketInput;

import java.time.LocalDate;

public class TicketParser {


    public static Ticket convertToTicket(TicketInput ticketInput) {
        Ticket aux = null;
        switch(Ticket.TicketType.valueOf(ticketInput.getType())) {
            case FEATURE_REQUEST:
                aux = new TicketFeatureRequest.BuilderFeature()
                        .id(AppSystem.nextTicketId())
                        .type(Ticket.TicketType.valueOf(ticketInput.getType()))
                        .title(ticketInput.getTitle())
                        .businessPriority(Ticket.BusinessPriority.valueOf(ticketInput.getBusinessPriority()))
                        .status(Ticket.Status.OPEN)
                        .expertiseArea(Ticket.ExpertiseArea.valueOf(ticketInput.getExpertiseArea()))
                        .description(ticketInput.getDescription())
                        .reportedBy(ticketInput.getReportedBy())
                        .businessValue(TicketFeatureRequest.BusinessValue.valueOf(ticketInput.getBusinessValue()))
                        .customerDemand(TicketFeatureRequest.CustomerDemand.valueOf(ticketInput.getCustomerDemand()))
                        .build();
                aux.setCreatedAt(LocalDate.parse(ticketInput.getCreatedAt()));
                break;

            case BUG:
                aux = new TicketBug.BuilderBug()
                        .id(AppSystem.nextTicketId())
                        .type(Ticket.TicketType.valueOf(ticketInput.getType()))
                        .title(ticketInput.getTitle())
                        .businessPriority(Ticket.BusinessPriority.valueOf(ticketInput.getBusinessPriority()))
                        .status(Ticket.Status.OPEN)
                        .expertiseArea(Ticket.ExpertiseArea.valueOf(ticketInput.getExpertiseArea()))
                        .description(ticketInput.getDescription())
                        .reportedBy(ticketInput.getReportedBy())
                        .expectedBehavior(ticketInput.getExpectedBehavior())
                        .actualBehavior(ticketInput.getActualBehavior())
                        .frequency(TicketBug.Frequency.valueOf(ticketInput.getFrequency()))
                        .severity(TicketBug.Severity.valueOf(ticketInput.getSeverity()))
                        .environment(ticketInput.getEnvironment())
                        .errorCode(ticketInput.getErrorCode())
                        .build();
                aux.setCreatedAt(LocalDate.parse(ticketInput.getCreatedAt()));
                break;

            case UI_FEEDBACK:

                aux = new TicketUIFeedback.BuilderUIFeedback()
                        .id(AppSystem.nextTicketId())
                        .type(Ticket.TicketType.valueOf(ticketInput.getType()))
                        .title(ticketInput.getTitle())
                        .businessPriority(Ticket.BusinessPriority.valueOf(ticketInput.getBusinessPriority()))
                        .status(Ticket.Status.OPEN)
                        .expertiseArea(Ticket.ExpertiseArea.valueOf(ticketInput.getExpertiseArea()))
                        .description(ticketInput.getDescription())
                        .reportedBy(ticketInput.getReportedBy())
                        .uiElementId(ticketInput.getUiElementId())
                        .businessValue((TicketUIFeedback.BusinessValue.valueOf(ticketInput.getBusinessValue())))
                        .usabilityScore(ticketInput.getUsabilityScore())
                        .screenshotUrl(ticketInput.getScreenshotUrl())
                        .suggestedFix(ticketInput.getSuggestedFix())
                        .build();
                aux.setCreatedAt(LocalDate.parse(ticketInput.getCreatedAt()));
                break;
        }

        return aux;
    }
}
