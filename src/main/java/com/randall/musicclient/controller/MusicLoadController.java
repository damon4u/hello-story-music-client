package com.randall.musicclient.controller;

import com.randall.musicclient.client.ProxyClient;
import com.randall.musicclient.constant.HttpConfig;
import com.randall.musicclient.entity.Proxy;
import com.randall.musicclient.entity.Song;
import com.randall.musicclient.loader.CommentLoader;
import com.randall.musicclient.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-20 14:03
 */
@RestController
public class MusicLoadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusicLoadController.class);

    @Autowired
    private ProxyClient proxyClient;

    @Autowired
    private CommentLoader commentLoader;


    @Autowired
    private HttpConfig httpConfig;
    
    /**
     * curl "http://10.236.160.243:8072/randomProxy"
     *
     * @return
     */
    @GetMapping("/randomProxy")
    Proxy getRandomProxy() {
        Proxy randomProxy = proxyClient.getRandomProxy();
        LOGGER.info("randomProxy={}", randomProxy);
        return randomProxy;
    }

    /**
     * curl -d ""  "http://10.236.160.243:8072/song/209996"
     *
     * @param songId
     * @return
     */
    @PostMapping("/song/{songId}")
    Tuple<Song, Proxy> song(@PathVariable("songId") Integer songId) {
        Tuple<Song, Proxy> songInfo = commentLoader.getSongInfo(songId, 3);
        return songInfo;
    }

    /**
     * curl -d ""  "http://10.236.160.243:8072/loadSong?start=209996&end=209996"
     *
     * @param start
     * @param end
     */
    @PostMapping("/loadSong")
    void loadSong(@RequestParam("start") int start, @RequestParam("end") int end) {
        commentLoader.loadSongs(start, end);
    }

    @RequestMapping("timeout")
    int getSongLoadTimeout() {
        return httpConfig.getSongLoadTimeout();
    }
}
