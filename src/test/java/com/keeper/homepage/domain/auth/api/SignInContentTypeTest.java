package com.keeper.homepage.domain.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.auth.application.SignInService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SignInContentTypeTest {

  private static final String VALID_JSON = """
      {
        "loginId": "loginId",
        "password": "password123"
      }
      """;

  private final SignInService signInService = mock(SignInService.class);
  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(new SignInController(signInService)).build();

  @Test
  void acceptsJsonContentType() throws Exception {
    when(signInService.signIn(any(), anyString(), any())).thenReturn(null);

    mockMvc.perform(post("/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_JSON))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      MediaType.TEXT_PLAIN_VALUE,
      MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      MediaType.MULTIPART_FORM_DATA_VALUE
  })
  void rejectsNonJsonContentTypes(String contentType) throws Exception {
    mockMvc.perform(post("/sign-in")
            .contentType(contentType)
            .content(VALID_JSON))
        .andExpect(status().isUnsupportedMediaType());

    verifyNoInteractions(signInService);
  }

  @Test
  void rejectsMissingContentType() throws Exception {
    mockMvc.perform(post("/sign-in")
            .content(VALID_JSON))
        .andExpect(status().isUnsupportedMediaType());

    verifyNoInteractions(signInService);
  }
}
