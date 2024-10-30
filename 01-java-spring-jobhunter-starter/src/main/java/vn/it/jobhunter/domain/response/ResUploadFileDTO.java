package vn.it.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResUploadFileDTO {
    private String fileName;
    private Instant uploadAt;
}
