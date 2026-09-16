package core;

public class PerformanceScoreBelowManagerFilter implements SearchInterface<UserDeveloper> {
    private double performanceScore;

    public PerformanceScoreBelowManagerFilter(double performanceScore) {
        this.performanceScore = performanceScore;
    }

    @Override
    public boolean found(UserDeveloper developer, SearchContext ctx) {
        return developer.getPerformanceScore() <= performanceScore;
    }
}