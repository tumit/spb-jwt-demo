package xyz.tumit.spbjwtdemo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import xyz.tumit.spbjwtdemo.member.model.Member;
import xyz.tumit.spbjwtdemo.security.model.ApplicationUser;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationIT {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<List<Member>> json;

    @Before
    public void setup() {
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void should_loggedin() throws Exception {
        // arrange
        val creds = ApplicationUser.builder()
                                   .username("tumit")
                                   .password("P@$$w0rd")
                                   .build();

        // act
        val requestBuilder = post("/login")
                .content(new ObjectMapper().writeValueAsBytes(creds))
                .accept(APPLICATION_JSON);
        val response = mvc.perform(requestBuilder).andReturn().getResponse();

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.value());
    }

    private String getToken(String username, String password) throws Exception {

        val creds = ApplicationUser.builder()
                                   .username(username)
                                   .password(password)
                                   .build();

        val requestBuilder = post("/login")
                .content(new ObjectMapper().writeValueAsBytes(creds))
                .accept(APPLICATION_JSON);
        MockHttpServletResponse response = mvc.perform(requestBuilder).andReturn().getResponse();
        return response.getHeader("Authorization");
    }


    @Test
    public void should_getData() throws Exception {

        // arrange
        val members = Arrays.asList(
                Member.builder().id(1L).name("cherprang").build(),
                Member.builder().id(2L).name("jennis").build()
        );
        val token = getToken("tumit", "P@$$w0rd");

        // act
        val requestBuilder = get("/members")
                .header("Authorization", token)
                .accept(APPLICATION_JSON);
        val response = mvc.perform(requestBuilder).andReturn().getResponse();

        // assert
        assertThat(response.getStatus()).isEqualTo(OK.value());
        assertThat(response.getContentAsString()).isEqualTo(json.write(members).getJson());
    }

    @Test
    public void should_accessDenied() throws Exception {

        // arrange
        // act
        val requestBuilder = get("/members").accept(APPLICATION_JSON);
        val response = mvc.perform(requestBuilder).andReturn().getResponse();

        // assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }
}
