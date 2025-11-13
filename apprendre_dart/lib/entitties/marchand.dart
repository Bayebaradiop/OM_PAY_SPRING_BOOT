class Marchand {
  final int? id;
  final String nomCommercial;
  final String codeMarchand;
  final String numeroMarchand;
  final String adresse;
  final String telephone;
  final double tauxCommission;
  final int utilisateurId;
  final String dateCreation;

  Marchand({
    this.id,
    required this.nomCommercial,
    required this.codeMarchand,
    required this.numeroMarchand,
    required this.adresse,
    required this.telephone,
    required this.tauxCommission,
    required this.utilisateurId,
    required this.dateCreation,
  });

  factory Marchand.fromJson(Map<String, dynamic> json) {
    return Marchand(
      id: json['id'] is int ? json['id'] : int.tryParse(json['id'].toString()),
      nomCommercial: json['nomCommercial'],
      codeMarchand: json['codeMarchand'],
      numeroMarchand: json['numeroMarchand'],
      adresse: json['adresse'],
      telephone: json['telephone'],
      tauxCommission: (json['tauxCommission'] as num).toDouble(),
      utilisateurId: json['utilisateurId'] is int 
          ? json['utilisateurId'] 
          : int.parse(json['utilisateurId'].toString()),
      dateCreation: json['dateCreation'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'nomCommercial': nomCommercial,
      'codeMarchand': codeMarchand,
      'numeroMarchand': numeroMarchand,
      'adresse': adresse,
      'telephone': telephone,
      'tauxCommission': tauxCommission,
      'utilisateurId': utilisateurId,
      'dateCreation': dateCreation,
    };
  }

  @override
  String toString() {
    return 'Marchand(id: $id, nomCommercial: $nomCommercial, codeMarchand: $codeMarchand, telephone: $telephone)';
  }
}