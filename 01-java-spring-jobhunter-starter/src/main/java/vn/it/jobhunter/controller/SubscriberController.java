
package vn.it.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import vn.it.jobhunter.domain.Subscriber;
import vn.it.jobhunter.service.SubscriberService;
import vn.it.jobhunter.utils.SecurityUtil;
import vn.it.jobhunter.utils.annotation.ApiMessage;
import vn.it.jobhunter.utils.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create a Subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {
        if (this.subscriberService.isEmailExist(subscriber.getEmail())) {
            throw new IdInvalidException("Subscriber da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.subscriberService.handleCreateSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("update Subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@Valid @RequestBody Subscriber postSubscriber)
            throws IdInvalidException {
        Subscriber currSubscriber = this.subscriberService.fetchSubscriberById(postSubscriber.getId());
        if (currSubscriber == null) {
            throw new IdInvalidException("Subscriber id = " + postSubscriber.getId() + "khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.subscriberService.handleUpdateSubscriber(postSubscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("get Subscriber skill")
    public ResponseEntity<Subscriber> getSubscriberSkill()
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.subscriberService.findByEmail(email));
    }

}
