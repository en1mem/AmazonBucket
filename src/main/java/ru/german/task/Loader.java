package ru.german.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.german.service.LoadService;

@Component
public class Loader {

    @Autowired
    private LoadService service;

    @Scheduled(cron = "0 45 11 * * *")
    public void updateDbVersion() {
        service.download();
    }
}
