package isetn.dsi22.tp6;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    // Vous pouvez ajouter des méthodes supplémentaires si nécessaire
}
