package core;

import java.util.List;
import java.util.stream.Collectors;

public interface CommandVisitor {
    void visit(UserManager userManager, AppSystem system);
    void visit(UserDeveloper userDeveloper, AppSystem system);
    void visit(UserReporter userReporter, AppSystem system);
    default <T extends Enum<T>> String sorted(List<T> values) {
        return values.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
