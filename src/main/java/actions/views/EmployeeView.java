package actions.views;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * DTOモデルから入出力用の値を保持するViewモデル
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeView {

    //ID
    private Integer id;

    //社員コード
    private String code;

    //社員名
    private String name;

    //パスワード
    private String password;

    //管理者権限フラグ
    private Integer adminFlag;

    //登録日時
    private LocalDateTime createdAt;

    //更新日時
    private LocalDateTime updatedAt;

    //削除フラグ
    private Integer deleteFlag;

}
