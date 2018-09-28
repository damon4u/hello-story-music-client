package com.randall.musicclient.dao;

import com.randall.musicclient.entity.Song;
import org.apache.ibatis.annotations.Insert;

public interface SongDao {

    @Insert("INSERT IGNORE INTO song (song_id, name, description, comment_count, image, create_time) " +
            "VALUES (#{songId}, #{name}, #{description}, #{commentCount}, #{image}, #{createTime})")
    int save(Song song);

}
