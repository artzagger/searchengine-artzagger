package searchengine.dto.startIndexing;

import lombok.Data;

@Data
public class StartIndexingResponse {
    private boolean result;
    private StartIndexingData startingIndexing;
}
