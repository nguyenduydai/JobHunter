
package vn.it.jobhunter.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.it.jobhunter.domain.Skill;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill c) {
        return this.skillRepository.save(c);
    }

    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public void handleDeleteSkill(long id) {
        Optional<Skill> s = this.skillRepository.findById(id);
        Skill currSkill = s.get();
        currSkill.getJobs().forEach(job -> job.getSkills().remove(currSkill));
        currSkill.getSubscribers().forEach(subscriber -> subscriber.getSkills().remove(currSkill));
        this.skillRepository.delete(currSkill);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        if (skill.isPresent())
            return skill.get();
        return null;
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> page = this.skillRepository.findAll(spec, pageable);
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

    public Optional<Skill> findById(long id) {
        return this.skillRepository.findById(id);
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }
}
