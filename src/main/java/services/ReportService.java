package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Report;
import models.validators.ReportValidator;

public class ReportService extends ServiceBase {

    /**
     * 指定した従業員が作成した日報データを指定されたページ数の一覧画面に表示する
     * @param employee
     * @param page
     * @return
     */
    public List<ReportView> getMinePerPage(EmployeeView employee, int page){
        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return ReportConverter.toViewList(reports);
    }

    /**
     * @param employee
     * @return
     */
    public long countAllMine(EmployeeView employee) {
        long count = (long)em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .getSingleResult();
        return count;
    }

    /**
     * 指定されたページ数の一覧画面に表示する日報データをリストで取得する
     * @param page
     * @return
     */
    public List<ReportView> getAllPerPage(int page){
        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL, Report.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1 ))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return ReportConverter.toViewList(reports);
    }

    /**
     * 日報の全件数を取得する
     * @return
     */
    public long countAll() {
        long report_count = (long)em.createNamedQuery(JpaConst.Q_REP_COUNT, Long.class)
                .getSingleResult();
        return report_count;
    }

    /**
     * idを条件に取得したデータをReportViewインスタンスで返す
     * @param id
     * @return
     */
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    /**
     * idを条件にデータを1件取得する
     * @param id
     * @return
     */
    private Report findOneInternal(int id) {
        return em.find(Report.class, id);
    }

    /**
     * 画面から入力された日報の登録内容をもとにデータを作成し、テーブルに登録する
     * @param rv
     * @return
     */
    public List<String> create(ReportView rv){
        List<String> errors = ReportValidator.validate(rv);
        if(errors.size() == 0) {
            LocalDateTime ldt = LocalDateTime.now();
            rv.setCreatedAt(ldt);
            rv.setUpdatedAt(ldt);
            createInternal(rv);
        }

        return errors;
    }

    /**
     * 日報データを1件登録する
     * @param rv
     */
    private void createInternal(ReportView rv) {
        em.getTransaction();
        em.persist(ReportConverter.toModel(rv));
        em.getTransaction().commit();
    }

    /**
     * 画面入力情報をもとに日報データを更新する
     * @param rv
     * @return
     */
    public List<String> update(ReportView rv){
        List<String> errors = ReportValidator.validate(rv);

        if(errors.size() == 0) {
            LocalDateTime ldt = LocalDateTime.now();
            rv.setUpdatedAt(ldt);
            updateInternal(rv);
        }

        return errors;
    }

    /**
     * 日報データを更新する
     * @param rv
     */
    private void updateInternal(ReportView rv) {
        em.getTransaction().begin();
        Report r = findOneInternal(rv.getId());
        ReportConverter.copyViewToModel(r, rv);
        em.getTransaction().commit();
    }

}
