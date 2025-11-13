class ApiConstants {
  // URL du backend (Render en production)
  static const String baseUrl = 'https://om-pay-spring-boot-1.onrender.com/api';
  
  // Endpoints Auth
  static const String utilisateursEndpoint = '/utilisateurs';
  static const String loginEndpoint = '/auth/login';
  
  // Endpoints Compte
  static const String monSoldeEndpoint = '/comptes/mon-solde';
}