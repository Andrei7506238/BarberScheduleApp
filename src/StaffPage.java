import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class StaffPage extends JPanel implements ActionListener {
    private JTextField txfKeywords;

    private JButton btnRefresh;
    private JButton btnAddStaff;
    private JButton btnEditStaff;
    private JButton btnDeleteStaff;

    private JPanel pnlTableWrap;
    private JScrollPane scpScrollPane;
    private JTable table;

    private ListSelectionModel listSelectionModel;
    private ArrayList<Staff> staffs;

    public StaffPage(){
        super();
        setLayout(null);

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

        JLabel lblService = new JLabel(Language.get("SP_LBL_KEYWORDS"));
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

        btnRefresh = new JButton(Language.get("SP_BTN_REFRESH"));
        btnRefresh.addActionListener(this);
        pnlBtns.add(btnRefresh);

        btnAddStaff = new JButton(Language.get("SP_BTN_ADD"));
        btnAddStaff.addActionListener(this);
        pnlBtns.add(btnAddStaff);

        btnEditStaff = new JButton(Language.get("SP_BTN_EDIT"));
        btnEditStaff.addActionListener(this);
        pnlBtns.add(btnEditStaff);

        btnDeleteStaff = new JButton(Language.get("SP_BTN_DELETE"));
        btnDeleteStaff.addActionListener(this);
        pnlBtns.add(btnDeleteStaff);

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

        if(e.getSource() == btnAddStaff){
            AddEditStaffPage addEditStaffPage = new AddEditStaffPage(-1);
            addEditStaffPage.setVisible(true);
        }

        if(e.getSource() == btnEditStaff){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];

            AddEditStaffPage addEditStaffPage = new AddEditStaffPage(staffs.get(selectedIndex).getId());
            addEditStaffPage.setVisible(true);
        }

        if(e.getSource() == btnDeleteStaff){
            if(listSelectionModel.getSelectedIndices().length == 0)
                return;
            int selectedIndex = listSelectionModel.getSelectedIndices()[0];

            boolean response =  DBManger.deleteStaff(staffs.get(selectedIndex));

            if(response)
                JOptionPane.showMessageDialog(this, Language.get("SP_MSG_DELETE_SUCCES"));
            else
                JOptionPane.showMessageDialog(this, Language.get("SP_MSG_DELETE_FAIL"));

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

        table = new JTable(new NonEditableTableModel(new Object[]{Language.get("SP_TBL_HEAD_USERNAME"), Language.get("SP_TBL_HEAD_FNAME"), Language.get("SP_TBL_HEAD_LNAME"), Language.get("SP_TBL_HEAD_TEL"), Language.get("SP_TBL_HEAD_NOTE")}, 0));
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
        staffs = DBManger.searchStaff(keywords);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while(model.getRowCount()>0)
            model.removeRow(0);

        for (Staff staff : staffs) {
            model.addRow(new Object[]{
                    staff.getUsername(),
                    staff.getFirstName(),
                    staff.getLastName(),
                    staff.getTel(),
                    staff.getInfo()
            });
        }
    }
}

