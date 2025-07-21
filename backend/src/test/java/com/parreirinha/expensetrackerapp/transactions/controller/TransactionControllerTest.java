package com.parreirinha.expensetrackerapp.transactions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parreirinha.expensetrackerapp.auth.service.JwtService;
import com.parreirinha.expensetrackerapp.config.SecurityConfiguration;
import com.parreirinha.expensetrackerapp.testconfig.NoCsrfTestConfig;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionRequestDto;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionResponseDto;
import com.parreirinha.expensetrackerapp.transactions.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.parreirinha.expensetrackerapp.transactions.domain.TransactionType;

@WebMvcTest(TransactionController.class)
@Import({SecurityConfiguration.class, NoCsrfTestConfig.class})
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;
    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getTransactions_ShouldReturn200_WhenExist() throws Exception {
        // Arrange
        List<TransactionResponseDto> transactions = Arrays.asList(
                new TransactionResponseDto(UUID.randomUUID(), BigDecimal.TEN, null, null, null),
                new TransactionResponseDto(UUID.randomUUID(), BigDecimal.ONE, null, null, null)
        );
        when(transactionService.getTransactions("user")).thenReturn(transactions);
        // Act & Assert
        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(BigDecimal.TEN))
                .andExpect(jsonPath("$[1].amount").value(BigDecimal.ONE));
    }

    @Test
    void getTransactions_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getTransaction_ShouldReturn200_WhenExists() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TransactionResponseDto dto = new TransactionResponseDto(id, BigDecimal.TEN, null, null, null);
        when(transactionService.getTransaction("user", id)).thenReturn(dto);
        // Act & Assert
        mockMvc.perform(get("/transactions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(BigDecimal.TEN));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getTransaction_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(transactionService.getTransaction("user", id)).thenThrow(new com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException("Transaction not found"));
        // Act & Assert
        mockMvc.perform(get("/transactions/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found"));
    }

    @Test
    void getTransaction_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(get("/transactions/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createTransaction_ShouldReturn201_WhenCreated() throws Exception {
        // Arrange
        TransactionRequestDto req = new TransactionRequestDto(
                BigDecimal.TEN,
                null, // categoryId
                TransactionType.INCOME, // ou outro valor válido
                LocalDate.now()
        );
        // Act & Assert
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createTransaction_ShouldReturn400_WhenInvalid() throws Exception {
        // Arrange
        TransactionRequestDto req = new TransactionRequestDto(null, null, null, null);
        doThrow(new IllegalArgumentException("Invalid transaction")).when(transactionService).createTransaction(Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"date\":\"Date is required\",\"amount\":\"Amount is required\",\"type\":\"Transaction type is required\"}"));
    }

    @Test
    void createTransaction_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        TransactionRequestDto req = new TransactionRequestDto(BigDecimal.TEN, null, null, null);
        // Act & Assert
        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateTransaction_ShouldReturn204_WhenUpdated() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TransactionRequestDto req = new TransactionRequestDto(
                BigDecimal.ONE,
                null, // categoryId
                TransactionType.INCOME, // ou outro valor válido
                LocalDate.now()
        );
        // Act & Assert
        mockMvc.perform(put("/transactions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateTransaction_ShouldReturn400_WhenInvalid() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TransactionRequestDto req = new TransactionRequestDto(null, null, null, null);
        doThrow(new IllegalArgumentException("Invalid transaction")).when(transactionService).updateTransaction(Mockito.eq(id), Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(put("/transactions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"date\":\"Date is required\",\"amount\":\"Amount is required\",\"type\":\"Transaction type is required\"}"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateTransaction_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TransactionRequestDto req = new TransactionRequestDto(
                BigDecimal.ONE,
                null, // categoryId
                TransactionType.INCOME,
                LocalDate.now()
        );
        doThrow(new com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException("Transaction not found")).when(transactionService).updateTransaction(Mockito.eq(id), Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(put("/transactions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found"));
    }

    @Test
    void updateTransaction_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TransactionRequestDto req = new TransactionRequestDto(BigDecimal.ONE, null, null, null);
        // Act & Assert
        mockMvc.perform(put("/transactions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateTransaction_ShouldReturn500_WhenUnexpectedException() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        TransactionRequestDto req = new TransactionRequestDto(
                BigDecimal.ONE,
                null, // categoryId
                TransactionType.INCOME,
                LocalDate.now()
        );
        doThrow(new RuntimeException("Unexpected error")).when(transactionService).updateTransaction(Mockito.eq(id), Mockito.eq("user"), Mockito.any());
        // Act & Assert
        mockMvc.perform(put("/transactions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteTransaction_ShouldReturn204_WhenDeleted() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(delete("/transactions/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteTransaction_ShouldReturn404_WhenNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException("Transaction not found")).when(transactionService).deleteTransaction(id, "user");
        // Act & Assert
        mockMvc.perform(delete("/transactions/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found"));
    }

    @Test
    void deleteTransaction_ShouldReturn403_WhenUnauthorized() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act & Assert
        mockMvc.perform(delete("/transactions/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void deleteTransaction_ShouldReturn500_WhenUnexpectedException() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new RuntimeException("Unexpected error")).when(transactionService).deleteTransaction(id, "user");
        // Act & Assert
        mockMvc.perform(delete("/transactions/" + id))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }
} 