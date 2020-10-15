package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplication;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceImplTest {

    // bring in the thing we're testing:
    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        // mocks: fake data or methods (ed. - in some non-Java, non-JS contexts, fake methods are "stubs". Or maybe mocks. *shrug*)
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void a_findUserById() {
        assertEquals("test_admin", userService.findUserById(4).getUsername());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void b_findUserByIdNonexistent() {
        userService.findUserById(1000);
    }

    @Test
    public void c_findByNameContaining() {
        assertEquals(1, userService.findByNameContaining("admin").size());
    }

    @Test
    public void d_findAll() {
        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void e_findByName() {
        assertEquals(4, userService.findByName("test_admin").getUserid());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void f_findByNameNonexistent() {
        userService.findByName("sir not appearing in this database");
    }

    @Test
    public void g_save() {
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

        User newUser = userService.save(u1);
        assertNotNull(newUser);
        assertEquals("test_admin_again", newUser.getUsername());
    }

    @Test
    public void i_update() {
        User u1 = new User();
        u1.setUserid(4);
        u1.setUsername("test_admin's_evil_twin");
        u1.setPassword("blah");
        u1.setPrimaryemail("joe@evil.co");
        u1.getUseremails().add(new Useremail(u1, "jane@evil.co"));
        Role role = new Role("evil admin");
        role.setRoleid(1);
        u1.getRoles().add(new UserRoles(u1, role));

        User newUser = userService.update(u1, 4);
        assertNotNull(newUser);
        assertEquals("test_admin's_evil_twin", newUser.getUsername());
    }

    @Test
    public void j_delete() {
        userService.delete(4);
        assertEquals(4, userService.findAll().size());
    }

    @Test
    public void k_deleteAll() {
        userService.deleteAll();
        assertEquals(0, userService.findAll().size());
    }
}