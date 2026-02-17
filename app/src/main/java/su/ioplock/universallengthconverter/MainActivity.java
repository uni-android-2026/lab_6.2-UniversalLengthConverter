package su.ioplock.universallengthconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI
    private EditText inputValue;
    private Spinner unitSpinner;
    private Button btnConvert, btnClear;
    private TextView errorText;

    private TextView outMm, outCm, outM, outKm, outInch, outFoot, outYard, outNauticalMile, outLightYear;

    // Units
    private enum Unit {
        MM("мм", 0.001),
        CM("см", 0.01),
        M("м", 1.0),
        KM("км", 1000.0),
        INCH("дюйм", 0.0254),
        FOOT("фут", 0.3048),
        YARD("ярд", 0.9144),
        NAUTICAL_MILE("морская миля", 1852.0),
        LIGHT_YEAR("световой год", 9.4607304725808e15);

        final String label;
        final double metersPerUnit;

        Unit(String label, double metersPerUnit) {
            this.label = label;
            this.metersPerUnit = metersPerUnit;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();
        setupSpinner();
        setupButtons();
    }

    private void bindViews() {
        inputValue = findViewById(R.id.inputValue);
        unitSpinner = findViewById(R.id.unitSpinner);
        btnConvert = findViewById(R.id.btnConvert);
        btnClear = findViewById(R.id.btnClear);
        errorText = findViewById(R.id.errorText);

        outMm = findViewById(R.id.outMm);
        outCm = findViewById(R.id.outCm);
        outM = findViewById(R.id.outM);
        outKm = findViewById(R.id.outKm);
        outInch = findViewById(R.id.outInch);
        outFoot = findViewById(R.id.outFoot);
        outYard = findViewById(R.id.outYard);
        outNauticalMile = findViewById(R.id.outNauticalMile);
        outLightYear = findViewById(R.id.outLightYear);
    }

    private void setupSpinner() {
        ArrayAdapter<Unit> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Unit.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);
        unitSpinner.setSelection(2); // по умолчанию "м"
    }

    private void setupButtons() {
        btnConvert.setOnClickListener(v -> convert());
        btnClear.setOnClickListener(v -> clearAll());
    }

    private void convert() {
        hideError();

        Double value = tryParseDouble(inputValue.getText() == null ? "" : inputValue.getText().toString());
        if (value == null) {
            showError("Введите корректное число.");
            return;
        }

        Unit from = (Unit) unitSpinner.getSelectedItem();
        if (from == null) {
            showError("Выберите единицу измерения.");
            return;
        }

        // to meters
        double meters = value * from.metersPerUnit;

        // from meters to all units
        setOutput(outMm, meters / Unit.MM.metersPerUnit);
        setOutput(outCm, meters / Unit.CM.metersPerUnit);
        setOutput(outM, meters / Unit.M.metersPerUnit);
        setOutput(outKm, meters / Unit.KM.metersPerUnit);
        setOutput(outInch, meters / Unit.INCH.metersPerUnit);
        setOutput(outFoot, meters / Unit.FOOT.metersPerUnit);
        setOutput(outYard, meters / Unit.YARD.metersPerUnit);
        setOutput(outNauticalMile, meters / Unit.NAUTICAL_MILE.metersPerUnit);
        setOutput(outLightYear, meters / Unit.LIGHT_YEAR.metersPerUnit);
    }

    private void clearAll() {
        hideError();
        inputValue.setText("");
        outMm.setText("-");
        outCm.setText("-");
        outM.setText("-");
        outKm.setText("-");
        outInch.setText("-");
        outFoot.setText("-");
        outYard.setText("-");
        outNauticalMile.setText("-");
        outLightYear.setText("-");
    }

    private void setOutput(TextView tv, double value) {
        // Автоформат: для больших/малых значений - экспоненциально, иначе - обычный вид
        String s;
        double abs = Math.abs(value);

        if (abs != 0 && (abs >= 1e9 || abs < 1e-6)) {
            s = String.format(Locale.US, "%.6e", value);
        } else {
            s = String.format(Locale.US, "%.6f", value);
            // убрать лишние нули и точку в конце
            s = trimZeros(s);
        }

        tv.setText(s);
    }

    private String trimZeros(String s) {
        if (!s.contains(".")) return s;
        // remove trailing zeros
        while (s.endsWith("0")) s = s.substring(0, s.length() - 1);
        // remove trailing dot
        if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        return s;
    }

    private Double tryParseDouble(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        // allow comma as decimal separator
        s = s.replace(',', '.');

        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showError(String msg) {
        errorText.setText(msg);
        errorText.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorText.setVisibility(View.GONE);
    }
}
