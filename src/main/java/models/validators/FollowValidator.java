package models.validators;

import java.util.ArrayList;
import java.util.List;

import constants.MessageConst;

public class FollowValidator {

    public static List<String> codeValidate(String followCode,String followerCode){
        List<String >errors = new ArrayList<String>();
        String followCheack = codeCheack(followCode);
        if(!followCheack.equals("")) {
            errors.add(followCheack);
        }

        String followerCheack = codeCheack(followerCode);
        if(!followerCheack.equals("")) {
            errors.add(followerCheack);
        }

        if(followCode.equals(followerCode)) {
            errors.add(MessageConst.E_SELF_FOLLOW.getMessage());
        }

        return errors;
    }

    private static String codeCheack(String code) {
        if(code == null || code.equals("")) {
            return MessageConst.E_NOCODE.getMessage();
        }
        return "";
    }

}
