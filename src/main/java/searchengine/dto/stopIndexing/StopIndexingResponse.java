package searchengine.dto.stopIndexing;


import lombok.Data;

@Data
public class StopIndexingResponse {
    private boolean result;
    private searchengine.dto.stopIndexing.StopIndexingData stopIndexing;
}
