package com.randall.musicclient.client;

import com.randall.musicclient.entity.CommentResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-14 11:01
 */
public interface MusicClient {

    @GET("song")
    @Headers("Referer: http://music.163.com/")
    Call<String> songInfo(@Query("id") long songId);

    @POST("weapi/v1/resource/comments/R_SO_4_{songId}/?csrf_token=d2c9e86c94efabcc4b5a1a6d757d417e")
    @FormUrlEncoded
    @Headers("Referer: http://music.163.com/")
    Call<CommentResponseBody> comment(@Path("songId") Long songId,
                                      @Field("params") String params,
                                      @Field("encSecKey") String encSecKey);
}
