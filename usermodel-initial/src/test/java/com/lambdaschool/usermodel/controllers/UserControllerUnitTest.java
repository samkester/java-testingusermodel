package com.lambdaschool.usermodel.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.usermodel.UserModelApplication;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.services.HelperFunctions;
import com.lambdaschool.usermodel.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private HelperFunctions helperFunctions;

    private List<User> users;

    @Before
    public void setUp() throws Exception {
        users = new ArrayList<>();

        Role r1 = new Role("admin");
        r1.setRoleid(1);
        Role r2 = new Role("user");
        r2.setRoleid(2);
        Role r3 = new Role("data");
        r3.setRoleid(3);

        // admin, data, user
        User u1 = new User("test_admin",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid(1);
        u1.getRoles()
                .add(new UserRoles(u1, r1));
        u1.getRoles()
                .add(new UserRoles(u1, r2));
        u1.getRoles()
                .add(new UserRoles(u1, r3));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails().get(0).setUseremailid(1);
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@mymail.local"));
        u1.getUseremails().get(1).setUseremailid(2);
        users.add(u1);

        // data, user
        User u2 = new User("test_cinnamon",
                "1234567",
                "cinnamon@lambdaschool.local");
        u2.getRoles()
                .add(new UserRoles(u2, r2));
        u2.getRoles()
                .add(new UserRoles(u2, r3));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "cinnamon@mymail.local"));
        u2.getUseremails().get(0).setUseremailid(3);
        u2.getUseremails()
                .add(new Useremail(u2,
                        "hops@mymail.local"));
        u2.getUseremails().get(1).setUseremailid(4);
        u2.getUseremails()
                .add(new Useremail(u2,
                        "bunny@email.local"));
        u2.getUseremails().get(2).setUseremailid(5);
        users.add(u2);

        // user
        User u3 = new User("test_barnbarn",
                "ILuvM4th!",
                "barnbarn@lambdaschool.local");
        u3.getRoles()
                .add(new UserRoles(u3, r2));
        u3.getUseremails()
                .add(new Useremail(u3,
                        "barnbarn@email.local"));
        u3.getUseremails().get(0).setUseremailid(6);
        users.add(u3);

        User u4 = new User("test_puttat",
                "password",
                "puttat@school.lambda");
        u4.getRoles()
                .add(new UserRoles(u4, r2));
        users.add(u4);

        User u5 = new User("test_misskitty",
                "password",
                "misskitty@school.lambda");
        u5.getRoles()
                .add(new UserRoles(u5, r2));
        users.add(u5);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listAllUsers() throws Exception {
        String apiUrl = "/users/users"; // note API call
        Mockito.when(userService.findAll()).thenReturn(users); // setup mock for the userService call

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON); // build an API request...
        MvcResult res = mockMvc.perform(rb).andReturn(); // ...invoke it...
        String result = res.getResponse().getContentAsString(); // ...save the result

        ObjectMapper mapper = new ObjectMapper(); // create a Jackson mapper
        String expected = mapper.writeValueAsString(users); // use it to manually convert users to JSON

        assertEquals(expected, result); // should return users as JSON
    }

    @Test
    public void getUserById() throws Exception {
        String apiUrl = "/users/user/1";
        Mockito.when(userService.findUserById(1)).thenReturn(users.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON); // build an API request...
        MvcResult res = mockMvc.perform(rb).andReturn(); // ...invoke it...
        String result = res.getResponse().getContentAsString(); // ...save the result

        ObjectMapper mapper = new ObjectMapper(); // create a Jackson mapper
        String expected = mapper.writeValueAsString(users.get(0)); // use it to manually convert users to JSON

        assertEquals(expected, result); // should return users as JSON
    }

    @Test
    public void getUserByIdNonexistent() throws Exception {
        String apiUrl = "/users/user/100";
        Mockito.when(userService.findUserById(100)).thenReturn(null);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON); // build an API request...
        MvcResult res = mockMvc.perform(rb).andReturn(); // ...invoke it...
        String result = res.getResponse().getContentAsString(); // ...save the result

        assertEquals("", result); // null item in JSON => ""
    }

    @Test
    public void getUserByName() throws Exception {
        String apiUrl = "/users/user/name/joe";
        Mockito.when(userService.findByName("joe")).thenReturn(users.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(rb).andReturn();
        String result = res.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(users.get(0));

        assertEquals(expected, result);
    }

    @Test
    public void getUserByNameNonexistent() throws Exception {
        String apiUrl = "/users/user/name/joe";
        Mockito.when(userService.findByName("joe")).thenReturn(null);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(rb).andReturn();
        String result = res.getResponse().getContentAsString();

        assertEquals("", result);
    }

    @Test
    public void getUserLikeName() throws Exception {
        String apiUrl = "/users/user/name/like/joe";
        Mockito.when(userService.findByNameContaining("joe")).thenReturn(users);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(rb).andReturn();
        String result = res.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(users);

        assertEquals(expected, result);
    }

    @Test
    public void addNewUser() throws Exception {
        User u1 = new User("test_admin_again",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid(4);

        Role r1 = new Role("admin");
        r1.setRoleid(1);
        u1.getRoles()
                .add(new UserRoles(u1, r1));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails().get(0).setUseremailid(12);

        ObjectMapper mapper = new ObjectMapper(); // create a Jackson mapper
        String userJson = mapper.writeValueAsString(u1); // use it to manually convert u1 to JSON


        String apiUrl = "/users/user";
        Mockito.when(userService.save(any(User.class))).thenReturn(u1);

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(userJson); // build an API request...
        MvcResult res = mockMvc.perform(rb).andExpect(status().isCreated()).andReturn(); // ...invoke it...
        String result = res.getResponse().getContentAsString(); // ...save the result

        assertEquals("", result); // null item in JSON => ""
    }

    @Test
    public void updateFullUser() throws Exception {
        User u1 = new User("test_admin_again",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid(4);

        Role r1 = new Role("admin");
        r1.setRoleid(1);
        u1.getRoles()
                .add(new UserRoles(u1, r1));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails().get(0).setUseremailid(12);

        ObjectMapper mapper = new ObjectMapper(); // create a Jackson mapper
        String userJson = mapper.writeValueAsString(u1); // use it to manually convert u1 to JSON


        String apiUrl = "/users/user/4";
        Mockito.when(userService.save(any(User.class))).thenReturn(u1);

        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(userJson);
        MvcResult res = mockMvc.perform(rb).andExpect(status().isOk()).andReturn();
        String result = res.getResponse().getContentAsString();

        assertEquals("", result);
    }

    @Test
    public void updateUser() throws Exception {
        User u1 = new User("test_admin_again",
                "password",
                "admin@lambdaschool.local");
        u1.setUserid(4);

        Role r1 = new Role("admin");
        r1.setRoleid(1);
        u1.getRoles()
                .add(new UserRoles(u1, r1));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));
        u1.getUseremails().get(0).setUseremailid(12);

        ObjectMapper mapper = new ObjectMapper(); // create a Jackson mapper
        String userJson = mapper.writeValueAsString(u1); // use it to manually convert u1 to JSON


        String apiUrl = "/users/user/4";
        Mockito.when(userService.update(any(User.class), eq(4))).thenReturn(u1);

        RequestBuilder rb = MockMvcRequestBuilders.patch(apiUrl).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(userJson);
        MvcResult res = mockMvc.perform(rb).andExpect(status().isOk()).andReturn();
        String result = res.getResponse().getContentAsString();

        assertEquals("", result);
    }

    @Test
    public void deleteUserById() throws Exception {
        String apiUrl = "/users/user/4";
        //Mockito.when(userService.delete(4)); // because the mock call doesn't DO anything, from the perspective of the UserController - that is, there's no return - we don't need to do anything here

        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(rb).andExpect(status().isOk()).andReturn();
        String result = res.getResponse().getContentAsString();

        assertEquals("", result);
    }
}