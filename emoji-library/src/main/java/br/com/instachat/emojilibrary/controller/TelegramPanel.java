package br.com.instachat.emojilibrary.controller;

import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.instachat.emojilibrary.R;
import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.EmojiEditText;

/**
 * Created by edgar on 18/02/2016.
 */
public class TelegramPanel {

    private static final String TAG = "TelegramPanel";

    private EmojiCompatActivity mActivity;

    private Toolbar mBottomPanel;
    private EmojiEditText mInput;
    private EmojiKeyboard mEmojiKeyboard;

    private Boolean isEmojiKeyboardVisible = Boolean.FALSE;

    // CONSTRUCTOR
    public TelegramPanel(EmojiCompatActivity activity) {
        this.mActivity = activity;
        this.initBottomPanel();
        this.setInputConfig();
        this.setOnBackPressed();
        this.mEmojiKeyboard = new EmojiKeyboard(this.mActivity, this.mInput);
    }

    // INITIALIZATION
    private void initBottomPanel() {
        this.mBottomPanel = (Toolbar) this.mActivity.findViewById(R.id.panel);
        this.mBottomPanel.setNavigationIcon(R.drawable.input_emoji);
        this.mBottomPanel.setTitleTextColor(0xFFFFFFFF);
        this.mBottomPanel.inflateMenu(R.menu.rsc_bottom_panel_menu);

        this.mBottomPanel.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TelegramPanel.this.isEmojiKeyboardVisible) {
                    if (TelegramPanel.this.mInput.isSoftKeyboardVisible()) {
                        TelegramPanel.this.mBottomPanel.setNavigationIcon(R.drawable.ic_keyboard_grey600_24dp);
                        TelegramPanel.this.mInput.hideSoftKeyboard();
                    } else {
                        TelegramPanel.this.mBottomPanel.setNavigationIcon(R.drawable.input_emoji);
                        TelegramPanel.this.mInput.showSoftKeyboard();
                    }
                } else {
                    TelegramPanel.this.mBottomPanel.setNavigationIcon(R.drawable.ic_keyboard_grey600_24dp);
                    TelegramPanel.this.showEmojiKeyboard(0);
                }
            }
        });

        this.mBottomPanel.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();

                if (i == R.id.action_attach) {

                } else if (i == R.id.action_mic) {

                }
                return Boolean.TRUE;
            }
        });

    }

    private void setInputConfig() {
        this.mInput = (EmojiEditText) this.mBottomPanel.findViewById(R.id.input);
        this.mInput.addOnSoftKeyboardListener(new EmojiEditText.OnSoftKeyboardListener() {
            @Override
            public void onSoftKeyboardDisplay() {
                if (!TelegramPanel.this.isEmojiKeyboardVisible) {
                    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                    scheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            Handler mainHandler = new Handler(TelegramPanel.this.mActivity.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    TelegramPanel.this.showEmojiKeyboard(0);
                                }
                            };
                            mainHandler.post(myRunnable);
                        }
                    }, 150, TimeUnit.MILLISECONDS);
                }
            }

            @Override
            public void onSoftKeyboardHidden() {
                if (TelegramPanel.this.isEmojiKeyboardVisible) {
                    TelegramPanel.this.hideEmojiKeyboard();
                }
            }
        });

        this.mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MenuItem attachButton = TelegramPanel.this.mBottomPanel.getMenu().findItem(R.id.action_mic);
                MenuItem micButton = TelegramPanel.this.mBottomPanel.getMenu().findItem(R.id.action_mic);
                if (!TelegramPanel.this.mInput.getText().toString().equals("")) {
                    TelegramPanel.this.mBottomPanel.findViewById(R.id.action_attach).animate().scaleX(0).scaleY(0).setDuration(150).start();
                    TelegramPanel.this.mBottomPanel.findViewById(R.id.action_mic).animate().scaleX(0).scaleY(0).setDuration(75).start();
                    micButton.setIcon(R.drawable.ic_send);
                    TelegramPanel.this.mBottomPanel.findViewById(R.id.action_mic).animate().scaleX(1).scaleY(1).setDuration(75).start();

                } else {
                    TelegramPanel.this.mBottomPanel.findViewById(R.id.action_attach).animate().scaleX(1).scaleY(1).setDuration(150).start();
                    TelegramPanel.this.mBottomPanel.findViewById(R.id.action_mic).animate().scaleX(0).scaleY(0).setDuration(75).start();
                    micButton.setIcon(R.drawable.ic_microphone_grey600_24dp);
                    TelegramPanel.this.mBottomPanel.findViewById(R.id.action_mic).animate().scaleX(1).scaleY(1).setDuration(75).start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setOnBackPressed() {
        this.mActivity.setOnBackPressed(new EmojiCompatActivity.OnBackPressed() {
            @Override
            public void onBackPressed() {
                if (TelegramPanel.this.isEmojiKeyboardVisible) {
                    TelegramPanel.this.hideEmojiKeyboard();
                } else {
                    TelegramPanel.this.mActivity.onBackPressed();
                }
            }
        });
    }

    private void showEmojiKeyboard(int delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TelegramPanel.this.isEmojiKeyboardVisible = Boolean.TRUE;
        TelegramPanel.this.mEmojiKeyboard.getEmojiKeyboardLayout().setVisibility(LinearLayout.VISIBLE);
    }

    private void hideEmojiKeyboard() {
        TelegramPanel.this.mBottomPanel.setNavigationIcon(R.drawable.input_emoji);
        TelegramPanel.this.isEmojiKeyboardVisible = Boolean.FALSE;
        TelegramPanel.this.mEmojiKeyboard.getEmojiKeyboardLayout().setVisibility(LinearLayout.GONE);
    }
}