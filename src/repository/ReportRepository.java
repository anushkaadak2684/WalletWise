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
        ensureSchema();
    }

    private void ensureSchema() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            try {
                st.executeUpdate("ALTER TABLE reports ADD COLUMN wallet_id INT");
            } catch (Exception ignored) {}
            try {
                st.executeUpdate("ALTER TABLE reports ADD COLUMN summary_details LONGTEXT");
            } catch (Exception ignored) {}
            try {
                st.executeUpdate("ALTER TABLE reports MODIFY COLUMN summary_details LONGTEXT");
            } catch (Exception ignored) {}
        } catch (Exception ex) {
            System.err.println("Database schema migration error in ReportRepository: " + ex.getMessage());
        }
    }

    public void save(Report report) throws SQLException {

        String sql =
            "INSERT INTO reports(wallet_id, report_type, generated_date, total_income, total_expense, total_savings, summary_details) VALUES(?,?,?,?,?,?,?)";

        Connection con = DBConnection.getConnection();

        PreparedStatement ps =
            con.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

        ps.setInt(1, report.getWalletId());
        ps.setString(2, report.getReportType().name());
        ps.setDate(3, Date.valueOf(report.getGeneratedDate()));
        ps.setBigDecimal(4, report.getTotalIncome());
        ps.setBigDecimal(5, report.getTotalExpense());
        ps.setBigDecimal(6, report.getTotalSavings());
        ps.setString(7, report.getSummaryDetails());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if(rs.next()) {
            report.setReportId(rs.getInt(1));
        }

        rs.close();
        ps.close();
        con.close();
    }

    
    public Report findById(int reportId) throws SQLException {

        String sql = "SELECT * FROM reports WHERE report_id=?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, reportId);

        ResultSet rs = ps.executeQuery();
        Report report = null;

        if(rs.next()) {
            report = mapReport(rs);
        }

        rs.close();
        ps.close();
        con.close();

        return report;
    }


    public List<Report> findByWalletId(int walletId) throws SQLException {

        List<Report> reports = new ArrayList<>();

        String sql = "SELECT * FROM reports WHERE wallet_id=? ORDER BY generated_date DESC, report_id DESC";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, walletId);

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            reports.add(mapReport(rs));
        }

        rs.close();
        ps.close();
        con.close();

        return reports;
    }


    public List<Report> findAll() throws SQLException {

        List<Report> reports = new ArrayList<>();

        String sql = "SELECT * FROM reports ORDER BY generated_date DESC, report_id DESC";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            reports.add(mapReport(rs));
        }

        rs.close();
        ps.close();
        con.close();

        return reports;
    }


    public void update(Report report) throws SQLException {

        String sql =
                "UPDATE reports SET wallet_id=?, report_type=?, generated_date=?, total_income=?, total_expense=?, total_savings=?, summary_details=? WHERE report_id=?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, report.getWalletId());
        ps.setString(2, report.getReportType().name());
        ps.setDate(3, Date.valueOf(report.getGeneratedDate()));
        ps.setBigDecimal(4, report.getTotalIncome());
        ps.setBigDecimal(5, report.getTotalExpense());
        ps.setBigDecimal(6, report.getTotalSavings());
        ps.setString(7, report.getSummaryDetails());
        ps.setInt(8, report.getReportId());

        ps.executeUpdate();

        ps.close();
        con.close();
    }


    public void delete(int reportId) throws SQLException {

        String sql = "DELETE FROM reports WHERE report_id=?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, reportId);

        ps.executeUpdate();

        ps.close();
        con.close();
    }


    private Report mapReport(ResultSet rs) throws SQLException {

        Report report = new Report();

        report.setReportId(
                rs.getInt("report_id")
        );

        try {
            report.setWalletId(rs.getInt("wallet_id"));
        } catch (Exception ignored) {}

        try {
            report.setSummaryDetails(rs.getString("summary_details"));
        } catch (Exception ignored) {}

        report.setReportType(
                ReportType.valueOf(
                        rs.getString("report_type")
                )
        );

        report.setGeneratedDate(
                rs.getDate("generated_date").toLocalDate()
        );

        report.setTotalIncome(
                rs.getBigDecimal("total_income")
        );

        report.setTotalExpense(
                rs.getBigDecimal("total_expense")
        );

        report.setTotalSavings(
                rs.getBigDecimal("total_savings")
        );

        return report;
    }
}