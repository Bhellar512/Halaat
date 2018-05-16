package Modal;

public class ContactNewModel {

    Name name;
    Phone phone;

    public ContactNewModel(Name name, Phone phone) {
        this.name = name;
        this.phone = phone;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }
}
