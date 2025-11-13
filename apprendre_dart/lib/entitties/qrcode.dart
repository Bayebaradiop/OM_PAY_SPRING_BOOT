class QrCode {
  final int? id;
  final String codeQr;
  final int compteId;
  final bool actif;
  final String dateCreation;
  final String? dateUtilisation;
  final int nombreUtilisations;

  QrCode({
    this.id,
    required this.codeQr,
    required this.compteId,
    required this.actif,
    required this.dateCreation,
    this.dateUtilisation,
    required this.nombreUtilisations,
  });

  factory QrCode.fromJson(Map<String, dynamic> json) {
    return QrCode(
      id: json['id'],
      codeQr: json['codeQr'],
      compteId: json['compteId'],
      actif: json['actif'],
      dateCreation: json['dateCreation'],
      dateUtilisation: json['dateUtilisation'],
      nombreUtilisations: json['nombreUtilisations'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'codeQr': codeQr,
      'compteId': compteId,
      'actif': actif,
      'dateCreation': dateCreation,
      'dateUtilisation': dateUtilisation,
      'nombreUtilisations': nombreUtilisations,
    };
  }

  @override
  String toString() {
    return 'QrCode(id: $id, codeQr: $codeQr, compteId: $compteId, actif: $actif, utilisations: $nombreUtilisations)';
  }
}