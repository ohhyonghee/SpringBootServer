package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
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

    public GetUserInfoRes selectUserInfo(int userIdx){
        String selectUserInfoQuery = "SELECT u.userIdx as userIdx,\n" +
                "            u.nickName as nickName,\n" +
                "            u.name as name,\n" +
                "            u.profileImgUrl as profileImgUrl,\n" +
                "            u.website as website,\n" +
                "            u.introduction as introduction,\n" +
                "            IF(postCount is null, 0, postCount) as postCount,\n" +
                "            IF(followerCount is null, 0, followerCount) as followerCount,\n" +
                "            If(followingCount is null, 0, followingCount) as followingCount,\n" +
                "        FROM User as u\n" +
                "            left join (select userIdx, count(postIdx) as postCount from Post WHERE status = 1 group by userIdx) p on p.userIdx = u.userIdx\n" +
                "            left join (select followerIdx, count(followIdx) as followerCount from Follow WHERE status = 1 group by followIdx) fc on fc.followerIdx = u.userIdx\n" +
                "            left join (select followeeIdx, count(followIdx) as followingCount from Follow WHERE status = 1 group by followIdx) f on f.followeeIdx = u.userIdx\n" +
                "        WHERE u.userIdx = ? and u.status = 1\n" ;
        int selectUserInfoParam=userIdx;  // ????
        return this.jdbcTemplate.queryForObject(selectUserInfoQuery,    // selectUserInfo에서 반환하는 값이 List이면 query  아니면 queryForObject
                (rs,rowNum) -> new GetUserInfoRes(
                        rs.getString("nickName"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("webSite"),
                        rs.getString("introduction"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getInt("postCount")
                ),selectUserInfoParam);
    }

    public List<GetUserPostRes> selectUserPost(int userIdx){
        String selectUserPostQuery =
                        "        SELECT p.postIdx as postIdx,\n" +
                        "            pi.imgUrl as postImgUrl\n" +
                        "        FROM Post as p\n" +
                        "            join PostImgUrl as pi on pi.postIdx = p.postIdx and pi.status = 1\n" +
                        "            join User as u on u.userIdx = p.userIdx\n" +
                        "        WHERE p.status = 1 and u.userIdx = ?\n" +
                        "        group by p.postIdx\n" +
                        //"        HAVING min(pi.postImgUrlIdx)\n" +   ??????
                        "        order by p.postIdx; " ;
        int selectUserPostParam=userIdx;  // ????
        return this.jdbcTemplate.query(selectUserPostQuery,    // selectUserInfo에서 반환하는 값이 List이면 query  아니면 queryForObject
                (rs,rowNum) -> new GetUserPostRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ),selectUserPostParam);
    }

    public GetUserRes getUserByEmail(String email){
        String getUserByEmailQuery = "select userIdx,name,nickName,email from User where email=?";
        String getUserByEmailParams = email;
        return this.jdbcTemplate.queryForObject(getUserByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUserByEmailParams);
    }


    public GetUserRes getUserByIdx(int userIdx){
        String getUserByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
        int getUserByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
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

    // userFeed 조회할떄 userIdx 가 유효한 Idx 인지 확인하는 과정
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParam);
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
