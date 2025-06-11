package com.prashant.dynamicformhandlerdemo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.ContentValues;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ContentValues previousData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openFormBtn = findViewById(R.id.btn_open_form);

        // Dummy pre-filled data
        previousData = new ContentValues();
        previousData.put("name", "John Doe");
        previousData.put("email", "john@example.com");
        previousData.put("option", "option1");

        openFormBtn.setOnClickListener(v -> {
            List<FormField> fields = Arrays.asList(
                    new FormField("name", R.id.input_name),
                    new FormField("email", R.id.input_email)
            );

            FormDialog.show(
                    this,
                    "User Details",
                    R.layout.dialog_form,
                    fields,
                    previousData,
                    R.id.radio_group,
                    R.id.radio_option1,
                    R.id.radio_option2,
                    values -> {
                        // Handle submitted values here
                        System.out.println("Saved: " + values);
                    }
            );
        });
    }
}