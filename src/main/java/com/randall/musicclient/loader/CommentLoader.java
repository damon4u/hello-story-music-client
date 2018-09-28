package com.randall.musicclient.loader;

import com.google.common.base.Preconditions;
import com.randall.musicclient.client.ProxyClient;
import com.randall.musicclient.constant.HttpConfig;
import com.randall.musicclient.dao.CommentDao;
import com.randall.musicclient.dao.SongDao;
import com.randall.musicclient.dao.UserDao;
import com.randall.musicclient.entity.Comment;
import com.randall.musicclient.entity.CommentReplied;
import com.randall.musicclient.entity.CommentResponse;
import com.randall.musicclient.entity.CommentResponseBody;
import com.randall.musicclient.entity.Proxy;
import com.randall.musicclient.entity.Song;
import com.randall.musicclient.entity.User;
import com.randall.musicclient.http.HttpProxyClientFactory;
import com.randall.musicclient.util.Tuple;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author damon4u
 * @version 2017-05-21 14:19
 */
@Component
public class CommentLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentLoader.class);

    private static Pattern NAME_PATTERN = Pattern.compile("<title>(.*) - 网易云音乐</title>");
    private static Pattern DESCRIPTION_PATTERN = Pattern.compile("<meta name=\"description\" content=\"(.*)\" />");
    private static Pattern IMAGE_PATTERN = Pattern.compile("<meta property=\"og:image\" content=\"(.*)\" />");

    @Autowired
    private SongDao songDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommentDao commentDao;
    
    @Autowired
    private ProxyClient proxyClient;
    
    @Autowired
    private HttpConfig httpConfig;

    /**
     * 获取歌曲和评论信息
     *
     * @param start 歌曲id起始
     * @param end 歌曲id结束
     */
    public void loadSongs(int start, int end) {
        for(int songId = start; songId <= end; songId++) {
            Tuple<Song, Proxy> songInfo = getSongInfo(songId, 3);
            if (songInfo != null) {
                loadCommentInfo(songInfo.getFirst(), songInfo.getSecond());
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public Tuple<Song, Proxy> getSongInfo(final long songId, int retry) {
        Proxy proxy = proxyClient.getRandomProxy();
        Song songInfo = getSongInfo(songId, proxy, httpConfig.getSongLoadTimeout());
        Tuple<Song, Proxy> result = null;
        if (songInfo == null) {
            proxyClient.delete(proxy.getId());
            if (retry > 0) {
                result = getSongInfo(songId, --retry);
            }
        } else {
            result = Tuple.newInstance(songInfo, proxy);
        }
        return result;
    }
    
    /**
     * 获取歌曲信息
     *
     * @param songId 歌曲id
     * @return 如果找到，返回：歌曲名称 - 歌手名称；否则返回null
     */
    public Song getSongInfo(long songId, Proxy proxy, int timeoutInSecond) {
        Response<String> response = null;
        try {
            response = HttpProxyClientFactory.musicClient(proxy, timeoutInSecond)
                    .songInfo(songId).execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        if (response == null) {
            return null;
        }
        String songInfo = response.body();
        Preconditions.checkArgument(StringUtils.isNotBlank(songInfo));
        Matcher nameMatcher = NAME_PATTERN.matcher(songInfo);
        Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(songInfo);
        Matcher imageMatcher = IMAGE_PATTERN.matcher(songInfo);
        String name = "";
        String description = "";
        String image = "";
        if (nameMatcher.find()) {
            name = nameMatcher.group(1);
        }
        if (descriptionMatcher.find()) {
            description = descriptionMatcher.group(1);
        }
        if (imageMatcher.find()) {
            image = imageMatcher.group(1);
        }
        if (StringUtils.isNotBlank(name)) {
            Song song = new Song();
            song.setSongId(songId);
            song.setName(name);
            song.setDescription(description);
            song.setImage(image);
            song.setCreateTime(new Date());
            LOGGER.info("song={}", song);
            return song;
        }
        return null;
    }

    /**
     * 获取歌曲评论
     * @param song  歌曲信息
     * @param proxy
     */
    private void loadCommentInfo(Song song, Proxy proxy) {
        Long songId = song.getSongId();
        String params = "flQdEgSsTmFkRagRN2ceHMwk6lYVIMro5auxLK/JywlqdjeNvEtiWDhReFI+QymePGPLvPnIuVi3dfsDuqEJW204VdwvX+gr3uiRBeSFuOm1VUSJ1HqOc+nJCh0j6WGUbWuJC5GaHTEE4gcpWXX36P4Eu4djoQBzoqdsMbCwoolb2/WrYw/N2hehuwBHO4Oz";
        String encSecKey = "0263b1cd3b0a9b621a819b73e588e1cc5709349b21164dc45ab760e79858bb712986ea064dbfc41669e527b767f02da7511ac862cbc54ea7d164fc65e0359962273616e68e694453fb6820fa36dd9915b2b0f60dadb0a6022b2187b9ee011b35d82a1c0ed8ba0dceb877299eca944e80b1e74139f0191adf71ca536af7d7ec25";
        Response<CommentResponseBody> response = null;
        try {
            response = HttpProxyClientFactory.musicClient(proxy, httpConfig.getSongLoadTimeout()).comment(songId, params, encSecKey).execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        long commentCount = 0;
        if (response != null) {
            CommentResponseBody commentResponseBody = response.body();
            if (commentResponseBody != null) {
                commentCount = commentResponseBody.getTotal();
                List<CommentResponse> hotComments = commentResponseBody.getHotComments();
                if (CollectionUtils.isNotEmpty(hotComments)) {
                    for (CommentResponse commentResponse : hotComments) {
                        Long userId = 0L;
                        User user = commentResponse.getUser();
                        if (user != null) {
                            userId = user.getUserId();
                            userDao.save(user);
                        }
                        Long beRepliedUserId = 0L;
                        String beRepliedContent = "";
                        List<CommentReplied> beRepliedList = commentResponse.getBeReplied();
                        if (CollectionUtils.isNotEmpty(beRepliedList)) {
                            CommentReplied commentReplied = beRepliedList.get(0);
                            User repliedUser = commentReplied.getUser();
                            if (repliedUser != null) {
                                beRepliedUserId = repliedUser.getUserId();
                                userDao.save(repliedUser);
                            }
                            beRepliedContent = commentReplied.getContent();
                        }
                        if (commentCount > 1000) {
                            // 总评论数少于1000的歌曲就扔掉了
                            Comment comment = new Comment();
                            comment.setCommentId(commentResponse.getCommentId());
                            comment.setSongId(songId);
                            comment.setLikedCount(commentResponse.getLikedCount());
                            comment.setContent(commentResponse.getContent());
                            comment.setUserId(userId);
                            comment.setBeRepliedUserId(beRepliedUserId);
                            comment.setBeRepliedContent(beRepliedContent);
                            comment.setCommentTime(new Date(commentResponse.getTime()));
                            comment.setCreateTime(new Date());
                            commentDao.save(comment);
                        }
                    }
                }
            }
            
        }
        song.setCommentCount(commentCount);
        songDao.save(song);
    }

}
