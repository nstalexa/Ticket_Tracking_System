package core;

import lombok.Getter;
import lombok.Setter;

public class ExpertiseAreaManagerFilter implements SearchInterface<UserDeveloper> {

    @Getter
    @Setter
    private UserDeveloper.ExpertiseArea expertiseArea;

    public ExpertiseAreaManagerFilter(UserDeveloper.ExpertiseArea expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    @Override
    public boolean found(UserDeveloper dev, SearchContext ctx) {
        return dev.getExpertiseArea().equals(expertiseArea);
    }
}
