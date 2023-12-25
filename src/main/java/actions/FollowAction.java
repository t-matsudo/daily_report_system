package actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.FollowView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.EmployeeService;
import services.FollowService;
import services.ReportService;

public class FollowAction extends ActionBase {

    private FollowService service;
    private EmployeeService empService;
    private ReportService repService;

    @Override
    public void process() throws ServletException, IOException {
        // TODO 自動生成されたメソッド・スタブ
        service = new FollowService();
        empService = new EmployeeService();
        repService = new ReportService();

        invoke();

        service.close();
        empService.close();
        repService.close();
    }

    /**
     * タイムラインの表示
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException{
        int page = getPage();
        List<ReportView> reports = null;
        long reportsCount = 0;
        EmployeeView follow = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
        //ログインユーザーのフォロー社員の登録IDリストを獲得
        List<FollowView>follows = service.getAllFollow(follow.getCode());
        if(follows.size() > 0) {
            List<EmployeeView> list = empService.getByCode(toStringList(follows));
            //獲得したIDリストからタイムラインを生成
            reports = repService.getMinePerPage(list, page);
            reportsCount = repService.countAllMine(list);
        }else {
            //フォローユーザー0件の処理（考えていない）
        }

        putRequestScope(AttributeConst.REPORTS, reports);
        putRequestScope(AttributeConst.REP_COUNT, reportsCount);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);

        //フラッシュメッセージが設定されている場合はリクエストスコープへ
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_FOL_INDEX);
    }


    /**
     * フォロー処理が行われた後に、元のユーザー画面に戻る処理
     * @throws ServletException
     * @throws IOException
     */
    public void follow() throws ServletException, IOException{
        List<String> errors = new ArrayList<String>();
        //対象の社員データを獲得
        EmployeeView follow = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
        EmployeeView follower = empService.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        if (follower == null || follower.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
            //データが削除されていたり、論理削除されている場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }

        //既にフォローが行われていた場合追加処理を行わない
        if(service.countFollowed(follow.getCode(), follower.getCode()) == 0) {
            //フォロー処理を行い、エラーが発生していたらログを記録
            errors.addAll(service.follow(follow.getCode(), follower.getCode()));
        }else {
            //フォロー済みの場合エラーメッセージを追加
            errors.add(MessageConst.E_FOLLOWED.getMessage());
        }

        if(errors.size() > 0) {
            putRequestScope(AttributeConst.ERR, errors);
        }

        //以下、元の画面に戻る処理
        putRequestScope(AttributeConst.EMPLOYEE, follower);
        forward(ForwardConst.FW_EMP_SHOW);
    }

    /**
     * フォロー解除処理が行われた後、元のユーザー画面に戻る
     * @throws ServletException
     * @throws IOException
     */
    public void unfollow() throws ServletException, IOException{
        List<String> errors = new ArrayList<String>();
        //対象の社員データを獲得
        EmployeeView follow = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
        EmployeeView follower = empService.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        if (follower == null || follower.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
            //データが削除されていたり、論理削除されている場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }

        //既にフォローが行われていた場合追加処理を行わない
        if(service.countFollowed(follow.getCode(), follower.getCode()) != 0) {
            //フォロー解除処理を行い、エラーが発生していたらログを記録
            errors.addAll(service.unfollow(follow.getCode(), follower.getCode()));
        }else {
            //フォロー解除済みの場合エラーメッセージを追加
            errors.add(MessageConst.E_UNFOLLOWED.getMessage());
        }

        if(errors.size() > 0) {
            putRequestScope(AttributeConst.ERR, errors);
        }

        //以下、元の画面に戻る処理
        putRequestScope(AttributeConst.EMPLOYEE, follower);
        forward(ForwardConst.FW_EMP_SHOW);

    }

    /**
     * タイムライン用詳細ページを表示
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException{
        //IDを条件に日報を1件取得
        ReportView rv = repService.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));
        if(rv == null) {
            //該当データがない場合、エラー
            forward(ForwardConst.FW_ERR_UNKNOWN);
        }else {
            putRequestScope(AttributeConst.REPORT, rv);

            forward(ForwardConst.FW_FOL_SHOW);
        }

    }

    /**
     * フォローリストを表示
     * @throws ServletException
     * @throws IOException
     */
    public void showFollow() throws ServletException, IOException{
        EmployeeView follow = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
        int page = getPage();
        List<FollowView> followList = service.getFollowPerPage(follow.getCode(), page);
        List<EmployeeView> list = empService.getByCode(toStringList(followList));
        long count = service.countAllFollow(follow.getCode());

        //取得したデータをリクエストスコープに設定
        putRequestScope(AttributeConst.EMPLOYEES, list);
        putRequestScope(AttributeConst.EMP_COUNT, count);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);

      //フラッシュメッセージがセッションに設定されている場合、リクエストスコープに移し替える
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }
        forward(ForwardConst.FW_FOL_FOLLOW);

    }

    /**フォロワーリストを表示
     * @throws ServletException
     * @throws IOException
     */
    public void showFollower() throws ServletException, IOException{

    }

    /**
     * 社員IDリストをString配列に変更
     * @param fvl
     * @return
     */
    private List<String> toStringList(List<FollowView> fvl){
        List<String> list = new ArrayList<String>();
        for(FollowView fv : fvl) {
            list.add(fv.getFollower());
        }

        return list;

    }

}
