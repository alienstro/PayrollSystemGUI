/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package learn;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Dashboard extends javax.swing.JFrame {

    private double overloadPay = 0;
    private double subTotal = 0;
    private double deductions = 0;
    private double pay = 0;
    private double allowance = 0;

    public Dashboard() {
        initComponents();
        connection(); //connect netbeans to mysql
        autoEmpID(); // auto increment employee id
        showTable(); // automatically show table in employee 
        JFrameCenterPositionTest(); // center the jframe
        showTablePay(); // automaticaly show data in table employee pay
        recordTable(); // automatically show data in table record
        nonePanel.setVisible(false);
        autoPayID(); // auto increment pad id
        nameSet(); // setting the welcome to "username"
        allowanceSet.setVisible(false);
        sumSalary(); // sum of salary 

    }

    String duplication = "";

    public void nameSet() {
        try {
            pst = con.prepareStatement("SELECT username from login"); // command to get the username from login table
            rs = pst.executeQuery(); // command to execute the query
            while (rs.next()) { // iterate to the all table rows and columns
                String name = rs.getString("username");
                nameLabel.setText(name); // setting text in nameLabel
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void sumSalary() {
        try {
            pst = con.prepareStatement("SELECT SUM(salary) from payroll"); // command to get the SUM of all of the salary from payroll table
            rs = pst.executeQuery(); // command to execute the query
            while (rs.next()) { // iterate to the all table rows and columns
                String sum = rs.getString("SUM(salary)"); // setting it to sum String
                sumLabel.setText("Total: " + sum); // set text to sumLabel
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salaryTeaching() {
        pay = 0;
        deductions = 0;
        subTotal = 0;
        overloadPay = 0;
        allowance = 0;

        double payRate = Double.parseDouble(payrateField.getText());
        double teachingHours = Double.parseDouble(teachingField.getText());

        // SUBTOTAL OF TEACHING HOURS AND PAYRATE
        if (teachingHours <= 96) {
            subTotal = teachingHours * payRate;
        } else if (teachingHours >= 97 && teachingHours <= 128) { // OVERLOAD PAY
            double numOverloadHours = teachingHours - 96;
            subTotal = 96 * payRate;
            overloadPay = numOverloadHours * (1.2 * payRate);
        } else {
            JOptionPane.showMessageDialog(this, "Maximum of 96 hours per month with 32 hours overtime!");
        }

        // DEDUCTION
        double noOfAbsentHours = Double.parseDouble(absentField.getText());
        deductions = noOfAbsentHours * payRate;

        allowance = (0.1 * subTotal);

        pay = (subTotal + overloadPay + allowance) - deductions;

        if (pay < 0) {
            pay = 0;
        }

    }

    private double monthlyRate = 0;
    private double noneAbsentAbsent = 0;
    private double noneAllowance = 0;
    private double noneDeductions = 0;
    private double overtimePay = 0;
    private double salaryNoneTeaching = 0;

    public void salaryNone() {
        monthlyRate = 0;
        noneAbsentAbsent = 0;
        noneAllowance = 0;
        noneDeductions = 0;
        overtimePay = 0;
        salaryNoneTeaching = 0;
        double noneTeachOvertime = Double.parseDouble(overtimeLabel.getText());

        // CONDITION FOR OVERTIME
        if (noneTeachOvertime >= 0 && noneTeachOvertime <= 48) {
            double monthlyRate = Double.parseDouble(monthlyLabel.getText());

            double hourlyRate = (monthlyRate / 30 / 8);

            overtimePay = noneTeachOvertime * (1.2 * hourlyRate);

            double noneAbsentHours = Double.parseDouble(absentLabel.getText());

            noneDeductions = noneAbsentHours * hourlyRate;

            // CONDITION IF THE ALLOWANCE CHECK IS SELECTED
            if (allowanceCheck.isSelected()) {
                double allowanceVal = 0;
                allowanceVal = Double.parseDouble(allowanceSet.getText());
                if (allowanceVal >= 1 && allowanceVal >= 10) {
                    double allowancePer = allowanceVal / 100;
                    noneAllowance = (allowancePer * monthlyRate);
                    salaryNoneTeaching = (hourlyRate + overtimePay + noneAllowance) - noneDeductions;
                } else {
                    JOptionPane.showMessageDialog(this, "Between 1% to 10% only");
                    
                }
            } else {
                salaryNoneTeaching = (hourlyRate + overtimePay) - noneDeductions;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Minimum of 0 hours and Maximum of 48 hours!");

        }

    }

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public void connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employeedb", "root", "");

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Dashboard.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void JFrameCenterPositionTest() { // SETTING THE JFRAME TO THE CENTER OF THE SCREEN
        try {
            setTitle("JFrameCenter Position");
            add(new JLabel("JFrame set to center of the screen", SwingConstants.CENTER), BorderLayout.CENTER);
            setSize(948, 527);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null); // this method display the JFrame to center position of a screen
            setVisible(true);
        } catch (Exception e) {

        }
    }

    public void autoEmpID() { // auto increment by 1 202300001 -> 202300002
        try {
            Statement s = con.createStatement();
            try {
                rs = s.executeQuery("SELECT MAX(employeeid) FROM employee"); // getting the highest value in employee/employeeid
                rs.next();
                rs.getString("MAX(employeeid)");

                if (rs.getString("MAX(employeeid)") == null) {
                    empidLabel.setText("202300001"); // setting the first label to 202300001
                } else {
                    long id = Long.parseLong(rs.getString("MAX(employeeid)").substring(4, rs.getString("MAX(employeeid)").length())); // ignores first 4 char
                    id++;
                    empidLabel.setText("2023" + String.format("%05d", id)); // if no "2023" it will have errors.

                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void autoPayID() { // auto increment by 1 202300001 -> 202300002
        try {
            Statement s = con.createStatement();
            try {
                rs = s.executeQuery("SELECT MAX(payid) FROM payroll"); // getting the highest value in employee/employeeid
                rs.next();

                if (rs.getString("MAX(payid)") == null) {
                    payidLabel.setText("pay00001"); // setting the first label to 00001
                } else {
                    long id = Long.parseLong(rs.getString("MAX(payid)").replaceAll("\\D", "")); // \\D remove any character from the string \\d remove any digit 
                    id++;
                    payidLabel.setText("pay" + String.format("%05d", id));

                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class
                    .getName()).log(Level.SEVERE, null, ex);
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

        noneButtonGroup = new javax.swing.ButtonGroup();
        payButtonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        empLabel = new javax.swing.JLabel();
        payLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        firstPayLabel = new javax.swing.JLabel();
        lastPayLabel = new javax.swing.JLabel();
        empidPayLabel = new javax.swing.JLabel();
        salaryField = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        payidLabel = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        recordsLabel = new javax.swing.JLabel();
        duplicateLabel = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jTabbedPane = new javax.swing.JTabbedPane();
        empPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        empTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        empidLabel = new javax.swing.JTextField();
        firstLabel = new javax.swing.JTextField();
        lastLabel = new javax.swing.JTextField();
        positionComboBox = new javax.swing.JComboBox<>();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        addToPayrollButton = new javax.swing.JButton();
        departmentLabel = new javax.swing.JComboBox<>();
        searchField = new javax.swing.JTextField();
        payPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        payTable = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        searchField1 = new javax.swing.JTextField();
        sumLabel = new javax.swing.JLabel();
        teachTabbedPane = new javax.swing.JTabbedPane();
        teaching = new javax.swing.JPanel();
        addPayButton = new javax.swing.JButton();
        viewPayButton = new javax.swing.JButton();
        deletePayButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        allowanceField = new javax.swing.JTextField();
        absentField = new javax.swing.JTextField();
        payrateField = new javax.swing.JTextField();
        teachingField = new javax.swing.JTextField();
        overloadField = new javax.swing.JTextField();
        totalPayField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        positionPayComboBox = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        nonePanel = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        monthlyLabel = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        overtimeLabel = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        absentLabel = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        allowanceCheck = new javax.swing.JCheckBox();
        allowanceLabel = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        overtimePayLabel = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        salaryNoneField = new javax.swing.JTextField();
        addNoneButton = new javax.swing.JButton();
        viewNoneButton = new javax.swing.JButton();
        deleteNoneButton = new javax.swing.JButton();
        allowanceSet = new javax.swing.JTextField();
        recordsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        recordTable = new javax.swing.JTable();
        searchField2 = new javax.swing.JTextField();
        showButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(1129, 849));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel1.setForeground(new java.awt.Color(204, 204, 255));

        empLabel.setBackground(new java.awt.Color(255, 255, 255));
        empLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        empLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        empLabel.setText("Manage Payroll");
        empLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        empLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                empLabelMouseClicked(evt);
            }
        });

        payLabel.setBackground(new java.awt.Color(255, 255, 255));
        payLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        payLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        payLabel.setText("Manage Employee");
        payLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        payLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                payLabelMouseClicked(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("Logout");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        firstPayLabel.setForeground(new java.awt.Color(204, 204, 255));
        firstPayLabel.setText("1");

        lastPayLabel.setForeground(new java.awt.Color(204, 204, 255));
        lastPayLabel.setText("1");

        empidPayLabel.setForeground(new java.awt.Color(204, 204, 255));
        empidPayLabel.setText("1");

        salaryField.setForeground(new java.awt.Color(204, 204, 255));
        salaryField.setText("1");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setText("__________________________");

        payidLabel.setBackground(new java.awt.Color(255, 255, 255));
        payidLabel.setForeground(new java.awt.Color(204, 204, 255));
        payidLabel.setText("1");

        jButton4.setBackground(new java.awt.Color(204, 204, 255));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setText("Generate Report");
        jButton4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        recordsLabel.setBackground(new java.awt.Color(255, 255, 255));
        recordsLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        recordsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recordsLabel.setText("Records");
        recordsLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        recordsLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                recordsLabelMouseClicked(evt);
            }
        });

        duplicateLabel.setForeground(new java.awt.Color(204, 204, 255));
        duplicateLabel.setText("1");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("WELCOME,");

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nameLabel.setText("BACK");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(duplicateLabel)
                            .addComponent(salaryField)
                            .addComponent(firstPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lastPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empidPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(payidLabel)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel22)))
                .addGap(0, 14, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel23)
                .addGap(21, 21, 21))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(payLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                            .addComponent(empLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(recordsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(nameLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addGap(47, 47, 47)
                .addComponent(payLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(empLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(recordsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79)
                .addComponent(duplicateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salaryField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstPayLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastPayLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(empidPayLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(payidLabel)
                .addGap(27, 27, 27)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 810));

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel2.setPreferredSize(new java.awt.Dimension(890, 57));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel21.setText("EMPLOYEE SHEET");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(267, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addGap(314, 314, 314))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jLabel21)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 20, 880, 50));

        jTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        empPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        empTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "First Name", "Last Name", "Department", "Position"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        empTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        empTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                empTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(empTable);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel4.setPreferredSize(new java.awt.Dimension(879, 295));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Employee ID:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("First Name:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Last Name:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Department:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Position:");

        empidLabel.setEditable(false);

        positionComboBox.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        positionComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Teaching", "None" }));

        addButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        addButton.setText("ADD");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        editButton.setText("EDIT");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        deleteButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        deleteButton.setText("DELETE");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        viewButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        viewButton.setText("VIEW");
        viewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewButtonActionPerformed(evt);
            }
        });

        clearButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        clearButton.setText("CLEAR");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        addToPayrollButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        addToPayrollButton.setText("ADD TO PAYROLL");
        addToPayrollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToPayrollButtonActionPerformed(evt);
            }
        });

        departmentLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        departmentLabel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CCS", "CHTM", "CAHS", "CEAS" }));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(11, 11, 11)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(empidLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(departmentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(firstLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(lastLabel))
                        .addGap(106, 106, 106)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(positionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(121, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addToPayrollButton, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(deleteButton)
                        .addGap(18, 18, 18)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(viewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(49, 49, 49))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(departmentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(positionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(firstLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lastLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(empidLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(viewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addToPayrollButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        searchField.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchField.setForeground(new java.awt.Color(204, 204, 204));
        searchField.setText("Search");
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchFieldMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchFieldMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchFieldMousePressed(evt);
            }
        });
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout empPanelLayout = new javax.swing.GroupLayout(empPanel);
        empPanel.setLayout(empPanelLayout);
        empPanelLayout.setHorizontalGroup(
            empPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        empPanelLayout.setVerticalGroup(
            empPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane.addTab("tab1", empPanel);

        payPanel.setBackground(new java.awt.Color(255, 255, 255));
        payPanel.setPreferredSize(new java.awt.Dimension(2000, 797));
        payPanel.setRequestFocusEnabled(false);
        payPanel.setVerifyInputWhenFocusTarget(false);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        payTable.setBackground(new java.awt.Color(204, 204, 204));
        payTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Pay ID", "First Name", "Last Name", "Position", "Total Pay (PHP)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        payTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        payTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                payTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(payTable);

        searchField1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchField1.setForeground(new java.awt.Color(204, 204, 204));
        searchField1.setText("Search");
        searchField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchField1MouseClicked(evt);
            }
        });
        searchField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchField1ActionPerformed(evt);
            }
        });
        searchField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchField1KeyReleased(evt);
            }
        });

        sumLabel.setText("Total:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sumLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(searchField1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(279, 279, 279)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(searchField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sumLabel)
                .addContainerGap())
        );

        teachTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        teachTabbedPane.setForeground(new java.awt.Color(255, 255, 255));

        teaching.setBackground(new java.awt.Color(255, 255, 255));
        teaching.setToolTipText("");
        teaching.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        addPayButton.setBackground(new java.awt.Color(204, 255, 255));
        addPayButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        addPayButton.setText("ADD/EDIT");
        addPayButton.setBorderPainted(false);
        addPayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPayButtonActionPerformed(evt);
            }
        });
        teaching.add(addPayButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 220, -1, -1));

        viewPayButton.setBackground(new java.awt.Color(204, 204, 255));
        viewPayButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        viewPayButton.setText("VIEW");
        viewPayButton.setBorderPainted(false);
        viewPayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewPayButtonActionPerformed(evt);
            }
        });
        teaching.add(viewPayButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 220, -1, -1));

        deletePayButton.setBackground(new java.awt.Color(255, 204, 204));
        deletePayButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        deletePayButton.setText("DELETE");
        deletePayButton.setBorderPainted(false);
        deletePayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePayButtonActionPerformed(evt);
            }
        });
        teaching.add(deletePayButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 220, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Payrate/Hr:");
        teaching.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 37, 90, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("Teaching Hours:");
        teaching.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 77, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("Overload:");
        teaching.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 77, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("Allowance:");
        teaching.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(473, 37, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("Absent Hours:");
        teaching.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 117, -1, -1));

        allowanceField.setEditable(false);
        allowanceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowanceFieldActionPerformed(evt);
            }
        });
        teaching.add(allowanceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 40, 230, -1));

        absentField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                absentFieldActionPerformed(evt);
            }
        });
        teaching.add(absentField, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 220, -1));
        teaching.add(payrateField, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 220, -1));
        teaching.add(teachingField, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 220, -1));

        overloadField.setEditable(false);
        teaching.add(overloadField, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 80, 230, -1));

        totalPayField.setEditable(false);
        totalPayField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalPayFieldActionPerformed(evt);
            }
        });
        teaching.add(totalPayField, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 120, 230, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("Total Pay:");
        teaching.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 117, -1, -1));

        positionPayComboBox.setEditable(true);
        positionPayComboBox.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        positionPayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Teaching", "None" }));
        positionPayComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                positionPayComboBoxMouseClicked(evt);
            }
        });
        positionPayComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionPayComboBoxActionPerformed(evt);
            }
        });
        teaching.add(positionPayComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 160, 137, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setText("Position:");
        teaching.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 160, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("Currently in:");
        teaching.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, -1));

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setText("Teaching");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        teaching.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 230, -1, -1));

        teachTabbedPane.addTab("Teaching", teaching);

        nonePanel.setBackground(new java.awt.Color(255, 255, 255));
        nonePanel.setForeground(new java.awt.Color(255, 255, 255));
        nonePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton3.setText("Non-Teaching");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        nonePanel.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 230, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("Currently in:");
        nonePanel.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, -1));
        nonePanel.add(monthlyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 220, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("Monthly Pay:");
        nonePanel.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 37, -1, -1));
        nonePanel.add(overtimeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 220, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("Overtime Hours:");
        nonePanel.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 77, -1, -1));

        absentLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                absentLabelActionPerformed(evt);
            }
        });
        nonePanel.add(absentLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 220, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setText("Absent Hours:");
        nonePanel.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(43, 117, -1, -1));

        allowanceCheck.setBackground(new java.awt.Color(255, 255, 255));
        allowanceCheck.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        allowanceCheck.setText("Allow Allowances?");
        allowanceCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowanceCheckActionPerformed(evt);
            }
        });
        nonePanel.add(allowanceCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 150, -1, -1));

        allowanceLabel.setEditable(false);
        allowanceLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowanceLabelActionPerformed(evt);
            }
        });
        nonePanel.add(allowanceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 40, 230, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setText("Allowance:");
        nonePanel.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(473, 37, -1, -1));

        overtimePayLabel.setEditable(false);
        nonePanel.add(overtimePayLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 80, 230, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel19.setText("Overtime:");
        nonePanel.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 77, -1, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel20.setText("Salary:");
        nonePanel.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 117, -1, -1));

        salaryNoneField.setEditable(false);
        nonePanel.add(salaryNoneField, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 120, 230, -1));

        addNoneButton.setBackground(new java.awt.Color(204, 255, 255));
        addNoneButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        addNoneButton.setText("ADD/EDIT");
        addNoneButton.setBorderPainted(false);
        addNoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNoneButtonActionPerformed(evt);
            }
        });
        nonePanel.add(addNoneButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 220, -1, -1));

        viewNoneButton.setBackground(new java.awt.Color(204, 204, 255));
        viewNoneButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        viewNoneButton.setText("VIEW");
        viewNoneButton.setBorderPainted(false);
        viewNoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewNoneButtonActionPerformed(evt);
            }
        });
        nonePanel.add(viewNoneButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 220, -1, -1));

        deleteNoneButton.setBackground(new java.awt.Color(255, 204, 204));
        deleteNoneButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        deleteNoneButton.setText("DELETE");
        deleteNoneButton.setBorderPainted(false);
        deleteNoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNoneButtonActionPerformed(evt);
            }
        });
        nonePanel.add(deleteNoneButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 220, -1, -1));

        allowanceSet.setForeground(new java.awt.Color(204, 204, 204));
        allowanceSet.setText("Enter the percentage 1% - 10%");
        allowanceSet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                allowanceSetMouseClicked(evt);
            }
        });
        allowanceSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowanceSetActionPerformed(evt);
            }
        });
        nonePanel.add(allowanceSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, 220, -1));

        teachTabbedPane.addTab("None", nonePanel);

        javax.swing.GroupLayout payPanelLayout = new javax.swing.GroupLayout(payPanel);
        payPanel.setLayout(payPanelLayout);
        payPanelLayout.setHorizontalGroup(
            payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(payPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(teachTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 831, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        payPanelLayout.setVerticalGroup(
            payPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payPanelLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(teachTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        jTabbedPane.addTab("tab2", payPanel);

        recordTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Employee ID", "Pay ID", "First Name", "Last Name", "Department", "Position", "Salary"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(recordTable);

        searchField2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        searchField2.setForeground(new java.awt.Color(204, 204, 204));
        searchField2.setText("Search");
        searchField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchField2MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchField2MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchField2MousePressed(evt);
            }
        });
        searchField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchField2ActionPerformed(evt);
            }
        });
        searchField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchField2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchField2KeyReleased(evt);
            }
        });

        showButton.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        showButton.setText("Show All Records");
        showButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout recordsPanelLayout = new javax.swing.GroupLayout(recordsPanel);
        recordsPanel.setLayout(recordsPanelLayout);
        recordsPanelLayout.setHorizontalGroup(
            recordsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recordsPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(recordsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(showButton)
                    .addGroup(recordsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(searchField2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 851, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        recordsPanelLayout.setVerticalGroup(
            recordsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recordsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showButton)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("tab3", recordsPanel);

        getContentPane().add(jTabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, 880, 770));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

        String empid = empidLabel.getText();
        String payid = payidLabel.getText();
        String firstname = firstLabel.getText();
        String lastname = lastLabel.getText();
        String department = (String) departmentLabel.getSelectedItem();
        String position = (String) positionComboBox.getSelectedItem();
        double salary = 0.0;

        duplicateLabel.setText(duplication);

        // CONDITION ALL FIELDS MUSH HAVE VALUE
        if (firstname.isEmpty() || lastname.isEmpty() || department.isEmpty() || position.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Please enter all fields!");
        } else if (empid.equals(duplication)) {
            JOptionPane.showMessageDialog(this, "No Duplication of employeeid. Please click the CLEAR button");
        } else {
            try {
                autoEmpID();
                autoPayID();
                pst = con.prepareStatement("INSERT INTO employee(employeeid,firstname,lastname,department,position) VALUES (?,?,?,?,?)"); // COMMAND TO QUERY
                pst.setString(1, empid);
                pst.setString(2, firstname);
                pst.setString(3, lastname);
                pst.setString(4, department);
                pst.setString(5, position);

                int temp1 = pst.executeUpdate(); // COMMAND TO EXECUTE QUERY

                pst = con.prepareStatement("INSERT INTO payroll(employeeid, payid, firstname,lastname,position,salary) VALUES (?,?,?,?,?,?)"); // COMMAND TO QUERY
                pst.setString(1, empid);
                pst.setString(2, payid);
                pst.setString(3, firstname);
                pst.setString(4, lastname);
                pst.setString(5, position);
                pst.setDouble(6, salary);
                empTable.getSelectionModel().clearSelection();
                int temp2 = pst.executeUpdate(); // COMMAND TO EXECUTE QUERY

                if (temp1 == 1) {
                    autoPayID();
                    autoEmpID();
                    firstLabel.setText("");
                    lastLabel.setText("");
                    departmentLabel.setSelectedItem("");
                    positionComboBox.setSelectedItem("");

                } else {
                    JOptionPane.showMessageDialog(this, "Please enter all fields!");

                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        showTable();
        showTablePay();
        recordTable();
    }//GEN-LAST:event_addButtonActionPerformed
    public void showTable() { // SHOW DATA IN empTable
        int temp2;

        try {
            pst = con.prepareStatement("SELECT * FROM employee");
            rs = pst.executeQuery();

            ResultSetMetaData rss = rs.getMetaData();
            temp2 = rss.getColumnCount();

            DefaultTableModel model = (DefaultTableModel) empTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                Vector array = new Vector(); // vector is like an arraylist it creates it own array of objects
                for (int temp1 = 1; temp1 <= temp2; temp1++) {
                    array.add(rs.getString("employeeid"));
                    array.add(rs.getString("firstname"));
                    array.add(rs.getString("lastname"));
                    array.add(rs.getString("department"));
                    array.add(rs.getString("position"));
                }

                model.addRow(array);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showTablePay() { // SHOW DATA IN payTable
        int temp2;

        try {
            pst = con.prepareStatement("SELECT * FROM payroll");
            rs = pst.executeQuery();

            ResultSetMetaData rss = rs.getMetaData();
            temp2 = rss.getColumnCount();

            DefaultTableModel model = (DefaultTableModel) payTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                Vector array = new Vector(); // vector is like an arraylist it creates it own array of objects
                for (int temp1 = 1; temp1 <= temp2; temp1++) {
                    array.add(rs.getString("employeeid"));
                    array.add(rs.getString("payid"));
                    array.add(rs.getString("firstname"));
                    array.add(rs.getString("lastname"));
                    array.add(rs.getString("position"));
                    array.add(rs.getString("salary"));
                }

                model.addRow(array);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recordTable() { // SHOW  DATA IN recordTable
        int temp2;

        try {
            pst = con.prepareStatement("SELECT employee.employeeid,payroll.payid, employee.firstname, employee.lastname, employee.department, employee.position,  payroll.salary FROM employee JOIN payroll ON employee.employeeid = payroll.employeeid;");
            rs = pst.executeQuery();

            ResultSetMetaData rss = rs.getMetaData();
            temp2 = rss.getColumnCount();

            DefaultTableModel model = (DefaultTableModel) recordTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                Vector array = new Vector(); // vector is like an arraylist it creates it own array of objects
                for (int temp1 = 1; temp1 <= temp2; temp1++) {
                    array.add(rs.getString("employeeid"));
                    array.add(rs.getString("payid"));
                    array.add(rs.getString("firstname"));
                    array.add(rs.getString("lastname"));
                    array.add(rs.getString("department"));
                    array.add(rs.getString("position"));
                    array.add(rs.getString("salary"));
                }

                model.addRow(array);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewButtonActionPerformed
        // REFRESH THE TABLE
        showTable();
        autoEmpID();
        autoPayID();
        empTable.getSelectionModel().clearSelection();
    }//GEN-LAST:event_viewButtonActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        ///////////////// not finisheddddd
        DefaultTableModel model = (DefaultTableModel) empTable.getModel();
        TableRowSorter<DefaultTableModel> model1 = new TableRowSorter<>(model);
        empTable.setRowSorter(model1);
        model1.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText())); // will find regardless of uppercase or not

    }//GEN-LAST:event_searchFieldKeyReleased

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int row = empTable.getSelectedRow();

        String empid = empidLabel.getText();
        String firstname = firstLabel.getText();
        String lastname = lastLabel.getText();
        String department = (String) departmentLabel.getSelectedItem();
        String position = (String) positionComboBox.getSelectedItem();

        // DELETE BUTTON IN empTable
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row", "Error", JOptionPane.PLAIN_MESSAGE);
        } else {
            try {
                pst = con.prepareStatement("DELETE FROM employee WHERE employeeid = ? AND firstname = ? and lastname = ? AND department = ?  AND position =  ? ");
                DefaultTableModel model = (DefaultTableModel) empTable.getModel();

                model.removeRow(row);
                pst.setString(1, empid);
                pst.setString(2, firstname);
                pst.setString(3, lastname);
                pst.setString(4, department);
                pst.setString(5, position);

                int rowsAffected = pst.executeUpdate();
                empTable.getSelectionModel().clearSelection();
                JOptionPane.showMessageDialog(this, "Deleted Successfully!");
                autoEmpID();
                autoPayID();

            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_deleteButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        int row = empTable.getSelectedRow();

        String empid = empidLabel.getText();
        String firstname = firstLabel.getText();
        String lastname = lastLabel.getText();
        String department = (String) departmentLabel.getSelectedItem();
        String position = (String) positionComboBox.getSelectedItem();

        // EDIT TABLE IN empTable
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row", "Error", JOptionPane.PLAIN_MESSAGE);
        } else {
            try {

                pst = con.prepareStatement("UPDATE employee SET firstname = ?, lastname = ?, department = ?,position = ? WHERE employeeid = ?");
                DefaultTableModel model = (DefaultTableModel) empTable.getModel();

                pst.setString(1, firstname);
                pst.setString(2, lastname);
                pst.setString(3, department);
                pst.setString(4, position);
                pst.setString(5, empid);

                model.setValueAt(firstname, row, 1);
                model.setValueAt(lastname, row, 2);
                model.setValueAt(department, row, 3);
                model.setValueAt(position, row, 4);

                int rowsAffected = pst.executeUpdate(); // updates query
                empTable.getSelectionModel().clearSelection();
                JOptionPane.showMessageDialog(this, "Update Successful!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid payrate value", "Error", JOptionPane.PLAIN_MESSAGE);

            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_editButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        firstLabel.setText("");
        lastLabel.setText("");
        departmentLabel.setSelectedItem("");
        positionComboBox.setSelectedItem("");
        autoEmpID();
        autoPayID();
        // CLEARING THE TEXTFIELDS

    }//GEN-LAST:event_clearButtonActionPerformed

    private void searchFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseClicked
        searchField.setText(""); // SETTING THE SEARCHFIELD WHEN CLICKED
    }//GEN-LAST:event_searchFieldMouseClicked

    private void searchFieldMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseExited

    }//GEN-LAST:event_searchFieldMouseExited

    private void addToPayrollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToPayrollButtonActionPerformed
        int row = empTable.getSelectedRow();

        // ADDING INFORMATION TO THE DATABASE
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row!");

        } else {
            try {
                autoPayID();
                String empid = empidLabel.getText();
                String payid = payidLabel.getText();
                String firstname = firstLabel.getText();
                String lastname = lastLabel.getText();
                String position = (String) positionComboBox.getSelectedItem();
                double salary = 0;

                pst = con.prepareStatement("INSERT INTO payroll(employeeid,payid,firstname,lastname,position,salary) VALUES (?,?,?,?,?,?)");
                pst.setString(1, empid);
                pst.setString(2, payid);
                pst.setString(3, firstname);
                pst.setString(4, lastname);
                pst.setString(5, position);
                pst.setDouble(6, salary);

                int temp2 = pst.executeUpdate();
                empTable.getSelectionModel().clearSelection();
                showTablePay();
                JOptionPane.showMessageDialog(this, "Added to Payroll");

            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_addToPayrollButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Login x = new Login();
        x.setVisible(true); // will show the Login.java
        this.dispose(); // will exit the window dashboard.java
    }//GEN-LAST:event_jButton1ActionPerformed

    private void payLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payLabelMouseClicked
        jTabbedPane.setSelectedIndex(0); // will switch panels
        empTable.getSelectionModel().clearSelection();
    }//GEN-LAST:event_payLabelMouseClicked

    private void empLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_empLabelMouseClicked
        jTabbedPane.setSelectedIndex(1); // will switch panels
        empTable.getSelectionModel().clearSelection();
    }//GEN-LAST:event_empLabelMouseClicked

    private void payTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_payTableMouseClicked
        
        // SETTING THE INFORMATION OF THE JTABLE TO THE JLABELS
        DefaultTableModel model = (DefaultTableModel) payTable.getModel();

        String empid = model.getValueAt(payTable.getSelectedRow(), 0).toString();
        String payid = model.getValueAt(payTable.getSelectedRow(), 1).toString();
        String first = model.getValueAt(payTable.getSelectedRow(), 2).toString();
        String last = model.getValueAt(payTable.getSelectedRow(), 3).toString();
        String position = model.getValueAt(payTable.getSelectedRow(), 4).toString();
        String salary = model.getValueAt(payTable.getSelectedRow(), 5).toString();
        duplication = model.getValueAt(payTable.getSelectedRow(), 4).toString();

        empidPayLabel.setText(empid);
        payidLabel.setText(payid);
        firstPayLabel.setText(first);
        lastPayLabel.setText(last);
        positionPayComboBox.setSelectedItem(position);
        salaryField.setText(salary);
    }//GEN-LAST:event_payTableMouseClicked

    private void positionPayComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionPayComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_positionPayComboBoxActionPerformed

    private void positionPayComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_positionPayComboBoxMouseClicked

    }//GEN-LAST:event_positionPayComboBoxMouseClicked

    private void totalPayFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalPayFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalPayFieldActionPerformed

    private void absentFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_absentFieldActionPerformed

    }//GEN-LAST:event_absentFieldActionPerformed

    private void allowanceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowanceFieldActionPerformed
        allowanceField.setText(Double.toString(allowance));
    }//GEN-LAST:event_allowanceFieldActionPerformed

    private void deletePayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePayButtonActionPerformed
        int row1 = payTable.getSelectedRow();

        // DELETE BUTTON FOR TEACHING PAYTABLE
        String empid = empidPayLabel.getText();
        String payid = payidLabel.getText();
        String firstname = firstPayLabel.getText();
        String lastname = lastPayLabel.getText();
        String position = (String) positionPayComboBox.getSelectedItem();
        double salary = Double.parseDouble(salaryField.getText());

        if (row1 < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row", "Error", JOptionPane.PLAIN_MESSAGE);
        } else {
            try {
                pst = con.prepareStatement("DELETE FROM payroll WHERE payid = ? AND firstname = ? and lastname = ? AND position =  ? AND salary = ?");
                DefaultTableModel model = (DefaultTableModel) payTable.getModel();

                model.removeRow(row1);
//                pst.setString(1, empid);
                pst.setString(1, payid);
                pst.setString(2, firstname);
                pst.setString(3, lastname);
                pst.setString(4, position);
                pst.setDouble(5, salary);

                int temp1 = pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted Successfully!");
                showTablePay();
                autoPayID();
                sumSalary();

            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_deletePayButtonActionPerformed

    private void viewPayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewPayButtonActionPerformed
        showTablePay();
        autoPayID();
        payTable.getSelectionModel().clearSelection();
        // refresh table
    }//GEN-LAST:event_viewPayButtonActionPerformed

    private void addPayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPayButtonActionPerformed
        
        // ADD INFORMATION TO THE DB
        int row1 = payTable.getSelectedRow();

        String payrate = payrateField.getText();
        String teaching1 = teachingField.getText();
        String absent = absentField.getText();

        if (payrate.isEmpty() || teaching1.isEmpty() || absent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields!");
        } else if ("None".equals(duplication)) {
            JOptionPane.showMessageDialog(this, "Only for Teaching employees");
        } else if (row1 < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row!");
        } else {
            try {
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                pay = 0;
                salaryTeaching();
                allowanceField.setText("");
                totalPayField.setText("");
                overloadField.setText("");
                totalPayField.setText(decimalFormat.format(pay));
                allowanceField.setText(decimalFormat.format(allowance));
                overloadField.setText(decimalFormat.format(overloadPay));

                double salary = pay;
                String payid = payidLabel.getText();

                pst = con.prepareStatement("UPDATE payroll SET salary = ? WHERE payid = ?"); // command to query

                pst.setDouble(1, salary);
                pst.setString(2, payid);

                int temp1 = pst.executeUpdate();
                showTablePay();
                recordTable();
                sumSalary();

            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }

    }//GEN-LAST:event_addPayButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        teachTabbedPane.setSelectedIndex(1); // SWITCH PANELS
        payTable.getSelectionModel().clearSelection();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        teachTabbedPane.setSelectedIndex(0); // SWITCH PANELS
        payTable.getSelectionModel().clearSelection();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void addNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNoneButtonActionPerformed
        int row1 = payTable.getSelectedRow();

        // ADD INFORMATION TO THE DB
        String monthly = monthlyLabel.getText();
        String overtime = overtimeLabel.getText();
        String absent = absentLabel.getText();

        if (row1 < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row!");
        } else if ("Teaching".equals(duplication)) {
            JOptionPane.showMessageDialog(this, "Only for Non-Teaching employees");
        } else {
            if (monthly.isEmpty() || overtime.isEmpty() || absent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all fields!");
            } else {
                try {

                    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

                    salaryNoneTeaching = 0;
                    salaryNone();
                    allowanceLabel.setText("");
                    overtimePayLabel.setText("");
                    salaryNoneField.setText("");
                    salaryNoneField.setText(decimalFormat.format(salaryNoneTeaching));
                    allowanceLabel.setText(decimalFormat.format(noneAllowance));
                    overtimePayLabel.setText(decimalFormat.format(overtimePay));

                    double salary = salaryNoneTeaching;
                    String payid = payidLabel.getText();

                    pst = con.prepareStatement("UPDATE payroll SET salary = ? WHERE payid = ?"); // command to query

                    pst.setDouble(1, salary);
                    pst.setString(2, payid);

                    int temp1 = pst.executeUpdate();
                    showTablePay();
                    sumSalary();

                } catch (SQLException ex) {
                    Logger.getLogger(Dashboard.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }//GEN-LAST:event_addNoneButtonActionPerformed

    private void viewNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewNoneButtonActionPerformed
        showTablePay();
        autoPayID();
        sumSalary();
        payTable.getSelectionModel().clearSelection();
        // refreshes table
    }//GEN-LAST:event_viewNoneButtonActionPerformed

    private void deleteNoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNoneButtonActionPerformed
        int row1 = payTable.getSelectedRow();

        String empid = empidPayLabel.getText();
        String payid = payidLabel.getText();
        String firstname = firstPayLabel.getText();
        String lastname = lastPayLabel.getText();
        String position = (String) positionPayComboBox.getSelectedItem();
        double salary = Double.parseDouble(salaryField.getText());

        if (row1 < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row", "Error", JOptionPane.PLAIN_MESSAGE);
        } else {
            try {
                pst = con.prepareStatement("DELETE FROM payroll WHERE payid = ? AND firstname = ? and lastname = ? AND position =  ? AND salary = ?");
                DefaultTableModel model = (DefaultTableModel) payTable.getModel();

                model.removeRow(row1);
//                pst.setString(1, empid);
                pst.setString(1, payid);
                pst.setString(2, firstname);
                pst.setString(3, lastname);
                pst.setString(4, position);
                pst.setDouble(5, salary);

                int temp1 = pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted Successfully!");
                showTablePay();
                autoPayID();
                sumSalary();

            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_deleteNoneButtonActionPerformed

    private void absentLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_absentLabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_absentLabelActionPerformed

    private void allowanceCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowanceCheckActionPerformed
        if (allowanceCheck.isSelected()) { 
            allowanceSet.setVisible(true);
        } else {
            allowanceSet.setVisible(false);
        }
    }//GEN-LAST:event_allowanceCheckActionPerformed

    private void allowanceLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowanceLabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_allowanceLabelActionPerformed

    private void searchField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchField1KeyPressed

    }//GEN-LAST:event_searchField1KeyPressed

    private void searchField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchField1MouseClicked
        searchField1.setText("");
    }//GEN-LAST:event_searchField1MouseClicked

    private void searchField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchField1ActionPerformed

    private void searchFieldMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldMousePressed

    private void searchField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchField1KeyReleased
        DefaultTableModel model = (DefaultTableModel) payTable.getModel();
        TableRowSorter<DefaultTableModel> model1 = new TableRowSorter<>(model);
        payTable.setRowSorter(model1);
        model1.setRowFilter(RowFilter.regexFilter("(?i)" + searchField1.getText())); // will search regardless if uppercase or not
    }//GEN-LAST:event_searchField1KeyReleased

    private void recordsLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recordsLabelMouseClicked
        jTabbedPane.setSelectedIndex(2);
        empTable.getSelectionModel().clearSelection();
    }//GEN-LAST:event_recordsLabelMouseClicked

    private void searchField2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchField2MouseClicked
        searchField2.setText("");
    }//GEN-LAST:event_searchField2MouseClicked

    private void searchField2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchField2MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_searchField2MouseExited

    private void searchField2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchField2MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchField2MousePressed

    private void searchField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchField2ActionPerformed

    private void searchField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchField2KeyReleased
        DefaultTableModel model = (DefaultTableModel) recordTable.getModel();
        TableRowSorter<DefaultTableModel> model1 = new TableRowSorter<>(model);
        recordTable.setRowSorter(model1);
        model1.setRowFilter(RowFilter.regexFilter("(?i)" + searchField2.getText()));
    }//GEN-LAST:event_searchField2KeyReleased

    private void showButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showButtonActionPerformed
        recordTable();
    }//GEN-LAST:event_showButtonActionPerformed

    private void empTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_empTableMouseClicked
        DefaultTableModel model = (DefaultTableModel) empTable.getModel();

        // storing to string when table row is selected
        String empid = model.getValueAt(empTable.getSelectedRow(), 0).toString();
        String firstname = model.getValueAt(empTable.getSelectedRow(), 1).toString();
        String lastname = model.getValueAt(empTable.getSelectedRow(), 2).toString();
        String department = model.getValueAt(empTable.getSelectedRow(), 3).toString();
        String position = model.getValueAt(empTable.getSelectedRow(), 4).toString();
        duplication = model.getValueAt(empTable.getSelectedRow(), 0).toString();

        //set to textField
        empidLabel.setText(empid);
        firstLabel.setText(firstname);
        lastLabel.setText(lastname);
        positionComboBox.setSelectedItem(position);
        departmentLabel.setSelectedItem(department);


    }//GEN-LAST:event_empTableMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        MessageFormat header = new MessageFormat("Payroll Reports");
        MessageFormat footer = new MessageFormat("Page {0, number, integer}");

        try {
            recordTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Print Error");
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);

        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void searchField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchField2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchField2KeyPressed

    private void allowanceSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowanceSetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_allowanceSetActionPerformed

    private void allowanceSetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_allowanceSetMouseClicked
        allowanceSet.setText("");
    }//GEN-LAST:event_allowanceSetMouseClicked

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField absentField;
    private javax.swing.JTextField absentLabel;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addNoneButton;
    private javax.swing.JButton addPayButton;
    private javax.swing.JButton addToPayrollButton;
    private javax.swing.JCheckBox allowanceCheck;
    private javax.swing.JTextField allowanceField;
    private javax.swing.JTextField allowanceLabel;
    private javax.swing.JTextField allowanceSet;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteNoneButton;
    private javax.swing.JButton deletePayButton;
    private javax.swing.JComboBox<String> departmentLabel;
    private javax.swing.JLabel duplicateLabel;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel empLabel;
    private javax.swing.JPanel empPanel;
    private javax.swing.JTable empTable;
    private javax.swing.JTextField empidLabel;
    private javax.swing.JLabel empidPayLabel;
    private javax.swing.JTextField firstLabel;
    private javax.swing.JLabel firstPayLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField lastLabel;
    private javax.swing.JLabel lastPayLabel;
    private javax.swing.JTextField monthlyLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.ButtonGroup noneButtonGroup;
    private javax.swing.JPanel nonePanel;
    private javax.swing.JTextField overloadField;
    private javax.swing.JTextField overtimeLabel;
    private javax.swing.JTextField overtimePayLabel;
    private javax.swing.ButtonGroup payButtonGroup;
    private javax.swing.JLabel payLabel;
    private javax.swing.JPanel payPanel;
    private javax.swing.JTable payTable;
    private javax.swing.JLabel payidLabel;
    private javax.swing.JTextField payrateField;
    private javax.swing.JComboBox<String> positionComboBox;
    private javax.swing.JComboBox<String> positionPayComboBox;
    private javax.swing.JTable recordTable;
    private javax.swing.JLabel recordsLabel;
    private javax.swing.JPanel recordsPanel;
    private javax.swing.JLabel salaryField;
    private javax.swing.JTextField salaryNoneField;
    private javax.swing.JTextField searchField;
    private javax.swing.JTextField searchField1;
    private javax.swing.JTextField searchField2;
    private javax.swing.JButton showButton;
    private javax.swing.JLabel sumLabel;
    private javax.swing.JTabbedPane teachTabbedPane;
    private javax.swing.JPanel teaching;
    private javax.swing.JTextField teachingField;
    private javax.swing.JTextField totalPayField;
    private javax.swing.JButton viewButton;
    private javax.swing.JButton viewNoneButton;
    private javax.swing.JButton viewPayButton;
    // End of variables declaration//GEN-END:variables
}
