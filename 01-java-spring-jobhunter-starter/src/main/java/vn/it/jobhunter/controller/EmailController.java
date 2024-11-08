package vn.it.jobhunter.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.it.jobhunter.service.EmailService;
import vn.it.jobhunter.service.SubscriberService;
import vn.it.jobhunter.utils.annotation.ApiMessage;

@Controller
@RequestMapping("/api/v1")
public class EmailController {
    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("send simple email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
        this.subscriberService.sendSubscriberEmailJobs();
        return "ok";
    }

}
