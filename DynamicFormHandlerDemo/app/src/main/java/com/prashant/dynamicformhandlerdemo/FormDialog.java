package com.prashant.dynamicformhandlerdemo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;
public class FormDialog {
    public static void show(Context context,
                            String title,
                            int layoutId,
                            List<FormField> fields,
                            ContentValues existing,
                            int radioGroupId,
                            int radioOption1Id,
                            int radioOption2Id,
                            OnFormSaveListener listener) {

        View dialogView = LayoutInflater.from(context).inflate(layoutId, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setView(dialogView);

        // Pre-fill existing values
        if (existing != null) {
            for (FormField field : fields) {
                EditText input = dialogView.findViewById(field.viewId);
                if (input != null && existing.containsKey(field.key)) {
                    input.setText(existing.getAsString(field.key));
                }
            }

            if (radioGroupId != -1 && existing.containsKey("option")) {
                RadioGroup group = dialogView.findViewById(radioGroupId);
                if (group != null) {
                    String option = existing.getAsString("option");
                    if ("option1".equalsIgnoreCase(option)) group.check(radioOption1Id);
                    else if ("option2".equalsIgnoreCase(option)) group.check(radioOption2Id);
                }
            }
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            ContentValues values = new ContentValues();
            for (FormField field : fields) {
                EditText input = dialogView.findViewById(field.viewId);
                if (input != null) {
                    values.put(field.key, input.getText().toString().trim());
                }
            }

            if (radioGroupId != -1) {
                RadioGroup group = dialogView.findViewById(radioGroupId);
                if (group != null) {
                    int selectedId = group.getCheckedRadioButtonId();
                    if (selectedId == radioOption1Id) values.put("option", "option1");
                    else if (selectedId == radioOption2Id) values.put("option", "option2");
                }
            }

            listener.onSave(values);
            Toast.makeText(context, "Form saved!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
