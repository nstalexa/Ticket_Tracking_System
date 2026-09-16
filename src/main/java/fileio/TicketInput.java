package fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class TicketInput {
    private String type;
    private String title;
    private String businessPriority;
    private String expertiseArea;
    private String description;
    private String reportedBy;
    private String createdAt;
    private String expectedBehavior;
    private String actualBehavior;
    private String frequency;
    private String severity;
    private String environment;
    private int errorCode;
    private String businessValue;
    private String customerDemand;
    private String uiElementId;
    private int usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

}
