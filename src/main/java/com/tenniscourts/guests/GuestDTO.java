package com.tenniscourts.guests;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GuestDTO {

    private Long id;

    private String name;
}
