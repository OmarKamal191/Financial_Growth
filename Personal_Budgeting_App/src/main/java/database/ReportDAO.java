package database;

import model.Report;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<Report> getExpensesByCategory(int userId) {
        List<Report> list = new ArrayList<>();

        String sql = """
            SELECT c.Name AS label, SUM(t.Amount) AS total
            FROM Transactions t
            JOIN Categories c ON t.CategoryId = c.CategoryId
            WHERE t.UserId = ? AND LOWER(t.Type) = 'expense'
            GROUP BY c.Name
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Report(
                        rs.getString("label"),
                        rs.getDouble("total")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Report> getMonthlyData(int userId, String type) {

        List<Report> list = new ArrayList<>();

        String sql = """
            SELECT 
                FORMAT(t.Date, 'MMM') AS label,
                MONTH(t.Date) AS monthNumber,
                SUM(t.Amount) AS total
            FROM dbo.Transactions t
            WHERE t.UserId = ?
            AND LOWER(t.Type) = ?
            GROUP BY FORMAT(t.Date, 'MMM'), MONTH(t.Date)
            ORDER BY monthNumber
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, type.toLowerCase());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Report(
                        rs.getString("label"),
                        rs.getDouble("total")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}