package repository;

import repository.interfaces.IReportRepository;
import model.Report;
import model.enums.ReportType;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportRepository implements IReportRepository {

    public ReportRepository() {
    }

    @Override
    public void save(Report report) {
        String sql = "INSERT INTO reports(wallet_id, report_type, generated_date, total_income, total_expense, total_savings, summary_details) VALUES(?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, report.getWalletId());
            ps.setString(2, report.getReportType().name());
            ps.setDate(3, Date.valueOf(report.getGeneratedDate()));
            ps.setBigDecimal(4, report.getTotalIncome());
            ps.setBigDecimal(5, report.getTotalExpense());
            ps.setBigDecimal(6, report.getTotalSavings());
            ps.setString(7, report.getSummaryDetails());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    report.setReportId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving report: " + e.getMessage(), e);
        }
    }

    @Override
    public Report findById(int reportId) {
        String sql = "SELECT * FROM reports WHERE report_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReport(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding report by id: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Report> findByWalletId(int walletId) {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE wallet_id=? ORDER BY generated_date DESC, report_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, walletId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reports by wallet id: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public List<Report> findAll() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY generated_date DESC, report_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reports.add(mapReport(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all reports: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public void update(Report report) {
        String sql = "UPDATE reports SET wallet_id=?, report_type=?, generated_date=?, total_income=?, total_expense=?, total_savings=?, summary_details=? WHERE report_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, report.getWalletId());
            ps.setString(2, report.getReportType().name());
            ps.setDate(3, Date.valueOf(report.getGeneratedDate()));
            ps.setBigDecimal(4, report.getTotalIncome());
            ps.setBigDecimal(5, report.getTotalExpense());
            ps.setBigDecimal(6, report.getTotalSavings());
            ps.setString(7, report.getSummaryDetails());
            ps.setInt(8, report.getReportId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating report: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int reportId) {
        String sql = "DELETE FROM reports WHERE report_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reportId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting report: " + e.getMessage(), e);
        }
    }

    private Report mapReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("report_id"));

        try {
            report.setWalletId(rs.getInt("wallet_id"));
        } catch (Exception ignored) {}

        try {
            report.setSummaryDetails(rs.getString("summary_details"));
        } catch (Exception ignored) {}

        report.setReportType(ReportType.valueOf(rs.getString("report_type")));
        report.setGeneratedDate(rs.getDate("generated_date").toLocalDate());
        report.setTotalIncome(rs.getBigDecimal("total_income"));
        report.setTotalExpense(rs.getBigDecimal("total_expense"));
        report.setTotalSavings(rs.getBigDecimal("total_savings"));

        return report;
    }
}