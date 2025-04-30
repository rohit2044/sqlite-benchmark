package org.example.repository.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Primary key class for Items entity
 * Represents a composite primary key consisting of itemId and requestId
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPK implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private Long itemId;
    private Long requestId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPK itemPK = (ItemPK) o;
        return Objects.equals(itemId, itemPK.itemId) &&
               Objects.equals(requestId, itemPK.requestId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemId, requestId);
    }
}