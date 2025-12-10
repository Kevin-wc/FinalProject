# 🌿 Health Quest — Android Wellness App

A gamified wellness application promoting healthy habits, personal growth, and community support.

---

## ⭐ Overview

**Health Quest** is a multi-feature Android application designed to help users improve their wellness through educational articles, quizzes, customizable profiles, and a full friends system.  
The app uses:

- **Firebase Authentication**
- **Firebase Realtime Database**
- **SharedViewModel**
- **Android Fragments**

to deliver a responsive, modern user experience.

---

## 🔐 Core Features

### 👤 User Accounts
- Firebase Authentication (Login, Register, Logout)
- Secure password handling + password reset
- Persistent user state through SharedViewModel

---

### 📘 Educational Articles + Quizzes
- Articles covering vaping, financial wellness, and social health  
- Short quizzes after each article  
- Score calculation + leaderboard integration  
- Points stored in Firebase in real time  

---

### 🏆 Leaderboard
- Real-time ranking of users based on quiz scores  
- Automatic sorting + instant updates  
- Firebase-driven scoring system  

---

## 👥 Profile System
- Update username (**with uniqueness validation**)  
- Update full name  
- Update profile picture (Base64, no Firebase Storage cost!)  
- Uses ActivityResultLauncher for modern gallery selection  
- Profile Fragment automatically refreshes UI when data changes  

---

## 🤝 Friends System
Includes:

- Username search  
- Sending friend requests  
- Receiving and accepting requests  
- Real-time lists for:  
  - Friends  
  - Sent requests  
  - Received requests  


---

## 🛠 Tech Stack

| Technology | Purpose |
|-----------|---------|
| **Java** | Main application logic |
| **XML** | UI layout |
| **Firebase Auth** | User login/registration |
| **Firebase Realtime Database** | Store users, scores, friends |
| **Base64 Encoding** | Profile picture storage |
| **Retrofit** | Fetch motivational quotes (optional feature) |
| **ViewModel / LiveData** | Persistent user state |

---


---

## 🧠 Lessons Learned

- Working with Firebase asynchronous callbacks  
- Designing scalable NoSQL structures  
- Image handling using Base64 encoding  
- Implementing ActivityResultLauncher  
- Fragment communication using SharedViewModel  
- Debugging UI lifecycle issues  
- Coordinating team development with GitHub  
- REST API integration and usage  

---

## 👥 Contributors

| Name | Role |
|------|------|
| **Kevin Gonzalez Ramon** | Profile system, friends system, Firebase integration |
| **Daron Baltazar** | Home navigation, articles, UI |
| **Navi Bountho** | Quizzes, leaderboard, API integration |

---

## 🔗 Repository

👉 https://github.com/Kevin-wc/FinalProject

---


