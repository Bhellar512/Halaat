package Modal;

/**
 * Created by Dell on 12/4/2017.
 */

public class Contact {
    String name;
    String phone;
    boolean selected;


    public Contact() {
    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
