package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;

//従業員に関わる処理を行うActionクラス
public class EmployeeAction extends ActionBase {

    private EmployeeService service;

    @Override
    public void process() throws ServletException, IOException {
        service = new EmployeeService();

        invoke();

        service.close();
    }

    /**
     * 一覧画面の表示を行う
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {
        if(checkAdmin()) {
            //指定されたページ数の一覧画面に表示するデータを取得
            int page = getPage();
            List<EmployeeView> employees = service.getPerPage(page);

            //すべての従業員データの件数を取得
            long employeeCount = service.countAll();

            //取得したデータをリクエストスコープに設定
            putRequestScope(AttributeConst.EMPLOYEES, employees);
            putRequestScope(AttributeConst.EMP_CONT, employeeCount);
            putRequestScope(AttributeConst.PAGE, page);
            putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);

            //フラッシュメッセージがセッションに設定されている場合、リクエストスコープに移し替える
            String flush = getSessionScope(AttributeConst.FLUSH);
            if (flush != null) {
                putRequestScope(AttributeConst.FLUSH, flush);
                removeSessionScope(AttributeConst.FLUSH);
            }
            forward(ForwardConst.FW_EMP_INDEX);

        }
    }

    /**
     * 新規登録画面の表示
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException {
        if(checkAdmin()) {
            //CSRF対策用トークン
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            //空の従業員データ
            putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView());

            forward(ForwardConst.FW_EMP_NEW);
        }
    }

    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        //CSRF対策
        if (checkToken() &&checkAdmin()) {
            EmployeeView ev = new EmployeeView(
                    null,
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
            String pepper = getContextScope(PropertyConst.PEPPER);
            List<String> errors = service.create(ev, pepper);

            if (errors.size() > 0) {
                //登録にエラーがあった場合
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);

                //新規登録画面の再表示
                forward(ForwardConst.FW_EMP_NEW);
            } else {
                //登録にエラーがなかった場合
                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }

    /**
     * 従業員データを取得し、編集画面に表示
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {
        if(checkAdmin()) {
            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
                //データが削除されていたり、論理削除されている場合はエラー画面を表示
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return;
            }

            //取得した従業員情報をリクエストにセット
            putRequestScope(AttributeConst.EMPLOYEE, ev);

            forward(ForwardConst.FW_EMP_SHOW);
        }
    }

    /**
     * 従業員データ編集画面に必要な内容の取得
    * @throws ServletException
    * @throws IOException
    */
    public void edit() throws ServletException, IOException {
        if(checkAdmin()) {
            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return;
            }
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.EMPLOYEE, ev);

            forward(ForwardConst.FW_EMP_EDIT);
        }
    }

    /**
     * 更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException{
        if(checkToken() &&checkAdmin()) {
            EmployeeView ev = new EmployeeView(
                    toNumber(getRequestParam(AttributeConst.EMP_ID)),
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue()
                    );
            String pepper = getContextScope(PropertyConst.PEPPER);
            List<String> errors = service.update(ev, pepper);

            if(errors.size() > 0) {
                //エラーが発生した際に編集画面を再表示させるための処理
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);

                forward(ForwardConst.FW_EMP_EDIT);
            }else {
                //正常に更新された際の処理
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }

    /**
     * 登録されているデータの論理削除を行う
     * @throws ServletException
     * @throws IOException
     */
    public void destroy() throws ServletException, IOException {
        //CSRF対策と管理者確認
        if(checkToken() && checkAdmin()) {

            service.destory(toNumber(getRequestParam(AttributeConst.EMP_ID)));
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());

            redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
        }
    }

    /**
     * 管理者かどうかを判断する
     * @return
     * @throws ServletException
     * @throws IOException
     */
    private boolean checkAdmin() throws ServletException, IOException{

        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        if(ev.getAdminFlag() != AttributeConst.ROLE_ADMIN.getIntegerValue()) {
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;
        }else {
            return true;
        }
    }
}
