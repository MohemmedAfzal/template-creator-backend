package com.example.textexpander;

import com.collablynk.accounts.test.auth.WithEdifecsUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = ANY)
class TextexpanderApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    @WithEdifecsUser()
    void testFileNameValidationEndpoint() throws Exception {
        mvc.perform(get("/validate/account123/testfile.txt"))
                .andExpect(status().isOk());
    }

}
