public class BarberService {
    private int id_service;
    private String name;
    private double price;
    private int minutes;
    private boolean active;

    public BarberService(){
        this(-1,"",0,0,true);
    }

    public BarberService(int idService, String name, double price, int minutes, boolean active) {
        this.id_service = idService;
        this.name = name;
        this.price = price;
        this.minutes = minutes;
        this.active = active;
    }

    public int getId_service() {
        return id_service;
    }

    public void setId_service(int id_service) {
        this.id_service = id_service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name;
    }
}
