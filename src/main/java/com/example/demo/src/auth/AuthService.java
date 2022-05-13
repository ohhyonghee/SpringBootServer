package com.example.demo.src.auth;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.auth.model.*;
//import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class AuthService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;


    @Autowired
    public AuthService(AuthDao authDao, AuthProvider authProvider, JwtService jwtService) {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;

    }
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {  // Req 를 받아서 Res를 반환해준다. Res에는 jwt정보를 담고있다.
        // 로그인 요청객체를 다오에 전달해서 유저다오에 전달해서 그에 해당하는 유저를 받아오고, 유저의 비밀번호를 받아올것임. 이 두개의 비밀번호를 비교할것임
        //먼저 포스트로그인 요청객체로 그에 알맞는 유저를 반환해줄것임  그러기 위해서 비밀번호를 가지고 있는 유저 모델이 필요하다.
        User user=authDao.getPwd(postLoginReq);    //이 비밀번호는 요청받은 로그인 모델에 있는 비밀번호
        //새로 받은 비밀번호를 암호화 해줄것임 util/SHA256  암호화 알고리즘
        String encryptPwd;   // 이 비밀번호는 데이터 베이스에있는비밀번호
        try{
            encryptPwd=new SHA256().encrypt(postLoginReq.getPwd());
        } catch(Exception exception) {
            System.out.println(exception);
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        //데이터베이스에있는 비밀번호와 입력받은 비밀번호를 비교해줘야함
        if(user.getPwd().equals(encryptPwd)){   // 두개를 비교  ?? 근데 하나는 암호화했고 하나는 안한건데...???
            int userIdx=user.getUserIdx();  //이상이 없다면 , userIdx를 통한 jwt를 발급할것
            String jwt =jwtService.createJwt(userIdx);  //utils/JwtService 이용
            return new PostLoginRes(userIdx,jwt);
        }
        else
            throw new BaseException(FAILED_TO_LOGIN);
    }
}
