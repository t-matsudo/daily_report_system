package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
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

    public void index() throws ServletException, IOException{
        //指定されたページ数の一覧画面に表示するデータを取得
        int page = getPage();
        List<EmployeeView> employees = service.getPerPage(page);

        //すべての従業員データを取得
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

}
