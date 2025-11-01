package com.example.qlquancoffe.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp quản lý kết nối đến MySQL Database
 * Sử dụng HikariCP Connection Pool để tối ưu hiệu suất
 */
public class DatabaseConnection {

    // ===================== CẤU HÌNH DATABASE =====================
    private static final String DB_HOST = "@maglev.proxy.rlwy.net";
    private static final String DB_PORT = "25382";
    private static final String DB_NAME = "railway";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "aUKfugtuQBefRjogUvVEyRAARDfbqqts";

    private static final String DB_URL = String.format(
            "jdbc:mysql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME
    );

    // ===================== HIKARICP DATASOURCE =====================
    private static HikariDataSource dataSource;

    // Khối static - chạy 1 lần khi class được load
    static {
        try {
            setupDataSource();
            System.out.println("✅ HikariCP Connection Pool đã được khởi tạo");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo Connection Pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cấu hình HikariCP DataSource
     */
    private static void setupDataSource() {
        HikariConfig config = new HikariConfig();

        // ===== CẤU HÌNH CƠ BẢN =====
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // ===== CẤU HÌNH CONNECTION POOL =====
        config.setMaximumPoolSize(10);           // Tối đa 10 connections
        config.setMinimumIdle(2);                // Tối thiểu 2 connections sẵn sàng
        config.setConnectionTimeout(30000);      // Timeout 30s khi lấy connection
        config.setIdleTimeout(600000);           // Connection idle 10 phút thì đóng
        config.setMaxLifetime(1800000);          // Connection tồn tại tối đa 30 phút

        // ===== CẤU HÌNH MYSQL =====
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // ===== CẤU HÌNH CHARSET & TIMEZONE =====
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("serverTimezone", "Asia/Ho_Chi_Minh");
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("allowPublicKeyRetrieval", "true");

        // ===== TÊN POOL (để dễ debug) =====
        config.setPoolName("QLQuanCoffee-Pool");

        // ===== HEALTH CHECK =====
        config.setConnectionTestQuery("SELECT 1");

        // Tạo DataSource
        dataSource = new HikariDataSource(config);
    }

    /**
     * Lấy connection từ pool
     * @return Connection object
     * @throws SQLException nếu không lấy được connection
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource chưa được khởi tạo!");
        }

        try {
            Connection conn = dataSource.getConnection();
            System.out.println("✅ Đã lấy connection từ pool (Active: " +
                    dataSource.getHikariPoolMXBean().getActiveConnections() + "/" +
                    dataSource.getHikariPoolMXBean().getTotalConnections() + ")");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy connection từ pool: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Đóng connection pool (gọi khi tắt ứng dụng)
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔒 Đã đóng HikariCP Connection Pool");
        }
    }

    /**
     * Kiểm tra connection pool có hoạt động không
     * @return true nếu pool OK
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Test connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * In thông tin về Connection Pool
     */
    public static void printPoolStats() {
        if (dataSource != null) {
            System.out.println("\n📊 THỐNG KÊ CONNECTION POOL:");
            System.out.println("   Active Connections: " +
                    dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("   Idle Connections: " +
                    dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("   Total Connections: " +
                    dataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println("   Threads Waiting: " +
                    dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }
    }

    /**
     * In thông tin database
     */
    public static void printDatabaseInfo() {
        try (Connection conn = getConnection()) {
            System.out.println("\n📊 THÔNG TIN DATABASE:");
            System.out.println("   Database: " + conn.getCatalog());
            System.out.println("   URL: " + conn.getMetaData().getURL());
            System.out.println("   User: " + conn.getMetaData().getUserName());
            System.out.println("   Driver: " + conn.getMetaData().getDriverName());
            System.out.println("   Driver Version: " + conn.getMetaData().getDriverVersion());

            // Lấy danh sách bảng
            System.out.println("\n📋 DANH SÁCH BẢNG:");
            ResultSet rs = conn.getMetaData().getTables(
                    null, null, "%", new String[]{"TABLE"}
            );
            while (rs.next()) {
                System.out.println("   - " + rs.getString("TABLE_NAME"));
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy thông tin database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy số lượng connections đang active
     */
    public static int getActiveConnections() {
        return dataSource != null ?
                dataSource.getHikariPoolMXBean().getActiveConnections() : 0;
    }

    /**
     * Lấy số lượng connections đang idle
     */
    public static int getIdleConnections() {
        return dataSource != null ?
                dataSource.getHikariPoolMXBean().getIdleConnections() : 0;
    }

    /**
     * Kiểm tra pool có đang hoạt động không
     */
    public static boolean isPoolRunning() {
        return dataSource != null && !dataSource.isClosed();
    }
}