public class Customer extends  Person{

    public Customer(){
        this(-1,"","","","","",true);
    }

    public Customer(int id, String username, String firstName, String lastName, String tel, String info, boolean active) {
        super(id, username, firstName, lastName, tel, info, active);
    }

    @Override
    public String toString() {
        return username;
    }
}
