#!/bin/bash
HOST="http://localhost:8080/api"

TIMESTAMP=$(date +%s)
TEACHER_USER="teacher_$TIMESTAMP"
STUDENT_USER="student_$TIMESTAMP"

echo "1. Register new Teacher ($TEACHER_USER)..."
curl -s -X POST $HOST/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$TEACHER_USER\", \"email\": \"$TEACHER_USER@test.com\", \"password\": \"123456\", \"fullName\": \"Teacher Test\", \"role\": \"ROLE_TEACHER\"}"

echo "2. Login as Teacher..."
TEACHER_TOKEN=$(curl -s -X POST $HOST/auth/signin \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$TEACHER_USER\", \"password\": \"123456\"}" | grep -o '"token":"[^"]*' | awk -F':' '{print $2}' | tr -d '"')

if [ -z "$TEACHER_TOKEN" ]; then
  echo "Teacher login failed"
  # print response
  curl -X POST $HOST/auth/signin \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$TEACHER_USER\", \"password\": \"123456\"}"
  exit 1
fi
echo "Teacher Token: Obtained" # $TEACHER_TOKEN"

echo "3. Register new Student ($STUDENT_USER)..."
curl -s -X POST $HOST/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$STUDENT_USER\", \"email\": \"$STUDENT_USER@test.com\", \"password\": \"123456\", \"fullName\": \"Student Test\", \"role\": \"ROLE_STUDENT\"}"

# Need Student ID. Can't get it from signup. 
# But we can get it by logging in as student, assuming we have endpoint? No.
# Creating a student via API is fine, but how to get ID using teacher account?
# Teacher doesn't have "list students" endpoint returning IDs easily unless we implemented filter.
# Hack: Login as Student, call "GET /api/history" (if exists) or "GET /api/me" (if exists)?
# Let's check AuthController/UserService. 
# Wait, I implemented `getAssignedCards` which returns cards for logged in user.
# But `assignCard` needs `studentId`.
# I need to know the Student ID.
# Since I can't easily get it, maybe I should assume I can query the DB or add an endpoint for "me".

echo "3b. Login as Student..."
STUDENT_TOKEN=$(curl -s -X POST $HOST/auth/signin \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$STUDENT_USER\", \"password\": \"123456\"}" | grep -o '"token":"[^"]*' | awk -F':' '{print $2}' | tr -d '"')

if [ -z "$STUDENT_TOKEN" ]; then
  echo "Student login failed"
  exit 1
fi

echo "Student Token: [${STUDENT_TOKEN}]"
echo "Token Length: ${#STUDENT_TOKEN}"

# To get Student ID: 
# Option A: Add /api/auth/me to get current user info.
# Option B: Use the `jwt` token to decode ID? The JWT contains sub (username).
# Option C: Just try to guess it? No.
# Option D: Fetch "all students" as Teacher? I don't think that endpoint exists.
# I'll check `AuthController` again. Login returns `JwtResponse`. Does it have ID?
# Let's check JwtResponse.java.

echo "Checking JwtResponse..."
LOGIN_RESP=$(curl -s -X POST $HOST/auth/signin \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$STUDENT_USER\", \"password\": \"123456\"}")
  
STUDENT_ID=$(echo $LOGIN_RESP | grep -o '"id":[0-9]*' | awk -F':' '{print $2}')

if [ -z "$STUDENT_ID" ]; then
    echo "Could not extract Student ID from login response."
    echo "Response: $LOGIN_RESP"
    exit 1
fi
echo "Student ID: $STUDENT_ID"

echo "4. Assign Card 1 to Student..."
# Assuming Card 1 exists (from seeding).
CARD_ID=1

RESPONSE=$(curl -s -X POST $HOST/cards/assign \
  -H "Authorization: Bearer $TEACHER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"cardId\": $CARD_ID, \"studentId\": $STUDENT_ID}")

echo "Assign Response: $RESPONSE"

echo "5. Verify Assigned Cards for Student..."
curl -v -X GET $HOST/cards/assigned \
  -H "Authorization: Bearer $STUDENT_TOKEN" > assigned_response.txt 2> assigned_debug.txt

ASSIGNED=$(cat assigned_response.txt)
echo "Assigned Cards: $ASSIGNED"
cat assigned_debug.txt

if [[ "$ASSIGNED" == *"id\":1"* ]] || [[ "$ASSIGNED" == *"Epiphany"* ]]; then 
  echo "SUCCESS: Student sees assigned card."
else
  echo "FAILURE: Student does not see assigned card."
fi
