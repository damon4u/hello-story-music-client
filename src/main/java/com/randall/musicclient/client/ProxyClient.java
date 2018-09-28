package com.randall.musicclient.client;

import com.randall.musicclient.entity.Proxy;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-20 13:54
 */
@FeignClient("proxy-client")
public interface ProxyClient {
    
    @GetMapping("/proxy")
    Proxy getRandomProxy();

    @DeleteMapping("/proxy/{id}")
    int delete(@PathVariable("id") Integer id);
}
