import java.sql.Date;

public class Appointment {
    private int idAppointment;
    private int idCustomer;
    private int idStaff;
    private int idService;

    private java.sql.Date datea;

    private int startHour;
    private int startMin;

    private int endHour;
    private int endMin;

    private String info;
    private double price;

    public Appointment(int id_appointment, int id_customer, int id_staff, int id_service, Date datea, int startHour, int startMin, int endHour, int endMin, String info, double price) {
        this.idAppointment = id_appointment;
        this.idCustomer = id_customer;
        this.idStaff = id_staff;
        this.idService = id_service;
        this.datea = datea;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
        this.info = info;
        this.price = price;
    }

    public Appointment(){
        idAppointment = -1;
    }

    public int getIdAppointment() {
        return idAppointment;
    }

    public void setIdAppointment(int idAppointment) {
        this.idAppointment = idAppointment;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public int getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(int idStaff) {
        this.idStaff = idStaff;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public Date getDatea() {
        return datea;
    }

    public void setDatea(Date datea) {
        this.datea = datea;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTimeFrame(){
        return Utils.getTimeFrame(startHour, startMin, endHour, endMin);
    }

    public int getDuration(){
        int minStart = startHour*60+startMin;
        int minEnd = endHour*60+endMin;
        int delta = minStart-minEnd;
        if(delta<0) delta+=1440;
        return delta;
    }
}
