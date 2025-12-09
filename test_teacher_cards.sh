#!/bin/bash
HOST="http://localhost:8080/api"

# Login as Teacher
echo "Logging in as Teacher (teacher1)..."
TEACHER_TOKEN=$(curl -s -X POST $HOST/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "teacher1", "password": "123456"}' | grep -o '"token":"[^"]*' | awk -F':' '{print $2}' | tr -d '"')

if [ -z "$TEACHER_TOKEN" ]; then
    echo "Teacher login failed. Creating teacher..."
    curl -s -X POST $HOST/auth/signup \
      -H "Content-Type: application/json" \
      -d '{"username": "teacher1", "email": "teacher1@test.com", "password": "123456", "fullName": "Teacher One", "role": "ROLE_TEACHER"}'
    
    TEACHER_TOKEN=$(curl -s -X POST $HOST/auth/signin \
      -H "Content-Type: application/json" \
      -d '{"username": "teacher1", "password": "123456"}' | grep -o '"token":"[^"]*' | awk -F':' '{print $2}' | tr -d '"')
fi

echo "Teacher Token: Obtained"

echo "1. Testing GET /api/cards (All Cards)..."
RESPONSE_ALL=$(curl -s -X GET $HOST/cards \
  -H "Authorization: Bearer $TEACHER_TOKEN")
echo "Response Length: ${#RESPONSE_ALL}"
# echo "Response: $RESPONSE_ALL"

echo "2. Testing GET /api/cards/assigned (Assigned to Me)..."
RESPONSE_ASSIGNED=$(curl -s -X GET $HOST/cards/assigned \
  -H "Authorization: Bearer $TEACHER_TOKEN")
echo "Response Length: ${#RESPONSE_ASSIGNED}"
echo "Response: $RESPONSE_ASSIGNED"
