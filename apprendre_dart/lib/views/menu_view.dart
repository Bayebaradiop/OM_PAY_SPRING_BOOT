import 'dart:io';
import '../services/auth_service.dart';
import 'login_view.dart';
import 'profile_view.dart';

class MenuView {
  final AuthService _authService;
  late final LoginView _loginView;
  late final ProfileView _profileView;

  MenuView(this._authService) {
    _loginView = LoginView(_authService);
    _profileView = ProfileView(_authService);
  }

  Future<void> afficher() async {
    bool continuer = true;

    _afficherBanniere();

    while (continuer) {
      _afficherMenu();
      
      stdout.write('\nChoisissez une option: ');
      String? choix = stdin.readLineSync();

      switch (choix) {
        case '1':
          await _loginView.afficher();
          break;
        
        case '2':
          _profileView.afficher();
          break;
        
        case '3':
          _deconnexion();
          break;
        
        case '0':
          continuer = false;
          print('\n👋 Au revoir !\n');
          break;
        
        default:
          print('\n Option invalide. Veuillez réessayer.\n');
      }

      if (continuer && choix != '0') {
        print('Appuyez sur ENTRÉE pour continuer...');
        stdin.readLineSync();
        _clearScreen();
      }
    }
  }

  /// Affiche la bannière d'accueil
  void _afficherBanniere() {
    print('╔════════════════════════════════════════════╗');
    print('║         OM PAY - APPLICATION CONSOLE       ║');
    print('║   Backend: om-pay-spring-boot-1.onrender   ║');
    print('╚════════════════════════════════════════════╝\n');
  }

  /// Affiche le menu principal
  void _afficherMenu() {
    print('╔════════════════════════════════════════════╗');
    print('║              MENU PRINCIPAL                ║');
    print('╠════════════════════════════════════════════╣');
    
    if (_authService.isLoggedIn()) {
      print('║  🟢 Statut: CONNECTÉ                       ║');
    } else {
      print('║  🔴 Statut: DÉCONNECTÉ                     ║');
    }
    
    print('╠════════════════════════════════════════════╣');
    print('║  1.  Se connecter                        ║');
    print('║  2.  Voir mes informations               ║');
    print('║  3.  Se déconnecter                      ║');
    print('║  0.  Quitter                             ║');
    print('╚════════════════════════════════════════════╝');
  }

  /// Déconnexion
  void _deconnexion() {
    print('\n╔════════════════════════════════════════════╗');
    print('║            DÉCONNEXION                     ║');
    print('╚════════════════════════════════════════════╝\n');

    if (!_authService.isLoggedIn()) {
      print('Vous n\'êtes pas connecté.\n');
      return;
    }

    _authService.logout();
    print(' Déconnexion réussie!\n');
  }

  void _clearScreen() {
    if (Platform.isWindows) {
      print(Process.runSync("cls", [], runInShell: true).stdout);
    } else {
      print(Process.runSync("clear", [], runInShell: true).stdout);
    }
  }
}
