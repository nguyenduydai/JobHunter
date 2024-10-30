
package vn.it.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import vn.it.jobhunter.domain.Job;
import vn.it.jobhunter.domain.response.ResCreateJobDTO;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.service.JobService;
import vn.it.jobhunter.utils.annotation.ApiMessage;
import vn.it.jobhunter.utils.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("create a job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("update a job")
    public ResponseEntity<Job> updateJob(@Valid @RequestBody Job postJob) throws IdInvalidException {
        Job currJob = this.jobService.fetchJobById(postJob.getId());
        if (currJob == null)
            throw new IdInvalidException("Job id = " + postJob.getId() + "khong ton tai");
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleUpdateJob(postJob, currJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete Job")
    public ResponseEntity<String> deleteJob(@PathVariable("id") long id) throws IdInvalidException {
        Job currJob = this.jobService.fetchJobById(id);
        if (currJob == null)
            throw new IdInvalidException("Job id = " + currJob.getId() + "khong ton tai");
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted Job");
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("fetch Job by id")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
        Job Job = this.jobService.fetchJobById(id);
        return ResponseEntity.ok(Job);
    }

    @GetMapping("/jobs")
    @ApiMessage("fetch all Job")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Filter Specification<Job> spec, Pageable pageable) {

        return ResponseEntity.ok(this.jobService.fetchAllJob(spec, pageable));
    }

}
