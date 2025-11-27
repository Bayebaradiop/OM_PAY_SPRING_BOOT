#!/bin/bash

echo "=========================================="
echo "TEST: /api/comptes/mon-solde sur RENDER"
echo "=========================================="
echo ""

# 1. Connexion
echo "1️⃣  Connexion..."
LOGIN_RESPONSE=$(curl -s -X POST https://om-pay-spring-boot-1.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "telephone": "771234567",
    "motDePasse": "Password123!"
  }')

echo "✅ Réponse login reçue"
echo ""

# Extraire le token
TOKEN=$(echo $LOGIN_RESPONSE | grep -oP '"token":\s*"\K[^"]+')

if [ -z "$TOKEN" ]; then
    echo "❌ Erreur: Impossible de récupérer le token"
    echo "Réponse: $LOGIN_RESPONSE"
    exit 1
fi

echo "🔑 Token récupéré: ${TOKEN:0:30}..."
echo ""

# 2. Tester l'endpoint /api/comptes/mon-solde
echo "2️⃣  Test de /api/comptes/mon-solde (récupération automatique du solde)..."
echo ""

SOLDE_RESPONSE=$(curl -s -X GET https://om-pay-spring-boot-1.onrender.com/api/comptes/mon-solde \
  -H "Authorization: Bearer $TOKEN")

echo "📊 Réponse du serveur:"
echo "$SOLDE_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$SOLDE_RESPONSE"
echo ""

# Vérifier si la réponse contient "success":true
if echo "$SOLDE_RESPONSE" | grep -q '"success":true'; then
    echo "✅ ✅ ✅ TEST RÉUSSI ✅ ✅ ✅"
    echo ""
    echo "Le solde a été récupéré automatiquement sans saisir le numéro de compte!"
else
    echo "❌ Le test a échoué"
    echo "Le déploiement sur Render n'est peut-être pas encore terminé."
    echo "Attendez quelques minutes et réessayez."
fi

echo ""
echo "=========================================="
echo "FIN DU TEST"
echo "=========================================="
