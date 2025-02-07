package com.beyond.StomachForce.review.converter;


import com.beyond.StomachForce.review.entity.Rating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Rating rating) {
        return (rating == null) ? null : rating.getValue();
    }

    @Override
    public Rating convertToEntityAttribute(Integer value) {
        return (value == null) ? null : Rating.fromValue(value);
    }
}
