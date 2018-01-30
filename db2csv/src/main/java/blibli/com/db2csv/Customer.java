package blibli.com.db2csv;

import java.util.Date;

public class Customer {

    private final long id;

    private final String firstName;

    private final String lastName;

    private final String birthdate;

    public Customer(long id, String firstName, String lastName, String birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }
}
