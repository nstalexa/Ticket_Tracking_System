package core;

public interface SearchInterface<T> {
    boolean found(T object, SearchContext context);
}
