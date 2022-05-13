package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

import static com.example.demo.utils.ValidationRegex.isRegexEmail;
//import static com.example.demo.utils.ValidationRegex.isRegexPassword;


@RestController
@RequestMapping("/auth")
public class AuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;




    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService){
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @PostMapping("/logIn") //Post Mapping , uri에 logIn 도 넣어주기로 약속
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){//PostLoginReq를 쿼리파라미터 바디로 받는다
        //데이터는 json 형태롤 보내야함..
        try{
            //이메일을 입력하지 않았을때
            if(postLoginReq.getEmail()==null){ //입력받은 객체에서 이메일값을 가져왔는데 null이면
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }

            //이메일 형식 검증
            if(!isRegexEmail(postLoginReq.getEmail())){//isRegexEmail은 이메일 정규식 확인하는함수이다. 안의 값이 정규하면 0리턴 정규안하면 1리턴
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            //비밀번호 입력하지 않았을때
            if(postLoginReq.getPwd()==null){ //입력받은 객체에서 이메일값을 가져왔는데 그것이 null 이면
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            PostLoginRes postLoginRes = authService.logIn(postLoginReq);  //service 로 넘겨준다
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

/*
    // 로그인
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{

            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            if(postLoginReq.getEmail() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if(postLoginReq.getPwd() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

            if(!isRegexEmail(postLoginReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
          //  if(!isRegexPassword(postLoginReq.getPwd())){
            //    return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
            //}
            PostLoginRes postLoginRes = authProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }*/

   /* @ResponseBody
    @GetMapping("/jwt")
    public BaseResponse<GetAutoLoginRes> autologin() throws BaseException{
        try{
            if(jwtService.getJwt()==null){
                return new BaseResponse<>(EMPTY_JWT);
            }
            else if(authProvider.checkJwt(jwtService.getJwt())==1){
                return new BaseResponse<>(INVALID_JWT);

            }

            else{
                String jwt=jwtService.getJwt();
                int userIdx=jwtService.getUserIdx();
                GetAutoLoginRes getAutoRes = userProvider.getAuto(jwt,userIdx);
                return new BaseResponse<>(getAutoRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }*/

}
