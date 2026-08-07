package test;

import util.DBConnection;
import java.sql.Connection;

public class ConnectionTest {

    public static void main(String[] args) {

        try(Connection con = DBConnection.getConnection()) {

            if (con != null && !con.isClosed()) {
                System.out.println(
                        "Database Connected Successfully"
                );
            }

        } catch(Exception e) {

            e.printStackTrace();
        }
    }
}
