import '../enums/role.dart';
import '../enums/statut.dart';

class Utilisateur {
  final int? id;
  final String nom;
  final String prenom;
  final String telephone;
  final String email;
  final String? motDePasse;
  final Role role;
  final Statut statut;
  final bool premiereConnexion;
  final String dateCreation;

  Utilisateur({
    this.id,
    required this.nom,
    required this.prenom,
    required this.telephone,
    required this.email,
    this.motDePasse,
    required this.role,
    required this.statut,
    this.premiereConnexion = true,
    required this.dateCreation,
  });

  factory Utilisateur.fromJson(Map<String, dynamic> json) {
    return Utilisateur(
      id: json['id'] is int ? json['id'] : int.tryParse(json['id'].toString()),
      nom: json['nom'],
      prenom: json['prenom'],
      telephone: json['telephone'],
      email: json['email'],
      motDePasse: json['motDePasse'],
      role: Role.fromString(json['role']),
      statut: Statut.fromString(json['statut']),
      premiereConnexion: json['premiereConnexion'] ?? true,
      dateCreation: json['dateCreation'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'nom': nom,
      'prenom': prenom,
      'telephone': telephone,
      'email': email,
      if (motDePasse != null) 'motDePasse': motDePasse,
      'role': role.name,
      'statut': statut.name,
      'premiereConnexion': premiereConnexion,
      'dateCreation': dateCreation,
    };
  }

  @override
  String toString() {
    return 'Utilisateur(id: $id, nom: $nom, prenom: $prenom, email: $email, role: $role)';
  }
}