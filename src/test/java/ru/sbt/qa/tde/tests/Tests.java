package ru.sbt.qa.tde.tests;

import org.junit.Assert;
import org.junit.Test;
import ru.sbt.qa.tde.core.Entities;
import ru.sbt.qa.tde.entities.Ignored;
import ru.sbt.qa.tde.entities.User;
import ru.sbt.qa.tde.entities.forms.ShippingForm;
import ru.sbt.qa.tde.loaders.XLSXLoader;

import java.io.File;

/**
 * Created by cyberspace on 6/25/2017.
 */
public class Tests {

    @Test
    public void xlsxLoaderTest() {
        XLSXLoader loader = new XLSXLoader(new File("").getAbsolutePath() + "\\src\\test\\resources\\example.xlsx");
        Entities.add(loader, "ru.sbt.qa.tde.entities");

        Assert.assertTrue(checkUserAsAdmin((User)Entities.getFirst("Admin")));

        Assert.assertTrue(checkUserAsAdmin(Entities.getFirst(User.class, "Admin")));

        Assert.assertTrue(checkUserAsAdmin2(Entities.getFirst(User.class, "Admin2")));

        Assert.assertTrue(checkShippingForm(Entities.getFirst(ShippingForm.class)));

        Assert.assertTrue(Entities.getFirst(Ignored.class) == null);
    }

    private boolean checkUserAsAdmin(User user) {
        System.out.println(user);
        return "myLogin".equals(user.login) &&
                "myPass".equals(user.password) &&
                new Integer(23).equals(user.age) &&
                Boolean.TRUE.equals(user.rememberMe);
    }

    private boolean checkUserAsAdmin2(User user) {
        System.out.println(user);
        return "myLogin2".equals(user.login) &&
                "myPass2".equals(user.password) &&
                new Integer(232).equals(user.age) &&
                !user.rememberMe; // Так как в эксельке это не определно, то тут false
    }

    private boolean checkShippingForm(ShippingForm sf) {
        System.out.println(sf);
        return "Город Мой".equals(sf.city) &&
                sf.street != null &&
                sf.street.isEmpty() &&
                sf.houseNumber == null;
    }
}
