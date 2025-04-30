package org.example.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.repository.entity.Items;

import java.util.ArrayList;
import java.util.List;

@Converter
public class SimilarItemListConverter implements AttributeConverter<List<Items.SimilarItem>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(List<Items.SimilarItem> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    @Override
    public List<Items.SimilarItem> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(dbData, 
                    new TypeReference<List<Items.SimilarItem>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}