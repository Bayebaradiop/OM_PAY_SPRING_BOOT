#!/bin/bash

echo "=== Test de l'endpoint /api/comptes/solde sur RENDER ==="
echo ""

# Étape 1: Login
echo "1. Login avec 779876543 / Pass123!..."
LOGIN_RESPONSE=$(curl -s -X POST "https://om-pay-spring-boot-1.onrender.com/api/auth/login" \
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

# Étape 2: Consulter le solde avec le nouvel endpoint
echo "2. Consultation du solde avec /api/comptes/solde (auto-récupération)..."
SOLDE_RESPONSE=$(curl -s -X GET "https://om-pay-spring-boot-1.onrender.com/api/comptes/solde" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

echo "Réponse solde: $SOLDE_RESPONSE"
echo ""

# Vérifier si c'est un succès
if echo "$SOLDE_RESPONSE" | grep -q '"success":true'; then
  SOLDE=$(echo $SOLDE_RESPONSE | grep -o '"data":[0-9.]*' | cut -d':' -f2)
  echo "✅ SUCCESS! Solde récupéré automatiquement: $SOLDE FCFA"
else
  echo "❌ Erreur lors de la consultation du solde"
fi
