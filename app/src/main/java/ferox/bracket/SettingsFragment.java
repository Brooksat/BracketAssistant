package ferox.bracket;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
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

import org.apache.commons.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    /**
     * Winner's bracket winner must lose twice to be eliminated
     */
    final static String GRANDS_DEFAULT = "";
    final static String GRANDS_SINGLE_MATCH = "single match";
    final static String GRANDS_SKIP_ = "skip";


    int setYear;
    int setMonth;
    int setDay;
    int setHour;
    int setMinute;
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


        tournament = new Tournament();
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
        setHour = calendar.get(Calendar.HOUR_OF_DAY);
        setMinute = calendar.get(Calendar.MINUTE);



        if (getContext() instanceof NewTournamentActivity) {
            applySettings.setText("Create");
        }



        dateDay.setOnClickListener(v -> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnDateSetListener, setYear, setMonth, setDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        dateTime.setOnClickListener(v -> {


            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mOnTimeSetListener, setHour, setMinute, true);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.show();
        });

        mOnDateSetListener = (view1, year, month, dayOfMonth) -> {

            setYear = year;
            setMonth = month;
            setDay = dayOfMonth;

            setDay(year, month, dayOfMonth);
        };

        mOnTimeSetListener = (view1, hourOfDay, minute) -> {

            setHour = hourOfDay;
            setMinute = minute;

            setTime(hourOfDay, minute);

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

        //Cannot not scroll from area in description box if focus
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

        if (tournamentURL != null) {
            ChallongeRequests.sendRequest(response -> getTournamentInfo(response), ChallongeRequests.tournamentShow(tournamentURL));
        }
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
        //handles formatting
        description.setText(Html.fromHtml(tournament.getDescription()));

        formatMenu.setSelection(formatMenuAdapter.getPosition(WordUtils.capitalize(tournament.getType())), true);
        Log.d("fragmentSettings", tournament.getType());


        holdThirdPlace.setChecked(tournament.isHoldThirdPlaceMatch());
        setGrandsModifier();


        ptsPerMatchWin.setText(String.valueOf(tournament.getSwissPtsForMatchWin()));
        ptsPerMatchTie.setText(String.valueOf(tournament.getSwissPtsForMatchTie()));
        ptsPerGameWin.setText(String.valueOf(tournament.getSwissPtsForGameWin()));
        ptsPerGameTie.setText(String.valueOf(tournament.getSwissPtsForGameTie()));
        ptsPerBye.setText(String.valueOf(tournament.getSwissPtsForBye()));

        if (tournament.getType() == SWISS.toLowerCase()) {
            ptsPerMatchWin.setText(String.valueOf(tournament.getSwissPtsForMatchWin()));
            ptsPerMatchTie.setText(String.valueOf(tournament.getSwissPtsForMatchTie()));
            ptsPerGameWin.setText(String.valueOf(tournament.getSwissPtsForGameWin()));
            ptsPerGameTie.setText(String.valueOf(tournament.getSwissPtsForGameTie()));
            ptsPerBye.setText(String.valueOf(tournament.getSwissPtsForBye()));
        } else {
            ptsPerMatchWin.setText(String.valueOf(tournament.getRrPtsForMatchWin()));
            ptsPerMatchTie.setText(String.valueOf(tournament.getRrPtsForMatchTie()));
            ptsPerGameWin.setText(String.valueOf(tournament.getRrPtsForGameWin()));
            ptsPerGameTie.setText(String.valueOf(tournament.getRrPtsForGameTie()));
        }


        checkInDuration.setText(String.valueOf(tournament.getCheckInDuration()));

        //TODO probably should navigate to java.time
        if (tournament.getStartAt() != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
                Calendar cal = Calendar.getInstance();
                cal.setTime(simpleDateFormat.parse(tournament.getStartAt()));
                setDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                Log.d("theDate", tournament.getStartAt() + " -> " + cal.getTime());

            } catch (ParseException e) {
                e.printStackTrace();

            }
        }


        maxParticipants.setText(String.valueOf(tournament.getSignUpCap()));
        showRounds.setChecked(tournament.isShowRounds());
        isTournamentPrivate.setChecked(tournament.isPrivate());
        notifyMatchOpen.setChecked(tournament.isNotifyUsersMatchesOpens());
        notifyTournamentOver.setChecked(tournament.isNotifyUsersTourneyOver());
        traditionalSeeding.setChecked(!tournament.isSequentialPairings());
        allowAttachments.setChecked(tournament.isAcceptAttachments());


    }

    private void setDay(int year, int month, int day) {
        String date = year + "/" + padLeadingZeros(month + 1, 2) + "/" + padLeadingZeros(day, 2);
        dateDay.setText(date);
    }

    private void setTime(int hour, int minute) {
        String time1 = padLeadingZeros(hour, 2) + ":" + padLeadingZeros(minute, 2);
        dateTime.setText(time1);
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

                    //handle situation when switching between RR and Swiss format
                    ptsPerMatchWin.setText(String.valueOf(tournament.getRrPtsForMatchWin()));
                    ptsPerMatchTie.setText(String.valueOf(tournament.getRrPtsForMatchTie()));
                    ptsPerGameWin.setText(String.valueOf(tournament.getRrPtsForGameWin()));
                    ptsPerGameTie.setText(String.valueOf(tournament.getRrPtsForGameTie()));


                    break;
                }
                case SWISS: {
                    getView().findViewById(R.id.single_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.double_elim_view).setVisibility(View.GONE);
                    getView().findViewById(R.id.round_robin_format).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.swiss_points_per_bye_layout).setVisibility(View.VISIBLE);
                    //handle situation when switching between RR and Swiss format
                    ptsPerMatchWin.setText(String.valueOf(tournament.getSwissPtsForMatchWin()));
                    ptsPerMatchTie.setText(String.valueOf(tournament.getSwissPtsForMatchTie()));
                    ptsPerGameWin.setText(String.valueOf(tournament.getSwissPtsForGameWin()));
                    ptsPerGameTie.setText(String.valueOf(tournament.getSwissPtsForGameTie()));
                    ptsPerBye.setText(String.valueOf(tournament.getSwissPtsForBye()));


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

    /**
     * @param number    int that is to be formatted
     * @param padAmount pad length
     * @return if padAmount > 0 then {@code String} that's been padded the specified amount
     * else returns String.valueOf(number)
     */
    private String padLeadingZeros(int number, int padAmount) {
        if (padAmount > 0) {
            return String.format(Locale.US, "%0" + 2 + "d", number);
        }

        return String.valueOf(number);
    }

    private void setGrandsModifier() {
        if (tournament.getGrandFinalsModifier() != null) {


            switch (tournament.getGrandFinalsModifier()) {
                case GRANDS_DEFAULT: {
                    grandFinalsModifier.check(R.id.grand_finals_default);
                    break;
                }
                case GRANDS_SINGLE_MATCH: {
                    grandFinalsModifier.check(R.id.grand_finals_single_match);
                    break;
                }
                case GRANDS_SKIP_: {
                    grandFinalsModifier.check(R.id.grand_finals_skip);
                    break;
                }
                default: {
                    grandFinalsModifier.check(R.id.grand_finals_default);
                }
            }
        } else {
            grandFinalsModifier.check(R.id.grand_finals_default);
        }
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
