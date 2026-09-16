package core;

import lombok.Getter;
import lombok.Setter;

public class SeniorityManagerFilter implements SearchInterface<UserDeveloper> {
    @Getter
    @Setter
    private UserDeveloper.Seniority seniority;

    public SeniorityManagerFilter(UserDeveloper.Seniority seniority) {
        this.seniority = seniority;
    }

    @Override
    public boolean found(UserDeveloper dev, SearchContext ctx) {
        return dev.getSeniority().equals(seniority);
    }
}


