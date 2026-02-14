package com.magic.lontrasarcanas.decktracker.integration;

import lombok.Data;
import java.util.List;

@Data
public class ScryfallResponseDTO {
    private List<ScryfallCardDTO> data;
    private List<String> not_found;
}
