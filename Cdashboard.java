import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author anthony
 */

public class Cdashboard extends javax.swing.JFrame {
    
    private String username;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private int userId;
    private String currentUsername;
    private String currentEmail;

    public Cdashboard(int userId, String username, String email, String password) {
        
        this.username = username;
        this.userId = userId;
        this.currentUsername = currentUsername;
        this.currentEmail = currentEmail;
        
        initComponents();
 
        setTitle("StudyWise Dashboard");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(PANEL_2, BorderLayout.NORTH); 
        getContentPane().add(PANEL_1, BorderLayout.CENTER); 

        PANEL_1.setLayout(new BorderLayout()); 

        setupAlarmPanel();
        startAlarmChecker();
        setupGraphPanel();
            }

        private Cdashboard() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    private void setupAlarmPanel() {
        JPanel alarmPanel = new JPanel();
        alarmPanel.setBackground(Color.WHITE);
        alarmPanel.setLayout(new BoxLayout(alarmPanel, BoxLayout.Y_AXIS));
        alarmPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fromLabel = new JLabel("From Time:");
        fromLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSpinner time1Spinner = new JSpinner(new SpinnerDateModel());
        time1Spinner.setEditor(new JSpinner.DateEditor(time1Spinner, "hh:mm a"));
        time1Spinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel toLabel = new JLabel("To Time:");
        toLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSpinner time2Spinner = new JSpinner(new SpinnerDateModel());
        time2Spinner.setEditor(new JSpinner.DateEditor(time2Spinner, "hh:mm a"));
        time2Spinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelLabel = new JLabel("Label:");
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField labelField = new JTextField(10);
        labelField.setMaximumSize(new Dimension(200, 25));
        labelField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addButton = new JButton("Track");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JList<String> alarmList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(alarmList);
        scrollPane.setMaximumSize(new Dimension(250, 100));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        addButton.addActionListener(e -> {
              String label = labelField.getText().trim();

    if (label.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Please enter a label for the alarm.",
                "Missing Label",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
    String time1Str = ((JSpinner.DateEditor) time1Spinner.getEditor()).getFormat().format(time1Spinner.getValue());
    String time2Str = ((JSpinner.DateEditor) time2Spinner.getEditor()).getFormat().format(time2Spinner.getValue());
    LocalTime time1 = LocalTime.parse(time1Str, formatter);
    LocalTime time2 = LocalTime.parse(time2Str, formatter);

    AlarmDatabase.insertAlarm(userId, time1, time2, label);

    DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("hh:mm a");
    listModel.addElement(time1.format(displayFormat) + " - " + time2.format(displayFormat) + " | " + label);

    labelField.setText("");
    refreshGraphPanel();

    JOptionPane.showMessageDialog(this,
            "Added: " + label + " from " + time1 + " to " + time2,
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    });

        alarmPanel.add(fromLabel);
        alarmPanel.add(time1Spinner);
        alarmPanel.add(toLabel);
        alarmPanel.add(time2Spinner);
        alarmPanel.add(labelLabel);
        alarmPanel.add(labelField);
        alarmPanel.add(addButton);
        alarmPanel.add(scrollPane);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.setLayout(new BorderLayout());
        wrapperPanel.add(alarmPanel, BorderLayout.CENTER);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setBackground(Color.WHITE);
        spacerPanel.setPreferredSize(new Dimension(50, 10));

       PANEL_1.setPreferredSize(new Dimension(600, 380));
       PANEL_1.setMinimumSize(new Dimension(600, 380));
       PANEL_1.setMaximumSize(new Dimension(600, 380));

       JScrollPane scrollPaneWrapper = new JScrollPane(wrapperPanel);
       scrollPaneWrapper.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
       scrollPaneWrapper.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       scrollPaneWrapper.setPreferredSize(new Dimension(681, 420));

       PANEL_1.setLayout(new BorderLayout());
       PANEL_1.add(scrollPaneWrapper, BorderLayout.CENTER);

       this.setSize(681, 420);
       this.setResizable(false);
       this.setPreferredSize(new Dimension(681, 420));
       this.pack();
       this.setLocationRelativeTo(null);
        }

        private void startAlarmChecker() {
    Timer timer = new Timer(1000, e -> {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        List<Alarm> alarms = AlarmDatabase.getPendingAlarms(userId);

        for (Alarm alarm : alarms) {
            if (!alarm.triggered && now.equals(alarm.endTime)) {
                JOptionPane.showMessageDialog(null,
                        "Your scheduled session for \"" + alarm.label + "\" has now ended.",
                        "StudyWise Reminder", JOptionPane.INFORMATION_MESSAGE);
                AlarmDatabase.markAlarmTriggered(alarm.id);
                break; 
            }
        }
    });
    timer.setInitialDelay(0);
    timer.start();
}

    private void setupGraphPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try (Connection conn = DriverManager.getConnection(AlarmDatabase.DB_URL, AlarmDatabase.DB_USER, AlarmDatabase.DB_PASS)) {
            String sql = "SELECT COUNT(*) AS count, DATE(start_time) AS day FROM alarm WHERE user_id = ? AND start_time >= CURDATE() - INTERVAL 7 DAY GROUP BY day";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

           int todayCount = 0, weekTotal = 0, monthTotal = 0;
           java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
           java.sql.Date startOfMonth = java.sql.Date.valueOf(java.time.LocalDate.now().withDayOfMonth(1));

           while (rs.next()) {
               int count = rs.getInt("count");
               java.sql.Date day = rs.getDate("day");

               if (day.equals(today)) {
                   todayCount = count;
               }
               if (!day.before(startOfMonth)) {
                    monthTotal += count;
               }
               weekTotal += count;
           }

dataset.setValue("Today", todayCount);
dataset.setValue("This Week", weekTotal - todayCount);
dataset.setValue("This Month", monthTotal);

        } catch (SQLException e) {
            e.printStackTrace();
            dataset.setValue("Today", 0);
            dataset.setValue("This Week", 0);
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            "User Activity",
            dataset,
            true,
            true,
            false
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("Today", new Color(255, 0, 0));
        plot.setSectionPaint("This Week", new Color(0, 255, 0));
        plot.setSectionPaint("This Month", new Color(0, 0, 255));

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setOpaque(false);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 300));

        JPanel graphPanel = new JPanel();
        graphPanel.setOpaque(false);
        graphPanel.setLayout(new BorderLayout());
        graphPanel.add(chartPanel, BorderLayout.CENTER);

        PANEL_1.add(graphPanel, BorderLayout.EAST);
    }

    private void refreshGraphPanel() {
    DefaultPieDataset dataset = new DefaultPieDataset();

    try (Connection conn = DriverManager.getConnection(AlarmDatabase.DB_URL, AlarmDatabase.DB_USER, AlarmDatabase.DB_PASS)) {
        String sql = "SELECT COUNT(*) AS count, DATE(start_time) AS day FROM alarm " +
             "WHERE user_id = ? AND MONTH(start_time) = MONTH(CURDATE()) " +
             "AND YEAR(start_time) = YEAR(CURDATE()) " +
             "GROUP BY day";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        int todayCount = 0, weekTotal = 0, monthTotal = 0;
        java.sql.Date today = java.sql.Date.valueOf(java.time.LocalDate.now());
        java.sql.Date startOfMonth = java.sql.Date.valueOf(java.time.LocalDate.now().withDayOfMonth(1)); 

        while (rs.next()) {
            int count = rs.getInt("count");
            java.sql.Date day = rs.getDate("day");
            if (day.equals(today)) {
                todayCount = count; 
            }
            if (!day.before(startOfMonth)) {
                monthTotal += count; 
            }
            weekTotal += count; 
        }

        dataset.setValue("Today", todayCount);
        dataset.setValue("This Week", weekTotal - todayCount);
        dataset.setValue("This Month", monthTotal);

    } catch (SQLException e) {
        e.printStackTrace();
        dataset.setValue("Today", 0);
        dataset.setValue("This Week", 0);
        dataset.setValue("This Month", 0);
    }

    JFreeChart pieChart = ChartFactory.createPieChart(
        "User Activity", dataset, true, true, false);

    PiePlot plot = (PiePlot) pieChart.getPlot();
    plot.setBackgroundPaint(null);
    plot.setOutlineVisible(false);
    plot.setSectionPaint("Today", new Color(255, 0, 0));
    plot.setSectionPaint("This Week", new Color(0, 255, 0));
    plot.setSectionPaint("This Month", new Color(0, 0, 255)); 

    ChartPanel chartPanel = new ChartPanel(pieChart);
    chartPanel.setOpaque(false);
    chartPanel.setPreferredSize(new java.awt.Dimension(300, 300));

    JPanel graphPanel = new JPanel();
    graphPanel.setOpaque(false);
    graphPanel.setLayout(new BorderLayout());
    graphPanel.add(chartPanel, BorderLayout.CENTER);

    PANEL_1.remove(1); 
    PANEL_1.add(graphPanel, BorderLayout.EAST); 

    PANEL_1.revalidate();
    PANEL_1.repaint();
}
    
    static class Alarm {
        int id;
        LocalTime startTime;
        LocalTime endTime;
        String label;
        boolean triggered;

        public Alarm(int id, LocalTime startTime, LocalTime endTime, String label, boolean triggered) {
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;
            this.label = label;
            this.triggered = triggered;
        }
    }

    static class AlarmDatabase {
        private static final String DB_URL = "jdbc:mysql://localhost:3306/studywise";
        private static final String DB_USER = "root";
        private static final String DB_PASS = "";

        public static void insertAlarm(int userId, LocalTime startTime, LocalTime endTime, String label) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO alarm (user_id, start_time, end_time, alarm_label) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                stmt.setTime(2, Time.valueOf(startTime));
                stmt.setTime(3, Time.valueOf(endTime));
                stmt.setString(4, label); 
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<Alarm> getPendingAlarms(int userId) {
            List<Alarm> alarms = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "SELECT * FROM alarm WHERE triggered = FALSE AND user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    LocalTime startTime = rs.getTime("start_time").toLocalTime();
                    LocalTime endTime = rs.getTime("end_time").toLocalTime();
                    String label = rs.getString("alarm_label");
                    boolean triggered = rs.getBoolean("triggered");
                    alarms.add(new Alarm(id, startTime, endTime, label, triggered));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return alarms;
        }

        public static void markAlarmTriggered(int id) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "UPDATE alarm SET triggered = TRUE WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        PANEL_1 = new javax.swing.JPanel();
        PANEL_2 = new javax.swing.JPanel();
        LABEL_S = new javax.swing.JLabel();
        LABEL_W = new javax.swing.JLabel();
        BUTTON_GOALS = new javax.swing.JButton();
        BUTTON_ACCOUNT = new javax.swing.JButton();
        BUTTON_LOGOUT = new javax.swing.JButton();
        LABEL_BULLET = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PANEL_1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout PANEL_1Layout = new javax.swing.GroupLayout(PANEL_1);
        PANEL_1.setLayout(PANEL_1Layout);
        PANEL_1Layout.setHorizontalGroup(
            PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        PANEL_1Layout.setVerticalGroup(
            PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
        );

        PANEL_2.setBackground(new java.awt.Color(241, 232, 181));
        PANEL_2.setPreferredSize(new java.awt.Dimension(337, 60));

        LABEL_S.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        LABEL_S.setForeground(new java.awt.Color(133, 26, 3));
        LABEL_S.setText("S");

        LABEL_W.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        LABEL_W.setForeground(new java.awt.Color(133, 26, 3));
        LABEL_W.setText("W");

        BUTTON_GOALS.setBackground(new java.awt.Color(133, 26, 3));
        BUTTON_GOALS.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        BUTTON_GOALS.setForeground(new java.awt.Color(133, 26, 3));
        BUTTON_GOALS.setText("Goals");
        BUTTON_GOALS.setBorder(null);
        BUTTON_GOALS.setContentAreaFilled(false);
        BUTTON_GOALS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUTTON_GOALSActionPerformed(evt);
            }
        });

        BUTTON_ACCOUNT.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        BUTTON_ACCOUNT.setText("Account");
        BUTTON_ACCOUNT.setBorder(null);
        BUTTON_ACCOUNT.setContentAreaFilled(false);
        BUTTON_ACCOUNT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUTTON_ACCOUNTActionPerformed(evt);
            }
        });

        BUTTON_LOGOUT.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        BUTTON_LOGOUT.setText("Logout");
        BUTTON_LOGOUT.setBorder(null);
        BUTTON_LOGOUT.setContentAreaFilled(false);
        BUTTON_LOGOUT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUTTON_LOGOUTActionPerformed(evt);
            }
        });

        LABEL_BULLET.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        LABEL_BULLET.setForeground(new java.awt.Color(0, 255, 102));
        LABEL_BULLET.setText("•");

        jButton1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jButton1.setText("Dashboard");
        jButton1.setBorder(null);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PANEL_2Layout = new javax.swing.GroupLayout(PANEL_2);
        PANEL_2.setLayout(PANEL_2Layout);
        PANEL_2Layout.setHorizontalGroup(
            PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PANEL_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PANEL_2Layout.createSequentialGroup()
                        .addComponent(LABEL_S)
                        .addGap(0, 0, 0)
                        .addComponent(LABEL_W))
                    .addComponent(LABEL_BULLET, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(BUTTON_GOALS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BUTTON_ACCOUNT)
                .addGap(18, 18, 18)
                .addComponent(BUTTON_LOGOUT)
                .addGap(25, 25, 25))
        );
        PANEL_2Layout.setVerticalGroup(
            PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PANEL_2Layout.createSequentialGroup()
                .addGroup(PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PANEL_2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LABEL_S)
                            .addComponent(LABEL_W))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LABEL_BULLET))
                    .addGroup(PANEL_2Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BUTTON_GOALS)
                            .addComponent(BUTTON_ACCOUNT)
                            .addComponent(BUTTON_LOGOUT)
                            .addComponent(jButton1))))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PANEL_2, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PANEL_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PANEL_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PANEL_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BUTTON_LOGOUTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUTTON_LOGOUTActionPerformed
        // TODO add your handling code here:
        
          int response = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Confirm Logout",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );

    if (response == JOptionPane.YES_OPTION) {
        
        Login login = new Login();
        login.setVisible(true);
        login.pack();
        login.setLocationRelativeTo(null);
        this.dispose();  
        
    }                       
        
    }//GEN-LAST:event_BUTTON_LOGOUTActionPerformed

    private void BUTTON_ACCOUNTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUTTON_ACCOUNTActionPerformed
        // TODO add your handling code here:
         showEditProfileDialog();
    }//GEN-LAST:event_BUTTON_ACCOUNTActionPerformed

    private void showEditProfileDialog() {
    JTextField usernameField = new JTextField(currentUsername, 20);
    JTextField emailField = new JTextField(currentEmail, 20);
    JPasswordField passwordField = new JPasswordField(20);

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Username:"));
    panel.add(usernameField);
    panel.add(new JLabel("Email:"));
    panel.add(emailField);
    panel.add(new JLabel("New Password:"));
    panel.add(passwordField);

    JCheckBox deleteAccountCheckBox = new JCheckBox("Delete Account");
    panel.add(deleteAccountCheckBox);

    int result = JOptionPane.showConfirmDialog(this, panel, "Edit Profile",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
        if (deleteAccountCheckBox.isSelected()) {
            int confirmDelete = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete your account? This action cannot be undone.", 
                "Confirm Account Deletion", 
                JOptionPane.YES_NO_OPTION);

            if (confirmDelete == JOptionPane.YES_OPTION) {
                deleteAccount();  
            }
        } else {
            String newUsername = usernameField.getText();
            String newEmail = emailField.getText();
            String newPassword = new String(passwordField.getPassword());

            if (!newUsername.isEmpty() && !newEmail.isEmpty() && !newPassword.isEmpty()) {
                updateUserDetailsInDatabase(userId, newUsername, newEmail, newPassword);
            } else {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

private void updateUserDetailsInDatabase(int userId, String username, String email, String password) {
    String query = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(
             AlarmDatabase.DB_URL, 
             AlarmDatabase.DB_USER, 
             AlarmDatabase.DB_PASS);
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, username);
        stmt.setString(2, email);
        stmt.setString(3, password); 
        stmt.setInt(4, userId);

        int rows = stmt.executeUpdate();

        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");
            currentUsername = username;  
            currentEmail = email;
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void deleteAccount() {
    String deleteQuery = "DELETE FROM users WHERE id = ?";
    
    try (Connection conn = DriverManager.getConnection(
             AlarmDatabase.DB_URL, 
             AlarmDatabase.DB_USER, 
             AlarmDatabase.DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

        pstmt.setInt(1, userId);
        int rows = pstmt.executeUpdate();

        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Your account has been deleted successfully.", 
                "Account Deleted", JOptionPane.INFORMATION_MESSAGE);

            redirectToLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Account deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error occurred while deleting your account. Please try again.", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void redirectToLogin() {
    this.dispose();

    Login loginPage = new Login();
    loginPage.setVisible(true);
    loginPage.pack();
    loginPage.setLocationRelativeTo(null);  
}
    
    private void BUTTON_GOALSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUTTON_GOALSActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_BUTTON_GOALSActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
     
        Dashboard dashboard = new Dashboard(userId);
        dashboard.setVisible(true);
        dashboard.pack();
        dashboard.setLocationRelativeTo(null);
        this.dispose();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cdashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cdashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cdashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cdashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cdashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BUTTON_ACCOUNT;
    private javax.swing.JButton BUTTON_GOALS;
    private javax.swing.JButton BUTTON_LOGOUT;
    private javax.swing.JLabel LABEL_BULLET;
    private javax.swing.JLabel LABEL_S;
    private javax.swing.JLabel LABEL_W;
    private javax.swing.JPanel PANEL_1;
    private javax.swing.JPanel PANEL_2;
    private javax.swing.JButton jButton1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
