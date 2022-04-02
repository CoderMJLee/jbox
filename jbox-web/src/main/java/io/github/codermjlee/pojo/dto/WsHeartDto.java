package io.github.codermjlee.pojo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author MJ
 */
@Getter
@Setter
public class WsHeartDto {
    private String clientId;
    public WsHeartDto(String clientId) {
        this.clientId = clientId;
    }
}
