package com.example.internet_shop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    void login_WithoutParams_ShouldReturnLoginView() {
        Model model = new BindingAwareModelMap();
        // Передаём null для cart, создаём новый session
        MockHttpSession session = new MockHttpSession();
        String result = authController.login(null, session, model);
        assertThat(result).isEqualTo("auth/login");
    }

    @Test
    void login_WithCartParam_ShouldShowCartHint() {
        Model model = new BindingAwareModelMap();
        MockHttpSession session = new MockHttpSession();
        String result = authController.login(true, session, model);
        assertThat(result).isEqualTo("auth/login");
        assertThat(model.containsAttribute("showCartHint")).isTrue();
    }

    @Test
    void login_WithSessionPendingProduct_ShouldShowCartHint() {
        Model model = new BindingAwareModelMap();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("pendingProductId", 1L);

        String result = authController.login(null, session, model);
        assertThat(result).isEqualTo("auth/login");
        assertThat(model.containsAttribute("showCartHint")).isTrue();
    }

    @Test
    void authController_ShouldBeLoaded() {
        assertThat(authController).isNotNull();
    }
}

