
package vn.it.jobhunter.controller;

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
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import vn.it.jobhunter.domain.Skill;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.service.SkillService;
import vn.it.jobhunter.utils.annotation.ApiMessage;
import vn.it.jobhunter.utils.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create a skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        if (skill.getName() != null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("skill name = " + skill.getName() + "da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(skill));
    }

    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill postSkill) throws IdInvalidException {
        Skill currSkill = this.skillService.fetchSkillById(postSkill.getId());
        if (currSkill == null)
            throw new IdInvalidException("skill id = " + postSkill.getId() + "khong ton tai");
        if (postSkill.getName() != null && this.skillService.isNameExist(postSkill.getName())) {
            throw new IdInvalidException("skill name = " + postSkill.getName() + "da ton tai");
        }
        currSkill.setName(postSkill.getName());
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.handleUpdateSkill(postSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("delete skill")
    public ResponseEntity<String> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill currSkill = this.skillService.fetchSkillById(id);
        if (currSkill == null)
            throw new IdInvalidException("skill id = " + currSkill.getId() + "khong ton tai");
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted skill");
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("fetch skill by id")
    public ResponseEntity<Skill> getSkillById(@PathVariable("id") long id) {
        Skill skill = this.skillService.fetchSkillById(id);
        return ResponseEntity.ok(skill);
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skill")
    public ResponseEntity<ResultPaginationDTO> getAllEntitySkill(@Filter Specification<Skill> spec, Pageable pageable) {

        return ResponseEntity.ok(this.skillService.fetchAllSkill(spec, pageable));
    }

}
