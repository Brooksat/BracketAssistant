package ferox.bracket;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Calendar;
import java.util.Locale;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    final static String SINGLE_ELIMINATION = "Single Elimination";
    final static String DOUBLE_ELIMINATION = "Double Elimination";
    final static String ROUND_ROBIN = "Round Robin";
    final static String SWISS = "Swiss";

    final static String MATCH_WIN = "Match Wins";
    final static String GAME_SET_WINS = "Game/Set Wins";
    final static String POINTS_SCORED = "Points Scored";
    final static String POINTS_DIFF = "Points Difference";
    final static String CUSTOM = "Custom";


    int setYear;
    int setMonth;
    int setDay;
    int setHour;
    String setMinute;
    int ampm;

    Tournament tournament;


    Calendar calendar;
    DatePickerDialog.OnDateSetListener mOnDateSetListener;
    TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
    Spinner formatMenu;
    ArrayAdapter<CharSequence> formatMenuAdapter;
    Spinner rankedByMenu;
    ArrayAdapter<CharSequence> rankedByMenuAdapter;

    EditText name;
    EditText url;
    EditText subDomain;
    EditText description;
    CheckBox holdThirdPlace;
    RadioGroup grandFinalsModifier;
    EditText ptsPerMatchWin;
    EditText ptsPerMatchTie;
    EditText ptsPerGameWin;
    EditText ptsPerGameTie;
    EditText ptsPerBye;
    TextView dateDay;
    TextView dateTime;
    EditText checkInDuration;
    EditText maxParticipants;
    CheckBox showRounds;
    CheckBox isTournamentPrivate;
    CheckBox notifyMatchOpen;
    CheckBox notifyTournamentOver;
    CheckBox traditionalSeeding;
    CheckBox allowAttachments;
    Button applySettings;

    String tournamentURL;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        tournamentURL = intent.getStringExtra("tournamentURL");
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        name = view.findViewById(R.id.tournament_name);
        url = view.findViewById(R.id.url);
        subDomain = view.findViewById(R.id.subdomain);
        description = view.findViewById(R.id.description);
        holdThirdPlace = view.findViewById(R.id.third_place_match_checkbox);
        grandFinalsModifier = view.findViewById(R.id.grand_finals_radio_group);
        ptsPerMatchWin = view.findViewById(R.id.points_per_match_win);
        ptsPerMatchTie = view.findViewById(R.id.points_per_match_tie);
        ptsPerGameWin = view.findViewById(R.id.points_per_gameset_win);
        ptsPerGameTie = view.findViewById(R.id.points_per_gameset_tie);
        ptsPerBye = view.findViewById(R.id.points_per_bye);

        //TODO need to accomodate for round robin points
        checkInDuration = view.findViewById(R.id.check_in_duration);
        maxParticipants = view.findViewById(R.id.max_number_participants);
        showRounds = view.findViewById(R.id.show_rounds_checkbox);
        isTournamentPrivate = view.findViewById(R.id.tournament_private_checkbox);
        notifyMatchOpen = view.findViewById(R.id.notify_match_open_checkbox);
        notifyTournamentOver = view.findViewById(R.id.notify_tournament_over_checkbox);
        traditionalSeeding = view.findViewById(R.id.traditional_seeding_checkbox);
        allowAttachments = view.findViewById(R.id.allow_attachments_checkbox);
        applySettings = view.findViewById(R.id.apply_settings);

        dateDay = view.findViewById(R.id.date_day);
        dateTime = view.findViewById(R.id.date_time);
        calendar = Calendar.getInstance();
        setYear = calendar.get(Calendar.YEAR);
        setMonth = calendar.get(Calendar.MONTH);
        setDay = calendar.get(Calendar.DAY_OF_MONTH);
        setHour = calendar.get(Calendar.HOUR);
        setMinute = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.MINUTE));
        ampm = calendar.get(Calendar.AM_PM);


        if (getContext() instanceof NewTournamentActivity) {
            applySettings.setText("Create");
        }

        String defDate = setYear + "/" + (setMonth + 1) + "/" + setDay;
        dateDay.setText(defDate);


        String time = setHour + ":" + setMinute + " " + (ampm == Calendar.AM ? "AM" : "PM");
        dateTime.setText(time);

        dateDay.setOnClickListener(v -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnDateSetListener, setYear, setMonth, setDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        dateTime.setOnClickListener(v -> {


            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnTimeSetListener, setHour, Integer.valueOf(setMinute), false);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.show();
        });

        mOnDateSetListener = (view1, year, month, dayOfMonth) -> {

            String date = year + "/" + (month + 1) + "/" + dayOfMonth;
            dateDay.setText(date);

            setYear = year;
            setMonth = month;
            setDay = dayOfMonth;
        };

        mOnTimeSetListener = (view12, hourOfDay, minute) -> {
            String minuteTmp = String.format(Locale.ENGLISH, "%02d", minute);

            Log.d("minute", minuteTmp);
            String AM_PM = hourOfDay < 12 ? "AM" : "PM";
            if (hourOfDay > 12) {
                hourOfDay = hourOfDay - 12;
            } else if (hourOfDay == 0) {
                hourOfDay = 12;
            }


            String time1 = hourOfDay + ":" + minuteTmp + " " + AM_PM;
            dateTime.setText(time1);

            setHour = hourOfDay;
            setMinute = minuteTmp;
        };


        formatMenu = view.findViewById(R.id.format_menu);
        formatMenu.setOnItemSelectedListener(this);
        formatMenuAdapter = ArrayAdapter.createFromResource(getContext(), R.array.format_array, R.layout.menu_spinner_item);
        formatMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatMenu.setAdapter(formatMenuAdapter);

        rankedByMenu = view.findViewById(R.id.ranked_by_menu);
        rankedByMenu.setOnItemSelectedListener(this);
        rankedByMenuAdapter = ArrayAdapter.createFromResource(getContext(), R.array.ranked_by_array, R.layout.menu_spinner_item);
        rankedByMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rankedByMenu.setAdapter(rankedByMenuAdapter);


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

        ChallongeRequests.sendRequest(response -> getTournamentInfo(response), ChallongeRequests.tournamentShow(tournamentURL));
        Log.d("onCreateView", "iscalled");
        return view;
    }

    private void getTournamentInfo(String response) {
        Log.d("getTournamentInfo", "isCalled");
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement tournamentInfo = jsonParser.parse(response);
        tournament = gson.fromJson(tournamentInfo.getAsJsonObject().get("tournament"), Tournament.class);
        Log.d("getTournamentInfo2", tournament.getName());

        name.setText(tournament.getName());
        url.setText(tournament.getUrl());
        subDomain.setText(tournament.getSubdomain());
        //TODO may have to process it first
        description.setText(tournament.getDescription());

        //TODO value from json dooesnt match in  onItemSelected
        formatMenu.setSelection(formatMenuAdapter.getPosition(tournament.getType()), true);
        Log.d("fragmentSettings", tournament.getType());


        holdThirdPlace.setChecked(tournament.isHoldThirdPlaceMatch());
        //TODO set radio for grand finals modifier

        //TODO probably should change all tournament values to strings
        ptsPerMatchWin.setText(String.valueOf(tournament.getSwissPtsForMatchWin()));
        ptsPerMatchTie.setText(String.valueOf(tournament.getSwissPtsForMatchTie()));
        ptsPerGameWin.setText(String.valueOf(tournament.getSwissPtsForGameWin()));
        ptsPerGameTie.setText(String.valueOf(tournament.getSwissPtsForGameTie()));
        ptsPerBye.setText(String.valueOf(tournament.getSwissPtsForBye()));

        ptsPerMatchWin.setText(String.valueOf(tournament.getSwissPtsForMatchWin()));
        ptsPerMatchTie.setText(String.valueOf(tournament.getSwissPtsForMatchTie()));
        ptsPerGameWin.setText(String.valueOf(tournament.getSwissPtsForGameWin()));
        ptsPerGameTie.setText(String.valueOf(tournament.getSwissPtsForGameTie()));
        ptsPerBye.setText(String.valueOf(tournament.getSwissPtsForBye()));

        checkInDuration.setText(String.valueOf(tournament.getCheckInDuration()));
        maxParticipants.setText(String.valueOf(tournament.getSignUpCap()));
        showRounds.setChecked(tournament.isShowRounds());
        isTournamentPrivate.setChecked(tournament.isPrivate());
        notifyMatchOpen.setChecked(tournament.isNotifyUsersMatchesOpens());
        notifyTournamentOver.setChecked(tournament.isNotifyUsersTourneyOver());
        traditionalSeeding.setChecked(!tournament.isSequentialPairings());
        allowAttachments.setChecked(tournament.isAcceptAttachments());


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String item = (String) parent.getItemAtPosition(pos);


        if (parent == getView().findViewById(R.id.format_menu)) {
            switch (item) {
                case SINGLE_ELIMINATION: {
                    getView().findViewById(R.id.single_elim_view).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.round_robin_format).setVisibility(View.GONE);
                    break;
                }
                case DOUBLE_ELIMINATION: {
                    getView().findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.double_elim_view).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.round_robin_format).setVisibility(View.GONE);
                    break;
                }
                case ROUND_ROBIN: {
                    getView().findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.round_robin_format).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.swiss_points_per_bye_layout).setVisibility(View.GONE);
                    break;
                }
                case SWISS: {
                    getView().findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.round_robin_format).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.swiss_points_per_bye_layout).setVisibility(View.VISIBLE);
                    break;
                }

            }
        }
        if (parent == getView().findViewById(R.id.ranked_by_menu)) {

            switch (item) {
                case CUSTOM: {
                    getView().findViewById(R.id.round_robin_custom_parameters).setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    getView().findViewById(R.id.round_robin_custom_parameters).setVisibility(View.GONE);
                    break;
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void applySettings() {

    }


    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
}
