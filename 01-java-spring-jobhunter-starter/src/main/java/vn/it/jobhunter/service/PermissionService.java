
package vn.it.jobhunter.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.it.jobhunter.domain.Permission;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleCreatePermission(Permission c) {
        return this.permissionRepository.save(c);
    }

    public Permission handleUpdatePermission(Permission permission) {
        Permission p = this.fetchPermissionById(permission.getId());
        if (p != null) {
            p.setName(permission.getName());
            p.setApiPath(permission.getApiPath());
            p.setMethod(permission.getMethod());
            p.setModule(permission.getModule());
        }
        return this.permissionRepository.save(this.permissionRepository.save(p));
    }

    public void handleDeletePermission(long id) {
        Optional<Permission> s = this.permissionRepository.findById(id);
        Permission currPermission = s.get();
        currPermission.getRoles().forEach(role -> role.getPermissions().remove(currPermission));
        this.permissionRepository.delete(currPermission);
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> Permission = this.permissionRepository.findById(id);
        if (Permission.isPresent())
            return Permission.get();
        return null;
    }

    public ResultPaginationDTO fetchAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(spec, pageable);
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

    public Optional<Permission> findById(long id) {
        return this.permissionRepository.findById(id);
    }

    public boolean isPermissionExists(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(p.getModule(), p.getApiPath(),
                p.getMethod());
    }

    public boolean isSameName(Permission p) {
        Permission pe = this.fetchPermissionById(p.getId());
        if (pe != null && pe.getName().equals(pe.getName())) {
            return true;
        }
        return false;
    }

}
