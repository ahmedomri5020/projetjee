package isetn.dsi22.tp6;

import org.springframework.data.jpa.repository.JpaRepository;

public interface adminRepository extends JpaRepository<Admin, Long> {
    // Ajoutez des méthodes personnalisées si nécessaire
    Admin findByUsername(String username);

}
