
package vn.it.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import vn.it.jobhunter.domain.Permission;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.service.PermissionService;
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
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("create a Permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        if (this.permissionService.isPermissionExists(permission)) {
            throw new IdInvalidException("Permission da ton tai");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.handleCreatePermission(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("update Permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission postPermission)
            throws IdInvalidException {
        Permission currPermission = this.permissionService.fetchPermissionById(postPermission.getId());
        if (currPermission == null)
            throw new IdInvalidException("Permission id = " + postPermission.getId() + "khong ton tai");
        if (this.permissionService.isPermissionExists(postPermission)) {
            if (this.permissionService.isSameName(postPermission)) {
                throw new IdInvalidException("Permission da ton tai");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.handleUpdatePermission(postPermission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete Permission")
    public ResponseEntity<String> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Permission currPermission = this.permissionService.fetchPermissionById(id);
        if (currPermission == null)
            throw new IdInvalidException("Permission id = " + currPermission.getId() + "khong ton tai");
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted Permission");
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("fetch Permission by id")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") long id) {
        Permission Permission = this.permissionService.fetchPermissionById(id);
        return ResponseEntity.ok(Permission);
    }

    @GetMapping("/permissions")
    @ApiMessage("fetch all Permission")
    public ResponseEntity<ResultPaginationDTO> getAllEntityPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.permissionService.fetchAllPermission(spec, pageable));
    }

}
