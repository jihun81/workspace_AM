package com.metanetglobal.knowledge.worker.intro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.metanetglobal.knowledge.worker.BuildConfig;
import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.auth.LoginApiInterface;
import com.metanetglobal.knowledge.worker.auth.bean.LoginRequestDTO;
import com.metanetglobal.knowledge.worker.auth.bean.LoginResponseDTO;
import com.metanetglobal.knowledge.worker.common.AMSettings;
import com.metanetglobal.knowledge.worker.common.BaseFragment;
import com.metanetglobal.knowledge.worker.common.DefaultRestClient;
import com.metanetglobal.knowledge.worker.common.DialogHelper;
import com.metanetglobal.knowledge.worker.common.PreferenceManager;
import com.metanetglobal.knowledge.worker.common.UserInfoManager;
import com.metanetglobal.knowledge.worker.common.Utils;
import com.metanetglobal.knowledge.worker.main.MainActivity;
import com.github.ajalt.timberkt.Timber;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.fragment.app.FragmentActivity;

/**
 * Intro Fragment
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseFragment
 */
public class IntroFragment extends BaseFragment {
    private final String TAG = IntroFragment.class.getSimpleName();

    /**
     * Handler for WeakReference
     */
    private static class IntroHandler extends Handler {
        private final WeakReference<IntroFragment> weakReference;

        public IntroHandler(IntroFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }

        public IntroFragment getWeakReference() {
            return weakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            IntroFragment theFrag = weakReference.get();
            if(theFrag != null) {
                theFrag.handleMessage(msg);
            }
        }
    }

    private IntroHandler mHandler = new IntroHandler(this);

    private void handleMessage(Message msg) {
        // NOTHING
    }

    /**
     * Default Constructor
     *
     * @return  IntroFragment Instance
     */
    public static IntroFragment getInstance() {
        IntroFragment fragment = new IntroFragment();
        return fragment;
    }

    /**
     * Get Root Layout
     *
     * @return Layout of this Fragment
     */
    @Override
    public int getLayout() {
        return R.layout.fragment_intro;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.tag(TAG).i("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.tag(TAG).i("onActivityCreated");

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getVersion();
            }
        }, 2000);
    }

    /**
     * Main 으로 이동
     *
     * @param userInfo 회원 정보
     * @param workInOut 출퇴근 목록
     */
    private void goMain(String userInfo, String workInOut) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("USER_INFO", userInfo);
        intent.putExtra("WORK_IN_OUT", workInOut);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
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
        requestDTO.setUserId(PreferenceManager.getUserId(getActivity()));
   //     requestDTO.setUserPW(Utils.encryptSha1(PreferenceManager.getUserPwd(getActivity())));
        requestDTO.setUserPW(PreferenceManager.getUserPwd(getActivity()));

        requestDTO.setTelNo(Utils.getPhoneNumber(getContext(),this.getActivity()));



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

                            AMSettings.EMPNO = responseDTO.getUserInfo().getEmpNo(); //사원번호
                            AMSettings.ENDTIME = responseDTO.getUserInfo().getWorkOutTime()+"00"; //퇴근시간
                            AMSettings.STARTTIME = responseDTO.getUserInfo().getWorkInTime()+"00"; //출근시간

                            String workInOutListString = Utils.convertObjToJSON(responseDTO.getWorkInOutList());
                            Timber.tag(TAG).d("[doAuthentication][WORK_IN_OUT] " + workInOutListString);

                            goMain(userInfoString, workInOutListString);
                        } else {
                            switch (responseDTO.getErrorType()) {
                                default: {
                                    Timber.tag(TAG).e("[doAuthentication] ERROR TYPE : " + responseDTO.getErrorType());

                                    failedLogin(Utils.nullCheck(responseDTO.getMessage()));

                                    break;
                                }
                            }
                        }
                    } else {
                        Timber.tag(TAG).e("[doAuthentication] RESPONSE BODY IS NULL");
                        failedLogin("");
                    }
                } else {
                    Timber.tag(TAG).e("[doAuthentication] RESPONSE FAIL code : " + response.code());

                    failedLogin(Utils.nullCheck(response.message()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                Timber.tag(TAG).e("[doAuthentication][FAIL] message : " + t.getMessage());

                hideLoadingProgressBar();

                failedLogin("");
            }
        });
    }

    /**
     * Login 실패 시 Dialog 띄워주고 로그인 페이지로 이동시켜준다.
     *
     * @param message Message of Dialog
     */
    private void failedLogin(String message) {
        if(getActivity() != null) {
            PreferenceManager.setUserId(getActivity(), "");
            PreferenceManager.setUserPwd(getActivity(), "");

            DialogHelper.showNormalAlertDialog(getActivity(), message, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    goLogin();
                }
            });
        }
    }

    /**
     * 로그인이 되어있었다면 자동로그인 시켜준다.
     * 그렇지 않다면 Login 화면으로 이동 시켜준다.
     */
    private void checkLogin() {
        Timber.tag(TAG).d("doAuthentication]"+getActivity());
        if(getActivity() != null && !getActivity().isFinishing()) {
            Timber.tag(TAG).d("doAuthentication]"+getActivity()+"");
            if (UserInfoManager.isLogined(getActivity())) {
                doAuthentication(0, "");
            } else {
                goLogin();
            }
        }
    }

    /**
     * App 업데이트 검사를 위하여 Version Check 를 한다.
     *
     * output.json 파일을 읽어서 version name 을 가져온다.
     * 어떠한 에러가 발생해도 Login Check 를 진행한다.
     *
     */
    private void getVersion() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.DEBUG ? AMSettings.APP_DOWN_URL  : AMSettings.APP_DOWN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetVersionApiInterface service = retrofit.create(GetVersionApiInterface.class);
        service.getOutput().enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if(response.isSuccessful()) {
                    JsonArray jsonArray = response.body();
                    if(jsonArray != null && jsonArray.size() > 0) {
                        try {
                            JsonObject obj = jsonArray.get(0).getAsJsonObject();

                            Timber.tag(TAG).d("[getVersion] RESPONSE : " + obj.toString());

                            String deployVersion = obj.getAsJsonObject("apkInfo").get("versionName").getAsString();

                            // 현재 앱 버젼 가져온다.
                            String appVersion = BuildConfig.VERSION_NAME;

                            int comparedValue = Utils.CompareVersion.compare(appVersion, deployVersion);

                            if (comparedValue < 0) {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage(getResources().getString(R.string.update_popup_message))
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(AMSettings.APP_DOWN_URL + "download"));
                                                    startActivity(intent);
                                                    if (getActivity() != null) {
                                                        getActivity().finish();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    checkLogin();
                                                }
                                            }
                                        })
                                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                checkLogin();
                                            }
                                        })
                                        .create()
                                        .show();
                            } else {
                                // GetVersion 통과
                                checkLogin();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            checkLogin();
                        }
                    } else {
                        Timber.tag(TAG).e("[getVersion] RESPONSE BODY IS NULL");
                        checkLogin();
                    }
                } else {
                    Timber.tag(TAG).e("[getVersion] RESPONSE FAIL code : " + response.code());
                    checkLogin();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Timber.tag(TAG).e("[getVersion][FAIL] message : " + t.getMessage());
                checkLogin();
            }
        });
    }
}