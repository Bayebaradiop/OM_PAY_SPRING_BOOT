import '../services/auth_service.dart';

class ProfileView {
  final AuthService _authService;

  ProfileView(this._authService);

  /// Affiche les informations du profil
  void afficher() {
    print('\n╔════════════════════════════════════════════╗');
    print('║         INFORMATIONS UTILISATEUR           ║');
    print('╚════════════════════════════════════════════╝\n');

    if (!_authService.isLoggedIn()) {
      print('Vous n\'êtes pas connecté.\n');
      print(' Veuillez vous connecter d\'abord (Option 1)\n');
      return;
    }
    
    print('🔐 Session active: ✅ OUI');
    print('\n Vous êtes authentifié et connecté');
    print(' Toutes les requêtes utilisent automatiquement votre session');
    print('\n💡 Actions disponibles:');
    print('   - Consulter votre compte');
    print('   - Effectuer des transactions');
    print('   - Voir votre historique\n');
  }
}
