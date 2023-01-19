import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPage extends JFrame implements ActionListener, MenuListener {
    protected JMenuItem mitViewApp, mitAddApp;
    protected JMenuItem mitViewCustomer, mitAddCustomer;
    protected JMenuItem mitViewService, mitAddService;
    protected JMenuItem mitViewStaff, mitAddStaff;
    protected JMenu mnuDashboard;

    protected Staff currentStaff;
    protected JPanel pnlMain;
    protected boolean isAdmin;

    public MainPage(Staff currentStaff){
        super(Language.get("MN_HEAD"));
        this.currentStaff = currentStaff;
        this.isAdmin = DBManger.checkIsAdmin(currentStaff);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(812, 619);
        setResizable(false);

        createMenuBar();
        pnlMain = new DashboardPage(currentStaff);
        setContentPane(pnlMain);
        revalidate();
    }

    private void createMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        //Dashboard
        mnuDashboard = new JMenu("Dashboard");
        mnuDashboard.addMenuListener(this);
        menuBar.add(mnuDashboard);

        //Appointments
        JMenu mnuAppointment = new JMenu(Language.get("MN_MENU_APP"));
        mitViewApp = new JMenuItem(Language.get("MN_APP_VIEW"));
        mitViewApp.addActionListener(this);
        mitAddApp = new JMenuItem(Language.get("MN_APP_ADD"));
        mitAddApp.addActionListener(this);
        mnuAppointment.add(mitViewApp);
        mnuAppointment.add(mitAddApp);
        menuBar.add(mnuAppointment);

        //Customers
        JMenu mnuCustomers = new JMenu(Language.get("MN_MENU_CST"));
        mitViewCustomer = new JMenuItem(Language.get("MN_CST_VIEW"));
        mitViewCustomer.addActionListener(this);
        mitAddCustomer = new JMenuItem(Language.get("MN_CST_ADD"));
        mitAddCustomer.addActionListener(this);
        mnuCustomers.add(mitViewCustomer);
        mnuCustomers.add(mitAddCustomer);
        menuBar.add(mnuCustomers);

        if(!isAdmin){
            setJMenuBar(menuBar);
            return;
        }

        //Services
        JMenu mnuServices = new JMenu(Language.get("MN_MENU_SRV"));
        mitViewService = new JMenuItem(Language.get("MN_SRV_VIEW"));
        mitViewService.addActionListener(this);
        mitAddService = new JMenuItem(Language.get("MN_SRV_ADD"));
        mitAddService.addActionListener(this);
        mnuServices.add(mitViewService);
        mnuServices.add(mitAddService);
        menuBar.add(mnuServices);

        //Staff
        JMenu mnuStaffs = new JMenu(Language.get("MN_MENU_STF"));
        mitViewStaff = new JMenuItem(Language.get("MN_STF_VIEW"));
        mitViewStaff.addActionListener(this);
        mitAddStaff = new JMenuItem(Language.get("MN_STF_ADD"));
        mitAddStaff.addActionListener(this);
        mnuStaffs.add(mitViewStaff);
        mnuStaffs.add(mitAddStaff);
        menuBar.add(mnuStaffs);

        setJMenuBar(menuBar);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == mitViewApp){
            pnlMain = new AppointmentPage(currentStaff);
            setContentPane(pnlMain);
        }

        if(e.getSource() == mitAddApp){
            JFrame window = new AddEditAppointmentPage(-1);
            window.setVisible(true);
        }

        if(e.getSource() == mitViewCustomer){
            pnlMain = new CustomerPage();
            setContentPane(pnlMain);
        }

        if(e.getSource() == mitAddCustomer){
            JFrame window = new AddEditCustomerPage(-1);
            window.setVisible(true);
        }
        
        if(e.getSource() == mitViewService){
            pnlMain = new ServicePage();
            setContentPane(pnlMain);
        }

        if(e.getSource() == mitAddService){
            JFrame window = new AddEditServicePage(-1);
            window.setVisible(true);
        }

        if(e.getSource() == mitViewStaff){
            pnlMain = new StaffPage();
            setContentPane(pnlMain);
        }

        if(e.getSource() == mitAddStaff){
            JFrame window = new AddEditStaffPage(-1);
            window.setVisible(true);
        }

        revalidate();
    }

    @Override
    public void menuSelected(MenuEvent e) {
        if (e.getSource() == mnuDashboard){
            pnlMain = new DashboardPage(currentStaff);
            setContentPane(pnlMain);
        }

        revalidate();
    }

    @Override
    public void menuDeselected(MenuEvent e) {

    }

    @Override
    public void menuCanceled(MenuEvent e) {

    }
}
