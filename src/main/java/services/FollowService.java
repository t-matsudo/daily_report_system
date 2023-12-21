package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.FollowConverter;
import actions.views.FollowView;
import constants.JpaConst;
import models.Follow;
import models.validators.FollowValidator;

public class FollowService extends ServiceBase {

    /**
     * 1ページごとのフォローリストを獲得する
     * @param code
     * @param page
     * @return
     */
    public List<FollowView> getFollowPerPage(String code, int page){
        List<Follow> follows = em.createNamedQuery(JpaConst.Q_FOL_GET_ALL_FOLLOWLIST, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOW, code)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return FollowConverter.toList(follows);
    }

    /**
     * 1ページごとのフォロワーリストを獲得する
     * @param code
     * @param page
     * @return
     */
    public List<FollowView> getFollowerPerPage(String code, int page){
        List<Follow> followers = em.createNamedQuery(JpaConst.Q_FOL_GET_ALL_FOLLOWERLIST, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWER, code)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return FollowConverter.toList(followers);
    }

    /**
     * 全てのフォローリストを獲得する
     * @param code
     * @param page
     * @return
     */
    public List<FollowView> getAllFollow(String code){
        List<Follow> follows = em.createNamedQuery(JpaConst.Q_FOL_GET_ALL_FOLLOWLIST, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOW, code)
                .getResultList();
        return FollowConverter.toList(follows);
    }

    /**
     * 全てのフォロワーリストを獲得する
     * @param code
     * @param page
     * @return
     */
    public List<FollowView> getAllFollower(String code){
        List<Follow> followers = em.createNamedQuery(JpaConst.Q_FOL_GET_ALL_FOLLOWERLIST, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWER, code)
                .getResultList();
        return FollowConverter.toList(followers);
    }


    /**
     * フォローする機能
     * @param code：フォローする社員番号
     * @param followerCode：フォローされる社員番号
     * @return
     */
    public List<String> follow(String code,String followerCode){
        List<String> errors = FollowValidator.codeValidate(code, followerCode);
        if(errors.size() == 0) {
            create(new FollowView(
                    null,
                    code,
                    followerCode,
                    LocalDateTime.now()));
        }
        return errors;
    }

    /**
     * フォローを解除する機能
     * @param code
     * @param followerCode
     * @return
     */
    public List<String> unfollow(String code,String followerCode){
        List<String> errors = FollowValidator.codeValidate(code, followerCode);
        if(errors.size() == 0) {
            remove(code,followerCode);
        }
        return errors;
    }

    /**
     * フォローデータを1件DBに登録する
     * @param ev
     */
    private void create(FollowView fv) {
        em.getTransaction().begin();
        em.persist(FollowConverter.toModel(fv));
        em.getTransaction().commit();
    }

    /**
     * フォローデータを1件DBから物理削除
     * @param fv
     */
    private void remove(String code,String followerCode) {
        em.getTransaction().begin();
        em.remove(em.createNamedQuery(JpaConst.Q_FOL_GET_FOLLOWED_BY_CODE, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOW, code)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWER, followerCode)
                .getSingleResult());
        em.getTransaction().commit();
    }

    /**
     * フォロー関係をカウント形式で取得
     * @param follow
     * @param follower
     * @return
     */
    public long countFollowed(String follow, String follower) {
        long count = (long)em.createNamedQuery(JpaConst.Q_FOL_COUNT_FOLLOWED_BY_CODE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOW, follow)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWER, follower)
                .getSingleResult();
        return count;
    }

}
