import '../enums/statut.dart';

class Compte {
  final int? id;
  final String numeroCompte;
  final double solde;
  final String typeCompte;
  final Statut statut;
  final int utilisateurId;
  final String dateCreation;

  Compte({
    this.id,
    required this.numeroCompte,
    required this.solde,
    required this.typeCompte,
    required this.statut,
    required this.utilisateurId,
    required this.dateCreation,
  });

  factory Compte.fromJson(Map<String, dynamic> json) {
    return Compte(
      id: json['id'] is int ? json['id'] : int.tryParse(json['id'].toString()),
      numeroCompte: json['numeroCompte'],
      solde: (json['solde'] as num).toDouble(),
      typeCompte: json['typeCompte'],
      statut: Statut.fromString(json['statut']),
      utilisateurId: json['utilisateurId'] is int ? json['utilisateurId'] : int.parse(json['utilisateurId'].toString()),
      dateCreation: json['dateCreation'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'numeroCompte': numeroCompte,
      'solde': solde,
      'typeCompte': typeCompte,
      'statut': statut.name,
      'utilisateurId': utilisateurId,
      'dateCreation': dateCreation,
    };
  }

  @override
  String toString() {
    return 'Compte(id: $id, numeroCompte: $numeroCompte, solde: $solde, typeCompte: $typeCompte, statut: $statut)';
  }
}