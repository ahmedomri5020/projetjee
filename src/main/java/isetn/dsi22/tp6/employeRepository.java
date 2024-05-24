package isetn.dsi22.tp6;

import org.springframework.data.jpa.repository.JpaRepository;

public interface employeRepository extends JpaRepository<employe, Long> {
    employe findByUsername(String username);
    employe findByEmail(String email);
}
