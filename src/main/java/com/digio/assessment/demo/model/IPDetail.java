package com.digio.assessment.demo.model;


import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IPDetail {
    private String ipAddress;
    private LocalDateTime visitedDate;
    private String endPoint;
}
