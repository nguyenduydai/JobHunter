package vn.it.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.it.jobhunter.domain.Company;
import vn.it.jobhunter.domain.Role;
import vn.it.jobhunter.domain.User;
import vn.it.jobhunter.domain.response.ResultPaginationDTO;
import vn.it.jobhunter.domain.response.user.ResCreateUserDTO;
import vn.it.jobhunter.domain.response.user.ResUpdateUserDTO;
import vn.it.jobhunter.domain.response.user.ResUserDTO;
import vn.it.jobhunter.repository.UserRepository;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> c = this.companyService.findById(user.getCompany().getId());
            user.setCompany(c.isPresent() ? c.get() : null);
        }
        if (user.getRole() != null) {
            Role r = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }
        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User user) {
        User u = this.fetchUserById(user.getId());
        if (u != null) {
            u.setName(user.getName());
            u.setAddress(user.getAddress());
            u.setAge(user.getAge());
            u = this.userRepository.save(u);
        }
        if (user.getCompany() != null) {
            Optional<Company> c = this.companyService.findById(user.getCompany().getId());

            u.setCompany(c.isPresent() ? c.get() : null);
        }
        if (user.getRole() != null) {
            Role r = this.roleService.fetchRoleById(user.getRole().getId());
            u.setRole(r != null ? r : null);
        }

        return this.userRepository.save(u);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent())
            return user.get();
        return null;
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        rs.setMeta(mt);

        List<ResUserDTO> list = page.getContent().stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());
        rs.setResult(list);

        return rs;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser c = new ResCreateUserDTO.CompanyUser();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            c.setId(user.getCompany().getId());
            c.setName(user.getCompany().getName());
            res.setCompanyUser(c);
        }
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser c = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser r = new ResUserDTO.RoleUser();
        if (user.getCompany() != null) {
            c.setId(user.getCompany().getId());
            c.setName(user.getCompany().getName());
            res.setCompanyUser(c);
        }
        if (user.getRole() != null) {
            r.setId(user.getRole().getId());
            r.setName(user.getRole().getName());
            res.setRoleUser(r);
        }
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdateAt(user.getUpdatedAt());
        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser c = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            c.setId(user.getCompany().getId());
            c.setName(user.getCompany().getName());
            res.setCompanyUser(c);
        }

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAddress(user.getAddress());
        res.setGender(user.getGender());
        res.setUpdateAt(user.getUpdatedAt());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User u = this.handleGetUserByUsername(email);
        if (u != null) {
            u.setRefreshToken(token);
            this.userRepository.save(u);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
