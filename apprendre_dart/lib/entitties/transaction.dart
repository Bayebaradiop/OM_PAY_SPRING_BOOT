import '../enums/type_transaction.dart';
import '../enums/statut_transaction.dart';

class Transaction {
  final int? id;
  final String reference;
  final TypeTransaction typeTransaction;
  final double montant;
  final double frais;
  final double montantTotal;
  final int compteSourceId;
  final int compteDestinataireId;
  final String dateTransaction;
  final StatutTransaction statut;

  Transaction({
    this.id,
    required this.reference,
    required this.typeTransaction,
    required this.montant,
    required this.frais,
    required this.montantTotal,
    required this.compteSourceId,
    required this.compteDestinataireId,
    required this.dateTransaction,
    required this.statut,
  });

  factory Transaction.fromJson(Map<String, dynamic> json) {
    return Transaction(
      id: json['id'] is int ? json['id'] : int.tryParse(json['id'].toString()),
      reference: json['reference'],
      typeTransaction: TypeTransaction.fromString(json['typeTransaction']),
      montant: (json['montant'] as num).toDouble(),
      frais: (json['frais'] as num).toDouble(),
      montantTotal: (json['montantTotal'] as num).toDouble(),
      compteSourceId: json['compteSourceId'] is int ? json['compteSourceId'] : int.parse(json['compteSourceId'].toString()),
      compteDestinataireId: json['compteDestinataireId'] is int ? json['compteDestinataireId'] : int.parse(json['compteDestinataireId'].toString()),
      dateTransaction: json['dateTransaction'],
      statut: StatutTransaction.fromString(json['statut']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'reference': reference,
      'typeTransaction': typeTransaction.name,
      'montant': montant,
      'frais': frais,
      'montantTotal': montantTotal,
      'compteSourceId': compteSourceId,
      'compteDestinataireId': compteDestinataireId,
      'dateTransaction': dateTransaction,
      'statut': statut.name,
    };
  }

  @override
  String toString() {
    return 'Transaction(id: $id, reference: $reference, type: $typeTransaction, montant: $montant, statut: $statut)';
  }
}