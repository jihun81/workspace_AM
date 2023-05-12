package com.metanetglobal.knowledge.worker.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.ajalt.timberkt.Timber;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.auth.LoginApiInterface;
import com.metanetglobal.knowledge.worker.auth.bean.LoginRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO;
import com.metanetglobal.knowledge.worker.common.AMSettings;
import com.metanetglobal.knowledge.worker.common.BaseFragment;
import com.metanetglobal.knowledge.worker.common.DefaultRestClient;
import com.metanetglobal.knowledge.worker.common.DialogHelper;
import com.metanetglobal.knowledge.worker.common.GetCommCodeListApiInterface;
import com.metanetglobal.knowledge.worker.common.PreferenceManager;
import com.metanetglobal.knowledge.worker.common.Utils;
import com.metanetglobal.knowledge.worker.common.bean.CommCodeDTO;
import com.metanetglobal.knowledge.worker.common.bean.GetCommCodeListRequestDTO;
import com.metanetglobal.knowledge.worker.common.bean.GetCommCodeListResponseDTO;
import com.metanetglobal.knowledge.worker.common.ui.DefaultBottomSheetMenuItemAdapter;
import com.metanetglobal.knowledge.worker.common.utils.CustomTimePickerDialog;
import com.metanetglobal.knowledge.worker.main.otto_interfaces.Event_UpdateUserInfo;
import com.metanetglobal.knowledge.worker.schedule.bean.GetTaskListRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.GetTaskListResponseDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDeleteRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDeleteResponseDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDetailRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleDetailResponseDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleListItemDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleSaveRequestDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.ScheduleSaveResponseDTO;
import com.metanetglobal.knowledge.worker.schedule.bean.TaskListItemDTO;
import com.metanetglobal.knowledge.worker.schedule.otto_interfaces.Event_UpdateSchedule;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleDetailFragment extends BaseFragment {
    private final String TAG = ScheduleDetailFragment.class.getSimpleName();

    public static final int MODE_ADD = 0;
    public static final int MODE_MODIFY = 1;
    private int MODE = MODE_ADD;

    private RelativeLayout rootLayout;
    private NestedScrollView scrollView;

    private ImageButton headerBackButton;

    private TextView workStartDateDesc, workEndDateDesc;
    private RelativeLayout workStartDateLayout, workEndDateLayout;
    private TextView workStartDateText, workEndDateText;
    private RelativeLayout workStartTimeLayout, workEndTimeLayout;
    private TextView workStartTimeText, workEndTimeText;

    private TextView taskDesc;
    private RelativeLayout taskLayout;
    private TextView taskText;

    private TextView workTypeDesc;
    private RelativeLayout workTypeLayout;
    private TextView workTypeText;

    private TextInputLayout workDetailTextInputLayout;
    private TextInputEditText workDetailEditText;

    private TextView commentDesc;
    private TextView commentText;

    private Button deleteButton, saveButton;

    private BottomSheetDialog selectDialog;

    private final int SELECT_MODE_TASK = 10;
    private final int SELECT_MODE_WORK_TYPE = 11;

    private final int REQUEST_CODE_GET_SCHEDULE_DETAIL = 100;
    private final int REQUEST_CODE_SAVE_SCHEDULE = 101;
    private final int REQUEST_CODE_DELETE_SCHEDULE = 102;
    private final int REQUEST_CODE_GET_TASK_LIST = 103;

    private final int CHECK_MODE_START_DATE = 10;
    private final int CHECK_MODE_END_DATE = 11;
    private final int CHECK_MODE_START_TIME = 12;
    private final int CHECK_MODE_END_TIME = 13;
    private final int CHECK_MODE_VALIDATION = 14;


    private int startYear, startMonth, startDay;
    private int endYear, endMonth, endDay;

    private int startHour, startMinute;
    private int endHour, endMinute;

    private String taskRId = "";
    private String dutyCd = "";

    private List<CommCodeDTO> workTypeList;
    private List<CommCodeDTO> taskList;

    private String focusedWorkDetailHint;
    private String notFocusedWorkDetailHint;

    /**
     * Handler for WeakReference
     */
    private static class ScheduleDetailHandler extends Handler {
        private final WeakReference<ScheduleDetailFragment> weakReference;

        public ScheduleDetailHandler(ScheduleDetailFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        public ScheduleDetailFragment getWeakReference() {
            return weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            ScheduleDetailFragment theFrag = weakReference.get();
            if(theFrag != null) {
                theFrag.handleMessage(msg);
            }
        }
    }

    private ScheduleDetailHandler mHandler = new ScheduleDetailHandler(this);

    private boolean canClick = true;

    private void handleMessage(Message msg) {
        // NOTHING
    }

    /**
     * 중복 클릭 방지
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


    // 현재 키보드가 보여지고 있는지 여부
    private boolean isKeyBoardVisible = false;
    // 이전에 키보드가 보여지고 있었는지 여부 (onGlobalLayout() 이 두번 불려서 비교대상으로 사용함)
    private boolean prevIsKeyBoardVisible = false;

    /**
     * Keyboard 가 올라왔다면 Scroll 을 조금 내려줘서 EditText 영역을 확보한다.
     *
     */
    ViewTreeObserver.OnGlobalLayoutListener keyboardListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(getActivity() == null) return;

            Rect r = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(r);

            int screenHeight;
            if(Build.VERSION.SDK_INT >= 21) {
                screenHeight = Utils.getScreenHeight(getActivity());
            } else {
                screenHeight = rootLayout.getRootView().getHeight();
            }

            int heightDifference = screenHeight - (r.bottom);

            if (heightDifference > 100) {
                isKeyBoardVisible = true;
            } else {
                isKeyBoardVisible = false;
            }

            if(prevIsKeyBoardVisible != isKeyBoardVisible) {
                if(isKeyBoardVisible) {
                    if(scrollView != null) {
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                if(scrollView != null) {
                                    scrollView.smoothScrollBy(0, 150);
                                }
                            }
                        });
                    }
                }
                prevIsKeyBoardVisible = isKeyBoardVisible;
            }
        }
    };


    /**
     * Default Constructor
     *
     * @param mode          Whether of Mode (ADD / MODIFY)
     * @param scheduleId    Schedule Id
     * @return              ScheduleDetailFragment
     */
    public static ScheduleDetailFragment getInstance(int mode, String scheduleId,String date) {
        ScheduleDetailFragment fragment = new ScheduleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("MODE", mode);
        bundle.putString("SCHEDULE_ID", scheduleId);
        bundle.putString("SCHEDULE_DATE", date);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Get Root Layout
     *
     * @return Layout of this Fragment
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_schedule_detail;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.tag(TAG).i("onCreateView");

        View contentView = super.onCreateView(inflater, container, savedInstanceState);

        rootLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_detail_root_layout);

        scrollView = (NestedScrollView) contentView.findViewById(R.id.schedule_detail_scrollview);

        headerBackButton = (ImageButton) contentView.findViewById(R.id.schedule_detail_back_button);

        workStartDateDesc = (TextView) contentView.findViewById(R.id.schedule_detail_work_start_date_desc);
        workStartDateLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_detail_work_start_date_layout);
        workStartDateText = (TextView) contentView.findViewById(R.id.schedule_detail_work_start_date_text);
        workStartTimeLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_detail_work_start_time_layout);
        workStartTimeText = (TextView) contentView.findViewById(R.id.schedule_detail_work_start_time_text);

        workEndDateDesc = (TextView) contentView.findViewById(R.id.schedule_detail_work_end_date_desc);
        workEndDateLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_detail_work_end_date_layout);
        workEndDateText = (TextView) contentView.findViewById(R.id.schedule_detail_work_end_date_text);
        workEndTimeLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_detail_work_end_time_layout);
        workEndTimeText = (TextView) contentView.findViewById(R.id.schedule_detail_work_end_time_text);

        taskDesc = (TextView) contentView.findViewById(R.id.schedule_detail_task_desc);
        taskLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_detail_task_layout);
        taskText = (TextView) contentView.findViewById(R.id.schedule_detail_task_text);

        workTypeDesc = (TextView) contentView.findViewById(R.id.schedule_detail_work_type_desc);
        workTypeLayout = (RelativeLayout) contentView.findViewById(R.id.schedule_detail_work_type_layout);
        workTypeText = (TextView) contentView.findViewById(R.id.schedule_detail_work_type_text);

        workDetailTextInputLayout = (TextInputLayout) contentView.findViewById(R.id.schedule_detail_work_detail_text_input_layout);
        workDetailEditText = (TextInputEditText) contentView.findViewById(R.id.schedule_detail_work_detail_edittext);

        commentDesc = (TextView) contentView.findViewById(R.id.schedule_detail_comment_desc);
        commentText = (TextView) contentView.findViewById(R.id.schedule_detail_comment);

        deleteButton = (Button) contentView.findViewById(R.id.schedule_detail_delete_button);
        saveButton = (Button) contentView.findViewById(R.id.schedule_detail_save_button);

        /**
         * 디자인 가이드 상 * 의 색깔 표시를 위하여 아래와 같이 코딩.
         */
        Spannable spannable1 = new SpannableString(workStartDateDesc.getText().toString());
        spannable1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), workStartDateDesc.getText().toString().length() - 1, workStartDateDesc.getText().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        workStartDateDesc.setText(spannable1, TextView.BufferType.SPANNABLE);

        Spannable spannable2 = new SpannableString(workEndDateDesc.getText().toString());
        spannable2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), workEndDateDesc.getText().toString().length() - 1, workEndDateDesc.getText().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        workEndDateDesc.setText(spannable2, TextView.BufferType.SPANNABLE);

        Spannable spannable3 = new SpannableString(taskDesc.getText().toString());
        spannable3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), taskDesc.getText().toString().length() - 1, taskDesc.getText().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        taskDesc.setText(spannable3, TextView.BufferType.SPANNABLE);

        Spannable spannable4 = new SpannableString(workTypeDesc.getText().toString());
        spannable4.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), workTypeDesc.getText().toString().length() - 1, workTypeDesc.getText().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        workTypeDesc.setText(spannable4, TextView.BufferType.SPANNABLE);

        /**
         * Back Button ClickListener
         */
        headerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        /**
         * 시작일시 날짜 ClickListener
         */
        workStartDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canClick) {
                    setCanClickable();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                //if (checkDateValidation(CHECK_MODE_START_DATE, year, monthOfYear, dayOfMonth)) {//20190829 시작일자 종료일자 체크
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(year, monthOfYear, dayOfMonth);

                                    workStartDateText.setText(dateFormat.format(calendar.getTime()));
                                    workEndDateText.setText(dateFormat.format(calendar.getTime()));

                                    startYear = year;
                                    startMonth = monthOfYear;
                                    startDay = dayOfMonth;

                                    endYear = year;
                                    endMonth = monthOfYear;
                                    endDay = dayOfMonth;

                                    // 시작일자가 바뀌면 Task 를 초기화 해준다.
                                    taskRId = "";
                                    taskText.setText("");
                                //} else {//20190829 수정
                                  //  DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_date_validation_message)); //20190829 수정
                                //}
                            }
                        }, startYear, startMonth, startDay).show();
                    }
                }
            }
        });

        /**
         * 종료일시 날짜 ClickListener
         */
        workEndDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();
                    if(getActivity() != null && !getActivity().isFinishing()) {
                        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                if (checkDateValidation(CHECK_MODE_END_DATE, year, monthOfYear, dayOfMonth)) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(year, monthOfYear, dayOfMonth);

                                    workEndDateText.setText(dateFormat.format(calendar.getTime()));

                                    endYear = year;
                                    endMonth = monthOfYear;
                                    endDay = dayOfMonth;

                                    // 종료일자가 바뀌면 Task 를 초기화 해준다.
                                    taskRId = "";
                                    taskText.setText("");
                                } else {
                                    DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_date_validation_message));
                                }
                            }
                        }, endYear, endMonth, endDay).show();
                    }
                }
            }
        });




        /**
         * 시작일시 시간 ClickListener
         */


        workStartTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int TIME_PICKER_INTERVAL = 30;
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                if(canClick) {
                    setCanClickable();

                    if (getActivity() != null && !getActivity().isFinishing()) {


                        CustomTimePickerDialog mTimePicker;
                        mTimePicker = new CustomTimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                if (checkDateValidation(CHECK_MODE_START_TIME, selectedHour, selectedMinute, 0)) {
                                    workStartTimeText.setText((selectedHour > 9 ? selectedHour : "0" + selectedHour) + ":" + (selectedMinute > 9 ? selectedMinute : "0" + selectedMinute));

                                    startHour = selectedHour;
                                    startMinute = selectedMinute;
                                    // 시작일자가 바뀌면 Task 를 초기화 해준다.
                                    taskRId = "";
                                    taskText.setText("");

                                } else {
                                    DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_date_validation_message));
                                }
                            }

                        }, startHour, startMinute, false); // true의 경우 24시간 형식의 TimePicker 출현

                        if (getActivity() != null && getActivity().getFragmentManager() != null) {

                            mTimePicker.show();
                        }

                    }

                }

                //zero9 수정 20190813 아날로그시계-> 디지털시계

               /* if(canClick) {
                    setCanClickable();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                                        if (checkDateValidation(CHECK_MODE_START_TIME, hourOfDay, minute, 0)) {
                                            workStartTimeText.setText((hourOfDay > 9 ? hourOfDay : "0" + hourOfDay) + ":" + (minute > 9 ? minute : "0" + minute));
                                            startHour = hourOfDay;
                                            startMinute = minute;
                                            // 시작일자가 바뀌면 Task 를 초기화 해준다.
                                            taskRId = "";
                                            taskText.setText("");
                                        } else {
                                            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_date_validation_message));
                                        }
                                    }
                                },
                                startHour,
                                startMinute,
                                true

                        );

                        tpd.setTimeInterval(1, 30);



                        if(getActivity() != null && getActivity().getFragmentManager() != null) {
                            tpd.show(getActivity().getFragmentManager(), "TimePickerDialog");
                        }
                    }
                }*/
            }
        });

        /**
         * 종료일시 시간 ClickListener
         */
        workEndTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(canClick) {
                    setCanClickable();

                    if (getActivity() != null && !getActivity().isFinishing()) {


                        CustomTimePickerDialog mTimePicker;
                        mTimePicker = new CustomTimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                if (checkDateValidation(CHECK_MODE_END_TIME, selectedHour, selectedMinute, 0)) {
                                    workEndTimeText.setText((selectedHour > 9 ? selectedHour : "0" + selectedHour) + ":" + (selectedMinute > 9 ? selectedMinute : "0" + selectedMinute));
                                    endHour = selectedHour;
                                    endMinute = selectedMinute;

                                    // 종료일자가 바뀌면 Task 를 초기화 해준다.
                                    taskRId = "";
                                    taskText.setText("");
                                } else {
                                    DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_date_validation_message));
                                }
                                /*
                                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                    @Override
                                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                                        Log.d("1212",minute+"min");
                                    }
                                });
*/
                            }


                        }, endHour, endMinute, false); // true의 경우 24시간 형식의 TimePicker 출현

                        if (getActivity() != null && getActivity().getFragmentManager() != null) {
                            mTimePicker.show();
                        }

                    }

                }

                /*
                if(canClick) {
                    setCanClickable();
                    if(getActivity() != null && !getActivity().isFinishing()) {
                        com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
                                        if (checkDateValidation(CHECK_MODE_END_TIME, hourOfDay, minute, 0)) {
                                            workEndTimeText.setText((hourOfDay > 9 ? hourOfDay : "0" + hourOfDay) + ":" + (minute > 9 ? minute : "0" + minute));
                                            endHour = hourOfDay;
                                            endMinute = minute;
                                            // 종료일자가 바뀌면 Task 를 초기화 해준다.
                                            taskRId = "";
                                            taskText.setText("");
                                        } else {
                                            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_date_validation_message));
                                        }
                                    }
                                },
                                endHour,
                                endMinute,
                                true
                        );

                        tpd.setTimeInterval(1, 30);

                        if(getActivity() != null && getActivity().getFragmentManager() != null) {
                            tpd.show(getActivity().getFragmentManager(), "TimePickerDialog");
                        }
                    }
                }*/
            }
        });

        /**
         * TASK ClickListener
         */
        taskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    view.setSelected(true);

                    getTaskList();
                }
            }
        });

        /**
         * 업무유형 ClickListener
         */
        workTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    view.setSelected(true);

                    showBottomSheetDialog(SELECT_MODE_WORK_TYPE);
                }
            }
        });

        /**
         * 삭제 / 취소 버튼 ClickListener
         *
         * MODE 가 MODE_MODIFY 일 때 삭제버튼, MODE_ADD 일 때는 취소버튼이 된다.
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    if(MODE == MODE_MODIFY) {
                        if(getActivity() != null && !getActivity().isFinishing()) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.schedule_delete_confirm_message)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            deleteSchedule();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // NOTHING
                                        }
                                    }).create().show();
                        }
                    } else {
                        if(getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }
            }
        });

        /**
         * 저장 버튼 ClickListener
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    if(isSaveValid()) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.schedule_save_confirm_message)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        saveSchedule();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // NOTHING
                                    }
                                }).create().show();
                    }
                }
            }
        });

        /**
         * ScrollView 안에 EditText 및 TextView 가 들어가 있으면 내부 Scroll 이 되지 않는다.
         * 따라서 TouchListener 를 설정해주었다.
         */
        workDetailEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(view.getId() == R.id.schedule_detail_work_detail_edittext) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        commentText.setMovementMethod(new ScrollingMovementMethod());
        commentText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(view.getId() == R.id.schedule_detail_comment) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        focusedWorkDetailHint = getResources().getString(R.string.schedule_detail_work_detail);
        notFocusedWorkDetailHint = getResources().getString(R.string.schedule_detail_work_detail_hint);

        /**
         * Work Detail EditText 가 Focus / Focus off 일 시 Hint Text 가 다르다.
         */
        workDetailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    workDetailTextInputLayout.setHint(focusedWorkDetailHint);
                } else {
                    if(workDetailEditText.length() > 0) {
                        workDetailTextInputLayout.setHint(focusedWorkDetailHint);
                    } else {
                        workDetailTextInputLayout.setHint(notFocusedWorkDetailHint);
                    }
                }
            }
        });

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

        MODE = getArguments().getInt("MODE");
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";//zero9 특수문자 제거
        String date_s = "";
        date_s = getArguments().getString("SCHEDULE_DATE");

        if(date_s==null||date_s.equals("")||date_s=="null"){

        }else{
//            Log.d("headerDateTextView",date_s.replaceAll(match,"")+"");
            date_s = date_s.replaceAll(match,"");
            //date_s = date_s.replace(".","");
            date_s = date_s.substring(0,8);

        }


        if(MODE == MODE_ADD) {
            // 초기 값은 오늘 날짜
            Calendar today = Calendar.getInstance();


            if(date_s==null||date_s.equals("")||date_s=="null"){
                startYear = today.get(Calendar.YEAR);
                startMonth = today.get(Calendar.MONTH);
                startDay = today.get(Calendar.DAY_OF_MONTH);

                endYear = today.get(Calendar.YEAR);
                endMonth = today.get(Calendar.MONTH);
                endDay = today.get(Calendar.DAY_OF_MONTH);
            }else{

                startYear = Integer.parseInt(date_s.substring(0,4));
                startMonth = Integer.parseInt(date_s.substring(4,6))-1;
                startDay = Integer.parseInt(date_s.substring(6));
                today.set(startYear,startMonth,startDay);

                endYear = today.get(Calendar.YEAR);
                endMonth = today.get(Calendar.MONTH);
                endDay = today.get(Calendar.DAY_OF_MONTH);
            }


            startHour = today.get(Calendar.HOUR_OF_DAY);
            int minute = today.get(Calendar.MINUTE);

            if(minute < 15) {
                startMinute = 0;
            } else if (minute < 45) {
                startMinute = 30;
            } else {
                if(startHour == 23) {
                    startMinute = 30;
                } else {
                    startMinute = 0;
                    startHour++;
                }
            }

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.HOUR_OF_DAY, startHour);
            startCalendar.set(Calendar.MINUTE, startMinute);

            endHour = 18;
            endMinute = 0;

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
            endCalendar.set(Calendar.MINUTE, endMinute);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREAN);

            workStartDateText.setText(dateFormat.format(today.getTime()));
            workEndDateText.setText(dateFormat.format(today.getTime()));
            workStartTimeText.setText(timeFormat.format(startCalendar.getTime()));
            workEndTimeText.setText(timeFormat.format(endCalendar.getTime()));

            /**
             * Comment 영역은 작성이 불가능하다. 따라서 View 를 보여주지 않음.
             */
            commentDesc.setVisibility(View.GONE);
            commentText.setVisibility(View.GONE);

            deleteButton.setText(R.string.cancel);
        } else {
            /**
             * Comment 영역은 수정이 불가능하지만, 관리자가 쓸 수 있는 영역으로 View 는 보여줘야 한다.
             */
            commentDesc.setVisibility(View.VISIBLE);
            commentText.setVisibility(View.VISIBLE);

            deleteButton.setText(R.string.delete);
        }

        /**
         * Keyboard 가 올라온 것을 체크 하기 위하여 Listener 를 달아준다.
         */
        try {
            if (rootLayout != null && keyboardListener != null && MODE == MODE_ADD) {
                rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWorkTypeList();
    }

    @Override
    public void onDestroy() {
        /**
         * Keyboard 가 올라온 것을 체크 하기 위한 Listener 제거
         */
        try {
            if (rootLayout != null && keyboardListener != null && MODE == MODE_ADD) {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    /**
     * Show BottomSheetDialog
     *
     * @param what  SELECT_MODE
     */
    private void showBottomSheetDialog(final int what) {
        if(getActivity() == null) return;

        selectDialog = new BottomSheetDialog(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.bottomsheet_default_menu, null);

        Button cancelButton = (Button) view.findViewById(R.id.default_bottom_sheet_menu_cancel_button);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.default_bottom_sheet_menu_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        switch(what) {
            case SELECT_MODE_TASK: {
                recyclerView.setAdapter(new DefaultBottomSheetMenuItemAdapter(taskList, new DefaultBottomSheetMenuItemAdapter.ItemListener() {
                    @Override
                    public void onItemClick(CommCodeDTO item) {
                        taskRId = item.getCodeName();
                        taskText.setText(item.getMarkName());

                        if(selectDialog != null) {
                            selectDialog.dismiss();
                        }
                    }
                }));
                break;
            }
            case SELECT_MODE_WORK_TYPE: {
                recyclerView.setAdapter(new DefaultBottomSheetMenuItemAdapter(workTypeList, new DefaultBottomSheetMenuItemAdapter.ItemListener() {
                    @Override
                    public void onItemClick(CommCodeDTO item) {
                        dutyCd = item.getCodeName();
                        workTypeText.setText(item.getMarkName());

                        if(selectDialog != null) {
                            selectDialog.dismiss();
                        }
                    }
                }));

                break;
            }
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectDialog != null) {
                    selectDialog.dismiss();
                }
            }
        });

        selectDialog.setContentView(view);

        // BottomSheetDialog 의 Height 가 wrap_content 인데 정상적으로 다 보여주지 못한다. 따라서 대충 1500 정도 줘서 다 올린다.
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View)view.getParent());
        behavior.setPeekHeight(1500);

        selectDialog.show();
        selectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                selectDialog = null;

                switch(what) {
                    case SELECT_MODE_TASK: {
                        taskLayout.setSelected(false);
                        break;
                    }
                    case SELECT_MODE_WORK_TYPE: {
                        workTypeLayout.setSelected(false);
                        break;
                    }
                }
            }
        });
    }

    /**
     * Date Validation Check
     *
     * checkMode 가 VALIDATION 일 때는 value1, value2, value3 은 쓰지 않는다.
     *
     * @param checkMode What
     * @param value1   Year / HourOfDay
     * @param value2   Month / Minute
     * @param value3   Day
     * @return EndDate 가 startDate 보다 나중인지 여부
     */
    private boolean checkDateValidation(int checkMode, int value1, int value2, int value3) {
        try {
            Date startDate;
            Date endDate;

            switch(checkMode) {
                case CHECK_MODE_START_DATE: {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(value1, value2, value3, startHour, startMinute);
                    startDate = startCalendar.getTime();

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(endYear, endMonth, endDay, endHour, endMinute);
                    endDate = endCalendar.getTime();

                    break;
                }
                case CHECK_MODE_END_DATE: {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(startYear, startMonth, startDay, startHour, startMinute);
                    startDate = startCalendar.getTime();

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(value1, value2, value3, endHour, endMinute);
                    endDate = endCalendar.getTime();

                    break;
                }
                case CHECK_MODE_START_TIME: {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(startYear, startMonth, startDay, value1, value2);
                    startDate = startCalendar.getTime();

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(endYear, endMonth, endDay, endHour, endMinute);
                    endDate = endCalendar.getTime();

                    Log.d("startDate",startDate+"/"+endDate);

                    break;
                }
                case CHECK_MODE_END_TIME: {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(startYear, startMonth, startDay, startHour, startMinute);
                    startDate = startCalendar.getTime();

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(endYear, endMonth, endDay, value1, value2);
                    endDate = endCalendar.getTime();

                    break;
                }
                case CHECK_MODE_VALIDATION: {
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.set(startYear, startMonth, startDay, startHour, startMinute);
                    startDate = startCalendar.getTime();

                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.set(endYear, endMonth, endDay, endHour, endMinute);
                    endDate = endCalendar.getTime();

                    break;
                }
                default:
                    startDate = new Date();
                    endDate = new Date();
                    break;
            }

            int compareDate = startDate.compareTo(endDate);

            if(compareDate > 0) {
                // startDate 가 endDate 이후
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Validation Check of Save
     *
     * @return  Can save
     */
    private boolean isSaveValid() {
        if(getActivity() == null) {
            return false;
        }

        if(workStartDateText.length() == 0) {
            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_input_start_date_validation_message));

            return false;
        }
        else if (workStartTimeText.length() == 0) {
            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_input_start_time_validation_message));

            return false;
        }
        else if (workEndDateText.length() == 0) {
            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_input_end_date_validation_message));

            return false;
        }
        else if (workEndTimeText.length() == 0) {
            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_input_end_time_validation_message));

            return false;
        }
        else if (!checkDateValidation(CHECK_MODE_VALIDATION, 0, 0, 0)) {
            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_date_validation_message));

            return false;
        }
        else if(taskRId.isEmpty()) {
            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_input_task_validation_message));

            return false;
        }
        else if (dutyCd.isEmpty()) {
            DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_detail_input_work_type_validation_message));

            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Retry of Get Schedule Detail
     */
    public void retryGetScheduleDetail() {
        setReLogInCount(getReLogInCount() + 1);
        getScheduleDetail();
    }

    /**
     * Get Schedule Detail
     */
    private void getScheduleDetail() {
        String scheduleId = getArguments().getString("SCHEDULE_ID");

        ScheduleDetailRequestDTO requestDTO = new ScheduleDetailRequestDTO();
        requestDTO.setId(scheduleId);

        Timber.tag(TAG).d("[getScheduleDetail] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<ScheduleDetailApiInterface> restClient = new DefaultRestClient<>();
        ScheduleDetailApiInterface getDetailInterface = restClient.getClient(ScheduleDetailApiInterface.class);

        Call<ScheduleDetailResponseDTO> call = getDetailInterface.getScheduleDetail(requestDTO);
        call.enqueue(new Callback<ScheduleDetailResponseDTO>() {
            @Override
            public void onResponse(Call<ScheduleDetailResponseDTO> call, Response<ScheduleDetailResponseDTO> response) {
                if(response.isSuccessful()) {
                    ScheduleDetailResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[getScheduleDetail] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            hideLoadingProgressBar();

                            if (responseDTO.getWorkScheduleInfo() != null) {
                                Timber.tag(TAG).d("[getScheduleDetail][DetailInfo] " + Utils.convertObjToJSON(responseDTO.getWorkScheduleInfo()));

                                ScheduleListItemDTO itemDTO = responseDTO.getWorkScheduleInfo();

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
                                SimpleDateFormat showDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);

                                try {
                                    String startDt = Utils.nullCheck(itemDTO.getStartDt());

                                    if (startDt != null && startDt.length() > 0) {
                                        Date startDate = dateFormat.parse(startDt);

                                        Calendar startCalendar = Calendar.getInstance();
                                        startCalendar.setTime(startDate);

                                        startYear = startCalendar.get(Calendar.YEAR);
                                        startMonth = startCalendar.get(Calendar.MONTH);
                                        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);

                                        workStartDateText.setText(showDateFormat.format(startDate.getTime()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    String endDt = Utils.nullCheck(itemDTO.getEndDt());

                                    if (endDt != null && endDt.length() > 0) {
                                        Date endDate = dateFormat.parse(endDt);

                                        Calendar endCalendar = Calendar.getInstance();
                                        endCalendar.setTime(endDate);

                                        endYear = endCalendar.get(Calendar.YEAR);
                                        endMonth = endCalendar.get(Calendar.MONTH);
                                        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);

                                        workEndDateText.setText(showDateFormat.format(endDate.getTime()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm", Locale.KOREAN);
                                SimpleDateFormat showTimeFormat = new SimpleDateFormat("HH:mm", Locale.KOREAN);

                                try {
                                    String startTime = Utils.nullCheck(itemDTO.getStartTime());

                                    if (startTime != null && startTime.length() > 0) {
                                        Date startDate = timeFormat.parse(startTime);

                                        Calendar startCalendar = Calendar.getInstance();
                                        startCalendar.setTime(startDate);

                                        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
                                        startMinute = startCalendar.get(Calendar.MINUTE);

                                        workStartTimeText.setText(showTimeFormat.format(startDate.getTime()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    String endTime = Utils.nullCheck(itemDTO.getEndTime());

                                    if (endTime != null && endTime.length() > 0) {
                                        Date endDate = timeFormat.parse(endTime);

                                        Calendar endCalendar = Calendar.getInstance();
                                        endCalendar.setTime(endDate);

                                        endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
                                        endMinute = endCalendar.get(Calendar.MINUTE);

                                        workEndTimeText.setText(showTimeFormat.format(endDate.getTime()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                taskRId = Utils.nullCheck(itemDTO.getTaskId());
                                dutyCd = Utils.nullCheck(itemDTO.getDutyCd());

                                taskText.setText(Utils.nullCheck(itemDTO.getTaskNm()));
                                workTypeText.setText(Utils.nullCheck(itemDTO.getDutyCdNm()));
                                workDetailEditText.setText(Utils.nullCheck(itemDTO.getWrkDesc()));

                                if(workDetailEditText.length() > 0) {
                                    workDetailTextInputLayout.setHint(focusedWorkDetailHint);
                                } else {
                                    workDetailTextInputLayout.setHint(notFocusedWorkDetailHint);
                                }

                                commentText.setText(Utils.nullCheck(itemDTO.getWrkComnt()));
                            } else {
                                if(getActivity() != null && !getActivity().isFinishing()) {
                                    DialogHelper.showNormalAlertDialog(getActivity(), "");
                                }
                            }

                            setReLogInCount(0);
                        } else {
                            switch (responseDTO.getErrorType()) {
                                case AMSettings.ErrorType.SESSION_ERROR: {
                                    if (getReLogInCount() <= AMSettings.RELOGIN_RETRY_COUNT) {
                                        doAuthentication(REQUEST_CODE_GET_SCHEDULE_DETAIL, "");
                                    } else {
                                        hideLoadingProgressBar();

                                        if(getActivity() != null && !getActivity().isFinishing()) {
                                            DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                        }

                                        setReLogInCount(0);
                                    }
                                    break;
                                }
                                default: {
                                    hideLoadingProgressBar();

                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                    }

                                    setReLogInCount(0);

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[getScheduleDetail] RESPONSE BODY IS NULL");

                        hideLoadingProgressBar();

                        if(getActivity() != null && !getActivity().isFinishing()) {
                            DialogHelper.showNormalAlertDialog(getActivity(), "");
                        }

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[getScheduleDetail] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));
                    }

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<ScheduleDetailResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[getScheduleDetail][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                if(getActivity() != null && !getActivity().isFinishing()) {
                    DialogHelper.showNormalAlertDialog(getActivity(), "");
                }

                setReLogInCount(0);
            }
        });
    }

    /**
     * Retry of Save Schedule
     */
    public void retrySaveSchedule() {
        setReLogInCount(getReLogInCount() + 1);
        saveSchedule();
    }

    /**
     * Save Schedule
     */
    private void saveSchedule() {
        showLoadingProgressBar();

        String scheduleId = getArguments().getString("SCHEDULE_ID");

        ScheduleSaveRequestDTO requestDTO = new ScheduleSaveRequestDTO();
        if(MODE == MODE_MODIFY) {
            requestDTO.setId(scheduleId);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
        SimpleDateFormat showDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);

        try {
            String startDt = workStartDateText.getText().toString();

            if (startDt != null && startDt.length() > 0) {
                Date startDate = showDateFormat.parse(startDt);
                requestDTO.setStartDt(dateFormat.format(startDate.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String endDt = workEndDateText.getText().toString();

            if (endDt != null && endDt.length() > 0) {
                Date endDate = showDateFormat.parse(endDt);
                requestDTO.setEndDt(dateFormat.format(endDate.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm", Locale.KOREAN);
        SimpleDateFormat showTimeFormat = new SimpleDateFormat("HH:mm", Locale.KOREAN);

        try {
            String startTime = workStartTimeText.getText().toString();

            if(startTime != null && startTime.length() > 0) {
                Date startDate = showTimeFormat.parse(startTime);
                requestDTO.setStartTime(timeFormat.format(startDate.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String endTime = workEndTimeText.getText().toString();

            if(endTime != null && endTime.length() > 0) {
                Date endDate = showTimeFormat.parse(endTime);
                requestDTO.setEndTime(timeFormat.format(endDate.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        requestDTO.setTaskId(taskRId);
        requestDTO.setDutyCd(dutyCd);
        requestDTO.setWrkDesc(workDetailEditText.getText().toString());

        Timber.tag(TAG).d("[saveSchedule] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<ScheduleSaveApiInterface> restClient = new DefaultRestClient<>();
        ScheduleSaveApiInterface saveInterface = restClient.getClient(ScheduleSaveApiInterface.class);

        Call<ScheduleSaveResponseDTO> call = saveInterface.saveSchedule(requestDTO);
        call.enqueue(new Callback<ScheduleSaveResponseDTO>() {
            @Override
            public void onResponse(Call<ScheduleSaveResponseDTO> call, Response<ScheduleSaveResponseDTO> response) {
                if(response.isSuccessful()) {
                    ScheduleSaveResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[saveSchedule] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            hideLoadingProgressBar();

                            if(getActivity() != null && !getActivity().isFinishing()) {
                                DialogHelper.showNormalAlertDialog(getActivity(), getResources().getString(R.string.schedule_save_complete_message), new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        postEvent(new Event_UpdateSchedule());

                                        setReLogInCount(0);

                                        if (getActivity() != null) {
                                            getActivity().onBackPressed();
                                        }
                                    }
                                });
                            }
                        } else {
                            switch (responseDTO.getErrorType()) {
                                case AMSettings.ErrorType.SESSION_ERROR: {
                                    if (getReLogInCount() <= AMSettings.RELOGIN_RETRY_COUNT) {
                                        doAuthentication(REQUEST_CODE_SAVE_SCHEDULE, "");
                                    } else {
                                        hideLoadingProgressBar();

                                        if(getActivity() != null && !getActivity().isFinishing()) {
                                            DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                        }

                                        setReLogInCount(0);
                                    }
                                    break;
                                }
                                default: {
                                    hideLoadingProgressBar();

                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                    }

                                    setReLogInCount(0);

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[saveSchedule] RESPONSE BODY IS NULL");

                        hideLoadingProgressBar();

                        if(getActivity() != null && !getActivity().isFinishing()) {
                            DialogHelper.showNormalAlertDialog(getActivity(), "");
                        }

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[saveSchedule] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));
                    }

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<ScheduleSaveResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[saveSchedule][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                if(getActivity() != null && !getActivity().isFinishing()) {
                    DialogHelper.showNormalAlertDialog(getActivity(), "");
                }

                setReLogInCount(0);
            }
        });
    }

    /**
     * Retry of Delete Schedule
     */
    public void retryDeleteSchedule() {
        setReLogInCount(getReLogInCount() + 1);
        deleteSchedule();
    }

    /**
     * Delete Schedule
     */
    private void deleteSchedule() {
        showLoadingProgressBar();

        String scheduleId = getArguments().getString("SCHEDULE_ID");

        ScheduleDeleteRequestDTO requestDTO = new ScheduleDeleteRequestDTO();
        requestDTO.setId(scheduleId);

        Timber.tag(TAG).d("[deleteSchedule] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<ScheduleDeleteApiInterface> restClient = new DefaultRestClient<>();
        ScheduleDeleteApiInterface deleteInterface = restClient.getClient(ScheduleDeleteApiInterface.class);

        Call<ScheduleDeleteResponseDTO> call = deleteInterface.deleteSchedule(requestDTO);
        call.enqueue(new Callback<ScheduleDeleteResponseDTO>() {
            @Override
            public void onResponse(Call<ScheduleDeleteResponseDTO> call, Response<ScheduleDeleteResponseDTO> response) {
                if(response.isSuccessful()) {
                    ScheduleDeleteResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[deleteSchedule] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            hideLoadingProgressBar();

                            postEvent(new Event_UpdateSchedule());

                            if(getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        } else {
                            switch (responseDTO.getErrorType()) {
                                case AMSettings.ErrorType.SESSION_ERROR: {
                                    if (getReLogInCount() <= AMSettings.RELOGIN_RETRY_COUNT) {
                                        doAuthentication(REQUEST_CODE_SAVE_SCHEDULE, "");
                                    } else {
                                        hideLoadingProgressBar();

                                        if(getActivity() != null && !getActivity().isFinishing()) {
                                            DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                        }

                                        setReLogInCount(0);
                                    }
                                    break;
                                }
                                default: {
                                    hideLoadingProgressBar();

                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                    }

                                    setReLogInCount(0);

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[deleteSchedule] RESPONSE BODY IS NULL");

                        hideLoadingProgressBar();

                        if(getActivity() != null && !getActivity().isFinishing()) {
                            DialogHelper.showNormalAlertDialog(getActivity(), "");
                        }

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[deleteSchedule] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));
                    }

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<ScheduleDeleteResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[deleteSchedule][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                if(getActivity() != null && !getActivity().isFinishing()) {
                    DialogHelper.showNormalAlertDialog(getActivity(), "");
                }

                setReLogInCount(0);
            }
        });
    }

    /**
     * Get WorkType List
     *
     * Common Code 조회를 해서 가지고 온다.
     */
    private void getWorkTypeList() {
        showLoadingProgressBar();

        GetCommCodeListRequestDTO requestDTO = new GetCommCodeListRequestDTO();
        requestDTO.setGroupCode("WORK_TYPE_CD");

        Timber.tag(TAG).d("[getWorkTypeList] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<GetCommCodeListApiInterface> restClient = new DefaultRestClient<>();
        GetCommCodeListApiInterface getCommCodeListApiInterface = restClient.getClient(GetCommCodeListApiInterface.class);

        Call<GetCommCodeListResponseDTO> call = getCommCodeListApiInterface.getCommCodeList(requestDTO);
        call.enqueue(new Callback<GetCommCodeListResponseDTO>() {
            @Override
            public void onResponse(Call<GetCommCodeListResponseDTO> call, Response<GetCommCodeListResponseDTO> response) {
                if(response.isSuccessful()) {
                    GetCommCodeListResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[getWorkTypeList] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            if(responseDTO.getCommCodeList() != null) {
                                Timber.tag(TAG).d("[getWorkTypeList][CommCodeList] " + Utils.convertObjToJSON(responseDTO.getCommCodeList()));

                                workTypeList = responseDTO.getCommCodeList();

                                if(MODE == MODE_MODIFY) {
                                    // MODE_MODIFY 에서는 일정 상세 조회도 진행한다.
                                    getScheduleDetail();
                                } else {
                                    hideLoadingProgressBar();
                                }
                            } else {
                                hideLoadingProgressBar();

                                if(getActivity() != null && !getActivity().isFinishing()) {
                                    DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            if (getActivity() != null) {
                                                getActivity().onBackPressed();
                                            }
                                        }
                                    });
                                }
                            }
                        } else {
                            switch (responseDTO.getErrorType()) {
                                default: {
                                    hideLoadingProgressBar();

                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface) {
                                                if (getActivity() != null) {
                                                    getActivity().onBackPressed();
                                                }
                                            }
                                        });
                                    }

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[getWorkTypeList] RESPONSE BODY IS NULL");

                        hideLoadingProgressBar();

                        if(getActivity() != null && !getActivity().isFinishing()) {
                            DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    if (getActivity() != null) {
                                        getActivity().onBackPressed();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Timber.tag(TAG).e("[getWorkTypeList] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                if (getActivity() != null) {
                                    getActivity().onBackPressed();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<GetCommCodeListResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[getWorkTypeList][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                if(getActivity() != null && !getActivity().isFinishing()) {
                    DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Retry of Get Task List
     */
    private void retryGetTaskList() {
        setReLogInCount(getReLogInCount() + 1);
        getTaskList();
    }

    /**
     * Get Task List
     */
    private void getTaskList() {
        showLoadingProgressBar();

        GetTaskListRequestDTO requestDTO = new GetTaskListRequestDTO();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREAN);
        SimpleDateFormat showDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN);

        try {
            String startDt = workStartDateText.getText().toString();

            if (startDt != null && startDt.length() > 0) {
                Date startDate = showDateFormat.parse(startDt);
                requestDTO.setStartDt(dateFormat.format(startDate.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String endDt = workEndDateText.getText().toString();

            if (endDt != null && endDt.length() > 0) {
                Date endDate = showDateFormat.parse(endDt);
                requestDTO.setEndDt(dateFormat.format(endDate.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Timber.tag(TAG).d("[getTaskList] REQUEST : " + Utils.convertObjToJSON(requestDTO));

        DefaultRestClient<GetTaskListApiInterface> restClient = new DefaultRestClient<>();
        GetTaskListApiInterface getTaskListApiInterface = restClient.getClient(GetTaskListApiInterface.class);

        Call<GetTaskListResponseDTO> call = getTaskListApiInterface.getTaskList(requestDTO);
        call.enqueue(new Callback<GetTaskListResponseDTO>() {
            @Override
            public void onResponse(Call<GetTaskListResponseDTO> call, Response<GetTaskListResponseDTO> response) {
                if(response.isSuccessful()) {
                    GetTaskListResponseDTO responseDTO = response.body();

                    if(responseDTO != null) {
                        Timber.tag(TAG).d("[getTaskList] RESPONSE : " + Utils.convertObjToJSON(responseDTO));

                        if(responseDTO.isSuccess()) {
                            hideLoadingProgressBar();

                            if(responseDTO.getTaskList() != null) {
                                Timber.tag(TAG).d("[getTaskList][TaskList] " + Utils.convertObjToJSON(responseDTO.getTaskList()));

                                if(taskList != null) {
                                    taskList.clear();
                                } else {
                                    taskList = new ArrayList<>();
                                }

                                List<TaskListItemDTO> tempList = responseDTO.getTaskList();
                                List<CommCodeDTO> cList = new ArrayList<>();
                                for(TaskListItemDTO item : tempList) {
                                    CommCodeDTO dto = new CommCodeDTO();
                                    dto.setCodeId(item.getId());
                                    dto.setCodeName(item.getId());
                                    dto.setMarkName(item.getTaskNm());
                                    cList.add(dto);
                                }

                                taskList.addAll(cList);

                                showBottomSheetDialog(SELECT_MODE_TASK);
                            } else {
                                if(getActivity() != null && !getActivity().isFinishing()) {
                                    DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            getActivity().onBackPressed();
                                        }
                                    });
                                }
                            }

                            setReLogInCount(0);
                        } else {
                            switch (responseDTO.getErrorType()) {
                                case AMSettings.ErrorType.SESSION_ERROR: {
                                    if(getReLogInCount() <= AMSettings.RELOGIN_RETRY_COUNT) {
                                        doAuthentication(REQUEST_CODE_GET_TASK_LIST, "");
                                    } else {
                                        hideLoadingProgressBar();

                                        if(getActivity() != null && !getActivity().isFinishing()) {
                                            DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));
                                        }

                                        setReLogInCount(0);
                                    }
                                    break;
                                }
                                default: {
                                    hideLoadingProgressBar();

                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()), new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface) {
                                                getActivity().onBackPressed();
                                            }
                                        });
                                    }

                                    setReLogInCount(0);
                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[getTaskList] RESPONSE BODY IS NULL");

                        hideLoadingProgressBar();

                        if(getActivity() != null && !getActivity().isFinishing()) {
                            DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    if (getActivity() != null) {
                                        getActivity().onBackPressed();
                                    }
                                }
                            });
                        }

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[getTaskList] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));
                    }

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<GetTaskListResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[getTaskList][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                if(getActivity() != null && !getActivity().isFinishing()) {
                    DialogHelper.showNormalAlertDialog(getActivity(), "", new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }
                    });
                }

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
                                case REQUEST_CODE_GET_SCHEDULE_DETAIL: {
                                    retryGetScheduleDetail();

                                    break;
                                }
                                case REQUEST_CODE_SAVE_SCHEDULE: {
                                    retrySaveSchedule();

                                    break;
                                }
                                case REQUEST_CODE_DELETE_SCHEDULE: {
                                    retryDeleteSchedule();

                                    break;
                                }
                                case REQUEST_CODE_GET_TASK_LIST: {
                                    retryGetTaskList();

                                    break;
                                }
                            }
                        } else {
                            switch (responseDTO.getErrorType()) {
                                default: {
                                    Timber.tag(TAG).e("[doAuthentication] ERROR TYPE : " + responseDTO.getErrorType());

                                    hideLoadingProgressBar();

                                    if(getActivity() != null && !getActivity().isFinishing()) {
                                        DialogHelper.showNormalAlertDialog(getActivity(), responseDTO.getMessage());
                                    }

                                    setReLogInCount(0);

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[doAuthentication] RESPONSE BODY IS NULL");

                        hideLoadingProgressBar();

                        if(getActivity() != null && !getActivity().isFinishing()) {
                            DialogHelper.showNormalAlertDialog(getActivity(), "");
                        }

                        setReLogInCount(0);
                    }
                } else {
                    Timber.tag(TAG).e("[doAuthentication] RESPONSE FAIL code : " + response.code());

                    hideLoadingProgressBar();

                    if(getActivity() != null && !getActivity().isFinishing()) {
                        DialogHelper.showNormalAlertDialog(getActivity(), response.message());
                    }

                    setReLogInCount(0);
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[doAuthentication][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                if(getActivity() != null && !getActivity().isFinishing()) {
                    DialogHelper.showNormalAlertDialog(getActivity(), "");
                }

                setReLogInCount(0);
            }
        });
    }
}
