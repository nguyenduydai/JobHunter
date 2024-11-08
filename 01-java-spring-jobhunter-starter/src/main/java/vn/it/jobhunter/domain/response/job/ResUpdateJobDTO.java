
package vn.it.jobhunter.domain.response.job;

import lombok.Getter;
import lombok.Setter;
import vn.it.jobhunter.utils.constant.LevelEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateJobDTO {

    private long id;

    private String name;

    private String description;

    private LevelEnum level;

    private String location;

    private double salary;

    private int quantity;

    private boolean active;

    private Instant StartDate;

    private Instant EndDate;

    private String updatedBy;

    private Instant updatedAt;
    private List<String> skills;
}
