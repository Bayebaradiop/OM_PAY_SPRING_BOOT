-- Script de création de la table transfert_programme
-- À exécuter sur la base Render

CREATE TABLE IF NOT EXISTS transfert_programme (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_expediteur_id BIGINT NOT NULL,
    telephone_destinataire VARCHAR(20) NOT NULL,
    montant DOUBLE PRECISION NOT NULL,
    date_execution TIMESTAMP NOT NULL,
    statut VARCHAR(20) NOT NULL,
    date_creation TIMESTAMP NOT NULL,
    date_execution_reelle TIMESTAMP,
    message_erreur TEXT,
    CONSTRAINT fk_transfert_programme_utilisateur FOREIGN KEY (utilisateur_expediteur_id) 
        REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- Index pour optimiser les requêtes du scheduler
CREATE INDEX IF NOT EXISTS idx_transfert_programme_statut_date 
    ON transfert_programme(statut, date_execution);

-- Index pour les requêtes par utilisateur
CREATE INDEX IF NOT EXISTS idx_transfert_programme_utilisateur 
    ON transfert_programme(utilisateur_expediteur_id, date_creation DESC);

-- Commentaires
COMMENT ON TABLE transfert_programme IS 'Table pour stocker les transferts programmés à exécuter automatiquement';
COMMENT ON COLUMN transfert_programme.statut IS 'Valeurs possibles: ACTIF, TERMINE, ANNULE, ECHOUE';
COMMENT ON COLUMN transfert_programme.date_execution IS 'Date et heure prévue pour exécuter le transfert';
COMMENT ON COLUMN transfert_programme.date_execution_reelle IS 'Date et heure réelle de l''exécution';
COMMENT ON COLUMN transfert_programme.message_erreur IS 'Message d''erreur en cas d''échec de l''exécution';
