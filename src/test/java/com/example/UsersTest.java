package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {
    static Users u;

    @BeforeAll
    static void setUp(){
        u = new Users();
        u.setId(188);
        u.setName("Heston");
        u.setPassword("The Fat Duck");
        u.setType("Admin");
    }

    @Test
    public void userData(){
        assertEquals("Heston", u.getName());
        assertEquals("The Fat Duck", u.getPassword());
        assertEquals("Admin", u.getType());
        assertEquals(188, u.getId());
        u.setName("Jonny");
        assertEquals("Jonny", u.getName());
    }

}