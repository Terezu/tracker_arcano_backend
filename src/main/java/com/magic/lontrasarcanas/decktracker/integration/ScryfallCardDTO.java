package com.magic.lontrasarcanas.decktracker.integration;

import lombok.Data;
import java.util.Map;

@Data
public class ScryfallCardDTO {
    private String name;
    private Map<String, String> legalities;
}
