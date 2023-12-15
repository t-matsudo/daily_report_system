package actions.views;

import java.util.ArrayList;
import java.util.List;

import models.Report;

public class ReportConverter {

    /**
     * DTOモデルインスタンスの生成
     * @param rv
     * @return
     */
    public static Report toModel(ReportView rv) {
        return new Report(
                rv.getId(),
                EmployeeConverter.toModel(rv.getEmployee()),
                rv.getReportDate(),
                rv.getTitle(),
                rv.getContent(),
                rv.getCreatedAt(),
                rv.getUpdatedAt());
    }

    /**
     * Viewモデルインスタンスの生成
     * @param r
     * @return
     */
    public static ReportView toView(Report r) {
        if(r == null) {
            return null;
        }
        return new ReportView(
                r.getId(),
                EmployeeConverter.toView(r.getEmployee()),
                r.getReportDate(),
                r.getTitle(),
                r.getContent(),
                r.getCreatedAt(),
                r.getUpdatedAt()
                );
    }

    /**
     * DTOモデルリストからViewモデルリストを作成
     * @param list
     * @return
     */
    public static List<ReportView> toViewList(List<Report> list){
        List<ReportView> rvs = new ArrayList<>();

        for(Report r:list) {
            rvs.add(toView(r));
        }

        return rvs;
    }

    /**
     * Viewモデルの中をDTOモデルにコピー
     * @param r
     * @param rv
     */
    public static void copyViewToModel(Report r, ReportView rv) {
        r.setId(rv.getId());
        r.setEmployee(EmployeeConverter.toModel(rv.getEmployee()));
        r.setReportDate(rv.getReportDate());
        r.setTitle(rv.getTitle());
        r.setContent(rv.getContent());
        r.setCreatedAt(rv.getCreatedAt());
        r.setUpdatedAt(rv.getUpdatedAt());
    }
}
