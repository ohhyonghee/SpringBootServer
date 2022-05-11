package com.example.demo.src.post;


import com.example.demo.config.BaseException;

import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.model.GetPostRes;
import com.example.demo.src.post.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class PostProvider {

    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }



    // 유저 확인
    public int checkUserExist(int userIdx) throws BaseException{
        try{
            return postDao.checkUserExist(userIdx);
        } catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkPostExist(int postIdx) throws BaseException{   //Dao 에 있는 함수를 호출해서 validation 검증 . 근데 왜 이렇게 두단계로 나눠서하지?
        // 그리고 전에 강의에서 말한듯, 의미적 validation이라서 service에서 검증해야할때도, 그 메소드는 provider을 통해서 가야한다.
        try{
            return postDao.checkPostExist(postIdx);
        } catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //게시물 리스트 조회
    public List<GetPostRes> retrievePost(int userIdx) throws BaseException {

        if(checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{

            List<GetPostRes> getPost = postDao.selectPost(userIdx);

            return getPost;
        } catch(Exception exception){
            System.out.println(exception);
           throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     // 게시물 확인
     public int checkPostExist(int postIdx) throws BaseException{
     try{
     return postDao.checkPostExist(postIdx);
     } catch (Exception exception){
     throw new BaseException(DATABASE_ERROR);
     }
     }

     // 유저의 게시물인지 확인
     public int checkUserPostExist(int userIdx,int postIdx) throws BaseException{
     try{
     return postDao.checkUserPostExist(userIdx,postIdx);
     } catch (Exception exception){
     throw new BaseException(DATABASE_ERROR);
     }
     }

     // 이메일 확인
     public int checkEmailExist(String email) throws BaseException{
     try{
     return postDao.checkEmailExist(email);
     } catch (Exception exception){
     throw new BaseException(DATABASE_ERROR);
     }
     }

     // 이메일 확인
     public String checkUserStatus(String email) throws BaseException{
     try{
     return postDao.checkUserStatus(email);
     } catch (Exception exception){
     throw new BaseException(DATABASE_ERROR);
     }
     }
     **/


}
