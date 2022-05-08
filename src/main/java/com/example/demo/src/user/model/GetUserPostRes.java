package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserPostRes {
    private boolean _isMyFeed;
    private int postIdx;
    private String postImgUrl;


}
