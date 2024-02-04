package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.startIndexing.StartIndexingResponse;
import searchengine.dto.stopIndexing.StopIndexingResponse;

@Service
@RequiredArgsConstructor
public class StopIndexingServiceImpl implements StopIndexingService {

    @Override
    public StopIndexingResponse threadIsStopped() {


        StopIndexingResponse response = new StopIndexingResponse();
        response.setResult(true);
        return response;

    }

    void stopThread(Thread thread) {
        System.out.println("StopIndexingService");
        thread.interrupt();
    }

}
