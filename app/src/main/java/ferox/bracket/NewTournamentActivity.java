package ferox.bracket;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class NewTournamentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final static String SINGLE_ELIMINATION = "Single Elimination";
    final static String DOUBLE_ELIMINATION = "Double Elimination";
    final static String ROUND_ROBIN = "Round Robin";

    int setYear;
    int setMonth;
    int setDay;
    int setHour;
    String setMinute;
    int ampm;

    CheckBox registerAsTeam;

    TextView dateDay;
    TextView dateTime;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener mOnDateSetListener;
    TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    Spinner subdomainMenu;
    ArrayAdapter<CharSequence> subdomainMenuAdapter;
    Spinner formatMenu;
    ArrayAdapter<CharSequence> formatMenuAdapter;

    EditText description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tournament);

        registerAsTeam = findViewById(R.id.participants_checkbox_team_register);
        registerAsTeam.setSelected(true);

        dateDay = findViewById(R.id.date_day);
        dateTime = findViewById(R.id.date_time);
        calendar = Calendar.getInstance();
        setYear = calendar.get(Calendar.YEAR);
        setMonth = calendar.get(Calendar.MONTH);
        setDay = calendar.get(Calendar.DAY_OF_MONTH);
        setHour = calendar.get(Calendar.HOUR);
        setMinute = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.MINUTE));
        ampm = calendar.get(Calendar.AM_PM);


        String defDate = setYear + "/" + (setMonth + 1) + "/" + setDay;
        dateDay.setText(defDate);


        String time = setHour + ":" + setMinute + " " + (ampm == Calendar.AM ? "AM" : "PM");
        dateTime.setText(time);

        dateDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calendar = Calendar.getInstance();
//                setYear = calendar.get(Calendar.YEAR);
//                setMonth = calendar.get(Calendar.MONTH);
//                setDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnDateSetListener, setYear, setMonth, setDay);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                calendar = Calendar.getInstance();
//                int setHour = calendar.get(Calendar.HOUR);
//                int setMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnTimeSetListener, setHour, Integer.valueOf(setMinute), false);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();
            }
        });

        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String date = year + "/" + (month + 1) + "/" + dayOfMonth;
                dateDay.setText(date);

                setYear = year;
                setMonth = month;
                setDay = dayOfMonth;
            }
        };

        mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String minuteTmp = String.format(Locale.ENGLISH, "%02d", minute);

                Log.d("minute", minuteTmp);
                String AM_PM = hourOfDay < 12 ? "AM" : "PM";
                if (hourOfDay > 12) {
                    hourOfDay = hourOfDay - 12;
                } else if (hourOfDay == 0) {
                    hourOfDay = 12;
                }


                String time = hourOfDay + ":" + minuteTmp + " " + AM_PM;
                dateTime.setText(time);

                setHour = hourOfDay;
                setMinute = minuteTmp;
            }
        };

        subdomainMenu = findViewById(R.id.subdomain_menu);
        subdomainMenu.setOnItemSelectedListener(this);
        subdomainMenuAdapter = ArrayAdapter.createFromResource(this, R.array.subdomain_menu_array, R.layout.menu_spinner_item);
        subdomainMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subdomainMenu.setAdapter(subdomainMenuAdapter);

        formatMenu = findViewById(R.id.format_menu);
        formatMenu.setOnItemSelectedListener(this);
        formatMenuAdapter = ArrayAdapter.createFromResource(this, R.array.format_array, R.layout.menu_spinner_item);
        formatMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatMenu.setAdapter(formatMenuAdapter);

        description = findViewById(R.id.description);

        description.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.description) {
                if (v.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String item = (String) parent.getItemAtPosition(pos);


        if (parent == findViewById(R.id.format_menu)) {
            switch (item) {
                case SINGLE_ELIMINATION: {
                    findViewById(R.id.single_elim_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.round_robin_view).setVisibility(View.GONE);
                    break;
                }
                case DOUBLE_ELIMINATION: {
                    findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.double_elim_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.round_robin_view).setVisibility(View.GONE);
                    break;
                }
                case ROUND_ROBIN: {
                    findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    findViewById(R.id.round_robin_view).setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void showHostMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.host_menu);
        popup.show();
    }

    public void showFormatMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.single_elim) {
                findViewById(R.id.single_elim_view).setVisibility(View.VISIBLE);
                findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                findViewById(R.id.round_robin_view).setVisibility(View.GONE);
            }
            if (item.getItemId() == R.id.double_elim) {
                findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                findViewById(R.id.double_elim_view).setVisibility(View.VISIBLE);
                findViewById(R.id.round_robin_view).setVisibility(View.GONE);
            }
            if (item.getItemId() == R.id.round_robin) {
                findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                findViewById(R.id.round_robin_view).setVisibility(View.VISIBLE);
            }
            return false;
        });
        popup.inflate(R.menu.format_menu);
        popup.show();

    }


}
