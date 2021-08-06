/* package com.example;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;

import org.springframework.web.bind.annotation.*; //added by me
import org.springframework.http.MediaType; //added by me

import javax.servlet.http.HttpServletRequest; //added for session
import javax.servlet.http.HttpSession; //addedd for session

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest
public class UsageTest {


    @Autowired
    private MockMvc mvc;
    private DataSource dataSource;
    
    @Test
    public void inputUsagetoDB() throws Exception{
        try(Connection connection = dataSource.getConnection()){

            HttpSession session = mock(HttpSession.class);
            session.setAttribute("username","jonny");

            Statement stmt = connection.createStatement();
        
            String usage = "25!";
    
            mvc.perform(get("/time").content(usage, session).contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
    
            String sql = "SELECT * FROM userusage WHERE username = 'jonny'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                assertEquals("jonny", rs.getString("username"));
                assertEquals(25, rs.getInt("userusage"));
            }
    
            String delete = "DELETE FROM userUsage WHERE username='jonny'";
            stmt.executeUpdate(delete);
        }
    }

}
 */