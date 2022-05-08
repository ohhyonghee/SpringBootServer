package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.GetUserFeedRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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


    public GetUserFeedRes getUserByEmail(String email) throws BaseException{
        try{
            GetUserFeedRes getUserFeedRes = userDao.getUserByEmail(email);
            return getUserFeedRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
                    }


    public GetUserFeedRes getUserByIdx(int userIdx) throws BaseException{
        try{
            GetUserFeedRes getUserFeedRes = userDao.getUserByIdx(userIdx);
            return getUserFeedRes;
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



}
