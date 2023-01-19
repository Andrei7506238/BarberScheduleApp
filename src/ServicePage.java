import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServicePage extends JPanel implements ActionListener {
    private JButton btnRefresh;
    private JButton btnAddBarberService;
    private JButton btnEditBarberService;
    private JButton btnDeleteBarberService;

    private ArrayList<BarberService> allBarberServices;

    private JPanel pnlTableWrap;
    private JScrollPane scpScrollPane;
    private JTable table;

    private ListSelectionModel listSelectionModel;
    private ArrayList<BarberService> services;

    public ServicePage(){
        super();
        setLayout(null);
        String strDate = LocalDate.now().toString();

        //RefreshButtons
        createButtons();

        //Calendar
        createTable();

        //Refresh
        refreshTable();

    }

    private void createButtons(){
        JPanel pnlBtns = new JPanel();
        pnlBtns.setLayout(new GridLayout(1,4,15,15));
        pnlBtns.setLocation(20,65);
        pnlBtns.setSize(760,25);

        btnRefresh = new JButton(Language.get("BP_BTN_REFRESH"));
        btnRefresh.addActionListener(this);
        pnlBtns.add(btnRefresh);

        btnAddBarberService = new JButton(Language.get("BP_BTN_ADD"));
        btnAddBarberService.addActionListener(this);
        pnlBtns.add(btnAddBarberService);

        btnEditBarberService = new JButton(Language.get("BP_BTN_EDIT"));
        btnEditBarberService.addActionListener(this);
        pnlBtns.add(btnEditBarberService);

        btnDeleteBarberService = new JButton(Language.get("BP_BTN_DELETE"));
        btnDeleteBarberService.addActionListener(this);
        pnlBtns.add(btnDeleteBarberService);

        add(pnlBtns);
    }

    public void refreshPage(){
        refreshTable();
        pnlTableWrap.revalidate();
        revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnRefresh){
            refreshPage();
        }

        if(e.getSource() == btnAddBarberService){
            AddEditServicePage addEditBarberServicePage = new AddEditServicePage(-1);
            addEditBarberServicePage.setVisible(true);
        }

        if(e.getSource() == btnEditBarberService){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];

            AddEditServicePage addEditBarberServicePage = new AddEditServicePage(services.get(selectedIndex).getId_service());
            addEditBarberServicePage.setVisible(true);
        }

        if(e.getSource() == btnDeleteBarberService){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];

            boolean response = DBManger.deleteService(services.get(selectedIndex));

            if(response)
                JOptionPane.showMessageDialog(this, Language.get("BP_MSG_DELETE_SUCCES"));
            else
                JOptionPane.showMessageDialog(this, Language.get("BP_MSG_DELETE_SUCCES"));

            refreshTable();
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

        table = new JTable(new NonEditableTableModel(new Object[]{Language.get("BP_TBL_HEAD_NAME"), Language.get("BP_TBL_HEAD_PRICE"), Language.get("BP_TBL_HEAD_DURATION")}, 0));
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
        services = DBManger.getAllServices();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while(model.getRowCount()>0)
            model.removeRow(0);

        for (BarberService service : services) {
            model.addRow(new Object[]{
                    service.getName(),
                    service.getPrice(),
                    service.getMinutes()
            });
        }
    }
}

