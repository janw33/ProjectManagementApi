package com.janwypych.ProjectManagementApi.controllers.workspace;

import com.janwypych.ProjectManagementApi.TestDataUtil;
import com.janwypych.ProjectManagementApi.dtos.workspace.WorkspaceSummaryResponse;
import com.janwypych.ProjectManagementApi.entities.User;
import com.janwypych.ProjectManagementApi.entities.enums.WorkspaceRole;
import com.janwypych.ProjectManagementApi.services.WorkspaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GetWorkspacesTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkspaceService workspaceService;

    private Authentication createAuthentication() {
        User user = TestDataUtil.user();

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                null
        );
    }

    private RequestPostProcessor authenticatedUser() {
        return authentication(createAuthentication());
    }

    private void performGet(ResultMatcher... matchers) throws Exception {
        var result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/workspaces")
                        .with(authenticatedUser())
        );

        for (ResultMatcher matcher : matchers) {
            result.andExpect(matcher);
        }
    }
    @Test
    public void shouldReturn401WhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/workspaces"))
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    public void shouldReturnHttp200() throws Exception {
        Page<WorkspaceSummaryResponse> page =
                new PageImpl<>(List.of(
                        WorkspaceSummaryResponse.builder()
                                .id(1L)
                                .name("Workspace")
                                .role(WorkspaceRole.OWNER)
                                .build()
                ));

        when(workspaceService.getWorkspaces(any(User.class), any(Pageable.class)))
                .thenReturn(page);

        performGet(status().isOk(),
                jsonPath("$.content").isArray(),
                jsonPath("$.totalElements").value(1),
                jsonPath("$.content[0].id").value(1),
                jsonPath("$.content[0].name").value("Workspace"),
                jsonPath("$.content[0].role").value("OWNER"));
    }
}
