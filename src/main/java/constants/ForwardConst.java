package constants;

public enum ForwardConst {

    //action
    ACT("action"),
    ACT_TOP("Top"),
    ACT_EMP("Employee"),
    ACT_REP("Report"),
    ACT_AUTH("Auth"),
    ACT_FOLLOW("Follow"),


    //command
    CMD("command"),
    CMD_NONE(""),
    CMD_INDEX("index"),
    CMD_SHOW("show"),
    CMD_SHOW_LOGIN("showLogin"),
    CMD_LOGIN("login"),
    CMD_LOGOUT("logout"),
    CMD_NEW("entryNew"),
    CMD_CREATE("create"),
    CMD_EDIT("edit"),
    CMD_UPDATE("update"),
    CMD_DESTROY("destroy"),
    CMD_FOLLOW("follow"),
    CMD_UNFOLLOW("unfollow"),
    CMD_SHOWFOLLOW("showFollow"),
    CMD_SHOWFOLLOWER("showFollower"),

    //jsp
    FW_ERR_UNKNOWN("error/unknown"),
    FW_TOP_INDEX("topPage/index"),
    FW_LOGIN("login/login"),
    FW_EMP_INDEX("employees/index"),
    FW_EMP_SHOW("employees/show"),
    FW_EMP_NEW("employees/new"),
    FW_EMP_EDIT("employees/edit"),
    FW_REP_INDEX("reports/index"),
    FW_REP_SHOW("reports/show"),
    FW_REP_NEW("reports/new"),
    FW_REP_EDIT("reports/edit"),
    FW_FOL_INDEX("follows/index"),
    FW_FOL_SHOW("follows/showTimeline"),
    FW_FOL_FOLLOW("follows/follow"),
    FW_FOL_FOLLOWER("follows/follower");

    private final String text;

    //コンストラクタ
    private ForwardConst(final String text) {
        this.text = text;
    }

    //値取得
    public String getValue() {
        return this.text;
    }
}
