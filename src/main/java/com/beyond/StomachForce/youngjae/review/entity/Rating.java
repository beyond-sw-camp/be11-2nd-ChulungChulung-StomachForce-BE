package com.beyond.StomachForce.youngjae.review.entity;

import lombok.Getter;

@Getter
public enum Rating {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    Rating(int value) {
        this.value = value;
    }

    public static Rating fromValue(int value) {
        for (Rating rating : Rating.values()) {
            if (rating.getValue() == value) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Invalid rating value: " + value);
    }
}
