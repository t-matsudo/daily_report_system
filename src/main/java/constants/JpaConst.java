package constants;

public interface JpaConst {

    //persistence-unit名
    String PERSISTENCE_UNIT_NAME = "daily_report_system";

    //データ取得件数の最大値
    int ROW_PER_PAGE = 15;

    //従業員テーブル
    String TABLE_EMP = "employees";//テーブル名
    //従業員テーブルカラム
    String EMP_COL_ID = "id";
    String EMP_COL_CODE = "code";
    String EMP_COL_NAME = "name";
    String EMP_COL_PASS = "password";
    String EMP_COL_ADMIN_FLAG = "admin_flag";
    String EMP_COL_CREATED_AT = "created_at";
    String EMP_COL_UPDATED_AT = "updated_at";
    String EMP_COL_DELETE_FLAG = "delete_flag";

    int ROLE_ADMIN = 1;//管理者権限あり
    int ROLE_GENERAL = 0;//管理者権限なし
    int EMP_DEL_TRUE = 1;//削除済み
    int EMP_DEL_FALSE = 0;//削除前

    //日報テーブル
    String TABLE_REP = "repots";
    //日報テーブルカラム
    String REP_COL_ID = "id";
    String REP_COL_EMP = "employee_id";//日報を作成した従業員のID
    String REP_COL_REP_DATE = "repots_date";
    String REP_COL_TITLE = "title";
    String REP_COL_CONTENT = "content";
    String REP_COL_CREATED_AT = "created_at";
    String REP_COL_UPDATED_AT = "updated_at";

    //フォローテーブル
    String TABLE_FOL = "follows";
    //フォローテーブルカラム
    String FOL_COL_ID = "id";
    String FOL_COL_FOLLOW = "follow";
    String FOL_COL_FOLLOWER = "follower";
    String FOL_COL_DATE = "followDay";

    //Entity名
    String ENTITY_EMP = "employee";
    String ENTITY_REP = "report";
    String ENTITY_FOL = "follow";

    //JPQL内パラメータ
    String JPQL_PARM_CODE = "code";
    String JPQL_PARM_PASSWORD = "password";
    String JPQL_PARM_EMPLOYEE = "employee";
    String JPQL_PARM_FOLLOW = "follow";
    String JPQL_PARM_FOLLOWER = "follower";

    //NamedQueryのnameとquery
  //全ての従業員をidの降順に取得する
    String Q_EMP_GET_ALL = ENTITY_EMP + ".getAll"; //name
    String Q_EMP_GET_ALL_DEF = "SELECT e FROM Employee AS e ORDER BY e.id DESC"; //query
    //全ての従業員の件数を取得する
    String Q_EMP_COUNT = ENTITY_EMP + ".count";
    String Q_EMP_COUNT_DEF = "SELECT COUNT(e) FROM Employee AS e";
    //社員番号とハッシュ化済パスワードを条件に未削除の従業員を取得する
    String Q_EMP_GET_BY_CODE_AND_PASS = ENTITY_EMP + ".getByCodeAndPass";
    String Q_EMP_GET_BY_CODE_AND_PASS_DEF = "SELECT e FROM Employee AS e WHERE e.deleteFlag = 0 AND e.code = :" + JPQL_PARM_CODE + " AND e.password = :" + JPQL_PARM_PASSWORD;
    //指定した社員番号を保持する従業員の件数を取得する
    String Q_EMP_COUNT_REGISTERED_BY_CODE = ENTITY_EMP + ".countRegisteredByCode";
    String Q_EMP_COUNT_REGISTERED_BY_CODE_DEF = "SELECT COUNT(e) FROM Employee AS e WHERE e.code = :" + JPQL_PARM_CODE;
    //指定した社員番号を保持する従業員の件数を取得する
    String Q_EMP_GET_REGISTERED_BY_CODE = ENTITY_EMP + ".getRegisteredByCode";
    String Q_EMP_GET_REGISTERED_BY_CODE_DEF = "SELECT e FROM Employee e WHERE e.code in (:" + JPQL_PARM_CODE + ")";

    //全ての日報をidの降順に取得する
    String Q_REP_GET_ALL = ENTITY_REP + ".getAll";
    String Q_REP_GET_ALL_DEF = "SELECT r FROM Report AS r ORDER BY r.id DESC";
    //全ての日報の件数を取得する
    String Q_REP_COUNT = ENTITY_REP + ".count";
    String Q_REP_COUNT_DEF = "SELECT COUNT(r) FROM Report AS r";
    //指定した従業員が作成した日報を全件idの降順で取得する
    String Q_REP_GET_ALL_MINE = ENTITY_REP + ".getAllMine";
    String Q_REP_GET_ALL_MINE_DEF = "SELECT r FROM Report AS r WHERE r.employee in (:" + JPQL_PARM_EMPLOYEE + ") ORDER BY r.id DESC";
    //指定した従業員が作成した日報の件数を取得する
    String Q_REP_COUNT_ALL_MINE = ENTITY_REP + ".countAllMine";
    String Q_REP_COUNT_ALL_MINE_DEF = "SELECT COUNT(r) FROM Report AS r WHERE r.employee in(:" + JPQL_PARM_EMPLOYEE + ")";

    //指定した従業員のフォローリストを検索
    String Q_FOL_GET_ALL_FOLLOWLIST = ENTITY_FOL + ".getAllFollow";
    String Q_FOL_GET_ALL_FOLLOWLIST_DEF = "SELECT f FROM Follow AS f WHERE f.follow = :"+ JPQL_PARM_FOLLOW + " ORDER BY f.id DESC";
    //指定した従業員のフォロー数を取得
    String Q_FOL_COUNT_ALL_FOLLOW = ENTITY_FOL + ".countAllFollow";
    String Q_FOL_COUNT_ALL_FOLLOW_DEF = "SELECT COUNT(f) FROM Follow AS f WHERE f.follow = :"+ JPQL_PARM_FOLLOW + " ORDER BY f.id DESC";
    //指定した従業員のフォロワーリストを検索
    String Q_FOL_GET_ALL_FOLLOWERLIST = ENTITY_FOL + ".getAllFollower";
    String Q_FOL_GET_ALL_FOLLOWERLIST_DEF = "SELECT f FROM Follow AS f WHERE f.follower = :" + JPQL_PARM_FOLLOWER + " ORDER BY f.id DESC";
    //指定した従業員のフォロワー数を取得
    String Q_FOL_COUNT_ALL_FOLLOWER = ENTITY_FOL + ".countAllFollower";
    String Q_FOL_COUNT_ALL_FOLLOWER_DEF = "SELECT COUNT(f) FROM Follow AS f WHERE f.follower = :" + JPQL_PARM_FOLLOWER + " ORDER BY f.id DESC";
    //指定した2人の社員IDのフォロー関係の数を取得する
    String Q_FOL_COUNT_FOLLOWED_BY_CODE = ENTITY_FOL + ".countFollowedByCode";
    String Q_FOL_COUNT_FOLLOWED_BY_CODE_DEF = "SELECT COUNT(f) FROM Follow f WHERE f.follow = :" + JPQL_PARM_FOLLOW + " AND f.follower = :" + JPQL_PARM_FOLLOWER;
    //指定した2人の社員IDのフォロー関係を取得する
    String Q_FOL_GET_FOLLOWED_BY_CODE = ENTITY_FOL + ".getFollowedByCode";
    String Q_FOL_GET_FOLLOWED_BY_CODE_DEF = "SELECT f FROM Follow f WHERE f.follow = :" + JPQL_PARM_FOLLOW + " AND f.follower = :" + JPQL_PARM_FOLLOWER;

}
