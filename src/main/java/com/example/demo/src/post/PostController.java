package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;
//import static com.example.demo.utils.ValidationRegex.isRegexNickName;


@RestController
@RequestMapping("/post")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;




    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService){
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }




    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPostRes>> getPost(@RequestParam int userIdx){
        try{

            List<GetPostRes> getPost=postProvider.retrievePost(userIdx);
            return new BaseResponse<>(getPost);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq){//Post 메소드이고, pathvariable은 없으며 body에서 요청을 받는다.
        //데이터는 json 형태롤 보내야함..
        try{
            if(postPostReq.getContent().length()>450){
                return new BaseResponse<>(POST_POST_INVALID_CONTENT);
            }
            if(postPostReq.getPostImgUrl().size()<1){
                return new BaseResponse<>(POST_POST_EMPTY_IMGURL);
            }
            PostPostRes postPostRes=postService.createPost(postPostReq.getUserIdx(),postPostReq);
            return new BaseResponse<>(postPostRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



/**
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq) {
        if(postPostReq.getContent() == null){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        if(postPostReq.getContent().length()>450){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        if(postPostReq.getPostImgsUrl().size()<1){
            return new BaseResponse<>(POST_POSTS_EMPTY_IMGRUL);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            PostPostRes postPostRes = postService.createPost(userIdxByJwt,postPostReq);
            return new BaseResponse<>(postPostRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 게시글 수정
    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BaseResponse<String> modifyPost(@PathVariable("postIdx") int postIdx, @RequestBody PatchPostReq patchPostReq){
        if(patchPostReq.getContent() == null){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        if(patchPostReq.getContent().length()>450){
            return new BaseResponse<>(POST_POSTS_EMPTY_CONTENTS);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            postService.modifyPost(userIdxByJwt,postIdx,patchPostReq);
            String result = "회원정보 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 게시물 삭제
    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    public BaseResponse<String> deleteUser(@PathVariable("postIdx") int postIdx){
        try {

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            postService.deletePost(userIdxByJwt,postIdx);

            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
**/
}
