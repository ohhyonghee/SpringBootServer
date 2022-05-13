package com.example.demo.src.auth;


import com.example.demo.src.auth.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AuthDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public User getPwd(PostLoginReq postLoginReq){   // 바디값으로 받은 로그인요청객체에서 이메일을 받아서 유저들의 정보를 리스트로 리턴해주는 함수
        String getPwdQuery = "SELECT userIdx, name nickName ,email, pwd FROM User WHERE email=?"; //이메일을 매개변수로 받아서 유저의 정보를 가져오는 쿼리
        String getPwdParam = postLoginReq.getEmail(); //바디에서 받은 로그인요청객체에서 이메일을 추출한다
        return this.jdbcTemplate.queryForObject(getPwdQuery,  //jdbc로 쿼리 실행
                (rs,rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("pwd")
                ),
                getPwdParam);
    }


/**
    // 로그인
    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, name, nickName,  email, pwd from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("pwd")
                ),
                getPwdParams
                );

    }


    // 유저 확인
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    // 이메일 확인
    public int checkEmailExist(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }


    public String checkUserStatus(String email){
        String checkUserStatusQuery = "select status from User where email = ? ";
        String checkUserStatusParams = email;
        return this.jdbcTemplate.queryForObject(checkUserStatusQuery,
                String.class,
                checkUserStatusParams);

    }
**/
}
