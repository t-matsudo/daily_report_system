package actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.PropertyConst;

/**
 * @author matsudot
 *
 */
public abstract class ActionBase {
    protected ServletContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    /**
     * 初期化処理
     * @param servletContext
     * @param servletRequest
     * @param servletResponse
     */
    public void init(ServletContext servletContext,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        this.context = servletContext;
        this.request = servletRequest;
        this.response = servletResponse;
    }

    /**
     * フロントコントローラからの呼び出し処理
     * @throws ServletException
     * @throws IOException
     */
    public abstract void process() throws ServletException, IOException;

    /**
     * パラメータのcommandに該当するメソッドの実行
     * @throws ServletException
     * @throws IOException
     */
    protected void invoke() throws ServletException, IOException{
        Method commandMethod;
        try {
            //パラメータからcommandを取得
            String command = request.getParameter(ForwardConst.CMD.getValue());

            //commandに該当するメソッドを実行する
            commandMethod = this.getClass().getDeclaredMethod(command, new Class[0]);
            commandMethod.invoke(this, new Object[0]);

        }catch(NoSuchMethodException | SecurityException | IllegalAccessException|
                InvocationTargetException | NullPointerException e) {
            //例外が発生した場合にコンソールに表示し、エラー画面を呼び出す
            e.printStackTrace();
            forward(ForwardConst.FW_ERR_UNKNOWN);
        }

    }

    /**
     * 指定されたJSPの呼び出しを行う
     * @param target JSPの指定
     * @throws ServletException
     * @throws IOException
     */
    protected void forward(ForwardConst target) throws ServletException,IOException{
        //JSPファイルの相対パスを作成
        String forward = String.format("/WEB-INF/views/%s.jsp", target.getValue());
        RequestDispatcher dispatcher = request.getRequestDispatcher(forward);

        dispatcher.forward(request, response);
    }

    /**
     * URLの構築とリダイレクト
     * @param action
     * @param command
     * @throws ServletException
     * @throws IOException
     */
    protected void redirect(ForwardConst action, ForwardConst command)
            throws ServletException, IOException{

        //URLの構築
        String redirectUrl = request.getContextPath() + "/?action" + action.getValue();
        if(command != null) {
            redirectUrl = redirectUrl + "&command=" + command.getValue();
        }
        //URLへリダイレクト
        response.sendRedirect(redirectUrl);
    }

    /**
     * CSRF対策：セッションIDが不正の場合はエラー画面を表示
     * @return
     * @throws ServletException
     * @throws IOException
     */
    protected boolean checkToken() throws ServletException, IOException{
        String _token = getRequestParam(AttributeConst.TOKEN);

        if(_token != null || !(_token.equals(getTokenId()))) {
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;
        }else {
            return true;
        }
    }

    /**
     * セッションIDの取得
     * @return セッションID
     */
    protected String getTokenId() {
        return request.getSession().getId();
    }

    /**
     * リクエスパラメータから引数で指定したパラメータの値を取得
     * @param key
     * @return
     */
    protected String getRequestParam(AttributeConst key) {
        return request.getParameter(key.getValue());
    }

    /**
     * リクエストから表示を要求されているページ数を取得し、返却する
     * @return
     */
    protected int getPage() {
        int page;
        page = toNumber(request.getParameter(AttributeConst.PAGE.getValue()));
        if(page == Integer.MIN_VALUE) {
            page = 1;
        }
        return page;
    }

    /**
     * 文字列を数値に変換する
     * @param strNumber
     * @return
     */
    protected int toNumber(String strNumber) {
        int number = 0;
        try {
            number = Integer.parseInt(strNumber);
        }catch(Exception e) {
            number = Integer.MIN_VALUE;
        }
        return number;
    }

    /**
     * 文字列をLocalDate型に変換する
     * @param strDate
     * @return
     */
    protected LocalDate toLocalDate(String strDate) {
        if(strDate == null || strDate.equals("")) {
            return LocalDate.now();
        }
        return LocalDate.parse(strDate);
    }

    /**
     * リクエストスコープにパラメータを設定する
     * @param <V>
     * @param key
     * @param value
     */
    protected <V> void putRequestScope(AttributeConst key, V value) {
        request.setAttribute(key.getValue(), value);
    }

    /**
     * セッションスコープから指定されたパラメータの値を取得し、返却する
     * @param <R>
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <R> R getSessionScope(AttributeConst key) {
        return (R) request.getSession().getAttribute(key.getValue());
    }

    /**
     * セッションスコープにパラメータを設定する
     * @param <V>
     * @param key
     * @param value
     */
    protected <V> void putSessionScope(AttributeConst key, V value) {
        request.getSession().setAttribute(key.getValue(), value);
    }

    /**
     * セッションスコープから指定された名前のパラメータを除去する
     * @param key
     */
    protected void removeSessionScope(AttributeConst key) {
        request.getSession().removeAttribute(key.getValue());
    }

    @SuppressWarnings("unchecked")
    protected <R> R getContextScope(PropertyConst key) {
        return (R)context.getAttribute(key.getValue());
    }

}
