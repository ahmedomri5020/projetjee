package isetn.dsi22.tp6;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptedApplicationsRepository extends JpaRepository<AcceptedApplications, Long> {
	public AcceptedApplications save(AcceptedApplications acceptedApplication);
    List<AcceptedApplications> findByEmail(String email);

	
}


