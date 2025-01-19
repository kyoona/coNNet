package houseInception.connet.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import houseInception.connet.dto.EmailDto;
import houseInception.connet.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FriendControllerTest {

    private MockMvc mockMvc;

    @Mock
    FriendController friendController;
    @InjectMocks
    FriendService friendService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(friendController)
                .build();
    }

    @Test
    void requestFriend() throws Exception {
        EmailDto emailDto = new EmailDto("email");

        mockMvc.perform(post("/friends/request")
                        .content(objectMapper.writeValueAsString(emailDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void requestFriend_이메일공백() throws Exception {
        EmailDto emailDto = new EmailDto(" ");

        mockMvc.perform(post("/friends/request")
                        .content(objectMapper.writeValueAsString(emailDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}