package com.randall.musicclient.constant;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-20 16:59
 */
@Data
@Component
@RefreshScope
public class HttpConfig {

    @Value("${songLoadTimeout}")
    private int songLoadTimeout;
}
