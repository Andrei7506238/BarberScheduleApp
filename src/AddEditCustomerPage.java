import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class AddEditCustomerPage extends JFrame implements ActionListener {
    private Customer customer;

    private JTextField txfFirstName, txfLastName, txfUsername, txfTel, txfNote;

    private JButton btnConfirmation;
    private JButton btnCancel;
    private JButton btnGenerateUsername;

    private boolean objectContructionFinished=false;

    public AddEditCustomerPage(int idCustomer){
        super("Customer");

        setSize(513,433);
        setResizable(false);
        setLayout(null);

        if(idCustomer == -1){
            customer = new Customer();
        }
        else{
            customer = DBManger.getCustomerById(idCustomer);

            if(customer == null){
                JOptionPane.showMessageDialog(this, Language.get("EC_SUPER_TITLE"));
                dispose();
                return;
            }
        }

        JPanel frame = new JPanel();
        frame.setLayout(new GridLayout(5,2, 5,5));
        frame.setLocation(20, 10);
        frame.setSize(460, 200);

        JLabel lblFirstName = new JLabel(Language.get("EC_LBL_FNAME"));
        frame.add(lblFirstName);
        txfFirstName = new JTextField();
        txfFirstName.addActionListener(this);
        frame.add(txfFirstName);

        JLabel lblLastName = new JLabel(Language.get("EC_LBL_LNAME"));
        frame.add(lblLastName);
        txfLastName = new JTextField();
        txfLastName.addActionListener(this);
        frame.add(txfLastName);

        JPanel pnlUsername = new JPanel();
        pnlUsername.setLayout(new GridLayout(1,2,20,20));
        JLabel lblUsername = new JLabel(Language.get("EC_LBL_USERNAME"));
        pnlUsername.add(lblUsername);
        btnGenerateUsername = new JButton(Language.get("EC_BTN_GEN"));
        btnGenerateUsername.addActionListener(this);
        pnlUsername.add(btnGenerateUsername);
        frame.add(pnlUsername);
        txfUsername = new JTextField();
        txfUsername.addActionListener(this);
        frame.add(txfUsername);

        JLabel lblTel = new JLabel(Language.get("EC_LBL_TEL"));
        frame.add(lblTel);
        txfTel = new JTextField();
        txfTel.addActionListener(this);
        frame.add(txfTel);

        JLabel lblNote = new JLabel(Language.get("EC_LBL_NOTE"));
        frame.add(lblNote);
        txfNote = new JTextField();
        txfNote.addActionListener(this);
        frame.add(txfNote);

        add(frame);
        if(idCustomer!=-1){
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

        btnCancel = new JButton(Language.get("EC_BTN_CANCEL"));
        btnCancel.addActionListener(this);
        pnlButtons.add(btnCancel);

        btnConfirmation = new JButton(Language.get("EC_BTN_OK"));
        btnConfirmation.addActionListener(this);
        pnlButtons.add(btnConfirmation);

        add(pnlButtons);
    }

    private void populateFieldsOnUpdate(){
        txfFirstName.setText(customer.getFirstName());
        txfLastName.setText(customer.getLastName());
        txfUsername.setText(customer.getUsername());
        txfTel.setText(customer.getTel());
        txfNote.setText(customer.getInfo());
    }

    void updateInMemoryCustomer(){
        customer.setFirstName(txfFirstName.getText());
        customer.setLastName(txfLastName.getText());
        customer.setUsername(txfUsername.getText());
        customer.setTel(txfTel.getText());
        customer.setInfo(txfNote.getText());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //Skip all actions triggered by object constuction
        if(!objectContructionFinished)
            return;

        updateInMemoryCustomer();

        if(e.getSource() == btnCancel){
            dispose();
        }

        if(e.getSource() == btnConfirmation){
            if(validateErrors())
                return;

            boolean success = DBManger.insertOrUpdateCustomer(customer);
            if(success){
                JOptionPane.showMessageDialog(this, Language.get("EC_MSG_DELETE_SUCCES"));
                dispose();
            }
            else {
                JOptionPane.showMessageDialog(this, Language.get("EC_MSG_DELETE_FAIL"));
            }
        }

        if(e.getSource() == btnGenerateUsername){
            txfUsername.setText(generateUsername());
        }
    }

    private String generateUsername(){
        String generated = txfFirstName.getText() + txfLastName.getText();
        Customer other = DBManger.getCustomerByUsername(generated);
        if(other==null) {
            return generated;
        }

        while (true){
            Random rng = new Random();
            generated = txfFirstName.getText() + txfLastName.getText() + (rng.nextInt()%10000);
            other = DBManger.getCustomerByUsername(generated);
            if(other==null) {
                return generated;
            }
        }
    }


    private boolean validateErrors(){
        Customer other = DBManger.getCustomerByUsername(txfUsername.getText());
        if(other!=null && other.getId() != customer.getId()){
            JOptionPane.showMessageDialog(this, Language.get("EC_ERR_ALREADYTAKEN"));
            return true;
        }
        return false;
    }

}
