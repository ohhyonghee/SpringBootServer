package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/user")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }



    /**
     * 회원 조회 API
     * [GET] /users
     * 이메일 검색 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<GetUserRes>
     */
    //Query String
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users pathvariable 에 받아줬으면 uri 에도 명시해주자
    public BaseResponse<GetUserFeedRes> getUserFeed(@PathVariable("userIdx")int userIdx) {
        try{
            GetUserFeedRes getUserFeedRes = userProvider.retrieveUserFeed(userIdx,userIdx);
            return new BaseResponse<>(getUserFeedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/{userIdx}/X") // (GET) 127.0.0.1:9000/users/:userIdx uri가 겹치면 안된다
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx")int userIdx) {
        try{

            GetUserRes getUserRes = userProvider.getUserByIdx(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("") // (POST) 127.0.0.1:9000/users
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        // 이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저삭제 API
     * [DELETE] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> deleteUserByIdx(@PathVariable("userIdx") int userIdx){
        try {
            /*
            if(Character.isDigit((char)userIdx)==false){
                return new BaseResponse<>(INVAILD_USERIDX);
            } 어차피 매개변수에서 int 가 아닌 값이 오면 안된다.*/
            if(userIdx<0){
                return new BaseResponse<>(INVAILD_USERIDX); //INVAILD_USERIDX는 BaseResponseStatus에 기록
            }  // int 로 받은 userIdx 가 오류가 될 수 있는 상황. userIdx 가 음수일떄. 0일때는 혹시 몰라서 남겨놓음.  형식적 validation
            userService.deleteUserByIdx(userIdx); // UserService 로 넘겨준다.
            String result = "유저 삭제에 성공 했습니다."; //성공했을떄를 가정 . 실패했다면 전에서 걸렸음
            return new BaseResponse<>(result); //성공결과를 반환.  BaseResponse는 code , result 등을 가지는 객체
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }





    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */

    @ResponseBody
    @PatchMapping("/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            /* TODO: jwt는 다음주차에서 배울 내용입니다!
            jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            */

            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getNickName());
            userService.modifyUserName(patchUserReq);

            String result = "";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
