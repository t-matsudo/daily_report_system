package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import constants.JpaConst;
import models.Employee;
import models.validators.EmployeeValidator;
import utils.EncryptUtil;

public class EmployeeService extends ServiceBase {

    /**
     * 指定されたページ数の一覧画面に表示するデータリストをViewモデルで返す
     * @param page
     * @return
     */
    public List<EmployeeView> getPerPage(int page) {
        List<Employee> employees = em.createNamedQuery(JpaConst.Q_EMP_GET_ALL, Employee.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return EmployeeConverter.toViewList(employees);
    }

    /**
     * 従業員テーブルのデータ件数を取得し、返す
     * @return
     */
    public long countAll() {
        long empCount = (long)em.createNamedQuery(JpaConst.Q_EMP_COUNT,Long.class)
                .getSingleResult();
        return empCount;
    }

    /**
     * 社員番号、パスワードを条件に取得したデータをEmployeeViewのインスタンスで返す
     * @param code
     * @param plainPass
     * @param pepper
     * @return
     */
    public EmployeeView findOne(String code, String plainPass, String pepper) {
        Employee emp = null;
        try {
            String pass = EncryptUtil.getPasswordEncrypt(plainPass, pepper);

            emp = em.createNamedQuery(JpaConst.Q_EMP_GET_BY_CODE_AND_PASS, Employee.class)
                    .setParameter(JpaConst.JPQL_PARM_CODE, code)
                    .setParameter(JpaConst.JPQL_PARM_PASSWORD, pass)
                    .getSingleResult();
        }catch(NoResultException e) {
        }

        return EmployeeConverter.toView(emp);

    }

    /**
     * idを条件に取得したデータをEmployeeインスタンスで返却
     * @param id
     * @return
     */
    public EmployeeView findOne(int id) {
        Employee e = findOneInternal(id);
        return EmployeeConverter.toView(e);
    }

    /**
     * 社員番号を条件に該当するデータ件数を取得し返却する
     * @param code
     * @return
     */
    public long countByCode(String code) {
        //指定した社員番号を保持する従業員の件数を取得する
        long employees_count = (long) em.createNamedQuery(JpaConst.Q_EMP_COUNT_REGISTERED_BY_CODE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_CODE, code)
                .getSingleResult();
        return employees_count;
    }

    /**画面から入力された内容をもとに、従業員テーブルにデータを1件作成
     * @param ev
     * @param pepper
     * @return
     */
    public List<String> create(EmployeeView ev, String pepper){
        //パスワードをハッシュ化して設定
        String pass = EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper);
        ev.setPassword(pass);

        LocalDateTime now = LocalDateTime.now();
        ev.setCreatedAt(now);
        ev.setUpdatedAt(now);

        List<String> errors = EmployeeValidator.validate(this, ev, true, true);

        if(errors.size() == 0) {
            create(ev);
        }

        return errors;
    }

    /**
     * 入力情報をもとに従業員のデータを1件作成し、従業員テーブルを更新する
     * @param ev
     * @param pepper
     * @return
     */
    public List<String> update(EmployeeView ev, String pepper){
        //IDを条件に登録済みの従業員情報を取得する
        EmployeeView savedEmp = findOne(ev.getId());

        boolean validateCode = false;
        if(!savedEmp.getCode().equals(ev.getCode())) {
            //社員番号を更新時のみ、パスワードにバリデーションフラグを立てる
            validateCode = true;
            savedEmp.setCode(ev.getCode());

        }

        boolean validatePass = false;
        if(ev.getPassword() != null && !ev.getPassword().equals("")) {
            //パスワード更新時のみ、パスワードにバリデーションフラグを立てる
            validatePass = true;
            savedEmp.setPassword(
                    EncryptUtil.getPasswordEncrypt(ev.getPassword(), pepper));
        }

        //その他要素の格納
        savedEmp.setName(ev.getName());
        savedEmp.setAdminFlag(ev.getAdminFlag());
        savedEmp.setUpdatedAt(LocalDateTime.now());

        List<String> errors = EmployeeValidator.validate(this, savedEmp, validateCode, validatePass);
        if(errors.size() == 0) {
            update(savedEmp);
        }

        return errors;
    }

    /**
     * IDをもとに該当するデータの論理削除を行う
     * @param id
     */
    public void destory(Integer id) {
        EmployeeView savedEmp = findOne(id);

        savedEmp.setUpdatedAt(LocalDateTime.now());
        savedEmp.setDeleteFlag(JpaConst.EMP_DEL_TRUE);

        update(savedEmp);
    }

    /**
     * 社員番号とパスワードをもとに条件を検索し、データが取得できるかどうかを確認する
     * @param code
     * @param plainPass
     * @param pepper
     * @return 結果を返す(true:成功、false:失敗)
     */
    public Boolean validateLogin(String code, String plainPass, String pepper) {
        boolean isValidEmployee = false;
        if(code != null&& !code.equals("") && plainPass != null && !plainPass.equals("")) {
            EmployeeView ev = findOne(code, plainPass, pepper);

            if(ev != null && ev.getId() != null) {
                isValidEmployee = true;
            }
        }

        return isValidEmployee;
    }

    /**
     * IDを条件にデータを1件取得し、Employeeインスタンスで返却
     * @param id
     * @return
     */
    private Employee findOneInternal(int id) {
        Employee e = em.find(Employee.class, id);

        return e;
    }

    /**
     * 従業員データを1件DBに登録する
     * @param ev
     */
    private void create(EmployeeView ev) {
        em.getTransaction().begin();
        em.persist(EmployeeConverter.toModel(ev));
        em.getTransaction().commit();
    }

    /**
     * 従業員データを1件更新する
     * @param ev
     */
    private void update(EmployeeView ev) {
        em.getTransaction().begin();
        Employee e = findOneInternal(ev.getId());
        EmployeeConverter.copyViewToModel(e, ev);
        em.getTransaction().commit();
    }
}
