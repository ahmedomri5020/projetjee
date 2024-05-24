package isetn.dsi22.tp6;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class loginController {

    @Autowired
    private employeRepository employeRepository;
    
    @Autowired // Autowire the suggestionRepository
    private SuggestionRepository suggestionRepository;
    @Autowired
    private AcceptedApplicationsRepository acceptedApplicationsRepository;
    @Autowired
    private AcceptedSuggestionRepository acceptedSuggestionRepository;

    @Autowired
    private CongeRepository congeRepository;
    @Autowired
    private adminRepository AdminRepository;
    @GetMapping("/home")
    public String afficherHome(Model model) {
        // Add any necessary model attributes
        return "home"; // Return the name of the view template (home.html)
    }
    @GetMapping("/homeadmin")
    public String afficherHomeAdmin(Model model) {
        // Add any necessary model attributes
        return "homeadmin"; // Return the name of the view template (home.html)
    }
    @GetMapping("/profile")
    public String afficherProfile(Model model) {
        employe employe = new employe(); // Create a new employe object
        model.addAttribute("employe", employe); // Add employe object to the model
        return "profile"; // Return the name of the view template (profile.html)
    }

    @GetMapping("/leaveapplication")
    public String afficherApplication(Model model) {
        // Add any necessary model attributes
        return "leaveapplication"; // Return the name of the view template (home.html)
    }

    @GetMapping("/auth")
    public String afficherAuth(Model model) {
        model.addAttribute("employe", new employe());
        return "auth";
    }
    
    @GetMapping("/admin")
    public String afficherAdmin(Model model) {
        model.addAttribute("employe", new employe());
        return "admin";
    }
    

    @GetMapping("/suggestions")
    public String showSuggestions(Model model) {
        List<Suggestion> suggestions = suggestionRepository.findAll();
        model.addAttribute("suggestions", suggestions);
        return "suggestions";
    }
    @GetMapping("/leaveapplications")
    public String showLeaveApplications(Model model) {
        List<Conge> conges = congeRepository.findAll(); // Assuming you have a CongeRepository injected
        model.addAttribute("conges", conges);
        return "leaveapplications";
    }
    @GetMapping("/myapplications")
    public String showAcceptedApplications(Model model, HttpSession session) {
        // Get the email of the logged-in user from the session
        String userEmail = (String) session.getAttribute("email");

        // Fetch accepted applications associated with the logged-in user's email
        List<AcceptedApplications> acceptedApplicationsList = acceptedApplicationsRepository.findByEmail(userEmail);

        // Add the filtered list to the model
        model.addAttribute("acceptedApplicationsList", acceptedApplicationsList);

        // Return the name of your Thymeleaf template
        return "myapplications";
    }


    
    
    @PostMapping("/accept-suggestion")
    public String acceptSuggestion(@RequestParam("id") Long id) {
        // Find the suggestion by ID
        Suggestion suggestion = suggestionRepository.findById(id).orElse(null);
        if (suggestion == null) {
            return "Suggestion not found!";
        }

        // Save the suggestion to accepted suggestions table
        AcceptedSuggestion acceptedSuggestion = new AcceptedSuggestion();
        acceptedSuggestion.setFullName(suggestion.getFullName());
        acceptedSuggestion.setEmail(suggestion.getEmail());
        acceptedSuggestion.setSuggestion(suggestion.getSuggestion());
        acceptedSuggestionRepository.save(acceptedSuggestion);

        // Delete the suggestion from suggestions table
        suggestionRepository.delete(suggestion);

        // Redirect back to the suggestions page
        return "redirect:/suggestions";
    }

    

    @PostMapping("/auth")
    public String authentifier(@ModelAttribute("employe") employe employe, HttpSession session, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth";
        }

        employe employeTrouve = employeRepository.findByUsername(employe.getUsername());

        if (employeTrouve != null && employeTrouve.getPassword().equals(employe.getPassword())) {
            // Authentification réussie, stocker les informations de l'utilisateur dans la session
            session.setAttribute("fullName", employeTrouve.getFullName());
            session.setAttribute("email", employeTrouve.getEmail());

            model.addAttribute("message", "Connexion réussie !");
            return "redirect:/home";
        } else {

        }

        return "auth";
    }

    @PostMapping("/admin")
    public String authentifierAdmin(@ModelAttribute("admin") Admin admin, HttpSession session, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin";
        }

        Admin adminTrouve = AdminRepository.findByUsername(admin.getUsername());

        if (adminTrouve != null && adminTrouve.getPassword().equals(admin.getPassword())) {
            // Authentification réussie, stocker les informations de l'administrateur dans la session
            session.setAttribute("fullName", adminTrouve.getFullName());
            session.setAttribute("email", adminTrouve.getEmail());

            model.addAttribute("message", "Connexion réussie !");
            return "redirect:/homeadmin";
        } else {
            model.addAttribute("erreurAuth", true);
            model.addAttribute("message", "Nom d'utilisateur ou mot de passe incorrect !");
        }

        return "admin";
    }


    @PostMapping("/send-suggestion")
    public String soumettreSuggestion(@RequestParam("suggestion") String suggestion, HttpSession session) {
        String fullName = (String) session.getAttribute("fullName");
        String email = (String) session.getAttribute("email");

        // Insérer la suggestion dans la base de données avec le nom complet et l'e-mail de l'utilisateur
        Suggestion newSuggestion = new Suggestion();
        newSuggestion.setFullName(fullName);
        newSuggestion.setEmail(email);
        newSuggestion.setSuggestion(suggestion);
        suggestionRepository.save(newSuggestion);

        // Rediriger vers la page d'accueil ou une autre page après avoir soumis la suggestion
        return "redirect:/home";
    }
    @PostMapping("/apply-for-leave")
    public String demanderConge(@RequestParam("days") int days, @RequestParam("reason") String reason, HttpSession session, Model model) {
        String fullName = (String) session.getAttribute("fullName");
        String email = (String) session.getAttribute("email");

        // Set full name and email for the leave application
        Conge newConge = new Conge();
        newConge.setFullName(fullName);
        newConge.setEmail(email);
        newConge.setEtat("Pending");
        newConge.setDays(days);
        newConge.setReason(reason);
        congeRepository.save(newConge); // Utilize CongeRepository to save the leave application

        // Redirect to the home page or another page after submitting the leave application
        return "redirect:/home";
    }
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("employe") employe updatedEmploye, HttpSession session) {
        String email = (String) session.getAttribute("email"); // Get the current email from the session
        employe existingEmploye = employeRepository.findByEmail(email); // Retrieve the existing employe object

        if (existingEmploye != null) {
            // Update the existing employe object with the new values
            existingEmploye.setUsername(updatedEmploye.getUsername());
            existingEmploye.setFullName(updatedEmploye.getFullName());
            existingEmploye.setPassword(updatedEmploye.getPassword()); // Handle password securely
            existingEmploye.setEmail(updatedEmploye.getEmail());

            // Save the updated employe object to the database
            employeRepository.save(existingEmploye);

            // Update session attributes if necessary
            session.setAttribute("username", updatedEmploye.getUsername()); // Update session with new username if changed
            session.setAttribute("email", updatedEmploye.getEmail()); // Update session with new email if changed
            session.setAttribute("fullName", updatedEmploye.getFullName());

            // Redirect the user to the profile page or home page
            return "redirect:/profile"; // Redirect to profile page to see updated data
        }

        return "redirect:/profile"; // Redirect to profile if user not found
    }

    
    @PostMapping("/approve-leave")
    public String approveLeave(@RequestParam("id") Long id) {
        // Find the leave application by ID
        Conge conge = congeRepository.findById(id).orElse(null);
        if (conge == null) {
            return "Leave application not found!";
        }

        // Save the leave application to AcceptedApplications table
        AcceptedApplications acceptedApplications = new AcceptedApplications();
        acceptedApplications.setFullName(conge.getFullName());
        acceptedApplications.setEmail(conge.getEmail());
        acceptedApplications.setDays(conge.getDays());
        acceptedApplications.setReason(conge.getReason());
        acceptedApplications.setEtat("Accepted");
        acceptedApplicationsRepository.save(acceptedApplications);
        // Delete the leave application from the Conge table
        congeRepository.delete(conge);

        return "redirect:/leaveapplications";
    }

    @PostMapping("/decline-leave")
    public String declineLeave(@RequestParam("id") Long id) {
        // Find the leave application by ID
        Conge conge = congeRepository.findById(id).orElse(null);
        if (conge == null) {
            return "Leave application not found!";
        }

        // Delete the leave application from the Conge table
        congeRepository.delete(conge);
        
        return "redirect:/leaveapplications";
    }


}
