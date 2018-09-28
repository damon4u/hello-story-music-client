package com.randall.musicclient.dao;

import com.randall.musicclient.entity.Comment;
import org.apache.ibatis.annotations.Insert;

public interface CommentDao {

    @Insert("INSERT IGNORE INTO comment (comment_id, song_id, liked_count, content, user_id, be_replied_user_id, be_replied_content, comment_time, create_time) " +
            "VALUES (#{commentId}, #{songId}, #{likedCount}, #{content}, #{userId}, #{beRepliedUserId}, #{beRepliedContent}, #{commentTime}, #{createTime})")
    int save(Comment comment);

}
