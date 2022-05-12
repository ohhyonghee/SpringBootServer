package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.GetUserFeedRes;
import com.example.demo.src.user.model.GetUserInfoRes;
import com.example.demo.src.user.model.GetUserPostRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_EMPTY_USER_ID;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }


    public GetUserFeedRes retrieveUserFeed(int userIdxByJwt, int userIdx) throws BaseException{   //userIdx를 두개를 받음 하나는 userIdxByJwt로
        Boolean isMyFeed=true;  //일단 내 피드인게 맞다고 가정 한 뒤
        if(checkUserExist(userIdx)==0){ // 의미적 validation. 유저가 존재하지않으면 오류코드. 이 함수는 provider에 있다.
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        try{
            if(userIdxByJwt!=userIdx) //  혹시 내피드가 아니라면 거짓으로
                isMyFeed=false;
            GetUserInfoRes getUserInfoRes = userDao.selectUserInfo(userIdx);  //유저의 정보를 받아오는 객체
            List<GetUserPostRes> getUserPostRes = userDao.selectUserPost(userIdx); //유저의 게시물 리스트를 받아오는 객체
            GetUserFeedRes getUserFeedRes = new GetUserFeedRes(isMyFeed,getUserInfoRes,getUserPostRes); //
            return getUserFeedRes;
        }
        catch (Exception exception) {
            //System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
                    }


    public GetUserRes getUserByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUserRes = userDao.getUserByIdx(userIdx);
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BaseException{
        try{
            return userDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }





}
