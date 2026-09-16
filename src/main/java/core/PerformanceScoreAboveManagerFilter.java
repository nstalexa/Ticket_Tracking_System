package core;

public class PerformanceScoreAboveManagerFilter implements SearchInterface<UserDeveloper> {
    private double performanceScore;

    public PerformanceScoreAboveManagerFilter(double performanceScore) {
        this.performanceScore = performanceScore;
    }

    @Override
    public boolean found(UserDeveloper developer, SearchContext ctx) {
        return developer.getPerformanceScore() >= performanceScore;
    }
}