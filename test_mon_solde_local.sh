#!/bin/bash

echo "=== Test de l'endpoint /api/comptes/mon-solde en LOCAL ==="
echo ""

# Étape 1: Login
echo "1. Login avec 779876543 / Pass123!..."
LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8083/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"telephone":"779876543","motDePasse":"Pass123!"}')

echo "Réponse login: $LOGIN_RESPONSE"
echo ""

# Extraire le token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ Erreur: Impossible d'obtenir le token"
  echo "Réponse complète: $LOGIN_RESPONSE"
  exit 1
fi

echo "✓ Token obtenu: ${TOKEN:0:50}..."
echo ""

# Étape 2: Consulter le solde
echo "2. Consultation du solde avec le token (endpoint modifié)..."
SOLDE_RESPONSE=$(curl -s -X GET "http://localhost:8083/api/comptes/solde" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

echo "Réponse solde: $SOLDE_RESPONSE"
echo ""

# Vérifier si c'est un succès
if echo "$SOLDE_RESPONSE" | grep -q '"success":false'; then
  echo "❌ Erreur lors de la consultation du solde"
else
  echo "✓ Solde récupéré avec succès!"
fi
