import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddEditServicePage extends JFrame implements ActionListener {
    private BarberService service;

    private JTextField txfName, txfPrice, txfDuration;

    private JButton btnConfirmation;
    private JButton btnCancel;

    private boolean objectContructionFinished=false;

    public AddEditServicePage(int idService){
        super(Language.get("EB_SUPER_TITLE"));

        setSize(513,433);
        setResizable(false);
        setLayout(null);

        if(idService == -1){
            service = new BarberService();
        }
        else{
            service = DBManger.getServiceById(idService);

            if(service == null){
                JOptionPane.showMessageDialog(this, Language.get("EB_ERR_SRVNOTEXIST"));
                dispose();
                return;
            }
        }

        JPanel frame = new JPanel();
        frame.setLayout(new GridLayout(5,2, 5,5));
        frame.setLocation(20, 10);
        frame.setSize(460, 200);

        JLabel lblName = new JLabel(Language.get("EB_LBL_NAME"));
        frame.add(lblName);
        txfName = new JTextField();
        txfName.addActionListener(this);
        frame.add(txfName);

        JLabel lblPrice = new JLabel(Language.get("EB_LBL_PRICE"));
        frame.add(lblPrice);
        txfPrice = new JTextField();
        txfPrice.addActionListener(this);
        frame.add(txfPrice);

        JLabel lblDuration = new JLabel(Language.get("EB_LBL_DURATION"));
        frame.add(lblDuration);
        txfDuration = new JTextField();
        txfDuration.addActionListener(this);
        frame.add(txfDuration);

        add(frame);
        if(idService!=-1){
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

        btnCancel = new JButton(Language.get("EB_BTN_CANCEL"));
        btnCancel.addActionListener(this);
        pnlButtons.add(btnCancel);

        btnConfirmation = new JButton(Language.get("EB_BTN_OK"));
        btnConfirmation.addActionListener(this);
        pnlButtons.add(btnConfirmation);

        add(pnlButtons);
    }

    private void populateFieldsOnUpdate(){
        txfName.setText(service.getName());
        txfPrice.setText( String.valueOf(service.getPrice()));
        txfDuration.setText(String.valueOf(service.getMinutes()));
    }

    void updateInMemoryService(){
        service.setName(txfName.getText());
        service.setPrice(Double.parseDouble(txfPrice.getText()));
        service.setMinutes(Integer.parseInt(txfDuration.getText()));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //Skip all actions triggered by object constuction
        if(!objectContructionFinished)
            return;

        try{
            updateInMemoryService();
        } catch (Exception ex){
            //Nothing
        }

        if(e.getSource() == btnCancel){
            dispose();
        }

        if(e.getSource() == btnConfirmation){
            if(validateErrors())
                return;

            boolean success = DBManger.insertOrUpdateService(service);
            if(success){
                JOptionPane.showMessageDialog(this, Language.get("EB_MSG_DELETE_SUCCES"));
                dispose();
            }
            else {
                JOptionPane.showMessageDialog(this, Language.get("EB_MSG_DELETE_FAIL"));
            }
        }
    }

    private boolean validateErrors(){
        BarberService other = DBManger.getServiceByName(txfName.getText());
        if(other!=null && other.getId_service() != service.getId_service()){
            JOptionPane.showMessageDialog(this, Language.get("EB_ERR_TAKEN"));
            return true;
        }

        //Validate duration
        try{
            int duration = Integer.parseInt(txfDuration.getText());

            if(duration < 0) {
                JOptionPane.showMessageDialog(this, Language.get("EB_ERR_DURATION_NEGATIVE"));
                return true;
            }

        } catch (Exception ex){
            JOptionPane.showMessageDialog(this, Language.get("EB_ERR_DURATION_INVALID"));
            return true;
        }

        //Validate price
        try{
            double price = Double.parseDouble(txfPrice.getText());

            if(price < 0) {
                JOptionPane.showMessageDialog(this, Language.get("EB_ERR_PRICE_NEGATIVE"));
                return true;
            }
        } catch (Exception ex){
            JOptionPane.showMessageDialog(this, Language.get("EB_ERR_PRICE_INVALID"));
            return true;
        }
        return false;
    }
}
