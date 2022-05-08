package com.example.demo.src.user;


import com.example.demo.src.user.model.DeleteUserReq;
import com.example.demo.src.user.model.GetUserFeedRes;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserFeedRes> getUsers(){
        String getUserQuery = "select userIdx,name,nickName,email from User";
        return this.jdbcTemplate.query(getUserQuery,
                (rs,rowNum) -> new GetUserFeedRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")
                ));
    }

    public GetUserFeedRes getUserByEmail(String email){
        String getUserByEmailQuery = "select userIdx,name,nickName,email from User where email=?";
        String getUserByEmailParams = email;
        return this.jdbcTemplate.queryForObject(getUserByEmailQuery,
                (rs, rowNum) -> new GetUserFeedRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUserByEmailParams);
    }


    public GetUserFeedRes getUserByIdx(int userIdx){
        String getUserByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
        int getUserByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserByIdxQuery,
                (rs, rowNum) -> new GetUserFeedRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUserByIdxParams);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, nickName, phone, email, password) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getNickName(),postUserReq.getPhone(), postUserReq.getEmail(), postUserReq.getPassword()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public int deleteUserByIdx(int userIdx){    //유저삭제. 삭제할 유저 idx 를 받아서,  수정성공한 row 의 갯수를 반환
        String deleteUserByIdxQuery = "delete from User where userIdx=?";    //database에 보낼 query useridx를 받아야한다.
        int deleteUserByIdxParams = userIdx;  // 어떤 값을 삭제할지 저장하는 params , 함수의 매개변수로 받은값

        return this.jdbcTemplate.update(deleteUserByIdxQuery,deleteUserByIdxParams); // jdbcTemplate 템플릿 참고  update는 insert , delete, modify 가능. 매개변수에 따라 다르다.
    }
}
