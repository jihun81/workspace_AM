package com.metanetglobal.knowledge.worker.schedule;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.auth.LoginApiInterface;
import com.metanetglobal.knowledge.worker.auth.bean.LoginRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO;
import com.metanetglobal.knowledge.worker.common.AMSettings;
import com.metanetglobal.knowledge.worker.common.BaseFragment;
import com.metanetglobal.knowledge.worker.common.DefaultRestClient;
import com.metanetglobal.knowledge.worker.common.DialogHelper;
import com.metanetglobal.knowledge.worker.common.PreferenceManager;
import com.metanetglobal.knowledge.worker.common.Utils;
import com.metanetglobal.knowledge.worker.common.utils.OnChildItemClickListener;
import com.metanetglobal.knowledge.worker.main.otto_interfaces.Event_UpdateUserInfo;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleListByDateRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleListByDateResponseDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleListItemDTO;
import com.github.ajalt.timberkt.Timber;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Schedule List Fragment
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseFragment
 */
public class ScheduleListFragment extends BaseFragment {
    private final String TAG = ScheduleListFragment.class.getSimpleName();

    private final int REQUEST_CODE_GET_SCHEDULE_LIST_BY_DATE = 1;

    private ImageButton backButton;
    private RelativeLayout headerLayout;
    private ImageButton leftArrow, rightArrow;
    private TextView headerDateTextView;
    private Button addButton;

    private View mRecyclerViewTopBorder;
    private RecyclerView mRecyclerView;
    private LinearLayout emptyView;

    private Date currentDate;
    private SimpleDateFormat sdf;

    private ScheduleListAdapter mAdapter;

    /**
     * Handler for WeakReference
     */
    private static class ScheduleListHandler extends Handler {
        private final WeakReference<ScheduleListFragment> weakReference;

        public ScheduleListHandler(ScheduleListFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        public ScheduleListFragment getWeakReference() {
            return weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            ScheduleListFragment theFrag = weakReference.get();
            if(theFrag != null) {
                theFrag.handleMessage(msg);
            }
        }
    }

    private ScheduleListHandler mHandler = new ScheduleListHandler(this);

    private boolean canClick = true;

    private void handleMessage(Message msg) {
        // NOTHING
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
     * Default Constructor
     *
     * @return  ScheduleListFragment
     */
    public static ScheduleListFragment getInstance() {
        ScheduleListFragment fragment = new ScheduleListFragment();
        return fragment;
    }

    /**
     * Get Root Layout
     *
     * @return Layout of this Fragment
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_schedule_list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.tag(TAG).i("onCreateView");

        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        backButton = (ImageButton) contentView.findViewById(R.id.schedule_list_back_button);
        headerLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_list_header_layout);
        addButton = (Button) contentView.findViewById(R.id.schedule_list_top_bar_add_button);
        leftArrow = (ImageButton) contentView.findViewById(R.id.schedule_list_header_left_arrow);
        rightArrow = (ImageButton) contentView.findViewById(R.id.schedule_list_header_right_arrow);
        headerDateTextView = (TextView) contentView.findViewById(R.id.schedule_list_header_date_textview);
        mRecyclerViewTopBorder = contentView.findViewById(R.id.schedule_list_recyclerview_top_divider);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.schedule_list_recyclerview);
        emptyView = (LinearLayout) contentView.findViewById(R.id.schedule_list_empty_layout);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goPrevDate();
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNextDate();
            }
        });

        headerDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);

                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        currentDate = calendar.getTime();

                        setCurrentDateString();
                        getScheduleListByDate();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getActivity(), ScheduleDetailActivity.class);
                    intent.putExtra("SCHEDULE_DATE", headerDateTextView.getText());
                    intent.putExtra("MODE", ScheduleDetailFragment.MODE_ADD);
                    startActivity(intent);
                }
            }
        });

        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Timber.tag(TAG).i("onViewCreated");

        if(getActivity() != null) {
            getActivity().startPostponedEnterTransition();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.tag(TAG).i("onActivityCreated");

        // 오늘 날짜를 currentDate 로 설정
        currentDate = new Date();
        sdf = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREAN);
        setCurrentDateString();

        // Initialize RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        refreshList();
    }

    /**
     * 어제 날짜로 이동
     */
    private void goPrevDate() {
        Date prevDate = new Date(currentDate.getTime() - 24 * 60 * 60 * 1000);
        currentDate.setTime(prevDate.getTime());
        setCurrentDateString();

        refreshList();
    }

    /**
     * 내일 날짜로 이동
     */
    private void goNextDate() {
        Date prevDate = new Date(currentDate.getTime() + 24 * 60 * 60 * 1000);
        currentDate.setTime(prevDate.getTime());
        setCurrentDateString();

        refreshList();
    }

    /**
     * 현재 날짜를 Header 에 보여준다.
     */
    private void setCurrentDateString() {
        String currentDateString = sdf.format(currentDate);
        headerDateTextView.setText(currentDateString);
    }

    /**
     * Empty View 보여주기 설정
     *
     * @param isShow    Is Show EmptyView
     */
    private void showEmptyView(boolean isShow) {
        if(isShow) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private OnChildItemClickListener<ScheduleListItemDTO> listItemClickListener = new OnChildItemClickListener<ScheduleListItemDTO>() {
        @Override
        public void onChildItemClick(ScheduleListItemDTO item) {
            if(canClick) {
                setCanClickable();

                if(getActivity() != null) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getActivity(), ScheduleDetailActivity.class);
                    intent.putExtra("SCHEDULE_ID", item.getId());
                    intent.putExtra("MODE", ScheduleDetailFragment.MODE_MODIFY);
                    startActivity(intent);
                }
            }
        }
    };

    /**
     * Refresh List
     */
    public void refreshList() {
        setReLogInCount(0);
        getScheduleListByDate();
    }

    /**
     * Retry of Get Schedule List
     */
    public void retryGetScheduleListByDate() {
        setReLogInCount(getReLogInCount() + 1);
        getScheduleListByDate();
    }

    /**
     * Get Schedule List
     */
    private void getScheduleListByDate() {
        showLoadingProgressBar();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);

        ScheduleListByDateRequestDTO requestDTO = new ScheduleListByDateRequestDTO();
        requestDTO.setSearchDate(dateFormat.format(currentDate));

        Timber.tag(TAG).d("[getScheduleListByDate] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<ScheduleListApiInterface> restClient = new DefaultRestClient<>();
        ScheduleListApiInterface getListInterface = restClient.getClient(ScheduleListApiInterface.class);

        Call<ScheduleListByDateResponseDTO> call = getListInterface.getScheduleListByDate(requestDTO);
        call.enqueue(new Callback<ScheduleListByDateResponseDTO>() {
            @Override
            public void onResponse(Call<ScheduleListByDateResponseDTO> call, Response<ScheduleListByDateResponseDTO> response) {
                if(response.isSuccessful()) {
                    ScheduleListByDateResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[getScheduleListByDate] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            hideLoadingProgressBar();

                            if(responseDTO.getScheduleList() != null && responseDTO.getScheduleList().size() > 0) {
                                Timber.tag(TAG).d("[getScheduleListByDate][ScheduleList] " + Utils.convertObjToJSON(responseDTO.getScheduleList()));

                                showEmptyView(false);

                                mAdapter = new ScheduleListAdapter(responseDTO.getScheduleList(), listItemClickListener);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                showEmptyView(true);
                            }

                            setReLogInCount(0);
                        } else {
                            switch (responseDTO.getErrorType()) {
                                case AMSettings.ErrorType.SESSION_ERROR: {
                                    if (getReLogInCount() <= AMSettings.RELOGIN_RETRY_COUNT) {
                                        doAuthentication(REQUEST_CODE_GET_SCHEDULE_LIST_BY_DATE, "");
                                    } else {
                                        hideLoadingProgressBar();

                                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));

                                        showEmptyView(true);

                                        setReLogInCount(0);
                                    }
                                    break;
                                }
                                default: {
                                    hideLoadingProgressBar();

                                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));

                                    showEmptyView(true);

                                    setReLogInCount(0);

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[getScheduleListByDate] RESPONSE IS NULL");

                        hideLoadingProgressBar();

                        DialogHelper.showNormalAlertDialog(getActivity(), "");

                        showEmptyView(true);

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[getScheduleListByDate] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));

                    showEmptyView(true);

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<ScheduleListByDateResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[getScheduleListByDate][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                DialogHelper.showNormalAlertDialog(getActivity(), "");

                showEmptyView(true);

                setReLogInCount(0);
            }
        });
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

                            postEvent(new Event_UpdateUserInfo(userInfoString, workInOutListString));

                            switch(requestCode) {
                                case REQUEST_CODE_GET_SCHEDULE_LIST_BY_DATE: {
                                    retryGetScheduleListByDate();

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
                        Timber.tag(TAG).e("[doAuthentication] RESPONSE IS NULL");

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
}
