# 📱 Android Firebase Firestore CRUD App

A simple Android application demonstrating **Create**, **Read**, **Update**, and **Delete** (CRUD) operations using **Firebase Firestore**.

[![Android Studio](https://img.shields.io/badge/IDE-Android%20Studio-green)](https://developer.android.com/studio)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-blue)](https://firebase.google.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](#license)

---

## 🚀 Features

- ✅ Add new users
- 👀 View user list in real-time
- 🔁 Update existing users
- ❌ Delete users
- ⚡ Real-time Firestore Sync
- 📌 Tap to auto-fill user details

---

## 📸 View Demo

- [Firebase Firestore CRUD Demo Video](https://drive.google.com/file/d/1of2YHD-QasSxwy43x6iqBvwjSUTMhQTE/view?usp=drive_link)

---

<details>
<summary><strong>🧰 Prerequisites</strong></summary>

- Android Studio (Latest)
- Java JDK
- Firebase account
</details>


<details>
<summary><strong>🔥 Firebase Setup</strong></summary>

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (e.g., `FirestoreCrudApp`)
3. Add an Android app to the project  
   Package name: `com.example.firestorecrudapp`
4. Download and place `google-services.json` inside your project at `/app/`
5. Enable Firestore in test mode from Firebase Console
6. Modify Gradle files as follows:

**Project-level `build.gradle`:**
```groovy
classpath 'com.google.gms:google-services:4.4.1'
```

App-level `build.gradle`
```groovy
plugins {
    id 'com.google.gms.google-services'
}
dependencies {
    implementation 'com.google.firebase:firebase-firestore:25.0.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
}
```
✅ Sync Gradle & you're good to go!

</details>

<details> <summary><strong>🏗️ Project Structure</strong></summary>

```bash
app/
├── java/com/example/firestorecrudapp/
│   ├── MainActivity.java
│   ├── User.java
│   └── UserAdapter.java
├── res/layout/
│   ├── activity_main.xml
│   └── item_user.xml
├── AndroidManifest.xml
├── google-services.json
```
</details>

<details> <summary><strong>📦 Code Explanation</strong></summary>

## 📁 User.java
A POJO with name, email, and Firestore id. Firestore auto-maps documents using this model.

## 🖼️ Layouts
- `activity_main.xml`: UI with input fields, buttons, RecyclerView.

- `item_user.xml`: Represents each user item.

## 🔄 UserAdapter.java
Binds List<User> to RecyclerView with click listeners.

## ⚙️ MainActivity.java
Handles:

- Initialization

- Firestore connection

- Add/Update/Delete/Read logic
</details>

## 🤝 Contributing
Contributions are welcome! Please open issues or pull requests if you'd like to improve the app.