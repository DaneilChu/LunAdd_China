package com.infinity.lunadd.mvp.view.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.jaeger.library.StatusBarUtil;
import com.orhanobut.logger.Logger;
import com.infinity.lunadd.R;
import com.infinity.lunadd.base.BaseActivity;
import com.infinity.lunadd.mvp.presenter.home.impl.AddPostImpl;
import com.infinity.lunadd.mvp.view.home.IAddPostView;
import com.infinity.lunadd.widget.ChoosePicDialog;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by Administrator on 2016/4/21.
 */
public class AddPostActivity extends BaseActivity implements IAddPostView {


    @Inject
    ChoosePicDialog mDialog;
    @Inject
    AddPostImpl mAddPostPresenter;
    File imgfile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.iv_album)
    ImageView ivAlbum;
    @BindView(R.id.iv_submit)
    ImageView ivSubmit;
    @BindView(R.id.emojicon_menu_container)
    FrameLayout emojiconMenuContainer;
    protected InputMethodManager inputMethodManager;
    protected EaseEmojiconMenuBase emojiconMenu;
    protected LayoutInflater layoutInflater;
    private final Handler handler = new Handler();
    private Context context=this;



    @Override
    protected void initData() {

    }

    @Override
    public void initDagger() {
        mActivityComponent.inject(this);

    }

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_addpost);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void initViewsAndListener() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(emojiconMenu == null){
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(com.hyphenate.easeui.R.layout.ease_layout_emojicon_menu, null);
            List<EaseEmojiconGroupEntity>  emojiconGroupList = new ArrayList<>();
            emojiconGroupList.add(new EaseEmojiconGroupEntity(com.hyphenate.easeui.R.drawable.e1f60a2,  Arrays.asList(EaseDefaultEmojiconDatas.getData())));
            ((EaseEmojiconMenu)emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);
        emojiconMenu.setVisibility(View.GONE);


        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuBase.EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if(emojicon.getEmojiText() != null){
                    onEmojiconInputEvent(EaseSmileUtils.getSmiledText(context,emojicon.getEmojiText()));
                }
            }

            @Override
            public void onDeleteImageClicked() {
                onEmojiconDeleteEvent();
            }
        });

    }

    /**
     * append emoji icon to editText
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent){
        etContent.append(emojiContent);
    }

    /**
     * delete emojicon
     */
    public void onEmojiconDeleteEvent(){
        if (!TextUtils.isEmpty(etContent.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            etContent.dispatchKeyEvent(event);
        }
    }


    @Override
    public void initPresenter() {
        mAddPostPresenter.attachView(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAddPostPresenter.detachView();
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    @OnClick(R.id.iv_submit)
    @Override
    public void submit(){
        String content = etContent.getText().toString();
        mAddPostPresenter.commit(content, imgfile);

    }

    /**
     * 显示或隐藏表情页
     */
    @OnClick(R.id.iv_emoji)
    protected void toggleEmojicon() {
        if (emojiconMenu.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    emojiconMenu.setVisibility(View.VISIBLE);
                }
            }, 50);
        } else {
            emojiconMenu.setVisibility(View.GONE);

        }
    }

    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode !=
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnClick(R.id.iv_gallery)
    @Override
    public void showDialog() {
        mDialog.show();
    }

    @Override
    public void hideDialog() {
        mDialog.dismiss();
    }

    @Override
    public void setResultCode(Intent intent) {
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource imageSource, int i) {
                showToast("出错啦，请重新试试");
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource, int i) {
                UCrop.of(Uri.fromFile(file), Uri.fromFile(file))
                        .withMaxResultSize(800, 800).start(AddPostActivity.this);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource, int i) {
                if (imageSource == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(AddPostActivity.this);
                    if (photoFile != null) {
                        photoFile.delete();
                    }

                }
            }
        });
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            try {
                imgfile = new File(new URI(UCrop.getOutput(data).toString()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            Glide.with(this).load(imgfile).into(ivAlbum);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            showToast("出错啦，请稍后再试");
            Throwable throwable = UCrop.getError(data);
            if (throwable != null) {
                Logger.e(throwable.getMessage());
            }
        }

    }

}
