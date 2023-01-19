import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class AddEditAppointmentPage extends JFrame implements ActionListener {
    private Appointment appointment;

    private JComboBox<Customer> cmbCustomer;
    private JComboBox<BarberService> cmbService;
    private JComboBox<Staff> cmbStaff;
    private JTextField txfDate;
    private JComboBox<Integer> cmbStartHour, cmbStartMin;
    private JTextField txfDuration;
    private JTextField txfPrice;
    private JTextArea infoStatus;
    private JTextField txfNote;

    private ArrayList<Customer> customers;
    private ArrayList<BarberService> services;
    private ArrayList<Staff> staffs;

    private JButton btnConfirmation;
    private JButton btnCancel;
    private JButton btnCheck;

    private boolean objectContructionFinished=false;

    public AddEditAppointmentPage(int idAppointment){
        this(idAppointment, LocalDate.now().toString(), null);
    }

    public AddEditAppointmentPage(int idAppointment, String date, Staff schStaff){
        super(Language.get("EA_SUPER_TITLE"));

        setSize(513,433);
        setResizable(false);
        setLayout(null);

        if(idAppointment == -1){
            appointment = new Appointment();
        }
        else{
            appointment = DBManger.getAppointmentById(idAppointment);

            if(appointment == null){
                JOptionPane.showMessageDialog(this, Language.get("EA_ERR_APPNOTEXIST"));
                dispose();
                return;
            }
        }

        JPanel frame = new JPanel();
        frame.setLayout(new GridLayout(8,2, 5,5));
        frame.setLocation(20, 10);
        frame.setSize(460, 200);

        //Select Customer #1
        JLabel lblCustomer = new JLabel(Language.get("EA_LBL_CUSTOMER"));
        frame.add(lblCustomer);

        customers = DBManger.getAllCustomer();
        cmbCustomer = new JComboBox(customers.toArray());
        cmbCustomer.addActionListener(this);
        AutoCompleteDecorator.decorate(cmbCustomer);
        frame.add(cmbCustomer);


        //Select Service #2
        JLabel lblService = new JLabel(Language.get("EA_LBL_SERVICE"));
        frame.add(lblService);

        services = DBManger.getAllServices();
        cmbService = new JComboBox(services.toArray());
        cmbService.addActionListener(this);
        AutoCompleteDecorator.decorate(cmbService);
        frame.add(cmbService);


        //Select staff #3
        JLabel lblStaff = new JLabel(Language.get("EA_LBL_STAFF"));
        frame.add(lblStaff);

        staffs = DBManger.getAllStaff();
        cmbStaff = new JComboBox(staffs.toArray());
        cmbStaff.addActionListener(this);
        AutoCompleteDecorator.decorate(cmbStaff);
        frame.add(cmbStaff);

        //If Add Appointment called from SchedulePage
        if(schStaff != null && idAppointment == -1){
            for(int i=0; i< staffs.size(); i++){
                if(staffs.get(i).getId() == schStaff.getId()){
                    cmbStaff.setSelectedIndex(i);
                    break;
                }
            }
        }

        //Date #4
        JLabel lblDate = new JLabel(Language.get("EA_LBL_DATE"));
        frame.add(lblDate);

        txfDate = new JTextField(date);
        txfDate.addActionListener(this);
        frame.add(txfDate);

        //Schedule Hour #5
        JLabel lblHour = new JLabel(Language.get("EA_LBL_HOUR"));
        frame.add(lblHour);

        JPanel hourFrame = new JPanel(new GridLayout(1,2,10,10));
        String[] hours = new String[24];
        for(int i=0; i<24;i++){
            hours[i] = Utils.formatNumberTwoDigits(i);
        }
        cmbStartHour = new JComboBox(hours);
        cmbStartHour.addActionListener(this);
        hourFrame.add(cmbStartHour);

        String[] minutes = new String[12];
        for(int i=0; i<12;i++)
            minutes[i] = Utils.formatNumberTwoDigits(5*i);
        cmbStartMin = new JComboBox(minutes);
        cmbStartMin.addActionListener(this);
        hourFrame.add(cmbStartMin);
        frame.add(hourFrame);

        //Time Duration #6
        JLabel lblDuration = new JLabel(Language.get("EA_LBL_DURATION"));
        frame.add(lblDuration);

        txfDuration = new JTextField();
        txfDuration.addActionListener(this);
        frame.add(txfDuration);

        //Price #7
        JLabel lblPrice = new JLabel(Language.get("EA_LBL_PRICE"));
        frame.add(lblPrice);

        txfPrice = new JTextField();
        txfPrice.addActionListener(this);
        frame.add(txfPrice);

        //Info #8
        JLabel lblNote = new JLabel(Language.get("EA_LBL_NOTE"));
        frame.add(lblNote);

        txfNote = new JTextField();
        txfNote.addActionListener(this);
        frame.add(txfNote);

        //Status
        infoStatus = new JTextArea(Language.get("EA_TXA_STATUS"));
        infoStatus.setEditable(false);
        infoStatus.setLocation(20, 225);
        infoStatus.setSize(460, 100);
        add(infoStatus);

        add(frame);

        if(idAppointment!=-1){
            populateFiledsOnUpdate();
        }else{
            updatePriceAndDurationAfterService();
            cmbStartHour.setSelectedIndex(LocalTime.now().getHour());
            int minute = (int) Math.ceil(LocalTime.now().getMinute()/5.0);
            if(minute==12)
                minute = minute-1;
            cmbStartMin.setSelectedIndex(minute);
            updateInMemoryAppointment();
        }

        createButtons();
        objectContructionFinished = true;
    }

    private void createButtons(){
        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new GridLayout(1,3, 10, 10));
        pnlButtons.setLocation(20, 355);
        pnlButtons.setSize(460, 20);

        btnCancel = new JButton(Language.get("EA_BTN_CANCEL"));
        btnCancel.addActionListener(this);
        pnlButtons.add(btnCancel);

        btnCheck = new JButton(Language.get("EA_BTN_CHECK"));
        btnCheck.addActionListener(this);
        pnlButtons.add(btnCheck);

        btnConfirmation = new JButton(Language.get("EA_BTN_OK"));
        btnConfirmation.addActionListener(this);
        pnlButtons.add(btnConfirmation);

        add(pnlButtons);
    }

    private void populateFiledsOnUpdate(){
        //Customer
        int position = 0;
        for(int i=0; i<customers.size(); i++){
            if(customers.get(i).getId() == appointment.getIdCustomer())
                position = i;
        }
        cmbCustomer.setSelectedIndex(position);

        //Service
        position = 0;
        for(int i=0; i<services.size(); i++){
            if(services.get(i).getId_service() == appointment.getIdService())
                position = i;
        }
        cmbService.setSelectedIndex(position);

        //Staff
        position = 0;
        for(int i=0; i<staffs.size(); i++){
            if(staffs.get(i).getId() == appointment.getIdStaff()){
                position = i;
            }
        }
        cmbStaff.setSelectedIndex(position);

        //Date
        txfDate.setText(appointment.getDatea().toString());

        //Stat Time
        cmbStartHour.setSelectedIndex(appointment.getStartHour());
        cmbStartMin.setSelectedIndex(appointment.getStartMin()/5);

        //Duration
        LocalTime timeStart = LocalTime.parse(Utils.formatNumberTwoDigits(appointment.getStartHour()) + ":" + Utils.formatNumberTwoDigits(appointment.getStartMin()));
        LocalTime timeEnd = LocalTime.parse(Utils.formatNumberTwoDigits(appointment.getEndHour()) + ":" + Utils.formatNumberTwoDigits(appointment.getEndMin()));
        long duration = Duration.between(timeStart, timeEnd).toMinutes();
        if(duration < 0) duration = duration+1440;
        txfDuration.setText(Long.toString(duration));


        //Price
        txfPrice.setText(Double.toString(appointment.getPrice()));

        //Note
        txfNote.setText(appointment.getInfo());

        //Status
        updateStatus();
    }

    void updateInMemoryAppointment(){
        appointment.setIdCustomer(findCustomer().getId());
        appointment.setIdService(findService().getId_service());
        appointment.setIdStaff(findStaff().getId());
        appointment.setDatea(Date.valueOf(txfDate.getText()));

        int sh, sm, eh, em, delta;
        sh = cmbStartHour.getSelectedIndex();
        sm = cmbStartMin.getSelectedIndex()*5;
        delta = Integer.parseInt(txfDuration.getText());

        LocalTime startTime = LocalTime.parse(Utils.formatNumberTwoDigits(sh) + ":" + Utils.formatNumberTwoDigits(sm) + ":00");
        startTime = startTime.plusMinutes(delta);

        eh = startTime.getHour();
        em = startTime.getMinute();

        appointment.setStartHour(sh);
        appointment.setStartMin(sm);
        appointment.setEndHour(eh);
        appointment.setEndMin(em);

        appointment.setPrice(Double.parseDouble(txfPrice.getText()));
        appointment.setInfo(txfNote.getText());
    }

    void updateStatus(){
        String statusStr = Language.get("EA_TXA_STATUS") + Language.get("EA_TXA_TIMEFRAME") + Utils.getTimeFrame(appointment.getStartHour(), appointment.getStartMin(), appointment.getEndHour(), appointment.getEndMin()) +
                Language.get("EA_TXA_WORKDAY") + Utils.getWorkdayStr(appointment.getDatea().toString())+
                Language.get("EA_TXA_CUSTOMER") + findCustomer().getUsername() + Language.get("EA_TXA_TEL") + findCustomer().getTel();

        infoStatus.setText(statusStr);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Skip all actions triggered by object constuction
        if(!objectContructionFinished)
            return;

        if(e.getSource() == cmbService){
            updatePriceAndDurationAfterService();
        }

        //Try to update inMemoryAppointment, regrardless of what action was performed
        try {
            updateInMemoryAppointment();
            updateStatus();
        }
        catch (Exception ex){
            //Do nothing
        }

        if(e.getSource() == btnCancel){
            dispose();
        }

        if(e.getSource() == btnCheck){
            if(validateErrors())
                return;
            validateWarnings();
        }

        if(e.getSource() == btnConfirmation){
            if(validateErrors())
                return;
            if(validateWarnings())
                return;

            if(!DBManger.insertOrUpdateAppointment(appointment)){
                JOptionPane.showMessageDialog(null, Language.get("EA_MSG_DELETE_FAIL"));
            }
            else{
                JOptionPane.showMessageDialog(null, Language.get("EA_MSG_DELETE_SUCCES"));
                dispose();
            }
        }
    }

    private Customer findCustomer(){
        int selected = cmbCustomer.getSelectedIndex();
        return customers.get(selected);
    }

    private BarberService findService(){
        int selected = cmbService.getSelectedIndex();
        return services.get(selected);
    }

    private Staff findStaff(){
        int selected = cmbStaff.getSelectedIndex();
        return staffs.get(selected);
    }

    private void updatePriceAndDurationAfterService(){
        txfPrice.setText(Double.toString(findService().getPrice()));
        txfDuration.setText(Integer.toString(findService().getMinutes()));
    }

    private boolean validateWarnings(){
        Date date = Date.valueOf(txfDate.getText());
        if(!Utils.checkInWorkTimeTable(date, cmbStartHour.getSelectedIndex(), cmbStartMin.getSelectedIndex(), Integer.parseInt(txfDuration.getText()))){
            int confCode = JOptionPane.showConfirmDialog(null, Language.get("EA_WARN_OUTSIDESCH"));
            if(confCode!=0)
                return true;
        }

        Appointment intersectedAppointment = checkIfItInteresctsOtherAppointments();
        if(intersectedAppointment != null){
            int confCode = JOptionPane.showConfirmDialog(null, Language.get("EA_WARN_TIMEFRAME_BUSY")
                    + Utils.getTimeFrame(intersectedAppointment.getStartHour(), intersectedAppointment.getStartMin(), intersectedAppointment.getEndHour(), intersectedAppointment.getEndMin()));
            if(confCode!=0)
                return true;
        }

        Date currentDate = Date.valueOf(LocalDate.now());
        Date insertedDate = appointment.getDatea();
        if(insertedDate.before(currentDate)){
            int confCode = JOptionPane.showConfirmDialog(null, Language.get("EA_WARN_PAST"));
            if(confCode!=0)
                return true;
        }

        return false;
    }

    private Appointment checkIfItInteresctsOtherAppointments(){
        ArrayList<Appointment> appntsIntersect = DBManger.getAppointmentsIntersectTimeframe(appointment);
        if(appntsIntersect.size()==0)
            return null;
        if(appntsIntersect.size()==1 && appntsIntersect.get(0).getIdAppointment() == appointment.getIdAppointment())
            return null;

        if(appntsIntersect.get(0).getIdAppointment() != appointment.getIdAppointment())
            return appntsIntersect.get(0);
        return appntsIntersect.get(1);
    }

    private boolean validateErrors(){
        try{
            Customer selectedCustomer = findCustomer();
            Staff selectedStaff = findStaff();
            BarberService selectedService = findService();

            if(selectedService == null)
                throw new Exception("Invalid selected service");
            if(selectedCustomer == null)
                throw new Exception("Invalid selected customer");
            if(selectedStaff == null)
                throw new Exception("Invalid selected staff");
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, Language.get("EA_ERROR_INVALID_SELECTION") + ex.getMessage());
            return true;
        }

        try{
            Date date = Date.valueOf(txfDate.getText());
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, Language.get("EA_ERROR_INVALID_DATE"));
            return true;
        }

        try{
            int hour = cmbStartHour.getSelectedIndex();
            int minute = cmbStartMin.getSelectedIndex() * 5;

            Time time = Time.valueOf(Utils.formatNumberTwoDigits(hour) + ":" + minute + ":00");
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, Language.get("EA_ERROR_INVALID_TIME"));
            return true;
        }

        try{
            int deltaTime = Integer.parseInt(txfDuration.getText());
            if(deltaTime<0)
                throw new Exception("Duration can't be negative");
            if(deltaTime>=1440)
                throw new Exception("Duration can't be more than a day");
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, Language.get("EA_ERROR_INVALID_DURATION") + e.getMessage());
            return true;
        }

        try{
            double price = Double.parseDouble(txfPrice.getText());

            if(price < 0)
                throw new Exception("Price can't be negative");
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, Language.get("EA_ERROR_INVALID_PRICE") + e.getMessage());
            return true;
        }

        return false;
    }

}
