package core;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class Comment {
    @Getter
    @Setter
    private User author;
    @Getter
    @Setter
    private String content;
    @Getter
    @Setter
    private LocalDate createdAt;

    public Comment(User author, String content, LocalDate createdAt) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }
}
