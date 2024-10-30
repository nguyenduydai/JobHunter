
package vn.it.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.it.jobhunter.domain.Role;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.service.RoleService;
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
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a Role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role)
            throws IdInvalidException {
        if (this.roleService.isNameExist(role.getName())) {
            throw new IdInvalidException("Role da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.roleService.handleCreateRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("update Role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role postRole)
            throws IdInvalidException {
        Role currRole = this.roleService.fetchRoleById(postRole.getId());
        if (currRole == null) {
            throw new IdInvalidException("Role id = " + postRole.getId() + "khong ton tai");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.handleUpdateRole(postRole, currRole));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete Role")
    public ResponseEntity<String> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Role currRole = this.roleService.fetchRoleById(id);
        if (currRole == null)
            throw new IdInvalidException("Role id = " + currRole.getId() + "khong ton tai");
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted Role");
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("fetch Role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) {
        Role Role = this.roleService.fetchRoleById(id);
        return ResponseEntity.ok(Role);
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all Role")
    public ResponseEntity<ResultPaginationDTO> getAllEntityRole(@Filter Specification<Role> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.roleService.fetchAllRole(spec, pageable));
    }

}
