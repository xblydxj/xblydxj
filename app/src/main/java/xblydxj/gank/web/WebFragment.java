package xblydxj.gank.web;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xblydxj.gank.R;
import xblydxj.gank.utils.SnackUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by 46321 on 2016/7/16/016.
 */
public class WebFragment extends Fragment implements WebContract.View {

    @Bind(R.id.web_header_title)
    TextView mWebHeaderTitle;
    @Bind(R.id.web_content)
    WebView mWebContent;
    @Bind(R.id.web_fab_back)
    FloatingActionButton mWebFabBack;
    @Bind(R.id.web_fab_forward)
    FloatingActionButton mWebFabForward;
    @Bind(R.id.web_progress)
    ProgressBar mWebProgress;
    private WebContract.Presenter mPresenter;
    private Toolbar mToolBar;

    public static WebFragment newInstance(String url, String desc) {
        Bundle arguments = new Bundle();
        arguments.putString("url", url);
        arguments.putString("desc", desc);
        WebFragment webFragment = new WebFragment();
        webFragment.setArguments(arguments);
        return webFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unSubscribe();
    }

    @Override
    public void setPresenter(WebContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View web = inflater.inflate(R.layout.fragment_web, container, false);
        initToolBar();
        initWebView();
        initFab();
        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        return web;
    }


    private void initFab() {
        mWebFabForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.goForward(mWebContent);
            }
        });
        mWebFabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.goBack(mWebContent);
            }
        });
    }


    private void initWebView() {
        mWebContent.canGoBack();
        int progress = mWebContent.getProgress();
        mWebContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mWebProgress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebProgress.setVisibility(View.INVISIBLE);
            }
        });
        mWebContent.setElevation(5);

    }

    private void initToolBar() {
        setHasOptionsMenu(true);
        mToolBar = (Toolbar) getActivity().findViewById(R.id.web_tool_bar);
        mToolBar.setTitle("详情");
        mToolBar.setTitleTextColor(Color.WHITE);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mPresenter.share();
                SnackUtils.showSnackLong(mToolBar, "share", "i see");
                return true;
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showTitle(String desc) {
        mWebHeaderTitle.setText(desc);
    }

    @Override
    public void showWeb(String url) {
        mWebContent.loadUrl(url);
    }


    @Override
    public void updateProgress(int progress) {
        mWebProgress.setProgress(progress);
    }
}
