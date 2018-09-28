package com.randall.musicclient.entity;

import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-14 14:38
 */
@Data
public class CommentResponseBody {
    
    private Integer code;
    
    private Long userId;
    
    private List<CommentResponse> topComments;
    
    private List<CommentResponse> hotComments;

    private List<CommentResponse> comments;

    private Long total;
    
    private Boolean moreHot;

    private Boolean more;
    
    private Boolean isMusician;
    
}
