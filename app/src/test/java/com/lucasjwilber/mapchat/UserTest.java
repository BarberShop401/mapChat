package com.lucasjwilber.mapchat;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserTest {
    @Test
    public void testCommentInstantiate() {
        User vik = new User("Vik", "vik@gmail.com");
        System.out.println("vik = " + vik);
    }
}