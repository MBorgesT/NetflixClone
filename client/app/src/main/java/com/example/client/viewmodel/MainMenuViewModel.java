package com.example.client.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.client.model.MediaMetadata;
import com.example.client.repository.MediaRepository;
import com.example.client.util.MediaDownloadUtil;

import java.util.List;

public class MainMenuViewModel extends ViewModel {

    private final MediaRepository mediaRepository;

    private final MutableLiveData<List<MediaMetadata>> mediaListLiveData;
    private final MutableLiveData<String> messageLiveData;

    public MainMenuViewModel() {
        mediaRepository = MediaRepository.getInstance();

        mediaListLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<MediaMetadata>> getMediaListLiveData() {
        return mediaListLiveData;
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void fetchMedias() {
        mediaRepository.fetchMediaList(new MediaRepository.OnMediaListFetchListener() {
            @Override
            public void onMediaListFetch(List<MediaMetadata> medias) {
                mediaListLiveData.setValue(medias);
            }

            @Override
            public void onMediaListFetchFailure(String message) {
                messageLiveData.setValue(message);
            }
        });
    }

    public void downloadMedia(int mediaId, String folderName) {
        mediaRepository.fetchChunkUris(mediaId, new MediaRepository.OnChunkUrisFetchListener() {
            @Override
            public void onChunkUrisFetch(List<String> uris) {
                downloadFiles(mediaId, uris, folderName);
            }

            @Override
            public void onChunkUrisFetchFailure(String message) {
                messageLiveData.setValue(message);
            }
        });
    }

    private void downloadFiles(int mediaId, List<String> uris, String folderName) {
        MediaRepository.OnDownloadStatusUpdateListener listener =
                new MediaRepository.OnDownloadStatusUpdateListener() {
            @Override
            public void onDownloadStatusUpdate(String message) {
                fetchMedias();
            }
        };

        mediaRepository.updateDownloadStatus(
                mediaId, MediaMetadata.DownloadStatus.DOWNLOADING,
                listener);
        fetchMedias();

        MediaDownloadUtil.downloadFiles(uris, folderName,
                new MediaDownloadUtil.OnMediaDownloadListener() {
            @Override
            public void onMediaDownload() {
                mediaRepository.updateDownloadStatus(
                        mediaId, MediaMetadata.DownloadStatus.DOWNLOADED,
                        listener);
                mediaRepository.singalMeshAvailability(mediaId, new MediaRepository.OnSignalAvailabityListener() {
                    @Override
                    public void onSignalAvailability(String message) {
                        messageLiveData.setValue(message);
                    }
                });
                fetchMedias();
            }

            @Override
            public void onMediaDownloadFailed() {
                mediaRepository.updateDownloadStatus(
                        mediaId, MediaMetadata.DownloadStatus.NOT_DOWNLOADED,
                        listener);
                fetchMedias();
            }
        });
    }

    public void deleteDownloadedMedia(int mediaId, String folderName) {
        MediaRepository.OnDownloadStatusUpdateListener listener =
                new MediaRepository.OnDownloadStatusUpdateListener() {
                    @Override
                    public void onDownloadStatusUpdate(String message) {
                        fetchMedias();
                    }
                };

        MediaDownloadUtil.deleteMediaDownloadFolder(folderName);
        mediaRepository.updateDownloadStatus(
                mediaId, MediaMetadata.DownloadStatus.NOT_DOWNLOADED,
                listener);
        mediaRepository.singalMeshUnavailability(mediaId, new MediaRepository.OnSignalAvailabityListener() {
            @Override
            public void onSignalAvailability(String message) {
                messageLiveData.setValue(message);
            }
        });
    }

}
