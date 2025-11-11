package om.example.om_pay.security;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import om.example.om_pay.model.Utilisateur;
import om.example.om_pay.repository.UtilisateurRepository;

/**
 * Service de chargement des détails utilisateur pour Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UtilisateurRepository utilisateurRepository;

    

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String telephone) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le téléphone: " + telephone));

        // Créer l'autorité basée sur le rôle
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())
        );

        return new User(
                utilisateur.getTelephone(),
                utilisateur.getMotDePasse(),
                authorities
        );
    }
}
