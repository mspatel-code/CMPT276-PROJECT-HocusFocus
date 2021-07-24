/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.util.SocketUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

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
import java.util.Map;
import java.util.*;
import java.text.*;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }

  @GetMapping(path = "/pomodoro")
  public String getPomodoro(Map<String, Object> model) {
    return "pomodoro";
  }
  
  @GetMapping(path = "/chat")
  public String getChatApp(Map<String, Object> model) {
    return "chat";
  }

  @GetMapping("/pomodoro/signup")
  public String getSignupForm(Map<String, Object> model) {
    Users userInput = new Users();
    model.put("userInput", userInput);
    return "signup";
  }

  @PostMapping(path = "/pomodoro/signup", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String handleBrowserSingupSubmit(Map<String, Object> model, Users userInput, HttpSession session)
      throws Exception {
    // save the user data into the database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS userLogin (id serial, userName varchar(20), userType varchar(20), userPassword varchar(20))");

      String mult = "SELECT userName FROM userLogin";
      ResultSet rs = stmt.executeQuery(mult);
      while (rs.next()) {
        if (rs.getString("userName").equals(userInput.getName())) {
          return "error_signup";
        }
      }

      String sql = "INSERT INTO userLogin (userName,userType,userPassword) VALUES ('" + userInput.getName() + "' , '"
          + userInput.getType() + "' , '" + userInput.getPassword() + "')";
      stmt.executeUpdate(sql);
      System.out.println(userInput.getName() + " " + userInput.getType() + " " + userInput.getPassword());

      session.setAttribute("username", userInput.getName());
      session.setAttribute("type", userInput.getType());

      return "redirect:/pomodoro";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/pomodoro/login")
  public String getLoginForm(Map<String, Object> model) {
    Users userLogin = new Users();
    model.put("userLogin", userLogin);
    return "login";
  }

  @PostMapping(path = "/pomodoro/login", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public String getUserData(Map<String, Object> model, Users logInfo, HttpSession session) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      String sql = "SELECT userName, userPassword, userType FROM userLogin";

      ResultSet rs = stmt.executeQuery(sql);

      while (rs.next()) {
        if (rs.getString("userName").equals(logInfo.getName())
            && rs.getString("userPassword").equals(logInfo.getPassword())) {
          
          session.setAttribute("username", rs.getString("userName"));
          session.setAttribute("type", rs.getString("userType"));
          return "redirect:/pomodoro";
        }
      }
      return "error_login";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/pomodoro/admin")
  public String getAdminResult(Map<String, Object> model, HttpSession session) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      if (!session.getAttribute("type").equals("Admin")) {
        return "error_admin";
      }

      ResultSet rs = stmt.executeQuery("SELECT * FROM userLogin");

      ArrayList<Users> output = new ArrayList<Users>();
      while (rs.next()) {
        if (!rs.getString("userName").equals(session.getAttribute("username"))) {
          Users user = new Users();
          user.setName(rs.getString("userName"));
          user.setId(rs.getInt("id"));
          user.setType(rs.getString("userType"));
          output.add(user);
        }
      }

      model.put("records", output);
      return "admin";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

  @GetMapping("/delete/{pid}")
  public String deleteUser(Map<String, Object> model, @PathVariable String pid) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("DELETE FROM userLogin WHERE id=" + pid);
      System.out.println("Successfully Deleted");

      return "delete";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping("/logout")
  public String getLogout(Map<String, Object> model, HttpSession session) {
    session.removeAttribute("username");
    session.invalidate();
    return "redirect:/pomodoro";
  }


  @ResponseBody
  @PostMapping(path = "/time", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
  public void getUserUsage(@RequestBody String usage, HttpSession session) {
    try (Connection connection = dataSource.getConnection()) {
      usage = removeLastChar(usage);

      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS userUsage (id serial, userName varchar(20), userUsage smallint, date timestamp)");

      if (session != null) {
        String sql = "INSERT INTO userUsage (userName,userUsage,date) VALUES ('" + session.getAttribute("username")
            + "' , '" + usage + "' , now())";

        stmt.executeUpdate(sql);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(session.getAttribute("username") + " " + usage + " " + date);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @GetMapping("/pomodoro/report")
  public String getUserUsage(Map<String, Object> model, HttpSession session){
    try (Connection connection =  dataSource.getConnection()){
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM userUsage WHERE userName=" + "'"+ session.getAttribute("username")+ "'");
      int usage = 0;
      while(rs.next()){
        usage += rs.getInt("userUsage");
      }
      model.put("records", usage);
      return "report";
    } catch (Exception e){
      model.put("message", e.getMessage());
      return "error";
    }
  }


  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }


  public static String removeLastChar(String s) {
    return (s == null || s.length() == 0)
      ? null 
      : (s.substring(0, s.length() - 1));
  }

}
