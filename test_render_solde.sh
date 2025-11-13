#!/bin/bash

echo "========================================"
echo "TEST SUR RENDER: /api/comptes/mon-solde"
echo "========================================"
echo ""

# 1. Se connecter pour obtenir le token
echo "1. Connexion sur Render..."
LOGIN_RESPONSE=$(curl -s -X POST https://om-pay-spring-boot-1.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "779876543",
    "motDePasse": "Pass123!"
  }')

echo "Réponse login: $LOGIN_RESPONSE"
echo ""

# Extraire le token du JSON
TOKEN=$(echo $LOGIN_RESPONSE | grep -oP '"token":\s*"\K[^"]+')

if [ -z "$TOKEN" ]; then
    echo "❌ Erreur: Impossible de récupérer le token"
    exit 1
fi

echo "✅ Token récupéré: ${TOKEN:0:30}..."
echo ""

# 2. Appeler le nouvel endpoint avec le token
echo "2. Test endpoint /api/comptes/mon-solde (automatique)..."
SOLDE_RESPONSE=$(curl -s -X GET https://om-pay-spring-boot-1.onrender.com/api/comptes/mon-solde \
  -H "Authorization: Bearer $TOKEN")

echo "Réponse: $SOLDE_RESPONSE"
echo ""

echo "========================================"
echo "✅ TEST TERMINÉ"
echo "========================================"
