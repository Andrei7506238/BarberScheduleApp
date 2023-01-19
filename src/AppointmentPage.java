import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class AppointmentPage extends JPanel implements ActionListener {
    private JComboBox cmbStaff;
    private JTextField txfDate;

    private JButton btnRefresh;
    private JButton btnAddAppointment;
    private JButton btnEditAppointment;
    private JButton btnDeleteAppointment;
    private JLabel lblDayType;

    private ArrayList<Staff> allStaff;

    private JPanel pnlTableWrap;
    private JScrollPane scpScrollPane;
    private JTable table;

    private ListSelectionModel listSelectionModel;
    private ArrayList<Appointment> appointments;

    public AppointmentPage(Staff loggedStaff){
        super();
        setLayout(null);
        String strDate = LocalDate.now().toString();

        //Filters
        createFilter(strDate, loggedStaff);

        //RefreshButtons
        createButtons();

        //Calendar
        createTable();

        //Refresh
        refreshTable();

    }

    private void createFilter(String strDate, Staff loggedStaff){
        //Filter
        JPanel pnlFilter = new JPanel();
        pnlFilter.setLayout(new GridLayout(2, 2));
        pnlFilter.setLocation(20,10);
        pnlFilter.setSize(760,50);

        JLabel lblStaff = new JLabel(Language.get("AP_LBL_STAFF"));
        pnlFilter.add(lblStaff);

        allStaff = DBManger.getAllStaff();
        cmbStaff = new JComboBox(allStaff.toArray());

        for(int i=0; i<allStaff.size(); i++){
            if(loggedStaff == cmbStaff.getSelectedItem()){
                cmbStaff.setSelectedIndex(i);
                break;
            }
        }

        cmbStaff.addActionListener(this);
        pnlFilter.add(cmbStaff);


        JLabel lblDate = new JLabel(Language.get("AP_LBL_DATE"));
        pnlFilter.add(lblDate);

        txfDate = new JTextField();
        txfDate.addActionListener(this);
        txfDate.setText(strDate);
        txfDate.requestFocus();
        pnlFilter.add(txfDate);

        add(pnlFilter);
    }

    private void createButtons(){
        JPanel pnlBtns = new JPanel();
        pnlBtns.setLayout(new GridLayout(1,5,15,15));
        pnlBtns.setLocation(20,65);
        pnlBtns.setSize(760,25);

        btnRefresh = new JButton(Language.get("AP_BTN_REFRESH"));
        btnRefresh.addActionListener(this);
        pnlBtns.add(btnRefresh);

        btnAddAppointment = new JButton(Language.get("AP_BTN_ADD"));
        btnAddAppointment.addActionListener(this);
        pnlBtns.add(btnAddAppointment);

        btnEditAppointment = new JButton(Language.get("AP_BTN_EDIT"));
        btnEditAppointment.addActionListener(this);
        pnlBtns.add(btnEditAppointment);

        btnDeleteAppointment = new JButton(Language.get("AP_BTN_DELETE"));
        btnDeleteAppointment.addActionListener(this);
        pnlBtns.add(btnDeleteAppointment);

        lblDayType = new JLabel(Language.get("AP_WEEKDAY_LBL") + Language.get(Utils.getWorkdayStr(txfDate.getText())), SwingConstants.CENTER);
        pnlBtns.add(lblDayType);

        add(pnlBtns);
    }

    public void refreshPage(){
        String date = txfDate.getText();
        try {
            LocalDate.parse(date);
        }
        catch (DateTimeParseException ex){
            JOptionPane.showMessageDialog(this, Language.get("AP_ERR_INVDATE_CONTENT"), Language.get("AP_ERR_INVDATE_TITLE"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        lblDayType.setText(Language.get("AP_WEEKDAY_LBL") + Language.get(Utils.getWorkdayStr(txfDate.getText())));

        refreshTable();
        pnlTableWrap.revalidate();
        revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnRefresh){
            refreshPage();
        }

        if(e.getSource() == cmbStaff){
            refreshPage();
        }

        if(e.getSource() == txfDate){
            refreshPage();
        }

        if(e.getSource() == btnAddAppointment){
            AddEditAppointmentPage addEditAppointmentPage = new AddEditAppointmentPage(-1, txfDate.getText(), (Staff) cmbStaff.getSelectedItem());
            addEditAppointmentPage.setVisible(true);
        }

        if(e.getSource() == btnEditAppointment){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];

            AddEditAppointmentPage addEditAppointmentPage = new AddEditAppointmentPage(appointments.get(selectedIndex).getIdAppointment(), null, null);
            addEditAppointmentPage.setVisible(true);
        }

        if(e.getSource() == btnDeleteAppointment){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];
            boolean response = DBManger.deleteAppointment(appointments.get(selectedIndex));

            if(response)
                JOptionPane.showMessageDialog(this, Language.get("AP_MSG_DELETE_SUCCES"));
            else
                JOptionPane.showMessageDialog(this, Language.get("AP_MSG_DELETE_FAIL"));

            refreshPage();
        }
    }

    void createTable(){
        pnlTableWrap = new JPanel();
        pnlTableWrap.setLocation(20, 100);
        pnlTableWrap.setSize(760, 480);
        add(pnlTableWrap);

        class NonEditableTableModel extends DefaultTableModel{
            NonEditableTableModel(Object[] objects, int lines){
                super(objects, lines);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        }

        table = new JTable(new NonEditableTableModel(new Object[]{Language.get("AP_TBL_HEAD_USERNAME"), Language.get("AP_TBL_HEAD_SERVICE"), Language.get("AP_TBL_HEAD_TIME"), Language.get("AP_TBL_HEAD_PRICE"), Language.get("AP_TBL_HEAD_NOTE")}, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel = table.getSelectionModel();

        scpScrollPane = new JScrollPane(table);
        scpScrollPane.setPreferredSize(new Dimension(760,460));
        scpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pnlTableWrap.add(scpScrollPane);
        add(pnlTableWrap);
    }


    private void refreshTable(){
        appointments = DBManger.getScheduleUserDate((String) cmbStaff.getSelectedItem().toString(), java.sql.Date.valueOf(txfDate.getText()));

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while(model.getRowCount()>0)
            model.removeRow(0);

        for (Appointment appointment : appointments) {
            Customer customer = DBManger.getCustomerById(appointment.getIdCustomer());
            BarberService service = DBManger.getServiceById(appointment.getIdService());


            model.addRow(new Object[]{
                    customer.getUsername(),
                    service.getName(),
                    appointment.getTimeFrame(),
                    appointment.getPrice(),
                    appointment.getInfo(),
            });
        }
    }
}

