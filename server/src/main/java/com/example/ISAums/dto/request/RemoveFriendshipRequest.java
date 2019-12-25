package com.example.ISAums.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveFriendshipRequest {

    private UUID mineId;

    private UUID friendsId;

}
