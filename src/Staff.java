public class Staff extends Person{
    protected String password;

    public Staff(){
        this(-1,"","","","",true, "", null);
    }
    public Staff(int id, String firstName, String lastName, String tel, String info, boolean active, String username, String password) {
        super(id, username, firstName, lastName, tel, info, active);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username;
    }
}
