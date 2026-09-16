package fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class SearchInput {
    private String searchType;
    private String businessPriority;
    private String type;
    private String createdAt;
    private String createdBefore;
    private String createdAfter;
    private Boolean availableForAssignment;
    private List<String> keywords;
    private String expertiseArea;
    private String seniority;
    private Integer performanceScoreAbove;
    private Integer performanceScoreBelow;
}
