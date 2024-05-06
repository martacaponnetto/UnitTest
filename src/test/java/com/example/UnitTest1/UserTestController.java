package com.example.UnitTest1;


import com.example.UnitTest1.Controller.UserController;
import com.example.UnitTest1.Entities.User;
import com.example.UnitTest1.Repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    private User makeUser() {
        User user = new User();
        user.setFullName("Marta Caponnetto");
        user.setEmail("MC@gmail.com");
        return user;
    }

    @Test
    void createUser() throws Exception {
        User user = makeUser();

        String userJSON = objectMapper.writeValueAsString(user);

        MvcResult result = this.mvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON).content(userJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        User userResult = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

        assertThat(userResult.getId()).isNotNull();
        assertThat(userResult.getEmail()).isEqualTo(user.getEmail());
        assertThat(userResult.getFullName()).isEqualTo(user.getFullName());

    }


    @Test
    void getAllUsers() throws Exception {
        User user1 = makeUser();
        User user2 = makeUser();
        user2.setEmail("giovanni.bianchi@example.com");
        userRepository.save(user1);
        userRepository.save(user2);

        MvcResult result = this.mvc.perform(get("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<User> usersResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {
        });

        assertThat(usersResult).hasSize(2);
        assertThat(usersResult.get(0).getEmail()).isEqualTo(user1.getEmail());
        assertThat(usersResult.get(1).getEmail()).isEqualTo(user2.getEmail());
    }

    @Test
    void updateUser() throws Exception {
        User user = makeUser();
        userRepository.save(user);

        User updatedUser = new User();
        updatedUser.setFullName("Giuseppe Vderdi");
        updatedUser.setEmail("giuseppe.verdi@example.com");

        String updatedUserJSON = objectMapper.writeValueAsString(updatedUser);

        MvcResult result = this.mvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(updatedUserJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        User userResult = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

        assertThat(userResult.getId()).isEqualTo(user.getId());
        assertThat(userResult.getFullName()).isEqualTo(updatedUser.getFullName());
        assertThat(userResult.getEmail()).isEqualTo(updatedUser.getEmail());
    }

    @Test

    void deleteUser() throws Exception {
        User user = makeUser();
        userRepository.save(user);

        this.mvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }


}

