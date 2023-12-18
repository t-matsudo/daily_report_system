package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.ReportService;

public class ReportAction extends ActionBase {

    private ReportService service;

    @Override
    public void process() throws ServletException, IOException {
        service = new ReportService();

        invoke();

        service.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException{
        //指定されたページ数の一覧画面に表示する日報データの取得
        int page = getPage();
        List<ReportView> reports = service.getAllPerPage(page);

        long reportsCount = service.countAll();

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

        forward(ForwardConst.FW_REP_INDEX);
    }

    /**
     * 新規登録画面の表示
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException{
        putRequestScope(AttributeConst.TOKEN, getTokenId());

        ReportView rv = new ReportView();
        rv.setReportDate(LocalDate.now());
        putRequestScope(AttributeConst.REPORT, rv);
        forward(ForwardConst.FW_REP_NEW);
    }

    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException{
        if(checkToken()) {

            //日付データの作成
            LocalDate day = null;
            if(getRequestParam(AttributeConst.REP_DATE) == null
                    || getRequestParam(AttributeConst.REP_DATE).equals("")) {
                day = LocalDate.now();
            }else {
                day = LocalDate.parse(getRequestParam(AttributeConst.REP_DATE));
            }

            //セッションからログイン中の従業員情報を取得
            EmployeeView ev = (EmployeeView)getSessionScope(AttributeConst.LOGIN_EMP);

            //日報情報インスタンス作成
            ReportView rv = new ReportView(
                    null,
                    ev,
                    day,
                    getRequestParam(AttributeConst.REP_TITLE),
                    getRequestParam(AttributeConst.REP_CONTENT),
                    null,
                    null);

            //情報の登録
            List<String> errors = service.create(rv);
            if(errors.size() > 0) {
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.REPORT, rv);
                putRequestScope(AttributeConst.ERR, errors);

                forward(ForwardConst.FW_REP_NEW);
            }else {
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }

        }
    }

    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException{
        //IDを条件に日報を1件取得
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));
        if(rv == null) {
            //該当データがない場合、エラー
            forward(ForwardConst.FW_ERR_UNKNOWN);
        }else {
            putRequestScope(AttributeConst.REPORT, rv);

            forward(ForwardConst.FW_REP_SHOW);
        }
    }

    /**
     * 編集画面を表示
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException{
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        if(rv == null || ev.getId() != rv.getEmployee().getId()) {
            //日報が存在しない、もしくはログインしているIDと作成者のIDが異なる場合にエラー
            forward(ForwardConst.FW_ERR_UNKNOWN);
        }else {
            //必要な情報をリクエストに付与して編集画面を表示
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.REPORT, rv);

            forward(ForwardConst.FW_REP_EDIT);
        }
    }

    /**
     * 取得したデータで更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException{
        if(checkToken()) {
            ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

            rv.setReportDate(toLocalDate(getRequestParam(AttributeConst.REP_DATE)));
            rv.setTitle(getRequestParam(AttributeConst.REP_TITLE));
            rv.setContent(getRequestParam(AttributeConst.REP_CONTENT));

            List<String> errors = service.update(rv);

            if(errors.size() >0) {
                //エラーがあった場合、必要な情報をリクエストに乗せて編集画面を再表示
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.REPORT, rv);
                putRequestScope(AttributeConst.ERR, errors);

                forward(ForwardConst.FW_REP_EDIT);
            }else {
                //エラーが無かった場合、一覧画面にリダイレクト
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());
                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }
        }

    }
}
