package com.metanetglobal.knowledge.worker.auth;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;


import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.auth.bean.LoginRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO;
import com.metanetglobal.knowledge.worker.common.AMSettings;
import com.metanetglobal.knowledge.worker.common.BaseFragment;
import com.metanetglobal.knowledge.worker.common.DefaultRestClient;
import com.metanetglobal.knowledge.worker.common.DialogHelper;
import com.metanetglobal.knowledge.worker.common.PreferenceManager;
import com.metanetglobal.knowledge.worker.common.Utils;
import com.metanetglobal.knowledge.worker.main.MainActivity;
import com.github.ajalt.timberkt.Timber;
import com.metanetglobal.knowledge.worker.service.MyService;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login Fragment
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseFragment
 */
public class LoginFragment extends BaseFragment {
    private final String TAG = LoginFragment.class.getSimpleName();

    private LinearLayout rootLayout;
    private NestedScrollView scrollView;

    private View idBottomBorder, pwdBottomBorder;
    private TextInputLayout idLayout, pwdLayout;
    private TextInputEditText idEditText, pwdEditText;
    private Button loginButton;

    private String focusedIdHint;
    private String notFocusedIdHint;
    private String focusedPwdHint;
    private String notFocusedPwdHint;

    /**
     * Handler for WeakReference
     */
    private static class LoginHandler extends Handler {
        private final WeakReference<LoginFragment> weakReference;

        public LoginHandler(LoginFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        public LoginFragment getWeakReference() {
            return weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            LoginFragment theFrag = weakReference.get();
            if(theFrag != null) {
                theFrag.handleMessage(msg);
            }
        }
    }

    private LoginHandler mHandler = new LoginHandler(this);

    private void handleMessage(Message msg) {
        // NOTHING
    }

    private boolean canClick = true;

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

    // 현재 키보드가 보여지고 있는지 여부
    private boolean isKeyBoardVisible = false;
    // 이전에 키보드가 보여지고 있었는지 여부 (onGlobalLayout() 이 두번 불려서 비교대상으로 사용함)
    private boolean prevIsKeyBoardVisible = false;

    /**
     * Keyboard 가 올라왔는지 체크
     *
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
                                    scrollView.smoothScrollBy(0, 100);
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
     * @return LoginFragment
     */
    public static LoginFragment getInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    /**
     * Get Root Layout
     *
     * @return Layout of this Fragment
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_login;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.tag(TAG).i("onCreateView");

        View contentView = super.onCreateView(inflater, container, savedInstanceState);

        rootLayout = (LinearLayout) contentView.findViewById(R.id.login_root_layout);
        scrollView = (NestedScrollView) contentView.findViewById(R.id.login_scrollview);

        focusedIdHint = getResources().getString(R.string.login_id_edt_hint_focused);
        notFocusedIdHint = getResources().getString(R.string.login_id_edt_hint_not_focused);
        focusedPwdHint = getResources().getString(R.string.login_pwd_edt_hint_focused);
        notFocusedPwdHint = getResources().getString(R.string.login_pwd_edt_hint_not_focused);

        idLayout = (TextInputLayout) contentView.findViewById(R.id.login_id_text_input_layout);
        pwdLayout = (TextInputLayout) contentView.findViewById(R.id.login_pwd_text_input_layout);

        idEditText = (TextInputEditText) contentView.findViewById(R.id.login_id_text_input_edittext);
        pwdEditText = (TextInputEditText) contentView.findViewById(R.id.login_pwd_text_input_edittext);

        idBottomBorder = contentView.findViewById(R.id.login_id_text_input_layout_bottom_border);
        pwdBottomBorder = contentView.findViewById(R.id.login_pwd_text_input_layout_bottom_border);

        loginButton = (Button) contentView.findViewById(R.id.login_login_button);

        //본인 전화번호
        AMSettings.PHONE = Utils.getPhoneNumber(this.getContext(),this.getActivity());


        /**
         * ID EditText 의 Text Change 에 따른 Login Button 활성화 체크
         */
        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // NOTHING
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // NOTHING
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkLoginButtonStatus();
            }
        });

        /**
         * PWD EditText 의 Text Change 에 따른 Login Button 활성화 체크
         */
        pwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // NOTHING
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // NOTHING
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkLoginButtonStatus();
            }
        });

        /**
         * PWD EditText 의 IME Option 처리
         */
        pwdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    doAuthentication(0, "");

                    return true;
                }
                return false;
            }
        });

        /**
         * ID EditText 가 Focus 가 될때 와 안될때 Hint Text 가 다름.
         */
        idEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(getActivity() != null) {
                    if (hasFocus) {
                        idLayout.setHint(focusedIdHint);
                        idBottomBorder.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    } else {
                        if (idEditText.length() > 0) {
                            idLayout.setHint(focusedIdHint);
                        } else {
                            idLayout.setHint(notFocusedIdHint);
                        }

                        idBottomBorder.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_edittext_bottom_line_not_focused));
                    }
                }
            }
        });

        /**
         * PWD EditText 가 Focus 가 될때 와 안될때 Hint Text 가 다름.
         */
        pwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (getActivity() != null) {
                    if (hasFocus) {
                        pwdLayout.setHint(focusedPwdHint);
                        pwdBottomBorder.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                    } else {
                        if (pwdEditText.length() > 0) {
                            pwdLayout.setHint(focusedPwdHint);
                        } else {
                            pwdLayout.setHint(notFocusedPwdHint);
                        }

                        pwdBottomBorder.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_edittext_bottom_line_not_focused));
                    }
                }
            }
        });

        /**
         * Login Button Click Listener
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canClick) {
                    setCanClickable();

                    doAuthentication(0, "");
                }
            }
        });

        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.tag(TAG).i("onActivityCreated");

        try {
            if (rootLayout != null && keyboardListener != null) {
                rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Timber.tag(TAG).i("onDestroy");

        /**
         * Keyboard 가 올라왔는지 체크하는 Listener 제거
         */
        try {
            if (rootLayout != null && keyboardListener != null) {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    /**
     * Login
     *
     * @param requestCode   RequestCode of Login
     * @param stringParam   Param of Login
     */
    @Override
    public void doAuthentication(int requestCode, String stringParam) {
        showLoadingProgressBar();

        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUserId(idEditText.getText().toString());
       // requestDTO.setUserPW(Utils.encryptSha1(pwdEditText.getText().toString())); //암호화
        requestDTO.setUserPW(pwdEditText.getText().toString());

        requestDTO.setTelNo(AMSettings.PHONE);

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

                            // Preference 에 Data 저장 후 Main 으로 이동
                            PreferenceManager.setUserId(getActivity(), idEditText.getText().toString());
                            PreferenceManager.setUserPwd(getActivity(), pwdEditText.getText().toString());

                            AMSettings.EMPNO = responseDTO.getUserInfo().getEmpNo(); // 사원 번호 추가

                            AMSettings.ENDTIME = responseDTO.getUserInfo().getWorkOutTime()+"00"; //퇴근시간
                            AMSettings.STARTTIME = responseDTO.getUserInfo().getWorkInTime()+"00"; //출근시간

                            PreferenceManager.setUserCode(getActivity(), responseDTO.getUserInfo().getEmpNo());//사원번호 추가
                            Timber.tag(TAG).d("[doAuthentication][dd] " + responseDTO.getUserInfo().getEmpNo());

                            goMain(userInfoString, workInOutListString);
                        } else {
                            switch (responseDTO.getErrorType()) {
                                default: {
                                    Timber.tag(TAG).e("[doAuthentication] ERROR TYPE : " + responseDTO.getErrorType());

                                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(responseDTO.getMessage()));

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[doAuthentication] RESPONSE BODY IS NULL");

                        DialogHelper.showNormalAlertDialog(getActivity(), "");
                    }
                } else {
                    Timber.tag(TAG).e("[doAuthentication] RESPONSE FAIL code : " + response.code());

                    DialogHelper.showNormalAlertDialog(getActivity(), Utils.nullCheck(response.message()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[doAuthentication][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                DialogHelper.showNormalAlertDialog(getActivity(), "");
            }
        });
    }

    /**
     * Main 으로 이동
     *
     * @param userInfo 회원 정보
     * @param workInOut 출퇴근 목록
     */
    private void goMain(String userInfo, String workInOut) {
        if(getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("USER_INFO", userInfo);
            intent.putExtra("WORK_IN_OUT", workInOut);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    /**
     * Check Login Button Status
     */
    private void checkLoginButtonStatus() {
        if(idEditText.length() > 0 || pwdEditText.length() > 0) {
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }
}
