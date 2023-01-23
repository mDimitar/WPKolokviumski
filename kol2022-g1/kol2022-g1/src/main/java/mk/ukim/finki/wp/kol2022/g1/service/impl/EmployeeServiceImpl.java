package mk.ukim.finki.wp.kol2022.g1.service.impl;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.EmployeeType;
import mk.ukim.finki.wp.kol2022.g1.model.Skill;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidEmployeeIdException;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidSkillIdException;
import mk.ukim.finki.wp.kol2022.g1.repository.EmployeeRepository;
import mk.ukim.finki.wp.kol2022.g1.repository.SkillRepository;
import mk.ukim.finki.wp.kol2022.g1.service.EmployeeService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;

    private final PasswordEncoder passwordEncoder;
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillRepository skillRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> listAll() {
        return this.employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
    }

    @Override
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        List<Skill> skills = new ArrayList<>();
        for(Long idSkill: skillId){
            Skill skill = this.skillRepository.findById(idSkill).orElseThrow(InvalidSkillIdException::new);
            skills.add(skill);
        }
        Employee employee = new Employee(name, email, this.passwordEncoder.encode(password), type, skills, employmentDate);
        return this.employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        Employee employee = this.findById(id);
        List<Skill> skills = new ArrayList<>();
        for(Long idSkill: skillId){
            Skill skill = this.skillRepository.findById(idSkill).orElseThrow(InvalidSkillIdException::new);
            skills.add(skill);
        }
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(this.passwordEncoder.encode(password));
        employee.setType(type);
        employee.setSkills(skills);
        employee.setEmploymentDate(employmentDate);
        return this.employeeRepository.save(employee);
    }

    @Override
    public Employee delete(Long id) {
        Employee employee = this.findById(id);
        this.employeeRepository.delete(employee);
        return employee;
    }

    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {

        if(skillId != null && yearsOfService != null){
            Skill skill = this.skillRepository.findById(skillId).orElseThrow(InvalidSkillIdException::new);
            return this.employeeRepository.findAllBySkillsContainingAndEmploymentDateBefore(skill, LocalDate.now().minusYears(yearsOfService));
        }else if(skillId != null){
            Skill skill = this.skillRepository.findById(skillId).orElseThrow(InvalidSkillIdException::new);
            return this.employeeRepository.findAllBySkillsContaining(skill);
        }else if(yearsOfService != null){
            return this.employeeRepository.findAllByEmploymentDateBefore(LocalDate.now().minusYears(yearsOfService));
        }

        return this.employeeRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee user = this.employeeRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getType()));
        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
