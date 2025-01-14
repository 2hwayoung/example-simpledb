package simpledb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Article {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private boolean isBlind;

}
