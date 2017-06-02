/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.okserver.download;

import android.os.Handler;
import android.os.Message;

import com.lzy.okgo.utils.OkLogger;
import com.lzy.okserver.listener.DownloadListener;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：用于在主线程回调下载UI
 * 修订历史：
 * ================================================
 */
public class DownloadUIHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
        MessageBean messageBean = (MessageBean) msg.obj;
        if (messageBean != null) {
            DownloadInfo info = messageBean.downloadInfo;
            String errorMsg = messageBean.errorMsg;
            Exception e = messageBean.e;
            DownloadListener listener = info.getListener();
            if (listener != null) executeListener(listener, info, errorMsg, e);
        } else {
            OkLogger.e("DownloadUIHandler DownloadInfo null");
        }
    }

    private void executeListener(DownloadListener listener, DownloadInfo info, String errorMsg, Exception e) {
        int state = info.getState();
        switch (state) {
            case DownloadManager.NONE:
            case DownloadManager.WAITING:
            case DownloadManager.DOWNLOADING:
            case DownloadManager.PAUSE:
                listener.onProgress(info);
                break;
            case DownloadManager.FINISH:
                listener.onProgress(info);   //结束前再次回调进度，避免最后一点数据没有刷新
                listener.onFinish(info);
                break;
            case DownloadManager.ERROR:
                listener.onProgress(info);   //结束前再次回调进度，避免最后一点数据没有刷新
                listener.onError(info, errorMsg, e);
                break;
        }
    }

    public static class MessageBean {
        public DownloadInfo downloadInfo;
        public String errorMsg;
        public Exception e;
    }
}
