package com.xiaozi.android.camera.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaozi.android.camera.BuildConfig;
import com.xiaozi.android.camera.R;
import com.xiaozi.android.camera.utils.Logger;

import java.io.IOException;

public class MainActivity extends BaseActivity {
    private SurfaceView mSurfaceView = null;
    private ImageView mPreviewImageView = null;
    private View mCutBorderView = null;
    private Button mTakePictureButton = null;

    private Camera mCamera = null;
    private SurfaceHolder mSurfaceHolder = null;

    private final static int REQUEST_PERMISSIONS_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera != null) {
            try {
                mCamera.reconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.i(LOG_TAG, "onRequestPermissionsResult");
        Logger.d(LOG_TAG, "onRequestPermissionsResult requestCode : " + requestCode);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                int grantedCount = 0;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) grantedCount++;
                }
                Logger.d(LOG_TAG, "onRequestPermissionsResult grantedCount : " + grantedCount);

                if (grantedCount == grantResults.length) {
                    initCamera();
                } else {

                }
                break;
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        Logger.init(BuildConfig.DEBUG);
        Logger.d(LOG_TAG, "initialize mDisplayMetrics.widthPixels : " + mDisplayMetrics.widthPixels);
        Logger.d(LOG_TAG, "initialize mDisplayMetrics.heightPixels : " + mDisplayMetrics.heightPixels);
        Logger.d(LOG_TAG, "initialize mDisplayMetrics.densityDpi : " + mDisplayMetrics.densityDpi);
        Logger.d(LOG_TAG, "initialize mDisplayMetrics.density : " + mDisplayMetrics.density);
    }

    @Override
    protected void initView() {
        super.initView();
        mSurfaceView = findViewById(R.id.main_surface_view);
        mPreviewImageView = findViewById(R.id.main_preview_image_view);
        mCutBorderView = findViewById(R.id.main_cut_border_view);
        mTakePictureButton = findViewById(R.id.main_take_picture_button);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Logger.i(LOG_TAG, "surfaceCreated");
                checkPermissions();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Logger.i(LOG_TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Logger.i(LOG_TAG, "surfaceDestroyed");
                if (mCamera != null) {
                    mCamera.release();
                }
            }
        });

        mTakePictureButton.setOnClickListener(onClickListener);
    }

    private void checkPermissions() {
        Logger.i(LOG_TAG, "checkPermissions");
        int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasCameraPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA);

        if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS_CODE);
        }

        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    REQUEST_PERMISSIONS_CODE);
        }

        if ((hasWriteExternalStoragePermission & hasCameraPermission) == PackageManager.PERMISSION_GRANTED) {
            initCamera();
        }
    }

    private void initCamera() {
        Logger.i(LOG_TAG, "initCamera");
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            try {
                mCamera = Camera.open();
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(parameters);
//                mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
//                    @Override
//                    public void onPreviewFrame(byte[] data, Camera camera) {
//                        Logger.i(LOG_TAG, "initCamera onPreviewFrame");
//                    }
//                });
//                mCamera.setAutoFocusMoveCallback(new Camera.AutoFocusMoveCallback() {
//                    @Override
//                    public void onAutoFocusMoving(boolean start, Camera camera) {
//                        Logger.i(LOG_TAG, "initCamera onAutoFocusMoving");
//                    }
//                });
//                mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        Logger.i(LOG_TAG, "initCamera onAutoFocus");
//                    }
//                });
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(mActivity, "No Camera.", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_take_picture_button:
                    if (mCamera != null) {
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                Logger.i(LOG_TAG, "onAutoFocus");
                                Logger.d(LOG_TAG, "onAutoFocus success : " + success);
                                if (success) {
                                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                                        @Override
                                        public void onPictureTaken(byte[] data, Camera camera) {
                                            Logger.i(LOG_TAG, "onPictureTaken");
                                            Logger.d(LOG_TAG, "onPictureTaken data.length : " + data.length);
                                            Bitmap srcBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            int srcBitmapWidth = srcBitmap.getWidth();
                                            int srcBitmapHeight = srcBitmap.getHeight();
                                            int cutBorderWidth = mCutBorderView.getWidth();
                                            int cutBorderHeight = mCutBorderView.getHeight();
                                            float scaledWidth = srcBitmapWidth / (float) mDisplayMetrics.widthPixels;
                                            float scaledHeight = srcBitmapHeight / (float) mDisplayMetrics.heightPixels;
                                            int dstCutWidth = (int) (cutBorderWidth * scaledWidth);
                                            int dstCutHeight = (int) (cutBorderHeight * scaledHeight);
                                            int dstStartX = (srcBitmapWidth - dstCutWidth) / 2;
                                            int dstStartY = (srcBitmapHeight - dstCutHeight) / 2;
                                            Bitmap dstBitmap = Bitmap.createScaledBitmap(srcBitmap,
                                                    (int) (160 * mDisplayMetrics.density), (int) (90 * mDisplayMetrics.density), false);
                                            Bitmap croppedBitmap = Bitmap.createBitmap(srcBitmap, dstStartX, dstStartY, dstCutWidth, dstCutHeight);
                                            Logger.d(LOG_TAG, "onPictureTaken srcBitmapWidth : " + srcBitmapWidth);
                                            Logger.d(LOG_TAG, "onPictureTaken srcBitmapHeight : " + srcBitmapHeight);
                                            Logger.d(LOG_TAG, "onPictureTaken cutBorderWidth : " + cutBorderWidth);
                                            Logger.d(LOG_TAG, "onPictureTaken cutBorderHeight : " + cutBorderHeight);
                                            Logger.d(LOG_TAG, "onPictureTaken scaledWidth : " + scaledWidth);
                                            Logger.d(LOG_TAG, "onPictureTaken scaledHeight : " + scaledHeight);
                                            Logger.d(LOG_TAG, "onPictureTaken dstCutWidth : " + dstCutWidth);
                                            Logger.d(LOG_TAG, "onPictureTaken dstCutHeight : " + dstCutHeight);
                                            Logger.d(LOG_TAG, "onPictureTaken dstStartX : " + dstStartX);
                                            Logger.d(LOG_TAG, "onPictureTaken dstStartY : " + dstStartY);
                                            mPreviewImageView.setImageBitmap(croppedBitmap);
                                            mCamera.startPreview();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    break;
            }
        }
    };
}
