package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.GetUserFeedRes;
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
    @GetMapping("") // (GET) 127.0.0.1:9000/users pathvariable 에 받아줬으면 uri 에도 명시해주자
    public BaseResponse<List<GetPostRes>> getPost(@RequestParam int userIdx) {  //getPost함수는 userIdx를 통해서 GetPostRes 리스트들을 반환 매개변수는 쿼리파라미터로 받는다
        try{
            List<GetPostRes> getPostRes = postProvider.retrievePost(userIdx); //provider을 호출해서, getPostRes 리스트들을 받아온다.
            return new BaseResponse<>(getPostRes);
        } catch(BaseException exception){
            System.out.println(exception);
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq){//Post 메소드이고, pathvariable은 없으며 body에서 요청을 받는다.
        //데이터는 json 형태롤 보내야함..
        try{
            if(postPostReq.getContent().length()>450){
                return new BaseResponse<>(POST_POST_INVALID_CONTENT);   //validation 처리.
            }
            if(postPostReq.getPostImgUrl().size()<1){
                return new BaseResponse<>(POST_POST_EMPTY_IMGURL);     //validation 처리 .
            }
            PostPostRes postPostRes=postService.createPost(postPostReq.getUserIdx(),postPostReq);  //userIdx를 따로 받아주는이유는 Dao에서 편하게 넣어주기 위해서 getter 사용
            return new BaseResponse<>(postPostRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{postIdx}") //Patch Mapping , pathVariable로 postIdx를 받아줌 아래에 함수의 매개변수로 pathVariable 사용가능
    public BaseResponse<String> modifyPost(@PathVariable ("postIdx") int postIdx, @RequestBody PatchPostReq patchPostReq){//postidx를 패스배리어블로 받고 그것을 postIdx에 넣음 , 쿼리파라미터도 받음
        //데이터는 json 형태롤 보내야함..
        try{
            if(patchPostReq.getContent().length()>450){
                return new BaseResponse<>(POST_POST_INVALID_CONTENT);   //validation 처리.
            }
            String patchPostRes="complete modify";
            postService.modifyPost(patchPostReq.getUserIdx(),postIdx,patchPostReq);  //userIdx를 따로 받아주는이유는 Dao에서 편하게 넣어주기 위해서 getter 사용
            return new BaseResponse<>(patchPostRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{postIdx}/status") //Patch Mapping , pathVariable로 postIdx를 받아줌 아래에 함수의 매개변수로 pathVariable 사용가능 , 같은 Patch 이므로 pathvariable로 하나 더 받아줌
    // {} 의값은 pathvariable 즉 매개변수로, 직접 넣어줘야하고,  pachmapping 만 하고있는 status는 uri에만 넣어주면 된다.
    public BaseResponse<String> deletePost(@PathVariable ("postIdx") int postIdx){//postidx를 패스배리어블로 받고 그것을 postIdx에 넣음
        //데이터는 json 형태롤 보내야함..
        try{
            String deletePostRes="complete delete";
            postService.deletePost(postIdx);  // postIdx로 delete한다. 그런데, 나중에 user인지 확인해야하지않나? jwt로..
            return new BaseResponse<>(deletePostRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }






/**
 *
 * @ResponseBody
 *     @GetMapping("")
 *     public BaseResponse<List<GetPostRes>> getPost(@RequestParam int userIdx){
 *         try{
 *
 *             List<GetPostRes> getPost=postProvider.retrievePost(userIdx);
 *             return new BaseResponse<>(getPost);
 *         } catch (BaseException exception){
 *             return new BaseResponse<>(exception.getStatus());
 *         }
 *     }
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
