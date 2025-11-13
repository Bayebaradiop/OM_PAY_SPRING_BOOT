#!/bin/bash

echo "========================================"
echo "TEST DU NOUVEL ENDPOINT /api/comptes/mon-solde"
echo "========================================"
echo ""

# 1. Se connecter pour obtenir le token
echo "1. Connexion..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "779876543",
    "motDePasse": "Pass123!"
  }')

echo "Réponse login: $LOGIN_RESPONSE"
echo ""

# Extraire le token
TOKEN=$(echo $LOGIN_RESPONSE | grep -oP '"token":\s*"\K[^"]+')

if [ -z "$TOKEN" ]; then
    echo "❌ Erreur: Impossible de récupérer le token"
    exit 1
fi

echo "✅ Token récupéré: ${TOKEN:0:30}..."
echo ""

# 2. Appeler le nouvel endpoint avec le token
echo "2. Récupération du solde automatique..."
SOLDE_RESPONSE=$(curl -s -X GET http://localhost:8083/api/comptes/mon-solde \
  -H "Authorization: Bearer $TOKEN")

echo "Réponse solde: $SOLDE_RESPONSE"
echo ""

echo "========================================"
echo "TEST TERMINÉ"
echo "========================================"
