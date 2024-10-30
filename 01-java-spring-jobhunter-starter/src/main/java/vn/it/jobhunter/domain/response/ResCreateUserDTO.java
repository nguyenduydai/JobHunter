package vn.it.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.it.jobhunter.domain.response.ResUserDTO.CompanyUser;
import vn.it.jobhunter.utils.constant.GenderEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private CompanyUser companyUser;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
