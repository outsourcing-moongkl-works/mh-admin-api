package org.outsourcing.mhadminapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PausingStatus {
    private boolean isPaused;
    private LocalDateTime pausedAt;
    private int days;
    private int hours;
}
