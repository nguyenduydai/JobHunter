package vn.it.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.it.jobhunter.utils.constant.ResumeStateEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResResumeDTO {
    private long id;
    private String email;
    private String url;
    private ResumeStateEnum state;
    private String createdBy;

    private String updatedBy;

    private Instant createdAt;

    private Instant updatedAt;

    private JobResume jobResume;
    private UserResume userResume;
    private String companyName;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResume {
        private long id;
        private String name;
    }

}
