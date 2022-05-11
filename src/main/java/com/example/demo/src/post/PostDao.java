package com.example.demo.src.post;


import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private List<GetPostImgRes> getPostImgRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // 유저 확인
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }
    public int checkPostExist(int postIdx){  //validation   :  post가 존재하는지 service에서 처리하기 위한 메소드는 dao 에서 정의
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);
    }
    // 게시글 리스트 조회
    public List<GetPostRes> selectPost(int userIdx){
        String selectUserPostQuery = "\n" +
                "        SELECT p.postIdx as postIdx,\n" +
                "            u.userIdx as userIdx,\n" +
                "            u.nickName as nickName,\n" +
                "            u.profileImgUrl as profileImgUrl,\n" +
                "            p.content as content,\n" +
                "            IF(postLikeCount is null, 0, postLikeCount) as postLikeCount,\n" +
                "            IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "            case\n" +
                "                when timestampdiff(second, p.updatedAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')\n" +
                "                when timestampdiff(minute , p.updatedAt, current_timestamp) < 60\n" +
                "                    then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')\n" +
                "                when timestampdiff(hour , p.updatedAt, current_timestamp) < 24\n" +
                "                    then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')\n" +
                "                when timestampdiff(day , p.updatedAt, current_timestamp) < 365\n" +
                "                    then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')\n" +
                "                else timestampdiff(year , p.updatedAt, current_timestamp)\n" +
                "            end as updatedAt,\n" +
                "            IF(pl.status = 1, 'Y', 'N') as likeOrNot\n" +
                "        FROM Post as p\n" +
                "            join User as u on u.userIdx = p.userIdx\n" +
                "            left join (select postIdx, userIdx, count(postReactionIdx) as postLikeCount from PostReaction WHERE status = 1 group by postIdx) plc on plc.postIdx = p.postIdx\n" +
                "            left join (select postIdx, count(commentIdx) as commentCount from Comment WHERE status = 1 group by postIdx) c on c.postIdx = p.postIdx\n" +
                "            left join Follow as f on f.followeeIdx = p.userIdx and f.status = 1\n" +
                "            left join PostReaction as pl on pl.userIdx = f.followerIdx and pl.postIdx = p.postIdx\n" +
                "        WHERE f.followerIdx = ? and p.status = 1\n" +
                "        group by p.postIdx;\n" ;
        int selectUserPostParam = userIdx;
        return this.jdbcTemplate.query(selectUserPostQuery,
                (rs,rowNum) -> new GetPostRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                         getPostImgRes = this.jdbcTemplate.query(
                                         "SELECT pi.postImgUrlIdx,\n"+
                                         "            pi.imgUrl\n" +
                                         "        FROM PostImgUrl as pi\n" +
                                         "            join Post as p on p.postIdx = pi.postIdx\n" +
                                         "        WHERE pi.status = 1 and p.postIdx = ?;\n",
                        (rk,rownum) -> new GetPostImgRes(
                                rk.getInt("postImgUrlIdx"),
                                rk.getString("imgUrl"))
                                 ,rs.getInt("postIdx"))),selectUserPostParam);
    }
    public int insertPost(int userIdx, String content){
        String insertPostQuery = "INSERT INTO Post(userIdx,content) VALUE(?,?)";  //쿼리문 공부하기
        Object []insertPostParam= new Object[] {userIdx,content};  // ? , ? 에 들어갈 무언가들을 Param으로 받는중
        this.jdbcTemplate.update(insertPostQuery,
                insertPostParam);//INSERT 문 사용할떄는 return이 아니라 UPDATE 를 해줘야함    this.jdbcTemplate 는 쿼리문을 그 뒤에 매개변수와 함께 실행
        String lastInsertIdxQuery="select last_insert_id()";   // 자동으로 가장 마지막에 들어간 idx 값 리턴
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery,int.class); //jdbc로 쿼리 실행 후 방금들어간 idx를 가져와서 리턴해준다
    }

    public int insertPostImg(int postIdx, PostImgUrlReq postImgUrlReq){   //for 문을 통해서 게시물의 여러 사진인, postImgUrl들이 순서대로 대입
        String insertPostImgQuery = "INSERT INTO PostImgUrl(postIdx,imgUrl) VALUE(?,?)"; //데이터 베이스에 입력해야하는 값들을 매개변수로, value 뒤의 값들은 입력받을 값들이다
        Object []insertPostImgParam= new Object[] {postIdx,postImgUrlReq.getImgUrl()};
        this.jdbcTemplate.update(insertPostImgQuery,
                insertPostImgParam);
        String lastInsertIdxQuery="select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery,int.class);
    }

    public int updatePost(int postIdx, String content){   // 객체를 받지않고 getter을 사용해서 바로 content 만 받아줌
        String updatePostQuery = "UPDATE Post SET content=? WHERE postIdx=?"; // ? 뒤의 값들은 입력받아야하는 변수들이고, 그것들은 Param으로 넘겨줄것이다.
        //그런데 왜 위에서는 VALUE 로 받아주고 여기서는 그대로?

        Object [] updatePostParam = new Object[] {content,postIdx};// 새로 생성한 배열들을 param 으로 받아서 넘겨줄것이다.
        return this.jdbcTemplate.update(updatePostQuery,
                updatePostParam);  // update는 성공한 row의 갯수를 반환한다
    }
    public int deletePost(int postIdx){
        String deletePostQuery = "UPDATE Post SET status=0 WHERE postIdx=?"; // ? 뒤의 값들은 입력받아야하는 변수들이고, 그것들은 Param으로 넘겨줄것이다.
        //그런데 왜 위에서는 VALUE 로 받아주고 여기서는 그대로?

        Object [] deletePostParam =new Object[] {postIdx};// param 으로 받아서 넘겨줄것이다. 값이 하나일때도 배열로 넘겨주는게 좋은가?
        return this.jdbcTemplate.update(deletePostQuery,
                deletePostParam);  // update는 성공한 row의 갯수를 반환한다
    }


/**
    // 회원 확인
    public String checkUserStatus(String email){
        String checkUserStatusQuery = "select status from User where email = ? ";
        String checkUserStatusParams = email;
        return this.jdbcTemplate.queryForObject(checkUserStatusQuery,
                String.class,
                checkUserStatusParams);

    }
    // 게시글 확인
    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);

    }
    // 이메일 확인
    public int checkEmailExist(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    // 게시물, 유저 확인
    public int checkUserPostExist(int userIdx, int postIdx){
        String checkUserPostQuery = "select exists(select postIdx from Post where postIdx = ? and userIdx=?) ";
        Object[]  checkUserPostParams = new Object[]{postIdx,userIdx};
        return this.jdbcTemplate.queryForObject(checkUserPostQuery,
                int.class,
                checkUserPostParams);

    }

    // 게시글 작성
    public int insertPost(int userIdx, PostPostReq postPostReq){
        String insertPostQuery =
                "        INSERT INTO Post(userIdx, content)\n" +
                "        VALUES (?, ?);";
        Object[] insertPostParams = new Object[]{userIdx,postPostReq.getContent()};
        this.jdbcTemplate.update(insertPostQuery, insertPostParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);

    }

    // 게시글 이미지 작성
    public int insertPostImgs(int postIdx, PostImgsUrlReq postImgsUrlReq){
        String insertPostImgQuery =
                "        INSERT INTO PostImgUrl(postIdx, imgUrl)\n" +
                "        VALUES (?, ?);";
        Object[] insertPostImgParams = new Object[]{postIdx,postImgsUrlReq.getImgUrl()};
        this.jdbcTemplate.update(insertPostImgQuery, insertPostImgParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    // 게시글  수정
    public int updatePost(int postIdx,  PatchPostReq patchPostReq){
        String updatePostQuery = "UPDATE Post\n" +
                "        SET content = ?\n" +
                "        WHERE postIdx = ?" ;
        Object[] updatePostParams = new Object[]{patchPostReq.getContent(), postIdx};

        return this.jdbcTemplate.update(updatePostQuery,updatePostParams);
    }

    //게시글 삭제
    public int updatePostStatus(int postIdx){
        String deleteUserQuery = "UPDATE Post\n" +
                "        SET status = 'INACTIVE'\n" +
                "        WHERE postIdx = ? ";
        Object[] deleteUserParams = new Object[]{postIdx};

        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }
 **/
}
