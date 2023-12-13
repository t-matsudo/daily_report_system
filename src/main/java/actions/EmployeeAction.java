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
    public void index() throws ServletException, IOException{
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
        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_EMP_INDEX);

    }

    /**
     * 新規登録画面の表示
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException{
        //CSRF対策用トークン
        putRequestScope(AttributeConst.TOKEN, getTokenId());
        //空の従業員データ
        putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView());

        forward(ForwardConst.FW_EMP_NEW);
    }

    public void create() throws ServletException, IOException{

        //CSRF対策
        if(checkToken()) {
            EmployeeView ev = new EmployeeView(
                    null,
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue()
                    );
            String pepper = getContextScope(PropertyConst.PEPPER);
            List<String> errors = service.create(ev, pepper);

            if(errors.size() > 0) {
                //登録にエラーがあった場合
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);

                //新規登録画面の再表示
                forward(ForwardConst.FW_EMP_NEW);
            }else {
                //登録にエラーがなかった場合
                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }
}
