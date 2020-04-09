package com.chowdury.demo.models;

import lombok.Data;

@Data
public class LocationStats {
    private String state;
    private  String country;
    private  int latestTotalCase;
    private  int diffFromPreviousDay;
}
