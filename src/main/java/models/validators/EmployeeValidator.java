package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.EmployeeView;
import constants.MessageConst;
import services.EmployeeService;

public class EmployeeValidator {
    /**
     * 従業員インスタンスの各項目についてバリデーションを行う
     * @param service
     * @param ev
     * @param codeDuplicateCheckFlag
     * @param passwordCheckFlag
     * @return エラーリスト
     */
    public static List<String> validate(EmployeeService service, EmployeeView ev,
            Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag){
        List<String> errors = new ArrayList<String>();

        //以下それぞれの要素チェックメソッドに飛ばす
        String codeError = validateCode(service, ev.getCode(), codeDuplicateCheckFlag);
        if(!codeError.equals("")) {
            errors.add(codeError);
        }

        String nameError = validateName(ev.getName());
        if(!nameError.equals("")) {
            errors.add(nameError);
        }

        String passError = validatePassword(ev.getPassword(), passwordCheckFlag);
        if(!passError.equals("")) {
            errors.add(passError);
        }

        return errors;
    }

    /**
     * 社員番号チェック
     * @param service
     * @param code
     * @param codeDuplicateCheckFlag
     * @return
     */
    private static String validateCode(EmployeeService service, String code, Boolean codeDuplicateCheckFlag) {
        //入力値がない場合、即座にエラーで返す
        if(code == null || code.equals("")) {
            return MessageConst.E_NOEMP_CODE.getMessage();
        }

        if(codeDuplicateCheckFlag) {
            long employeeCount = isDuplicateEmployee(service, code);

            if(employeeCount > 0) {
                return MessageConst.E_EMP_CODE_EXIST.getMessage();
            }
        }

        return "";
    }


    /**
     * @param service
     * @param code
     * @return
     */
    private static long isDuplicateEmployee(EmployeeService service, String code) {
        long employeeCount = service.countByCode(code);
        return employeeCount;
    }

    /**
     * 氏名に入力値があるかをチェックし、入力値が無ければエラー
     * @param name
     * @return
     */
    private static String validateName(String name) {
        if(name == null || name.equals("")) {
            return MessageConst.E_NONAME.getMessage();
        }

        return "";
    }

    /**
     * パスワードの入力チェックを行い、エラーを返す
     * @param password
     * @param passwordCheckFlag
     * @return
     */
    private static String validatePassword(String password, Boolean passwordCheckFlag) {
        if(passwordCheckFlag && (password == null || password.equals(""))) {
            return MessageConst.E_NOPASSWORD.getMessage();
        }

        return "";
    }

}
