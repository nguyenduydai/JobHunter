
package vn.it.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.it.jobhunter.domain.Permission;
import vn.it.jobhunter.domain.Role;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.repository.PermissionRepository;
import vn.it.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role handleCreateRole(Role r) {
        if (r.getPermissions() != null) {
            List<Long> p = r.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Permission> ps = this.permissionRepository.findByIdIn(p);
            r.setPermissions(ps);
        }
        return this.roleRepository.save(r);
    }

    public Role handleUpdateRole(Role r, Role roleDB) {
        if (r.getPermissions() != null) {
            List<Long> p = r.getPermissions().stream().map(x -> x.getId()).collect(Collectors.toList());
            List<Permission> ps = this.permissionRepository.findByIdIn(p);
            r.setPermissions(ps);
        }
        roleDB.setName(r.getName());
        roleDB.setActive(r.isActive());
        roleDB.setDescription(r.getDescription());
        roleDB.setPermissions(r.getPermissions());
        return this.roleRepository.save(roleDB);
    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public Role fetchRoleById(long id) {
        Optional<Role> Role = this.roleRepository.findById(id);
        if (Role.isPresent())
            return Role.get();
        return null;
    }

    public ResultPaginationDTO fetchAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(spec, pageable);
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

    public Optional<Role> findById(long id) {
        return this.roleRepository.findById(id);
    }

    public boolean isNameExist(String name) {
        return this.roleRepository.existsByName(name);
    }
}
