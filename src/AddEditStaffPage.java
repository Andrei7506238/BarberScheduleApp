import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class AddEditStaffPage extends JFrame implements ActionListener {
    private Staff staff;

    private JTextField txfFirstName, txfLastName, txfUsername, txfTel, txfNote;
    private JPasswordField psfPassword;

    private JButton btnConfirmation;
    private JButton btnCancel;
    private JButton btnGenerateUsername;

    private boolean objectContructionFinished=false;

    public AddEditStaffPage(int idStaff){
        super(Language.get("ES_SUPER_TITLE"));

        setSize(513,433);
        setResizable(false);
        setLayout(null);

        if(idStaff == -1){
            staff = new Staff();
        }
        else{
            staff = DBManger.getStaffById(idStaff);
        }

        JPanel frame = new JPanel();
        frame.setLayout(new GridLayout(6,2, 5,5));
        frame.setLocation(20, 10);
        frame.setSize(460, 200);

        JLabel lblFirstName = new JLabel(Language.get("ES_LBL_FNAME"));
        frame.add(lblFirstName);
        txfFirstName = new JTextField();
        txfFirstName.addActionListener(this);
        frame.add(txfFirstName);

        JLabel lblLastName = new JLabel(Language.get("ES_LBL_LNAME"));
        frame.add(lblLastName);
        txfLastName = new JTextField();
        txfLastName.addActionListener(this);
        frame.add(txfLastName);

        JPanel pnlUsername = new JPanel();
        pnlUsername.setLayout(new GridLayout(1,2,20,20));
        JLabel lblUsername = new JLabel(Language.get("ES_LBL_USERNAME"));
        pnlUsername.add(lblUsername);
        btnGenerateUsername = new JButton(Language.get("ES_BTN_GEN"));
        btnGenerateUsername.addActionListener(this);
        pnlUsername.add(btnGenerateUsername);
        frame.add(pnlUsername);
        txfUsername = new JTextField();
        txfUsername.addActionListener(this);
        frame.add(txfUsername);

        JLabel lblDate = new JLabel(Language.get("ES_LBL_TEL"));
        frame.add(lblDate);
        txfTel = new JTextField();
        txfTel.addActionListener(this);
        frame.add(txfTel);

        JLabel lblNote = new JLabel(Language.get("ES_LBL_NOTE"));
        frame.add(lblNote);
        txfNote = new JTextField();
        txfNote.addActionListener(this);
        frame.add(txfNote);

        JLabel lblPassword = new JLabel(Language.get("ES_LBL_PASSWORD"));
        frame.add(lblPassword);
        psfPassword = new JPasswordField();
        psfPassword.addActionListener(this);
        frame.add(psfPassword);

        add(frame);
        if(idStaff!=-1){
            populateFieldsOnUpdate();
        }

        createButtons();
        objectContructionFinished = true;
    }

    private void createButtons(){
        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new GridLayout(1,3, 50, 10));
        pnlButtons.setLocation(20, 355);
        pnlButtons.setSize(460, 20);

        btnCancel = new JButton(Language.get("ES_BTN_CANCEL"));
        btnCancel.addActionListener(this);
        pnlButtons.add(btnCancel);

        btnConfirmation = new JButton(Language.get("ES_BTN_OK"));
        btnConfirmation.addActionListener(this);
        pnlButtons.add(btnConfirmation);

        add(pnlButtons);
    }

    private void populateFieldsOnUpdate(){
        txfFirstName.setText(staff.getFirstName());
        txfLastName.setText(staff.getLastName());
        txfUsername.setText(staff.getUsername());
        txfTel.setText(staff.getTel());
        txfNote.setText(staff.getInfo());
    }

    void updateInMemoryStaff(){
        staff.setFirstName(txfFirstName.getText());
        staff.setLastName(txfLastName.getText());
        staff.setUsername(txfUsername.getText());
        staff.setTel(txfTel.getText());
        staff.setInfo(txfNote.getText());
        staff.setPassword(psfPassword.getText());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //Skip all actions triggered by object constuction
        if(!objectContructionFinished)
            return;

        updateInMemoryStaff();

        if(e.getSource() == btnCancel){
            dispose();
        }

        if(e.getSource() == btnConfirmation){
            if(validateErrors())
                return;

            boolean success = DBManger.insertOrUpdateStaff(staff);
            if(success){
                JOptionPane.showMessageDialog(this, Language.get("ES_MSG_DELETE_SUCCES"));
                dispose();
            }
            else {
                JOptionPane.showMessageDialog(this, Language.get("ES_MSG_DELETE_FAIL"));
            }
        }

        if(e.getSource() == btnGenerateUsername){
            txfUsername.setText(generateUsername());
        }
    }

    private String generateUsername(){
        String generated = txfFirstName.getText() + txfLastName.getText();
        Staff other = DBManger.getStaffByUsername(generated);
        if(other==null) {
            return generated;
        }

        while (true){
            Random rng = new Random();
            generated = txfFirstName.getText() + txfLastName.getText() + (rng.nextInt()%10000);
            other = DBManger.getStaffByUsername(generated);
            if(other==null) {
                return generated;
            }
        }
    }


    private boolean validateErrors(){
        Staff other = DBManger.getStaffByUsername(txfUsername.getText());
        if(other!=null && other.getId() != staff.getId()){
            JOptionPane.showMessageDialog(this, Language.get("ES_ERR_ALREADYTAKEN"));
            return true;
        }

        String password = psfPassword.getText();
        if(password != null && password.length() > 0){
            if(password.length()<6) {
                JOptionPane.showMessageDialog(this, Language.get("ES_ERR_PASSSIMPLE"));
                return true;
            }
        }
        return false;
    }
}