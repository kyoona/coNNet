package houseInception.connet.contoller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import houseInception.connet.service.UserBlockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserBlockControllerTest {

    private MockMvc mockMvc;

    @Mock
    UserBlockController userBlockController;
    @InjectMocks
    UserBlockService userBlockService;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(userBlockController)
                .build();
    }

    @Test
    void blockUser() throws Exception {
        mockMvc.perform(post("/userBlocks/1"))
                .andExpect(status().isOk());
    }
}