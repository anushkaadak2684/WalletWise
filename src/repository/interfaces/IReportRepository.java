package repository.interfaces;

import model.Report;
import java.util.List;

public interface IReportRepository {
    void save(Report report) throws Exception;
    Report findById(int reportId) throws Exception;
    List<Report> findByWalletId(int walletId) throws Exception;
    List<Report> findAll() throws Exception;
    void update(Report report) throws Exception;
    void delete(int reportId) throws Exception;
}
