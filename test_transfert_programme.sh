#!/bin/bash

echo "======================================"
echo "TEST TRANSFERT PROGRAMMÉ - OM PAY"
echo "======================================"
echo ""

# 1. Connexion et récupération du token
echo "📱 1. Connexion..."
TOKEN=$(curl -s -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"telephone": "771234567", "motDePasse": "Password123!"}' | jq -r '.data.token')

if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
    echo "❌ Erreur de connexion"
    exit 1
fi

echo "✅ Token obtenu: ${TOKEN:0:20}..."
echo ""

# 2. Calcul de la date dans 2 minutes
echo "⏰ 2. Préparation du transfert pour dans 2 minutes..."
DATE_EXECUTION=$(date -d "+2 minutes" "+%Y-%m-%dT%H:%M:%S")
echo "Date d'exécution: $DATE_EXECUTION"
echo ""

# 3. Création du transfert programmé
echo "💰 3. Création du transfert programmé..."
RESPONSE=$(curl -s -X POST http://localhost:8083/api/transferts-programmes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"telephoneDestinataire\": \"779876543\",
    \"montant\": 1000,
    \"dateExecution\": \"$DATE_EXECUTION\"
  }")

echo "$RESPONSE" | jq '.'

SUCCESS=$(echo "$RESPONSE" | jq -r '.success')
if [ "$SUCCESS" == "true" ]; then
    echo "✅ Transfert programmé créé avec succès!"
else
    echo "❌ Erreur lors de la création"
    exit 1
fi
echo ""

# 4. Vérifier la liste des transferts programmés
echo "📋 4. Liste de mes transferts programmés..."
curl -s -X GET http://localhost:8083/api/transferts-programmes/mes-transferts \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

echo "======================================"
echo "✅ Test terminé!"
echo "======================================"
echo ""
echo "⏳ Le transfert sera exécuté automatiquement dans 2 minutes."
echo "📊 Surveillez les logs avec: tail -f app.log"
echo "🔍 Vous verrez: 🔄 💸 ✅"
