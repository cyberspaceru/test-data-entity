package ru.sbt.qa.tde.entities.forms;

import ru.sbt.qa.tde.core.TestData;

/**
 * Created by cyberspace on 6/25/2017.
 */
@TestData
public class ShippingForm {

    public String city, street, houseNumber;

    @Override
    public String toString() {
        return "ShippingForm{" +
                "city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                '}';
    }
}
