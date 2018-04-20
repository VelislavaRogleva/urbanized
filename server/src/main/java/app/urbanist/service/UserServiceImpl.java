package app.urbanist.service;

import app.urbanist.entity.Role;
import app.urbanist.entity.User;

import app.urbanist.exception.EmailNotUniqueException;
import app.urbanist.exception.UsernameNotUniqueException;
import app.urbanist.model.binding.UserRegisterModel;
import app.urbanist.model.view.UserViewModel;
import app.urbanist.repository.RoleRepository;
import app.urbanist.repository.UserRepository;

import app.urbanist.util.ModelParser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegisterModel urm) throws EmailNotUniqueException, UsernameNotUniqueException {

        if(this.userRepository.findByUsername(urm.getUsername()) != null) {
            throw new UsernameNotUniqueException("Username is already in use");
        }

        if (urm.getEmail() != null && this.userRepository.findByEmail(urm.getEmail()) != null) {
            throw new EmailNotUniqueException("Email is already in use");
        }

        User user = ModelParser.getInstance().map(urm, User.class);
        user.setPassword(this.passwordEncoder.encode(urm.getPassword()));
        Role role = this.roleRepository.findByName("USER");

        role.getUsers().add(user);
        user.getRoles().add(role);

        this.roleRepository.save(role);

        return this.userRepository.save(user);
    }

    @Override
    public List<UserViewModel> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        List<UserViewModel> models = new ArrayList<>();
        for (User user : users) {
            UserViewModel uvm = ModelParser.getInstance().map(user, UserViewModel.class);
            models.add(uvm);
        }
        return models;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws org.springframework.security.core.userdetails.UsernameNotFoundException {
        User user = this.userRepository.findByUsername(username);
        if (user == null) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found");
        }

        Set<Role> roles = user.getRoles();
        Set<SimpleGrantedAuthority> grantedAuthorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())).collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    @Override
    public User getOne(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (!user.isPresent()) return null;
        return user.get();
    }
}
