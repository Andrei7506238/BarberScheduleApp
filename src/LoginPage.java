import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame implements ActionListener {
    private JTextField txfUsername = null;
    private JPasswordField psfPassword = null;
    JButton btnLogin;

    public LoginPage(){
        super(Language.get("LP_SUPER_TITLE"));

        setSize(413,400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        //Image
        ImageIcon imgBanner = new ImageIcon(Language.get("LP_BANNER_PATH"));
        Image image = imgBanner.getImage(); // transform it
        Image newimg = image.getScaledInstance(400, 150,  java.awt.Image.SCALE_SMOOTH);
        JLabel lblImage = new JLabel();
        lblImage.setIcon(new ImageIcon(newimg));
        lblImage.setVerticalTextPosition(SwingConstants.TOP);
        lblImage.setSize(new Dimension(400, 150));
        lblImage.setLocation(0,0);
        add(lblImage);

        //Fields
        JPanel pnlLogin = new JPanel(new GridLayout(2,2, 10, 10));
        pnlLogin.setLocation(50, 200);
        pnlLogin.setSize(300, 50);

        JLabel lblUsername = new JLabel(Language.get("LP_LBL_UNAME"), JLabel.RIGHT);
        pnlLogin.add(lblUsername);

        txfUsername = new JTextField();
        pnlLogin.add(txfUsername);

        JLabel lblPassword = new JLabel(Language.get("LP_LBL_UNAME"), JLabel.RIGHT);
        pnlLogin.add(lblPassword);

        psfPassword = new JPasswordField();
        psfPassword.addActionListener(this);
        pnlLogin.add(psfPassword);

        add(pnlLogin);


        //Login Button
        btnLogin = new JButton(Language.get("LP_BTN_LOGIN"));
        btnLogin.setLocation(150, 300);
        btnLogin.setSize(100, 25);
        btnLogin.addActionListener(this);

        add(btnLogin);

        //Set visible
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnLogin || e.getSource() == psfPassword){
            Staff staff = DBManger.login(txfUsername.getText(), psfPassword.getText());

            if(staff == null){
                JOptionPane.showMessageDialog(this, Language.get("LP_ERR_WRNGCRED_CONTENT"), Language.get("LP_ERR_WRNGCRED_TITLE"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            MainPage mainPage = new MainPage(staff);
            mainPage.setVisible(true);
            setVisible(false);
        }
    }
}
