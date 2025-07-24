/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class game1 extends javax.swing.JFrame {
    private int currentStage = 0;
    private int score = 0;

    private final String[] images = { 
        "/javaapplication3/FirstRound1.png", "/javaapplication3/FirstRound2.png", 
        "/javaapplication3/FirstRound1.png", "/javaapplication3/FirstRound2.png", 
        "/javaapplication3/FirstRound1.png", "/javaapplication3/FirstRound2.png" 
    };

    private final int[] stageScores = { 5, 10, 15 };

    public game1() {
        initComponents();
        loadImagesForStage();
    }

    private void loadImagesForStage() {
        jLabel1.setIcon(new ImageIcon(getClass().getResource(images[currentStage * 2])));
        jLabel3.setIcon(new ImageIcon(getClass().getResource(images[currentStage * 2 + 1])));
    }

    private void proceedToNextStage() {
        if (currentStage < 2) {
            currentStage++;
            loadImagesForStage();
        } else {
            JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + score);
            currentStage = 0; // إعادة ضبط المرحلة
            score = 0; // إعادة ضبط النقاط
            loadImagesForStage();
            jTextField1.setText("Scores: " + score); // تحديث عرض النقاط
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(153, 153, 255));
        jTextField1.setFont(new java.awt.Font("Tempus Sans ITC", 1, 18));
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));
        jTextField1.setText("Scores :");
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 130, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource(images[0])));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 140, 220, 250));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource(images[1])));
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, 190, 260));

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 460, 80, 30));

        jButton1.setBackground(new java.awt.Color(221, 221, 252));
        jButton1.setFont(new java.awt.Font("Tempus Sans ITC", 1, 19));
        jButton1.setForeground(new java.awt.Color(102, 102, 255));
        jButton1.setText("Submit");
        jButton1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 490, 100, 30));

        jLabel4.setFont(new java.awt.Font("Tempus Sans ITC", 1, 22));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Enter the number of difference in two pictures: ");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 470, 30));

        jButton2.setBackground(new java.awt.Color(221, 221, 252));
        jButton2.setFont(new java.awt.Font("Tempus Sans ITC", 1, 19));
        jButton2.setForeground(new java.awt.Color(102, 102, 255));
        jButton2.setText("Exit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.exit(0);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 490, 100, 30));

        jTextField2.setEditable(false);
        jTextField2.setBackground(new java.awt.Color(153, 153, 255));
        jTextField2.setFont(new java.awt.Font("Tempus Sans ITC", 1, 19));
        jTextField2.setForeground(new java.awt.Color(153, 153, 255));
        jTextField2.setText("Enter number of differences in two pictures:");
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 450, 580, 80));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaapplication3/WalGame.jpg")));
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -10, 590, 540));

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String answer = jTextField3.getText();

        // تحقق من الإجابة الصحيحة (يمكنك تعديل هذه القيم حسب الفروق الفعلية)
        String correctAnswer = currentStage == 0 ? "3" : currentStage == 1 ? "4" : "5"; 

        if (answer.equals(correctAnswer)) {
            score += stageScores[currentStage]; // إضافة النقاط بناءً على المرحلة الحالية
            jTextField1.setText("Scores: " + score); // تحديث عرض النقاط
            JOptionPane.showMessageDialog(this, "Correct Answer! + " + stageScores[currentStage] + " points.");
        } else {
            JOptionPane.showMessageDialog(this, "Wrong Answer.");
        }

        proceedToNextStage(); // الانتقال إلى المرحلة التالية
        jTextField3.setText(""); // تفريغ الحقل النصي للإجابة
    }

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {
        // هنا يمكنك إضافة أي معالجة عند الضغط على Enter في jTextField3
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(game1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new game1().setVisible(true);
            }
        });
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
}
