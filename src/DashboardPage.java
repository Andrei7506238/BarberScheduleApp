import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class DashboardPage extends JPanel implements ActionListener {
    private Staff staff;

    public DashboardPage(Staff loggedStaff){
        super();
        setLayout(null);
        staff = loggedStaff;

        loadHeader();
        loadFooter();
        loadImage();
        updateData();

        revalidate();
    }

    public void loadHeader(){
        JPanel pnlBlack = new JPanel(new GridLayout(1,4));
        pnlBlack.setLocation(0,0);
        pnlBlack.setSize(800, 25);
        pnlBlack.setBackground(new Color(0,0,0));

        JLabel lblLoggedAs = new JLabel(Language.get("DS_LOGGED_AS") + staff.getUsername());
        lblLoggedAs.setForeground(new Color(255,255,255));
        pnlBlack.add(lblLoggedAs, BorderLayout.WEST);

        add(pnlBlack);
    }

    public void loadFooter(){
        JPanel pnlBlack = new JPanel();
        pnlBlack.setLocation(0,520);
        pnlBlack.setSize(800, 75);
        pnlBlack.setBackground(new Color(0,0,0));

        JLabel lblFooterContent = new JLabel(Language.get("DS_FOOTER_CONTENT"));
        lblFooterContent.setForeground(new Color(255,255,255));
        pnlBlack.add(lblFooterContent, BorderLayout.WEST);

        add(pnlBlack);
    }

    public void loadImage(){
        ImageIcon imgBanner = new ImageIcon(Language.get("DS_WALLPAPER_PATH"));
        Image image = imgBanner.getImage(); // transform it
        Image newimg = image.getScaledInstance(800, 250,  java.awt.Image.SCALE_SMOOTH);
        JLabel lblImage = new JLabel();
        lblImage.setIcon(new ImageIcon(newimg));
        lblImage.setVerticalTextPosition(SwingConstants.TOP);
        lblImage.setSize(new Dimension(800, 250));
        lblImage.setLocation(0,30);
        add(lblImage);
    }

    public void updateData(){
        String strDate = LocalDate.now().toString();
        String strWeekday = Language.get(Utils.getWorkdayStr(strDate));
        ArrayList<Appointment> appointmentsToday = DBManger.getScheduleUserDate(staff.getUsername(), Date.valueOf(LocalDate.now()));
        double expectedIncome = 0;
        Appointment firstAppointment = null;
        Appointment lastAppointment = null;
        Appointment nextAppointment = null;
        for(Appointment app : appointmentsToday){
            expectedIncome += app.getPrice();
            //Find first and last appointment
            if(firstAppointment==null){
                firstAppointment = app;
                lastAppointment = app;
            }
            else
            {
                if(LocalTime.of(app.getStartHour(), app.getStartMin()).isBefore(LocalTime.of(firstAppointment.getStartHour(), firstAppointment.getStartMin()))){
                    firstAppointment = app;
                }

                if(LocalTime.of(app.getStartHour(), app.getStartMin()).plusMinutes(app.getDuration()).isAfter(LocalTime.of(lastAppointment.getStartHour(), lastAppointment.getStartMin()).plusMinutes(lastAppointment.getDuration()))){
                    lastAppointment = app;
                }
            }

            //Find next appointment
            if(LocalTime.of(app.getStartHour(), app.getStartMin()).plusMinutes(app.getDuration()).isAfter(LocalTime.now())){
                if(nextAppointment == null){
                    nextAppointment = app;
                }
                else{
                    if(LocalTime.of(app.getStartHour(), app.getStartMin()).isBefore(LocalTime.of(nextAppointment.getStartHour(), nextAppointment.getStartMin()))){
                        firstAppointment = app;
                    }
                }
            }
        }

        JPanel frame = new JPanel(new GridLayout(3,6));
        frame.setLocation(50, 300);
        frame.setSize(700, 100);

        JLabel lblDate = new JLabel(Language.get("DS_LBL_DATE"));
        frame.add(lblDate);
        JLabel infoDate = new JLabel(strDate);
        frame.add(infoDate);

        frame.add(new JPanel());
        frame.add(new JPanel());

        JLabel lblWeekday = new JLabel(Language.get("DS_LBL_WEEKDAY"));
        frame.add(lblWeekday);
        JLabel infoWeekday = new JLabel(strWeekday);
        frame.add(infoWeekday);

        JLabel lblAppNo = new JLabel(Language.get("DS_LBL_NUMAPPS"));
        frame.add(lblAppNo);
        JLabel infoAppNo = new JLabel(Integer.toString(appointmentsToday.size()));
        frame.add(infoAppNo);

        frame.add(new JPanel());
        frame.add(new JPanel());

        JLabel lblExpectedIncome = new JLabel(Language.get("DS_LBL_EXPINCOME"));
        frame.add(lblExpectedIncome);
        JLabel infoExpectedIncome = new JLabel(Double.toString(expectedIncome));
        frame.add(infoExpectedIncome);

        JLabel lblStartWork = new JLabel(Language.get("DS_LBL_TODAYSCH"));
        frame.add(lblStartWork);
        JLabel infoStartWork = new JLabel(firstAppointment==null ? Language.get("DS_NONE") :
                Utils.getTimeFrame(firstAppointment.getStartHour(), firstAppointment.getStartMin(), lastAppointment.getEndHour(), lastAppointment.getEndMin()));
        frame.add(infoStartWork);

        frame.add(new JPanel());
        frame.add(new JPanel());

        JLabel lblNextApp = new JLabel(Language.get("DS_LBL_NEXTAPP"));
        frame.add(lblNextApp);
        JLabel infoNextApp = new JLabel(nextAppointment==null ? Language.get("DS_NONE") :
                Utils.getTimeFrame(nextAppointment.getStartHour(), nextAppointment.getStartMin(), nextAppointment.getEndHour(), nextAppointment.getEndMin()));
        frame.add(infoNextApp);
        
        add(frame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
