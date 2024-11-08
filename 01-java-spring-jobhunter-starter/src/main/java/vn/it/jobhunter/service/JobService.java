package vn.it.jobhunter.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.it.jobhunter.domain.Company;
import vn.it.jobhunter.domain.Job;
import vn.it.jobhunter.domain.Skill;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.it.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.it.jobhunter.domain.response.user.ResCreateUserDTO;
import vn.it.jobhunter.repository.CompanyRepository;
import vn.it.jobhunter.repository.JobRepository;
import vn.it.jobhunter.repository.SkillRepository;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO handleCreateJob(Job j) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }
        // check company
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent())
                j.setCompany(cOptional.get());
        }
        return this.convertToResCreateJobDTO(this.jobRepository.save(j));
    }

    public ResUpdateJobDTO handleUpdateJob(Job j, Job jobInDb) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills().stream()
                    .map(x -> x.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDb.setSkills(dbSkills);
        }
        // check company
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent())
                jobInDb.setCompany(cOptional.get());
        }
        jobInDb.setName(j.getName());
        jobInDb.setSalary(j.getSalary());
        jobInDb.setQuantity(j.getQuantity());
        jobInDb.setLocation(j.getLocation());
        jobInDb.setLevel(j.getLevel());
        jobInDb.setActive(j.isActive());
        jobInDb.setStartDate(j.getStartDate());
        jobInDb.setEndDate(j.getEndDate());
        Job currj = this.jobRepository.save(jobInDb);
        return convertToResUpdateJobDTO(currj);
    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public Job fetchJobById(long id) {
        Optional<Job> job = this.jobRepository.findById(id);
        if (job.isPresent())
            return job.get();
        return null;
    }

    public ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> page = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(page.getContent());
        return rs;
    }

    public ResCreateJobDTO convertToResCreateJobDTO(Job job) {
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setDescription(job.getDescription());
        res.setLevel(job.getLevel());
        res.setLocation(job.getLocation());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setActive(job.isActive());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setCreatedAt(job.getCreatedAt());
        res.setCreatedBy(job.getCreatedBy());
        if (job.getSkills() != null) {
            res.setSkills(job.getSkills().stream()
                    .map(x -> x.getName()).collect(Collectors.toList()));
        }
        return res;
    }

    public ResUpdateJobDTO convertToResUpdateJobDTO(Job job) {
        ResUpdateJobDTO res = new ResUpdateJobDTO();
        res.setId(job.getId());
        res.setName(job.getName());
        res.setDescription(job.getDescription());
        res.setLevel(job.getLevel());
        res.setLocation(job.getLocation());
        res.setSalary(job.getSalary());
        res.setQuantity(job.getQuantity());
        res.setActive(job.isActive());
        res.setStartDate(job.getStartDate());
        res.setEndDate(job.getEndDate());
        res.setUpdatedAt(job.getUpdatedAt());
        res.setUpdatedBy(job.getUpdatedBy());
        if (job.getSkills() != null) {
            res.setSkills(job.getSkills().stream()
                    .map(x -> x.getName()).collect(Collectors.toList()));
        }
        return res;
    }
}
