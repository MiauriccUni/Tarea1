package ac.cr.tarea1.logic.entity.rol;

import ac.cr.tarea1.logic.entity.user.User;
import ac.cr.tarea1.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Order(2)
@Component
public class CommonUserSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public CommonUserSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ){
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createCommonUserSeeder();
    }
    private void createCommonUserSeeder(){
        User commonUser = new User();
        commonUser.setName("User");
        commonUser.setLastname("Common");
        commonUser.setEmail("common.user@gmail.com");
        commonUser.setPassword("commonuser123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRepository.findByEmail(commonUser.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        var user = new User();
        user.setName(commonUser.getName());
        user.setLastname(commonUser.getLastname());
        user.setEmail(commonUser.getEmail());
        user.setPassword(passwordEncoder.encode(commonUser.getPassword()));
        user.setRole(optionalRole.get());
        userRepository.save(user);

    }
}
