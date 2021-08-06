package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;

public class ReportTest {
    static Report r;

    @BeforeAll
    static void setUp(){
        r = new Report();
        r.setTotal(50);
        r.setAvg(27.5);
        r.setLongest(30);
        r.setShortest(2);
    }

    @Test
    public void reportData(){
        assertEquals(50, r.getTotal());
        assertEquals(27.5, r.getAvg());
        assertEquals(30, r.getLongest());
        assertEquals(2, r.getShortest());
        r.setTotal(75);
        r.setAvg(28);
        r.setLongest(188);
        r.setShortest(1);
        assertEquals(75, r.getTotal());
        assertEquals(28, r.getAvg());
        assertEquals(188, r.getLongest());
        assertEquals(1, r.getShortest());
    }

}
