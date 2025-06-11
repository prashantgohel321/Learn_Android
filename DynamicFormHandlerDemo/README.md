# DynamicFormHandlerDemo 🚀

A simple yet powerful Android demo project that shows how to build a **reusable, dynamic form input system** using:
- ✅ `List<Model>` (FormField)
- ✅ `ContentValues` for storing input data
- ✅ `Interface` for passing collected data
- ✅ Single, reusable dialog layout

---

## 🎯 Motive

In many Android apps, we often need to collect **form inputs** from users — like name, email, feedback, or order details. But creating a new layout and dialog for every form becomes:

- ❌ Time-consuming
- ❌ Hard to manage
- ❌ Repetitive

This project solves that problem by introducing a **dynamic dialog system** that works for any kind of form — using **one layout**, **one logic**, and **dynamic inputs**.

---

## Screenshots
<img src="/screenshots/S1.png" alt="Screenshot 1" height="420px">
<img src="/screenshots/S2.png" alt="Screenshot 2" height="420px">

---
## 📌 Features

- ✅ Reusable dialog that supports any number of fields
- ✅ Uses a simple model class (`FormField`) to map labels to input views
- ✅ Collects all inputs into a single `ContentValues` map
- ✅ Uses interface (`OnFormSaveListener`) to return collected data
- ✅ Optional radio group input (e.g., gender, type selection)
- ✅ Pre-fills existing data for easy updating
- ✅ Modular and highly extensible

---

## 📁 Folder Structure

<pre>
📦 DynamicFormHandlerDemo
├── MainActivity.java # Sample usage with a button
├── FormField.java # Model class for dynamic fields
├── OnFormSaveListener.java # Interface to return data
├── FormDialog.java # Core reusable dialog logic
├── res/
│ ├── layout/
│ │ ├── activity_main.xml # Layout with a button to launch dialog
│ │ └── dialog_form.xml # Layout used in reusable dialog
</pre>

---



## 🧠 Core Concepts Explained

### 1. `FormField.java` – Model Class
Defines a mapping between:
- the **field label (key)** (like `name`, `email`)
- and the **EditText view ID** in the layout.

This allows us to create flexible field lists like:
```java
List<FormField> fields = Arrays.asList(
    new FormField("name", R.id.input_name),
    new FormField("email", R.id.input_email)
);
```

### 2. `ContentValues` – Data Store
- Used to store key-value pairs from the inputs
- Automatically holds all the form data together
- You can easily store this in SQLite or send to API

### 3. `OnFormSaveListener` – Interface
- Custom callback used to return the filled ContentValues to the caller
- Keeps the dialog generic and reusable

### 4. `FormDialog.show(...)` – Reusable Dialog
The method:
 - Loads the dialog layout dynamically
 - Pre-fills inputs if data is given
 - Collects and returns all input values on save
 - Works with or without radio group

---

## 🧪 How to Use

### Step 1: Define Your Fields
```java
List<FormField> fields = Arrays.asList(
    new FormField("name", R.id.input_name),
    new FormField("email", R.id.input_email)
);
```

### Step 2: Prepare Existing Data (Optional)
```java
ContentValues prefill = new ContentValues();
prefill.put("name", "Alice");
prefill.put("email", "alice@example.com");
```

### Step 3: Show the Dialog
```java
FormDialog.show(
    this,
    "User Details",
    R.layout.dialog_form,
    fields,
    prefill,
    R.id.radio_group,
    R.id.radio_option1,
    R.id.radio_option2,
    values -> {
        // handle the result
        Log.d("DATA", values.toString());
    }
);
```

---

## 💡 Use Cases
 - Profile forms (Name, Email, Gender)
 - Booking input (Date, Time, Notes)
 - Feedback forms
 - Dynamic surveys
 - Admin order inputs
 - Address forms

## Benefits of This Approach
 - **Reusability:** One layout, used many times
 - **Less duplication:** Avoid copy-pasting form code
 - **Highly customizable:** Add/remove fields easily
 - **Clean structure:** Model + Interface = neat & modular