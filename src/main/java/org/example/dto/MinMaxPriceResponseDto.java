package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinMaxPriceResponseDto {
    private String category;
    private List<Map<String, String>> minPrice;
    private List<Map<String, String>> maxPrice;
}
