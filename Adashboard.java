
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author anthony
 */
public class Adashboard extends javax.swing.JFrame {
    
    private Connection con;
    private PreparedStatement pstmt;

    public Adashboard() {
        initComponents();
        try {
            Connection();
            showUserRoleChart(); 
        } catch (SQLException ex) {
            Logger.getLogger(Adashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        addMouseListeners();
    }
    
    private void addMouseListeners() {
    
    BUTTON_MUSERS.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            BUTTON_MUSERS.setBackground(new java.awt.Color(173, 216, 230));
            BUTTON_MUSERS.setOpaque(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            BUTTON_MUSERS.setBackground(null);
            BUTTON_MUSERS.setOpaque(false);
        }
    });

    
    BUTTON_LOGOUT.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            BUTTON_LOGOUT.setBackground(new java.awt.Color(173, 216, 230));
            BUTTON_LOGOUT.setOpaque(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            BUTTON_LOGOUT.setBackground(null);
            BUTTON_LOGOUT.setOpaque(false);
        }
    });
}

    private static final String DbName = "studywise";
    private static final String DbDriver = "com.mysql.cj.jdbc.Driver";
    private static final String DbUrl = "jdbc:mysql://localhost:3306/" + DbName;
    private static final String DbUsername = "root";
    private static final String DbPassword = "";

    public void Connection() throws SQLException {
        try {
            Class.forName(DbDriver);
            con = DriverManager.getConnection(DbUrl, DbUsername, DbPassword);
            if (con != null) {
                System.out.println("Connection successful");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Adashboard.class.getName()).log(Level.SEVERE, "Driver not found", ex);
        }
    }

    private void showUserRoleChart() {
        try {
            System.out.println("Loading user role chart...");

            String query = "SELECT role, COUNT(*) AS count FROM users GROUP BY role";
            pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            boolean hasData = false;

            while (rs.next()) {
                String role = rs.getString("role");
                int count = rs.getInt("count");
                System.out.println("Role: " + role + ", Count: " + count);
                dataset.addValue(count, "Users", role);
                hasData = true;
            }

            rs.close();
            pstmt.close();

            if (!hasData) {
                System.out.println("No data found for user roles.");
                return;
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                    "User Roles Distribution",
                    "Role",
                    "Number of Users",
                    dataset
            );
            

            barChart.setBackgroundPaint(new Color(255,255,255)); 

            CategoryPlot plot = barChart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE); 
            plot.setRangeGridlinePaint(Color.BLACK); 

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(133,26,3)); 

            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(681, 420));

            PANEL_1.removeAll();
            PANEL_1.setLayout(new BorderLayout());
            PANEL_1.add(chartPanel, BorderLayout.CENTER);
            PANEL_1.revalidate();
            PANEL_1.repaint();

            System.out.println("User role chart loaded successfully.");

        } catch (SQLException ex) {
            Logger.getLogger(Adashboard.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Failed to load user role chart: " + ex.getMessage());
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

        PANEL_2 = new javax.swing.JPanel();
        BUTTON_LOGOUT = new javax.swing.JButton();
        BUTTON_MUSERS = new javax.swing.JButton();
        BUTTON_REFRESH = new javax.swing.JButton();
        LABEL_S = new javax.swing.JLabel();
        LABEL_W = new javax.swing.JLabel();
        PANEL_3 = new javax.swing.JPanel();
        PANEL_1 = new javax.swing.JPanel();
        LABEL_BULLET = new javax.swing.JLabel();
        LABEL_Admin = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        PANEL_2.setBackground(new java.awt.Color(241, 232, 181));

        BUTTON_LOGOUT.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        BUTTON_LOGOUT.setText("Logout");
        BUTTON_LOGOUT.setBorder(null);
        BUTTON_LOGOUT.setContentAreaFilled(false);
        BUTTON_LOGOUT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUTTON_LOGOUTActionPerformed(evt);
            }
        });

        BUTTON_MUSERS.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        BUTTON_MUSERS.setText("Manage Users");
        BUTTON_MUSERS.setBorder(null);
        BUTTON_MUSERS.setContentAreaFilled(false);
        BUTTON_MUSERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUTTON_MUSERSActionPerformed(evt);
            }
        });

        BUTTON_REFRESH.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        BUTTON_REFRESH.setText("Dashboard");
        BUTTON_REFRESH.setBorder(null);
        BUTTON_REFRESH.setContentAreaFilled(false);
        BUTTON_REFRESH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BUTTON_REFRESHActionPerformed(evt);
            }
        });

        LABEL_S.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        LABEL_S.setForeground(new java.awt.Color(133, 26, 3));
        LABEL_S.setText("S");

        LABEL_W.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        LABEL_W.setForeground(new java.awt.Color(133, 26, 3));
        LABEL_W.setText("W");

        javax.swing.GroupLayout PANEL_2Layout = new javax.swing.GroupLayout(PANEL_2);
        PANEL_2.setLayout(PANEL_2Layout);
        PANEL_2Layout.setHorizontalGroup(
            PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PANEL_2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LABEL_S)
                .addGap(0, 0, 0)
                .addComponent(LABEL_W)
                .addGap(18, 18, 18)
                .addComponent(BUTTON_REFRESH)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BUTTON_MUSERS)
                .addGap(18, 18, 18)
                .addComponent(BUTTON_LOGOUT)
                .addGap(25, 25, 25))
        );
        PANEL_2Layout.setVerticalGroup(
            PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PANEL_2Layout.createSequentialGroup()
                .addGroup(PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PANEL_2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BUTTON_LOGOUT)
                            .addComponent(BUTTON_MUSERS)
                            .addComponent(BUTTON_REFRESH)))
                    .addGroup(PANEL_2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LABEL_S)
                            .addComponent(LABEL_W))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        PANEL_3.setBackground(new java.awt.Color(255, 255, 255));

        PANEL_1.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout PANEL_1Layout = new javax.swing.GroupLayout(PANEL_1);
        PANEL_1.setLayout(PANEL_1Layout);
        PANEL_1Layout.setHorizontalGroup(
            PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
        );
        PANEL_1Layout.setVerticalGroup(
            PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 229, Short.MAX_VALUE)
        );

        LABEL_BULLET.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LABEL_BULLET.setForeground(new java.awt.Color(0, 255, 102));
        LABEL_BULLET.setText("•");

        LABEL_Admin.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        LABEL_Admin.setText("Admin ");

        javax.swing.GroupLayout PANEL_3Layout = new javax.swing.GroupLayout(PANEL_3);
        PANEL_3.setLayout(PANEL_3Layout);
        PANEL_3Layout.setHorizontalGroup(
            PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PANEL_3Layout.createSequentialGroup()
                .addContainerGap(167, Short.MAX_VALUE)
                .addComponent(PANEL_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(166, 166, 166))
            .addGroup(PANEL_3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(LABEL_Admin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LABEL_BULLET)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PANEL_3Layout.setVerticalGroup(
            PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PANEL_3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LABEL_Admin)
                    .addComponent(LABEL_BULLET, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(PANEL_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PANEL_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PANEL_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PANEL_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(PANEL_3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    private void BUTTON_MUSERSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUTTON_MUSERSActionPerformed
         // TODO add your handling code here:
         
            String query = "SELECT id, username, email, role FROM users";

        try {
            pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            String[] columnNames = {"ID", "Username", "Email", "Role"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                public boolean isCellEditable(int row, int column) {
                    return column != 0; 
                }
            };

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");

                model.addRow(new Object[]{id, username, email, role});
            }

            JTable userTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(userTable);
            scrollPane.setPreferredSize(new java.awt.Dimension(600, 300));

            Object[] options = {"Add", "Update", "Delete", "Close"};

            while (true) {
                int choice = JOptionPane.showOptionDialog(
                        this,
                        scrollPane,
                        "Manage Users",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[3]
                );

                if (choice == 0) { 
                    String username = JOptionPane.showInputDialog(this, "Enter username:");
                    String email = JOptionPane.showInputDialog(this, "Enter email:");
                    String role = JOptionPane.showInputDialog(this, "Enter role:");

                    if (username != null && email != null && role != null) {
                        String defaultPassword = "123456"; 
                        String insertQuery = "INSERT INTO users (username, email, role, password) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertStmt = con.prepareStatement(insertQuery);
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, email);
                        insertStmt.setString(3, role);
                        insertStmt.setString(4, defaultPassword);
                        insertStmt.executeUpdate();
                        insertStmt.close();

                        model.addRow(new Object[]{null, username, email, role});
                    }

                } else if (choice == 1) { 
                    int row = userTable.getSelectedRow();
                    if (row >= 0) {
                        int id = (int) model.getValueAt(row, 0);
                        String username = (String) model.getValueAt(row, 1);
                        String email = (String) model.getValueAt(row, 2);
                        String role = (String) model.getValueAt(row, 3);

                        String updateQuery = "UPDATE users SET username=?, email=?, role=? WHERE id=?";
                        PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                        updateStmt.setString(1, username);
                        updateStmt.setString(2, email);
                        updateStmt.setString(3, role);
                        updateStmt.setInt(4, id);
                        updateStmt.executeUpdate();
                        updateStmt.close();
                        
                        JOptionPane.showMessageDialog(this, "Successfully updated user.", "Update Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Please select a row to update.");
                    }

                } else if (choice == 2) { 
                    int row = userTable.getSelectedRow();
                    if (row >= 0) {
                        int id = (int) model.getValueAt(row, 0);
                        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            String deleteQuery = "DELETE FROM users WHERE id=?";
                            PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
                            deleteStmt.setInt(1, id);
                            deleteStmt.executeUpdate();
                            deleteStmt.close();
                            model.removeRow(row);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Please select a row to delete.");
                    }

                } else {
                    break; 
                }
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_BUTTON_MUSERSActionPerformed

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
        
    }//GEN-LAST:event_BUTTON_LOGOUTActionPerformed
        }
    private void BUTTON_REFRESHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BUTTON_REFRESHActionPerformed
        // TODO add your handling code here:
      showUserRoleChart();
      JOptionPane.showMessageDialog(this, "Dashboard Refreshed!");
    }//GEN-LAST:event_BUTTON_REFRESHActionPerformed

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
            java.util.logging.Logger.getLogger(Adashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Adashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Adashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Adashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Adashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BUTTON_LOGOUT;
    private javax.swing.JButton BUTTON_MUSERS;
    private javax.swing.JButton BUTTON_REFRESH;
    private javax.swing.JLabel LABEL_Admin;
    private javax.swing.JLabel LABEL_BULLET;
    private javax.swing.JLabel LABEL_S;
    private javax.swing.JLabel LABEL_W;
    private javax.swing.JPanel PANEL_1;
    private javax.swing.JPanel PANEL_2;
    private javax.swing.JPanel PANEL_3;
    // End of variables declaration//GEN-END:variables
}
