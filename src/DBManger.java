import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;

public class DBManger {
    private static String connectionUrl = "jdbc:sqlserver://DESKTOP-D0HQQPH\\SQLEXPRESS;databaseName=dbBarber;user=sa;password=password;encrypt=true;trustServerCertificate=true";
    private static Connection connection = null;

    private static void createConn(){
        try {
            connection = DriverManager.getConnection(connectionUrl);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConn(){
        try {
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static Connection getConn(){
        if(connection == null){
            createConn();
        }

        return connection;
    }

    static public Staff login(String username, String password){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.loginStaff(?, ?)}");
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                Staff loggedStaff = new Staff(rs.getInt("id_staff"),
                        rs.getString("first_Name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active"),
                        rs.getString("username"),
                        rs.getString("password"));

                return loggedStaff;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }
    static public ArrayList<Appointment> getAppointmentsIntersectTimeframe(Appointment appointment){
        ArrayList<Appointment> arr = new ArrayList<>();
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getAppointmentsIntersectTimeframe(?,?,?,?,?,?)}");
            pstmt.setInt(1, appointment.getIdStaff());
            pstmt.setDate(2, appointment.getDatea());
            pstmt.setInt(3, appointment.getStartHour());
            pstmt.setInt(4, appointment.getStartMin());
            pstmt.setInt(5, appointment.getEndHour());
            pstmt.setInt(6, appointment.getEndMin());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Appointment appt = new Appointment(
                        rs.getInt("id_appointment"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_staff"),
                        rs.getInt("id_service"),
                        rs.getDate("datea"),
                        rs.getInt("start_hour"),
                        rs.getInt("start_min"),
                        rs.getInt("end_hour"),
                        rs.getInt("end_min"),
                        rs.getString("info"),
                        rs.getDouble("price")
                );
                arr.add(appt);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return arr;
    }
    static public boolean insertOrUpdateAppointment(Appointment appointment){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.addOrUpdateAppointment(?,?,?,?,?,?,?,?,?,?,?)}");
            pstmt.setInt(1, appointment.getIdAppointment());
            pstmt.setInt(2, appointment.getIdCustomer());
            pstmt.setInt(3, appointment.getIdStaff());
            pstmt.setInt(4, appointment.getIdService());
            pstmt.setDate(5, appointment.getDatea());
            pstmt.setInt(6, appointment.getStartHour());
            pstmt.setInt(7, appointment.getStartMin());
            pstmt.setInt(8, appointment.getEndHour());
            pstmt.setInt(9,appointment.getEndMin());
            pstmt.setString(10,appointment.getInfo());
            pstmt.setDouble(11, appointment.getPrice());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    static public boolean insertOrUpdateCustomer(Customer customer){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.addOrUpdateCustomer(?,?,?,?,?,?)};");
            pstmt.setInt(1, customer.getId());
            pstmt.setString(2, customer.getUsername());
            pstmt.setString(3, customer.getFirstName());
            pstmt.setString(4, customer.getLastName());
            pstmt.setString(5, customer.getTel());
            pstmt.setString(6, customer.getInfo());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    static public boolean insertOrUpdateService(BarberService service){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.addOrUpdateService(?,?,?,?)};");
            pstmt.setInt(1, service.getId_service());
            pstmt.setString(2, service.getName());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setInt(4, service.getMinutes());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    static public boolean insertOrUpdateStaff(Staff staff){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.addOrUpdateStaff(?,?,?,?,?,?,?)};");
            pstmt.setInt(1, staff.getId());
            pstmt.setString(2, staff.getFirstName());
            pstmt.setString(3, staff.getLastName());
            pstmt.setString(4, staff.getTel());
            pstmt.setString(5, staff.getInfo());
            pstmt.setString(6, staff.getUsername());
            pstmt.setString(7, staff.getPassword());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    static public ArrayList<Customer> searchCustomer(String[] keywords){
        class ComparatorCustomers implements Comparator<Customer>{
            @Override
            public int compare(Customer o1, Customer o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        }
        ArrayList<Customer> finalResult = new ArrayList<>();

        for(String keyword : keywords){
            ArrayList<Customer> partial = searchCustomerByOneKeyword(keyword);
            finalResult.addAll(partial);
        }

        if(finalResult.size() == 0)
            return finalResult;

        finalResult.sort(new ComparatorCustomers());
        ArrayList<Customer> listResult = new ArrayList<>();
        listResult.add(finalResult.get(0));

        for(int i=1; i<finalResult.size(); i++){
            if(finalResult.get(i).getId() != listResult.get(listResult.size()-1).getId())
                listResult.add(finalResult.get(i));
        }
        class ComparatorCustomersOrderByUsername implements Comparator<Customer>{
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        }

        listResult.sort(new ComparatorCustomersOrderByUsername());


        return listResult;
    }
    static public ArrayList<Staff> searchStaff(String[] keywords){
        class ComparatorCustomers implements Comparator<Staff>{
            @Override
            public int compare(Staff o1, Staff o2) {
                return Integer.compare(o1.getId(), o2.getId());
            }
        }
        ArrayList<Staff> finalResult = new ArrayList<>();

        for(String keyword : keywords){
            ArrayList<Staff> partial = searchStaffByOneKeyword(keyword);
            finalResult.addAll(partial);
        }

        if(finalResult.size() == 0)
            return finalResult;

        finalResult.sort(new ComparatorCustomers());
        ArrayList<Staff> listResult = new ArrayList<>();
        listResult.add(finalResult.get(0));

        for(int i=1; i<finalResult.size(); i++){
            if(finalResult.get(i).getId() != listResult.get(listResult.size()-1).getId())
                listResult.add(finalResult.get(i));
        }

        class ComparatorCustomersOrderByUsername implements Comparator<Staff>{
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        }

        listResult.sort(new ComparatorCustomersOrderByUsername());
        return listResult;
    }

    static public ArrayList<Staff> getAllStaff(){
        ArrayList<Staff> arr = new ArrayList<>();
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getAllStaff()}");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Staff staff = new Staff(rs.getInt("id_staff"),
                        rs.getString("first_Name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active"),
                        rs.getString("username"),
                        rs.getString("password"));
                arr.add(staff);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        class ComparatorCustomersOrderByUsername implements Comparator<Staff>{
            @Override
            public int compare(Staff o1, Staff o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        }

        arr.sort(new ComparatorCustomersOrderByUsername());
        return arr;
    }
    static public ArrayList<Appointment> getScheduleUserDate(String username, java.sql.Date date){
        ArrayList<Appointment> arr = new ArrayList<>();
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getScheduleUserDate(?,?)}");
            pstmt.setString(1, username);
            pstmt.setDate(2, date);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Appointment appt = new Appointment(
                        rs.getInt("id_appointment"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_staff"),
                        rs.getInt("id_service"),
                        rs.getDate("datea"),
                        rs.getInt("start_hour"),
                        rs.getInt("start_min"),
                        rs.getInt("end_hour"),
                        rs.getInt("end_min"),
                        rs.getString("info"),
                        rs.getDouble("price")
                );
                arr.add(appt);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        class ComparatorAppointmentByStartHour implements Comparator<Appointment>{
            @Override
            public int compare(Appointment o1, Appointment o2) {
                if(o1.getStartHour() == o2.getStartHour() && o1.getStartMin() == o2.getStartMin()){
                    return Integer.compare(o1.getDuration(), o2.getDuration());
                }
                return LocalTime.of(o1.getStartHour(), o1.getStartMin()).compareTo(LocalTime.of(o2.getStartHour(), o2.getStartMin()));
            }
        }

        arr.sort(new ComparatorAppointmentByStartHour());
        return arr;
    }
    static public ArrayList<Customer> getAllCustomer(){
        ArrayList<Customer> customers = new ArrayList<>();
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getAllCustomer()}");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Customer customer = new Customer(
                        rs.getInt("id_customer"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active")
                );
                customers.add(customer);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        class ComparatorCustomersOrderByUsername implements Comparator<Customer>{
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        }
        customers.sort(new ComparatorCustomersOrderByUsername());
        return customers;
    }
    static public ArrayList<BarberService> getAllServices(){
        ArrayList<BarberService> barberServices = new ArrayList<>();
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getAllService()}");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                BarberService barberService = new BarberService(
                                rs.getInt("id_service"),
                                rs.getString("name"),
                                rs.getDouble("price"),
                                rs.getInt("minutes"),
                                rs.getBoolean("active"));
                barberServices.add(barberService);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        class ComparatorServiceByName implements Comparator<BarberService>{
            @Override
            public int compare(BarberService o1, BarberService o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }

        barberServices.sort(new ComparatorServiceByName());
        return barberServices;
    }


    static public Staff getStaffById(int id){
        Staff staff = null;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getStaffById(?)}");
            pstmt.setInt(1,id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                 staff = new Staff(rs.getInt("id_staff"),
                        rs.getString("first_Name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active"),
                        rs.getString("username"),
                        null
                 );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return staff;
    }
    static public Customer getCustomerById(int id){
        Customer customer = null;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getCustomerById(?)}");
            pstmt.setInt(1,id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                customer = new Customer(rs.getInt("id_customer"),
                        rs.getString("username"),
                        rs.getString("first_Name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customer;
    }
    static public Customer getCustomerByUsername(String username){
        Customer customer = null;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getCustomerByUsername(?)}");
            pstmt.setString(1,username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                customer = new Customer(rs.getInt("id_customer"),
                        rs.getString("username"),
                        rs.getString("first_Name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customer;
    }
    static public Staff getStaffByUsername(String username){
        Staff staff = null;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getCustomerByUsername(?)}");
            pstmt.setString(1,username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                staff = new Staff(rs.getInt("id_customer"),
                        rs.getString("first_Name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active"),
                        rs.getString("username"),
                        null);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return staff;
    }
    static public BarberService getServiceById(int id){
        BarberService barberService = null;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getServiceById(?)}");
            pstmt.setInt(1,id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                barberService = new BarberService(rs.getInt("id_service"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("minutes"),
                        rs.getBoolean("active"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return barberService;
    }
    static public BarberService getServiceByName(String name){
        BarberService barberService = null;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getServiceByName(?)}");
            pstmt.setString(1,name);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                barberService = new BarberService(rs.getInt("id_service"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("minutes"),
                        rs.getBoolean("active"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return barberService;
    }
    static public Appointment getAppointmentById(int id){
        Appointment appointment = null;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getScheduleById(?)}");
            pstmt.setInt(1,id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                appointment = new Appointment(
                        rs.getInt("id_appointment"),
                        rs.getInt("id_customer"),
                        rs.getInt("id_staff"),
                        rs.getInt("id_service"),
                        rs.getDate("datea"),
                        rs.getInt("start_hour"),
                        rs.getInt("start_min"),
                        rs.getInt("end_hour"),
                        rs.getInt("end_min"),
                        rs.getString("info"),
                        rs.getDouble("price")
                );
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return appointment;
    }

    static public boolean deleteAppointment(Appointment appointment){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.deleteSchedule(?)}");
            pstmt.setInt(1, appointment.getIdAppointment());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    static public boolean deleteCustomer(Customer customer){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.deleteCustomer(?)}");
            pstmt.setInt(1, customer.getId());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    static public boolean deleteService(BarberService service){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.deleteService(?)}");
            pstmt.setInt(1, service.getId_service());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    static public boolean deleteStaff(Staff staff){
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.deleteStaff(?)}");
            pstmt.setInt(1, staff.getId());

            int noRows = pstmt.executeUpdate();
            return noRows==1;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    static public boolean checkIsAdmin(Staff staff){
        ArrayList<String> roles;
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.getStaffGroupes(?)}");
            pstmt.setInt(1,staff.id);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                String newRole = rs.getString("name");
                if(newRole.equals("Admin"))
                    return true;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    static private ArrayList<Customer> searchCustomerByOneKeyword(String keyword){
        keyword = "%" + keyword + "%";
        ArrayList<Customer> customers = new ArrayList<>();
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.searchCustomers(?)}");
            pstmt.setString(1, keyword);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Customer customer = new Customer(
                        rs.getInt("id_customer"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active")
                );
                customers.add(customer);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customers;
    }
    static private ArrayList<Staff> searchStaffByOneKeyword(String keyword){
        keyword = "%" + keyword + "%";
        ArrayList<Staff> staffs = new ArrayList<>();
        try{
            PreparedStatement pstmt = DBManger.getConn().prepareStatement("{call dbo.searchStaff(?)}");
            pstmt.setString(1, keyword);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Staff staff = new Staff(
                        rs.getInt("id_staff"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("tel"),
                        rs.getString("info"),
                        rs.getBoolean("active"),
                        rs.getString("username"),
                        null
                );
                staffs.add(staff);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        return staffs;
    }
}