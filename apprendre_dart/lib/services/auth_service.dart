import '../entitties/utilisateur.dart';
import '../repositories/api_repository.dart';
import '../utils/constants.dart';
import '../enums/role.dart';
import '../enums/statut.dart';

class LoginResponse {
  final String token;
  final Utilisateur utilisateur;

  LoginResponse({
    required this.token,
    required this.utilisateur,
  });
}

/// Service d'authentification
class AuthService {
  final ApiRepository _apiRepository = ApiRepository();

  /// Connexion d'un utilisateur
  Future<LoginResponse> login({
    required String telephone,
    required String motDePasse,
  }) async {
    try {

      final requestData = {
        'telephone': telephone,
        'motDePasse': motDePasse,
      };

      print('Tentative de connexion à: ${ApiConstants.baseUrl}${ApiConstants.loginEndpoint}');

      // Appel à l'API POST /api/auth/login
      final response = await _apiRepository.post(
        ApiConstants.loginEndpoint,
        requestData,
      );

  
      final data = response['data'] as Map<String, dynamic>;

      final token = data['token'] as String;

      // Créer l'objet Utilisateur à partir de la réponse
      final utilisateur = Utilisateur(
        telephone: data['telephone'],
        nom: data['nom'],
        prenom: data['prenom'],
        email: data['email'] ?? '',
        role: Role.fromString(data['role']),
        statut: Statut.ACTIF,
        dateCreation: DateTime.now().toIso8601String(),
      );

      _apiRepository.token = token;

      print('Connexion réussie! Token stocké.');

      return LoginResponse(
        token: token,
        utilisateur: utilisateur,
      );
    } catch (e) {
      print(' Erreur lors de la connexion: $e');
      throw Exception('Erreur lors de la connexion: $e');
    }
  }

  /// Vérifie si l'utilisateur est connecté
  bool isLoggedIn() {
    return _apiRepository.token != null && _apiRepository.token!.isNotEmpty;
  }

  /// Récupère le token actuel
  String? getToken() {
    return _apiRepository.token;
  }

  /// Déconnexion
  void logout() {
    _apiRepository.token = null;
    print(' Déconnexion effectuée.');
  }
}