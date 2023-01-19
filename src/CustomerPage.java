import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

public class CustomerPage extends JPanel implements ActionListener {

    private JComboBox cmbStaff;
    private JTextField txfKeywords;

    private JButton btnRefresh;
    private JButton btnAddCustomer;
    private JButton btnEditCustomer;
    private JButton btnDeleteCustomer;

    private ArrayList<Customer> allCustomers;

    private JPanel pnlTableWrap;
    private JScrollPane scpScrollPane;
    private JTable table;

    private ListSelectionModel listSelectionModel;
    private ArrayList<Customer> customers;

    public CustomerPage(){
        super();
        setLayout(null);
        String strDate = LocalDate.now().toString();

        //Filters
        createFilter();

        //RefreshButtons
        createButtons();

        //Calendar
        createTable();

        //Refresh
        refreshTable();

    }

    private void createFilter(){
        //Filter
        JPanel pnlFilter = new JPanel();
        pnlFilter.setLayout(new GridLayout(1, 2));
        pnlFilter.setLocation(20,10);
        pnlFilter.setSize(760,30);

        JLabel lblService = new JLabel(Language.get("CP_LBL_KEYWORDS"));
        pnlFilter.add(lblService);

        txfKeywords = new JTextField();
        txfKeywords.addActionListener(this);
        txfKeywords.requestFocus();
        pnlFilter.add(txfKeywords);

        add(pnlFilter);
    }

    private void createButtons(){
        JPanel pnlBtns = new JPanel();
        pnlBtns.setLayout(new GridLayout(1,4,15,15));
        pnlBtns.setLocation(20,65);
        pnlBtns.setSize(760,25);

        btnRefresh = new JButton(Language.get("CP_BTN_REFRESH"));
        btnRefresh.addActionListener(this);
        pnlBtns.add(btnRefresh);

        btnAddCustomer = new JButton(Language.get("CP_BTN_ADD"));
        btnAddCustomer.addActionListener(this);
        pnlBtns.add(btnAddCustomer);

        btnEditCustomer = new JButton(Language.get("CP_BTN_EDIT"));
        btnEditCustomer.addActionListener(this);
        pnlBtns.add(btnEditCustomer);

        btnDeleteCustomer = new JButton(Language.get("CP_BTN_DELETE"));
        btnDeleteCustomer.addActionListener(this);
        pnlBtns.add(btnDeleteCustomer);

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

        if(e.getSource() == txfKeywords){
            refreshPage();
        }

        if(e.getSource() == btnAddCustomer){
            AddEditCustomerPage addEditCustomerPage = new AddEditCustomerPage(-1);
            addEditCustomerPage.setVisible(true);
        }

        if(e.getSource() == btnEditCustomer){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];

            AddEditCustomerPage addEditCustomerPage = new AddEditCustomerPage(customers.get(selectedIndex).getId());
            addEditCustomerPage.setVisible(true);
        }

        if(e.getSource() == btnDeleteCustomer){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];

            boolean response =  DBManger.deleteCustomer(customers.get(selectedIndex));

            if(response)
                JOptionPane.showMessageDialog(this, Language.get("CP_MSG_DELETE_SUCCES"));
            else
                JOptionPane.showMessageDialog(this, Language.get("CP_MSG_DELETE_FAIL"));

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

        table = new JTable(new NonEditableTableModel(new Object[]{Language.get("CP_TBL_HEAD_USERNAME"), Language.get("CP_TBL_HEAD_FNAME"), Language.get("CP_TBL_HEAD_LNAME"), Language.get("CP_TBL_HEAD_TEL"), Language.get("CP_TBL_HEAD_NOTE")}, 0));
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
        String keywordString = txfKeywords.getText();
        String[] keywords = keywordString.split(" ");
        customers = DBManger.searchCustomer(keywords);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while(model.getRowCount()>0)
            model.removeRow(0);

        for (Customer customer : customers) {
            model.addRow(new Object[]{
                    customer.getUsername(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getTel(),
                    customer.getInfo()
            });
        }
    }
}

