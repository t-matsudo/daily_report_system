package models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = JpaConst.TABLE_FOL)
@NamedQueries({
    @NamedQuery(
            name = JpaConst.Q_FOL_GET_ALL_FOLLOWLIST,
            query = JpaConst.Q_FOL_GET_ALL_FOLLOWLIST_DEF),
    @NamedQuery(
            name = JpaConst.Q_FOL_COUNT_ALL_FOLLOW,
            query = JpaConst.Q_FOL_COUNT_ALL_FOLLOW_DEF),
    @NamedQuery(
            name = JpaConst.Q_FOL_GET_ALL_FOLLOWERLIST,
            query = JpaConst.Q_FOL_GET_ALL_FOLLOWERLIST_DEF),
    @NamedQuery(
            name = JpaConst.Q_FOL_COUNT_ALL_FOLLOWER,
            query = JpaConst.Q_FOL_COUNT_ALL_FOLLOWER_DEF),
    @NamedQuery(
            name = JpaConst.Q_FOL_COUNT_FOLLOWED_BY_CODE,
            query = JpaConst.Q_FOL_COUNT_FOLLOWED_BY_CODE_DEF),
    @NamedQuery(
            name = JpaConst.Q_FOL_GET_FOLLOWED_BY_CODE,
            query = JpaConst.Q_FOL_GET_FOLLOWED_BY_CODE_DEF)
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Follow {

    //データのID
    @Id
    @Column(name = JpaConst.FOL_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //フォローする社員の社員コード
    @Column(name = JpaConst.FOL_COL_FOLLOW, nullable = false)
    private String follow;

    //フォローされた社員の社員コード
    @Column(name = JpaConst.FOL_COL_FOLLOWER, nullable = false)
    private String follower;

    //フォローした日
    @Column(name = JpaConst.FOL_COL_DATE, nullable = false)
    private LocalDateTime followDay;

}
