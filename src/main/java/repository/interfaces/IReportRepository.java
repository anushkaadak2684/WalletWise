package repository.interfaces;

import model.Report;
import java.util.List;

public interface IReportRepository {
    void save(Report report);
    Report findById(int reportId);
    List<Report> findByWalletId(int walletId);
    List<Report> findAll();
    void update(Report report);
    void delete(int reportId);
}
