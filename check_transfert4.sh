#!/bin/bash
export PGPASSWORD='RhWoGlZtFqG3B2ue9YdPxFMu8aU7PbIm'
psql "postgresql://om_pay_user:RhWoGlZtFqG3B2ue9YdPxFMu8aU7PbIm@dpg-d48fgf3uibrs7397ha10-a.oregon-postgres.render.com:5432/om_pay_db?sslmode=require" -c "SELECT id, statut, message_erreur, date_execution, date_execution_reelle FROM transfert_programme WHERE id = 4;"
