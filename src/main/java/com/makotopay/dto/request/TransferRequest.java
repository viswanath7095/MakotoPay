package com.makotopay.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Invalid email format")
    private String receiverEmail;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Minimum transfer amount is 1")
    private BigDecimal amount;

    private String description;

    // Idempotency Key — double payment ఆపడానికి
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
}