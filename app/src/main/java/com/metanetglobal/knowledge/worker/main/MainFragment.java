package com.metanetglobal.knowledge.worker.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.auth.LoginApiInterface;
import com.metanetglobal.knowledge.worker.auth.LogoutApiInterface;
import com.metanetglobal.knowledge.worker.auth.bean.LoginRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LogoutRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LogoutResponseDTO;
import com.metanetglobal.knowledge.worker.common.AMSettings;
import com.metanetglobal.knowledge.worker.common.BaseFragment;
import com.metanetglobal.knowledge.worker.common.DefaultRestClient;
import com.metanetglobal.knowledge.worker.common.DialogHelper;
import com.metanetglobal.knowledge.worker.common.PreferenceManager;
import com.metanetglobal.knowledge.worker.common.Utils;
import com.metanetglobal.knowledge.worker.main.bean.AddWorkInOutRequestDTO;
import com.metanetglobal.knowledge.worker.main.bean.AddWorkInOutResponseDTO;
import com.github.ajalt.timberkt.Timber;
import com.metanetglobal.knowledge.worker.main.bean.PushRequestDTO;
import com.metanetglobal.knowledge.worker.main.bean.PushResponseDTO;
import com.metanetglobal.knowledge.worker.service.MyService;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main Fragment
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseFragment
 */
public class MainFragment extends BaseFragment {
    private final String TAG = MainFragment.class.getSimpleName();

    /**
     * 위치 권한 Request Code
     */
    private static final int LOCATION_PERMISSION_REQUEST = 10;

    /**
     * 출/퇴근 API Request Code
     */
    private final int REQUEST_CODE_ADD_WORK_IN_OUT = 20;

    /**
     * 출/퇴근 버튼 클릭 여부
     */
    private final int WORK_MODE_IN = 0;
    private final int WORK_MODE_OUT = 1;
    private int WORK_MODE = -1;

    private AutofitTextView userInfoUserName;
    private AutofitTextView userInfoUserPart;
    private TextView userInfoLogOutButton;

    private TextView dateInfoDate;
    private TextView dateInfoTime;

    private ImageButton onWorkButton;
    private ImageButton offWorkButton;
    private TextView workMessage;
    private LinearLayout workAlertLayout;
    private TextView workAlertText;

    private LinearLayout showScheduleButton;
    private LinearLayout addScheduleButton;

    /**
     * Location
     */
    // Current Location 을 가져오고 있는지 여부
    private boolean mRequestingLocationUpdates = false;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationManager locationManager;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Expired duration for location updates.
     */
    private static final long EXPIRED_LOCATION_MILLISECONDS = 15000;


    private double current_lat;
    private double current_lon;
    private String current_address;

    /**
     * Handler for WeakReference
     */
    private static class MainHandler extends Handler {
        private final WeakReference<MainFragment> weakReference;

        public MainHandler(MainFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        public MainFragment getWeakReference() {
            return weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            MainFragment theFrag = weakReference.get();
            if(theFrag != null) {
                theFrag.handleMessage(msg);
            }
        }
    }

    private MainHandler mHandler = new MainHandler(this);

    private boolean canClick = true;

    private void handleMessage(Message msg) {
        // NOTHING
    }

    /**
     * Default Constructor
     *
     * @param userInfo      회원 정보
     * @param workInOut     출퇴근 목록
     * @return              MainFragment Instance
     */
    public static MainFragment getInstance(String userInfo, String workInOut) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER_INFO", userInfo);
        bundle.putString("WORK_IN_OUT", workInOut);
        fragment.setArguments(bundle);

        return fragment;
    }


    public void getTokens(){


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = token;
                        Log.d(TAG, "getTokens():"+msg);
                        pushTokenSave(msg);
                        //  Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Get Root Layout
     *
     * @return Layout of this Fragment
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.tag(TAG).i("onCreateView");

        View contentView = super.onCreateView(inflater, container, savedInstanceState);

        userInfoUserName = (AutofitTextView) contentView.findViewById(R.id.main_contents_user_info_user_name);
        userInfoUserPart = (AutofitTextView) contentView.findViewById(R.id.main_contents_user_info_user_part);
        userInfoLogOutButton = (TextView) contentView.findViewById(R.id.main_contents_user_info_logout_button);

        // AMSettings.useOnOff == false 일 경우 주석 [START]
        dateInfoDate = (TextView) contentView.findViewById(R.id.main_contents_date_info_date);
        dateInfoTime = (TextView) contentView.findViewById(R.id.main_contents_date_info_time);

        onWorkButton = (ImageButton) contentView.findViewById(R.id.main_contents_work_info_onwork_button);
        offWorkButton = (ImageButton) contentView.findViewById(R.id.main_contents_work_info_offwork_button);

        workMessage = (TextView) contentView.findViewById(R.id.main_contents_work_info_message);
        workAlertLayout = (LinearLayout) contentView.findViewById(R.id.main_contents_work_info_alert_layout);
        workAlertText = (TextView) contentView.findViewById(R.id.main_contents_work_info_alert_text);
        // AMSettings.useOnOff == false 일 경우 주석 [END]

        showScheduleButton = (LinearLayout) contentView.findViewById(R.id.main_bottom_show_schedule_button_layout);
        addScheduleButton = (LinearLayout) contentView.findViewById(R.id.main_bottom_add_schedule_button_layout);

        /**
         * ClickListener of LogOut Button
         */
        userInfoLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    if(getActivity() != null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage(R.string.logout_confirm_message).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                // NOTHING
                            }
                        }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Nothing
                            }
                        }).create();
                        alertDialog.show();
                    }
                }
            }
        });

        /**
         * ClickListener of Show Schedule Button
         */
        showScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    if(getActivity() != null) {

                        String srchString = PreferenceManager.getUserId(getActivity());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AMSettings.APP_DOWN_URL +"worklist?id="+srchString));
                        startActivity(browserIntent);
/*                        Intent intent = new Intent();
                        intent.setClass(getActivity(), ScheduleListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/
                    }
                }
            }
        });

        /**
         * ClickListener of Add Schedule Button
         */
        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    if (getActivity() != null) {
/*                        Intent intent = new Intent();
                        intent.setClass(getActivity(), ScheduleDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("MODE", ScheduleDetailFragment.MODE_ADD);
                        startActivity(intent);*/
                    }
                }
            }
        });

        if(AMSettings.useOnOffWork) {
            /**
             * ClickListener of onWork Button
             */
            onWorkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(canClick) {
                        setCanClickable();

                        WORK_MODE = WORK_MODE_IN;
                        doCurrentLocation();
                    //    addWorkInOut();
                    }
                }
            });

            /**
             * ClickListener of offWork Button
             */
            offWorkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(canClick) {
                        setCanClickable();

                        WORK_MODE = WORK_MODE_OUT;
                      //  addWorkInOut();
                        doCurrentLocation();
                    }
                }
            });
        }

        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getActivity() != null) {
            getActivity().startPostponedEnterTransition();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.tag(TAG).i("onActivityCreated");


        FirebaseApp.initializeApp(getContext());
        getTokens();

        setLayoutFromData();


        /**
         * Location 관련 Initialize
         */
        if(getActivity() != null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            createLocationCallback();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Timber.tag(TAG).i("onResume");

        //setLayoutFromData();

        /**
         * Resume 시 Location 정보를 가져오던 중이었는지 체크를 한다.
         */
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (mRequestingLocationUpdates && !checkPermissions()) {
            requestPermissions(LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Timber.tag(TAG).i("onPause");

        /**
         * Pause 시 Location 정보를 가져오던 것을 Stop 한다.
         */
        stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).i("onDestroy");

        // 혹시 onPause 에서 stop 이 안되었을 경우를 대비하여 방어코드...
        stopLocationUpdates();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                if ((grantResults.length > 1 && ((grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED))) ||
                        (grantResults.length > 0 && ((grantResults[0] == PackageManager.PERMISSION_GRANTED)))) {
                    Timber.tag(TAG).d("[onRequestPermissionsResult] Permission Granted");
                    doCurrentLocation();
                } else {
                    // 퍼미션 거부 했을 때
                    Timber.tag(TAG).e("[onRequestPermissionsResult] Permission Not Granted");
                    initCurrentLocation();
                    mRequestingLocationUpdates = false;
                    addWorkInOut();
                }
                break;
            }
        }
    }

    /**
     * 연속 클릭 방지
     */
    private void setCanClickable() {
        canClick = false;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                canClick = true;
            }
        }, 500);
    }

    /**
     * 가져온 Data 를 가지고 Layout 에 보여준다.
     */
    private void setLayoutFromData() {
        if(getArguments() != null) {
            String userInfoString = getArguments().getString("USER_INFO");
            String workInOutString = "";

            if(AMSettings.useOnOffWork) {
                workInOutString = getArguments().getString("WORK_IN_OUT");
            }

            if (userInfoString != null && userInfoString.length() > 0) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<LoginResponseDTO.UserInfo>(){}.getType();
                    LoginResponseDTO.UserInfo userInfo = gson.fromJson(userInfoString, type);

                    String userName = Utils.nullCheck(userInfo.getEmpName()) + " " + Utils.nullCheck(userInfo.getPosition());
                    userInfoUserName.setText(userName);

                    userInfoUserPart.setText(Utils.nullCheck(userInfo.getPrmryDivNm()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN);
            SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");

            if (workInOutString != null && workInOutString.length() > 0) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<LoginResponseDTO.WorkInOutList>>(){}.getType();
                    List<LoginResponseDTO.WorkInOutList> workInOutList = gson.fromJson(workInOutString, type);

                    if (workInOutList.size() > 0) {
                        String startTimeString = "";
                        String endTimeString = "";
                        boolean isOffWork = false;    // 퇴근 여부

                        for (LoginResponseDTO.WorkInOutList item : workInOutList) {
                            try {
                                String date = item.getWrkInOutDate();

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                                Date mDate = dateFormat.parse(date);

                                String mDateString = sdf.format(mDate);
                                dateInfoDate.setText(mDateString);

                                String time = item.getWrkInOutTime();

                                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
                                Date mTime = timeFormat.parse(time);

                                if (item.getWrkInOutStatus().equalsIgnoreCase("IN")) {
                                    // 출근
                                    startTimeString = tdf.format(mTime);
                                } else {
                                    // 퇴근
                                    endTimeString = tdf.format(mTime);
                                    isOffWork = true;
                                }
                            } catch (Exception de) {
                                de.printStackTrace();
                            }
                        }

                        dateInfoTime.setText(startTimeString + " ~ " + endTimeString);


                        if (isOffWork) {
                            workMessage.setText(R.string.main_off_work_message);
                        } else {
                            workMessage.setText(R.string.main_on_work_message);
                        }

                        if(endTimeString.equals("")||endTimeString==null){
                            AMSettings.workOutTimeChk = "off";
                        }else{
                            AMSettings.workOutTimeChk = "on";
                        }
                    } else {
                        // 출/퇴근 기록이 없으면 오늘날짜만 표시 해준다.
                        Date today = new Date();
                        dateInfoDate.setText(sdf.format(today));

                        AMSettings.workInTimeChk = "off";
                        AMSettings.workOutTimeChk = "off";

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if(AMSettings.useOnOffWork) {
                    Date today = new Date();
                    dateInfoDate.setText(sdf.format(today));
                }
            }
        }
    }

    /**
     * 회원 정보가 업데이트 되면 Layout 쪽도 업데이트 해준다.
     *
     * @param userInfoString 회원 정보
     * @param workInOutListString 출퇴근 목록
     */
    public void refreshUserInfo(String userInfoString, String workInOutListString) {
        if(getArguments() != null) {
            getArguments().putString("USER_INFO", userInfoString);
            getArguments().putString("WORK_IN_OUT", workInOutListString);

            setLayoutFromData();
        }
    }

    /**
     * Login
     *
     * @param requestCode   RequestCode of Login
     * @param stringParam   Param of Login
     */
    @Override
    public void doAuthentication(final int requestCode, String stringParam) {
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUserId(PreferenceManager.getUserId(getActivity()));
        requestDTO.setUserPW(Utils.encryptSha1(PreferenceManager.getUserPwd(getActivity())));

        Timber.tag(TAG).d("[doAuthentication] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<LoginApiInterface> loginRestClient = new DefaultRestClient<>();
        LoginApiInterface loginInterface = loginRestClient.getClient(LoginApiInterface.class);

        Call<LoginResponseDTO> call = loginInterface.doLogin(requestDTO);
        call.enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                hideLoadingProgressBar();

                if(response.isSuccessful()) {
                    LoginResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[doAuthentication] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            String userInfoString = Utils.convertObjToJSON(responseDTO.getUserInfo());
                            Timber.tag(TAG).d("[doAuthentication][USER_INFO] " + userInfoString);

                            String workInOutListString = Utils.convertObjToJSON(responseDTO.getWorkInOutList());
                            Timber.tag(TAG).d("[doAuthentication][WORK_IN_OUT] " + workInOutListString);

                            refreshUserInfo(userInfoString, workInOutListString);

                            switch(requestCode) {
                                case REQUEST_CODE_ADD_WORK_IN_OUT: {
                                    retryAddWorkInOut();

                                    break;
                                }
                            }
                        } else {
                            switch (responseDTO.getErrorType()) {
                                default: {
                                    Timber.tag(TAG).e("[doAuthentication] ERROR TYPE : " + responseDTO.getErrorType());

                                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));

                                    setReLogInCount(0);

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[doAuthentication] RESPONSE BODY IS NULL");

                        DialogHelper.showNormalAlertDialog(getActivity(), "");

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[doAuthentication] RESPONSE FAIL code : " + response.code());

                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[doAuthentication][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                DialogHelper.showNormalAlertDialog(getActivity(), "");

                setReLogInCount(0);
            }
        });
    }

    /**
     * 로그아웃
     */
    private void logout() {
        showLoadingProgressBar();

        LogoutRequestDTO requestDTO = new LogoutRequestDTO();

        Timber.tag(TAG).d("[logout] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<LogoutApiInterface> logoutRestClient = new DefaultRestClient<>();
        LogoutApiInterface logoutInterface = logoutRestClient.getClient(LogoutApiInterface.class);

        Call<LogoutResponseDTO> call = logoutInterface.doLogout(requestDTO);


        call.enqueue(new Callback<LogoutResponseDTO>() {
            @Override
            public void onResponse(Call<LogoutResponseDTO> call, Response<LogoutResponseDTO> response) {
                hideLoadingProgressBar();

                if(response.isSuccessful()) {
                    LogoutResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[logout] RESPONSE : " + Utils.convertObjToJSON(responseDTO));
                    }

                    try {
                        PreferenceManager.setUserId(getActivity(), "");
                        PreferenceManager.setUserPwd(getActivity(), "");

                        AMSettings.COOKIESET.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    goLogin();
                } else {
                    Timber.tag(TAG).e("[logout] RESPONSE FAIL code : " + response.code());

                    try {
                        PreferenceManager.setUserId(getActivity(), "");
                        PreferenceManager.setUserPwd(getActivity(), "");

                        AMSettings.COOKIESET.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    goLogin();
                }
            }

            @Override
            public void onFailure(Call<LogoutResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[logout][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                try {
                    PreferenceManager.setUserId(getActivity(), "");
                    PreferenceManager.setUserPwd(getActivity(), "");

                    AMSettings.COOKIESET.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                goLogin();
            }
        });
    }

    /**
     * 근무일정 저장 재시도
     */
    private void retryAddWorkInOut() {
        setReLogInCount(getReLogInCount() + 1);
        addWorkInOut();
    }

    /**
     * 근무일정 저장
     */
    private void addWorkInOut() {
        showLoadingProgressBar();
        Timber.tag(TAG).d("[addWorkInOut] REQUEST : //");
        AddWorkInOutRequestDTO requestDTO = new AddWorkInOutRequestDTO();
        requestDTO.setWrkInOutStatus(WORK_MODE == WORK_MODE_IN ? "IN" : "OUT");
        if(current_lat != 0 && current_lon != 0) {
            requestDTO.setLatitudeNum(current_lat);
            requestDTO.setLongitudeNum(current_lon);
            requestDTO.setAddressInfo(current_address);
        }

        String empNO  = PreferenceManager.getUserCode(getActivity());
        String usrId  = PreferenceManager.getUserId(getActivity());

        requestDTO.setEmpNo(empNO);
        requestDTO.setUsrId(usrId);
        requestDTO.setTelNo(AMSettings.PHONE);
        requestDTO.setEmpNo(AMSettings.EMPNO);

        Timber.tag(TAG).d("[addWorkInOut] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<AddWorkInOutApiInterface> workInOutRestClient = new DefaultRestClient<>();
        AddWorkInOutApiInterface workInOutInterface = workInOutRestClient.getClient(AddWorkInOutApiInterface.class);

        Call<AddWorkInOutResponseDTO> call = workInOutInterface.addWorkInOut(requestDTO);

        call.enqueue(new Callback<AddWorkInOutResponseDTO>() {
            @Override
            public void onResponse(Call<AddWorkInOutResponseDTO> call, Response<AddWorkInOutResponseDTO> response) {
                if(response.isSuccessful()) {
                    // 성공
                    AddWorkInOutResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[addWorkInOut] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            hideLoadingProgressBar();

                            switch(WORK_MODE) {
                                case WORK_MODE_IN: {
                                    // 위치를 못가져 왔을 때 위치 정보가 꺼져 있어 출근시간만 확인 된다는 메시지를 보여준다.
                                    if(current_lat == 0 && current_lon == 0) {
                                        workAlertText.setText(R.string.main_on_work_alert_message);
                                        showLocationAlertMessage();
                                    }

                                    workMessage.setText(R.string.main_on_work_message);

                                    DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.main_on_work_success_message));

                                    break;
                                }
                                case WORK_MODE_OUT: {
                                    // 위치를 못가져 왔을 때 위치 정보가 꺼져 있어 퇴근시간만 확인 된다는 메시지를 보여준다.
                                    if(current_lat == 0 && current_lon == 0) {
                                        workAlertText.setText(R.string.main_off_work_alert_message);
                                        showLocationAlertMessage();
                                    }

                                    workMessage.setText(R.string.main_off_work_message);

                                    DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.main_off_work_success_message));

                                    break;
                                }
                            }

                            // Main 의 Layout 도 갱신해준다.
                            if(responseDTO.getWorkInOutList() != null) {
                                Timber.tag(TAG).d("[addWorkInOut] RESPONSE 1: " + Utils.convertObjToJSON(responseDTO));

                                if(getArguments() != null) {

                                    getArguments().putString("WORK_IN_OUT", Utils.convertObjToJSON(responseDTO.getWorkInOutList()));
                                    Timber.tag(TAG).d("[addWorkInOut] RESPONSE 2: " + Utils.convertObjToJSON(responseDTO));
                                    setLayoutFromData();
                                }
                            }

                            setReLogInCount(0);
                        } else {
                            Timber.tag(TAG).d("[addWorkInOut] RESPONSE 2: " + responseDTO.getErrorType());
                            switch (responseDTO.getErrorType()) {
                                case AMSettings.ErrorType.SESSION_ERROR: {
                                    if (getReLogInCount() <= AMSettings.RELOGIN_RETRY_COUNT) {
                                        doAuthentication(REQUEST_CODE_ADD_WORK_IN_OUT, "");
                                    } else {
                                        hideLoadingProgressBar();

                                        setReLogInCount(0);

                                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                    }
                                    break;
                                }
                                default: {
                                    hideLoadingProgressBar();

                                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));

                                    setReLogInCount(0);
                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[addWorkInOut] RESPONSE BODY IS NULL");

                        hideLoadingProgressBar();

                        DialogHelper.showNormalAlertDialog(getActivity(), "");

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[addWorkInOut] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<AddWorkInOutResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[addWorkInOut][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                DialogHelper.showNormalAlertDialog(getActivity(), "");

                setReLogInCount(0);
            }
        });
    }

    /**
     * 위치 정보가 꺼져 있어 출/퇴근 시간만 확인 됩니다 메시지 보여주기
     *
     */
    private void showLocationAlertMessage() {
        final Animation hideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.main_work_alert_dismiss);
        hideAnim.setInterpolator(getActivity(), android.R.anim.accelerate_interpolator);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // NOTHING
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                workAlertLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // NOTHING
            }
        });

        Animation showAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.main_work_alert_show);
        showAnim.setInterpolator(getActivity(), android.R.anim.accelerate_interpolator);
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // NOTHING
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Show Animation 이 종료되면 2초 후 Hide Animation 을 시작해준다.
                if(mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(workAlertLayout != null && hideAnim != null) {
                                workAlertLayout.startAnimation(hideAnim);
                            }
                        }
                    }, 2000);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // NOTHING
            }
        });

        if(workAlertLayout != null) {
            workAlertLayout.setVisibility(View.VISIBLE);
            workAlertLayout.startAnimation(showAnim);
        }
    }



    /**
     * LocationRequest 생성
     */
    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(EXPIRED_LOCATION_MILLISECONDS);
        return mLocationRequest;
    }

    /**
     * Location Callback 생성
     */
    private void createLocationCallback() {



        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Timber.tag(TAG).d("[LocationCallback] current_lat1 : " + current_lat + " current_lon : " + current_lon + " current_address : " + current_address);
                if(getActivity() != null) {
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        current_lat = locationResult.getLastLocation().getLatitude();
                        current_lon = locationResult.getLastLocation().getLongitude();
                        current_address = "";

/*                        // 주소변환
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(current_lat, current_lon, 1);
                            if(addresses == null || addresses.size() == 0) {
                                Timber.tag(TAG).e("[LocationCallback] Geocoder address is null");
                            } else {
                                current_address = addresses.get(0).getAddressLine(0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    } else {
                        Timber.tag(TAG).d("[LocationCallback] current_lat2 : " + current_lat + " current_lon : " + current_lon + " current_address : " + current_address);
                        initCurrentLocation();
                    }

                    addWorkInOut();

                    // 한번만 위치 가져온다.
                    stopLocationUpdates();
                }
            }
        };
    }

    /**
     * Initialize Current Location Information
     */
    private void initCurrentLocation() {
        current_lat = 0;
        current_lon = 0;
        current_address = "";
    }

    /**
     * 현재 위치 가져오기
     */
    private void doCurrentLocation() {
        if(!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    /**
     * Location Update 시작
     */
    private void startLocationUpdates() {
        Timber.tag(TAG).e("[startLocationUpdates] PlayService is not usable REQUEST");
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(!checkPlayServices()) {
                // PlayService 를 사용할 수 없을 때
                Timber.tag(TAG).e("[startLocationUpdates] PlayService is not usable~");
                initCurrentLocation();
                mRequestingLocationUpdates = false;
                addWorkInOut();

                return;
            }

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // 위치 꺼져있을 때
                Timber.tag(TAG).e("[startLocationUpdates] GPS Provider is not usable");
                initCurrentLocation();
                mRequestingLocationUpdates = false;
                addWorkInOut();
            } else {
                Timber.tag(TAG).d("[startLocationUpdates] Request Location Updates!!!");
                showLoadingProgressBar();

           //     locationManager.requestLocationUpdates(mProvider, 3000, 10, mListener);
                mFusedLocationClient.requestLocationUpdates(createLocationRequest(), mLocationCallback, null);
            }
        } else {
            Timber.tag(TAG).d("[startLocationUpdates] requestPermissions");
            requestPermissions(LOCATION_PERMISSION_REQUEST);
        }
    }

    /**
     * Stop Location Update
     */
    private void stopLocationUpdates() {
        if(getActivity() == null || !mRequestingLocationUpdates) return;

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mRequestingLocationUpdates = false;
                }
            });
    }

    /**
     * Permission 체크
     *
     * @return  Is granted Permissions
     */
    private boolean checkPermissions() {
        if(getActivity() != null) {
            int permissionState = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionState1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
            return (permissionState == PackageManager.PERMISSION_GRANTED) && (permissionState1 == PackageManager.PERMISSION_GRANTED);
        } else {
            return false;
        }
    }

    /**
     * Permission 요청
     *
     * @param requestCode   RequestCode of Permissions
     */
    private void requestPermissions(int requestCode) {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    /**
     * Google play service 사용 가능 여부 검사
     *
     * @return  PlayService 사용 가능 여부
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Timber.tag(TAG).e("[checkPlayServices] resultCode: " + resultCode);
            } else {
                Timber.tag(TAG).e("[checkPlayServices] This device is not supported.");
            }
            return false;
        }
        return true;
    }


    private void pushTokenSave(String token) {
        showLoadingProgressBar();
        Timber.tag(TAG).d("[pushTokenSave] REQUEST : //");
        PushRequestDTO requestDTO = new PushRequestDTO();

        String empNO  = PreferenceManager.getUserCode(getActivity());

        requestDTO.setEmpNo(empNO);
        requestDTO.setToken(token);
//        requestDTO.setEmpNo(AMSettings.EMPNO);

        Timber.tag(TAG).d("[PushInterface] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<PushInterface> workInOutRestClient = new DefaultRestClient<>();
        PushInterface pushInterface = workInOutRestClient.getClient(PushInterface.class);

        Call<PushResponseDTO> call = pushInterface.pushTokenSave(requestDTO);

        call.enqueue(new Callback<PushResponseDTO>() {
            @Override
            public void onResponse(Call<PushResponseDTO> call, Response<PushResponseDTO> response) {
                if (response.isSuccessful()) {
                    // 성공
                    PushResponseDTO responseDTO = response.body();

                    if (responseDTO != null) {
                        Timber.tag(TAG).d("[PushRequestDTO] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if (responseDTO.isSuccess()) {
                            hideLoadingProgressBar();

                        }


                    }


                }
            }

            @Override
            public void onFailure(Call<PushResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[addWorkInOut][FAIL] message : " + t.getMessage());
                hideLoadingProgressBar();
                DialogHelper.showNormalAlertDialog(getActivity(), "");

            }
        });
    }
}