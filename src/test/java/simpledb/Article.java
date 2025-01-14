package simpledb;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isBlind")
    private boolean isBlind;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

}
