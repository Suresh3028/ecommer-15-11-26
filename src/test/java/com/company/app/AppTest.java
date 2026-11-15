package com.company.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test
    void applicationShouldReturnCorrectMessage() {
        assertEquals(
            "E-commerce application is running!",
            App.message()
        );
    }
}
