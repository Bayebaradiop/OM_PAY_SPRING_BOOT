import 'dart:io';
import '../services/auth_service.dart';

class LoginView {
  final AuthService _authService;

  LoginView(this._authService);

  Future<bool> afficher() async {

    print('CONNEXION');

    stdout.write('📱 Téléphone: ');
    String? telephone = stdin.readLineSync()?.trim();

    stdout.write('🔑 Mot de passe: ');
    String? motDePasse = stdin.readLineSync()?.trim();

    if (telephone == null || telephone.isEmpty || 
        motDePasse == null || motDePasse.isEmpty) {
      print('\n❌ Téléphone et mot de passe requis!\n');
      return false;
    }

    try {
      print('\n🔄 Connexion en cours...\n');
      
      final loginResponse = await _authService.login(
        telephone: telephone,
        motDePasse: motDePasse,
      );

      print('╔════════════════════════════════════════════╗');
      print('║        ✅ CONNEXION RÉUSSIE !              ║');
      print('╠════════════════════════════════════════════╣');
      print('║  👤 ${loginResponse.utilisateur.prenom} ${loginResponse.utilisateur.nom}');
      print('║  📱 ${loginResponse.utilisateur.telephone}');
      print('║  📧 ${loginResponse.utilisateur.email}');
      print('║  🏷️  ${loginResponse.utilisateur.role}');
      print('╚════════════════════════════════════════════╝\n');
      
      return true;
      
    } catch (e) {
      print('\n╔════════════════════════════════════════════╗');
      print('║          ❌ ERREUR DE CONNEXION            ║');
      print('╠════════════════════════════════════════════╣');
      print('║  Vérifiez vos identifiants                ║');
      print('╚════════════════════════════════════════════╝\n');
      return false;
    }
  }
}
