package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PatchUserReq;
//import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;


    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }
    public PostPostRes createPost(int userIdx,PostPostReq postPostReq) throws BaseException {
        try{
            int postIdx=postDao.insertPost(userIdx,postPostReq.getContent());  //userIdx 를 따로 넘겨줘서 Dao에서 추가 쉽게
            for (int i=0;i<postPostReq.getPostImgUrl().size();i++){  // 한 게시물에 여러 사진을 넣어야하므로, 리스트로 받은 url들을 반복문을 통해서 호출
                // insertPostImg 함수를 따로 정의해서 여러번 넣어줘야한다.
                postDao.insertPostImg(postIdx,postPostReq.getPostImgUrl().get(i));
            }
            return new PostPostRes(postIdx);  // 방금 생긴 postIdx 를 반환해준다.
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }








    public void modifyPost(int userIdx,int postIdx, PatchPostReq patchPostReq) throws BaseException {
        if(postProvider.checkUserExist(userIdx) ==0){  //controller에서 생성한 객체의 메소드를 사용 먼저, 유저가 있는지  check
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(postProvider.checkPostExist(postIdx) ==0){    //validation 처리. 의미적 validation도 provider을 통해서 처리해야한다
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }
        try{
            int result=postDao.updatePost(postIdx,patchPostReq.getContent());  // Dao에서 잘 실행되면 1 아니면 0
            // userIdx는 Dao까지 갈 필요없고 update함수를 사용할것이므로 네이밍은 update

            if(result==0){
                throw new BaseException(MODIFY_FAIL_POST);
            }

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void deletePost(int postIdx) throws BaseException {
        if(postProvider.checkPostExist(postIdx) ==0){    //validation 처리. 의미적 validation도 provider을 통해서 처리해야한다
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }
        try{
            int result=postDao.deletePost(postIdx);  // Dao에서 잘 실행되면 1 아니면 0  쿼리문에서 사용하는 이름의 함수로 Dao 에서 정의해주면 좋다.
            if(result==0){
                throw new BaseException(DELETE_FAIL_POST);
            }

        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

/**
    //게시글 작성
    public PostPostRes createPost(int userIdx, PostPostReq postPostReq) throws BaseException {


        try{
            int postIdx = postDao.insertPost(userIdx, postPostReq);
            for(int i=0; i< postPostReq.getPostImgsUrl().size(); i++) {
                postDao.insertPostImgs(postIdx, postPostReq.getPostImgsUrl().get(i));
            }
            return new PostPostRes(postIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

/
    // 게시물 수정
    public void modifyPost(int userIdx,int postIdx, PatchPostReq patchPostReq) throws BaseException {
        if(postProvider.checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(postProvider.checkPostExist(postIdx) ==0){
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }

        if(postProvider.checkUserPostExist(userIdx, postIdx)==0){
            throw new BaseException(POSTS_EMPTY_USER_POST);
        }
        try{
            int result = postDao.updatePost(postIdx,patchPostReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_POST);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원 삭제
    public void deletePost(int userIdx,int postIdx) throws BaseException {
        if(postProvider.checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(postProvider.checkPostExist(postIdx) ==0){
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }

        if(postProvider.checkUserPostExist(userIdx, postIdx)==0){
            throw new BaseException(POSTS_EMPTY_USER_POST);
        }
        try{
            int result = postDao.updatePostStatus(postIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_POST);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
 **/
}
