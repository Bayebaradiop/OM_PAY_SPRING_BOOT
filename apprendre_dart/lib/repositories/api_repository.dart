import 'dart:convert';
import 'package:http/http.dart' as http;
import '../utils/constants.dart';

class ApiRepository {

  static final ApiRepository _instance = ApiRepository._internal();
  factory ApiRepository() => _instance;
  ApiRepository._internal();

  // ========== Gestion du token JWT ==========
  String? _token;

  String? get token => _token;
  set token(String? value) => _token = value;

  /// Génère les headers HTTP avec authentification optionnelle
  Map<String, String> _getHeaders({bool includeAuth = false}) {
    final headers = {'Content-Type': 'application/json'};
    if (includeAuth && _token != null) {
      headers['Authorization'] = 'Bearer $_token';
    }
    return headers;
  }
  // ===========================================

  Future<List<dynamic>> getAll(String endpoint) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        return json.decode(response.body) as List<dynamic>;
      } else {
        throw Exception('Erreur lors de la récupération des données: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }


  Future<Map<String, dynamic>> getById(String endpoint, int id) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint/$id');
      final response = await http.get(url);

      if (response.statusCode == 200) {
        return json.decode(response.body) as Map<String, dynamic>;
      } else {
        throw Exception('Ressource non trouvée: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }


  Future<List<dynamic>> getWithParams(String endpoint, Map<String, String> params) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint').replace(queryParameters: params);
      final response = await http.get(url);

      if (response.statusCode == 200) {
        return json.decode(response.body) as List<dynamic>;
      } else {
        throw Exception('Erreur lors de la recherche: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }


  Future<Map<String, dynamic>> create(String endpoint, Map<String, dynamic> data) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint');
      final response = await http.post(
        url,
        headers: {'Content-Type': 'application/json'},
        body: json.encode(data),
      );

      if (response.statusCode == 201) {
        return json.decode(response.body) as Map<String, dynamic>;
      } else {
        throw Exception('Erreur lors de la création: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }

  Future<Map<String, dynamic>> update(String endpoint, int id, Map<String, dynamic> data) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint/$id');
      final response = await http.put(
        url,
        headers: {'Content-Type': 'application/json'},
        body: json.encode(data),
      );

      if (response.statusCode == 200) {
        return json.decode(response.body) as Map<String, dynamic>;
      } else {
        throw Exception('Erreur lors de la mise à jour: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }

  Future<void> delete(String endpoint, int id) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint/$id');
      final response = await http.delete(url);

      if (response.statusCode != 200) {
        throw Exception('Erreur lors de la suppression: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }

  // ========== Méthodes pour l'authentification ==========
  
  /// Méthode POST pour l'authentification (accepte 200 et 201)
  
  Future<Map<String, dynamic>> post(
    String endpoint,
    Map<String, dynamic> data, {
    bool includeAuth = false,
  }) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint');
      
      print('🔵 POST Request to: $url');
      print('Body: ${json.encode(data)}');
      
      final response = await http.post(
        url,
        headers: _getHeaders(includeAuth: includeAuth),
        body: json.encode(data),
      );

      print('Response Status: ${response.statusCode}');
      print('Response Body: ${response.body}');

      if (response.statusCode == 200 || response.statusCode == 201) {
        return json.decode(response.body) as Map<String, dynamic>;
      } else {
        try {
          final errorBody = json.decode(response.body);
          throw Exception(errorBody['message'] ?? 'Erreur HTTP ${response.statusCode}');
        } catch (e) {
          throw Exception('Erreur HTTP ${response.statusCode}');
        }
      }
    } catch (e) {
      print('Erreur: $e');
      rethrow;
    }
  }

  /// GET avec authentification (pour les endpoints protégés)
  Future<Map<String, dynamic>> getWithAuth(String endpoint) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint');
      final response = await http.get(
        url,
        headers: _getHeaders(includeAuth: true),
      );

      if (response.statusCode == 200) {
        return json.decode(response.body) as Map<String, dynamic>;
      } else {
        throw Exception('Erreur HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }

  /// GET ALL avec authentification
  Future<List<dynamic>> getAllWithAuth(String endpoint) async {
    try {
      final url = Uri.parse('${ApiConstants.baseUrl}$endpoint');
      final response = await http.get(
        url,
        headers: _getHeaders(includeAuth: true),
      );

      if (response.statusCode == 200) {
        return json.decode(response.body) as List<dynamic>;
      } else {
        throw Exception('Erreur HTTP ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Erreur de connexion: $e');
    }
  }
}