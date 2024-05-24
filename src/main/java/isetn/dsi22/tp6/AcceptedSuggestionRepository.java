package isetn.dsi22.tp6;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcceptedSuggestionRepository extends JpaRepository<AcceptedSuggestion, Long> {
    // You can define custom query methods here if needed
    
    // Save method to save an accepted suggestion
    AcceptedSuggestion save(AcceptedSuggestion acceptedSuggestion);
}
