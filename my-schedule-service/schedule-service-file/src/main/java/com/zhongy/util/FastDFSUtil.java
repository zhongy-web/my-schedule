package com.zhongy.util;

import com.zhongy.file.FastDFSFile;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;

public class FastDFSUtil {
    /**
     * 加载Tracker连接信息
     */
    static {
        try {
            //查找classpath下的文件路径
            String filename = new ClassPathResource("fdfs_client.conf").getPath();
            //加载Tracker连接信息
            ClientGlobal.init(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param fastDFSFile
     * @return
     */
    public static String[] upload(FastDFSFile fastDFSFile) throws Exception{
        //获取Tracker
        TrackerServer trackerServer = getTrackerServer();

        //通过TrackerServer连接信息，可以获取Storage连接信息，创建StorageClient对象存储Storage的连接信息
        StorageClient storageClient = getStorageClient(trackerServer);

        //通过StorageClient访问Storage，实现文件上传，并且获取文件上传后的存储信息
        String[] uploads = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), null);

        return uploads;
    }

    /**
     * 获取文件信息
     * @param groupName :文件组名
     * @param remoteFileName : 文件的存储路径名字
     * @return
     */
    public static FileInfo getFile(String groupName, String remoteFileName) throws Exception {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = getStorageClient(trackerServer);
        //获取文件信息
        return storageClient.get_file_info(groupName, remoteFileName);
    }

    /**
     * 获取Storage信息
     * @return
     */
    public static StorageServer getStorages() throws Exception {
        //创建一个TrackerClient对象，通过创建一个TrackerClient对象访问创建一个TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer连接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getStoreStorage(trackerServer);
    }

    /**
     * 文件下载
     * @return
     */
    public InputStream downloadFile(String groupName, String remoteFileName) throws Exception{
        TrackerServer trackerServer = getTrackerServer();
        //通过TrackerServer获取Storage信息，创建StorageClient对象存储Storage信息
        StorageClient storageClient = getStorageClient(trackerServer);

        //文件下载
        byte[] buffer = storageClient.download_file(groupName, remoteFileName);
        return new ByteArrayInputStream(buffer);
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = getStorageClient(trackerServer);

        storageClient.delete_file(groupName, remoteFileName);
    }


    /**
     * 获取Tracker信息
     * @return
     */
    public static String getTrackerInfo() throws Exception {
        TrackerServer trackerServer = getTrackerServer();
        //Tracker的IP，HTTP端口
        String ip = trackerServer.getInetSocketAddress().getHostString();
        int port = ClientGlobal.getG_tracker_http_port();
        String url = "http://" + ip;
        return url;
    }

    /**
     * 获取Tracker
     * @return
     * @throws Exception
     */
    public static TrackerServer getTrackerServer() throws Exception {
        //创建一个TrackerClient对象，通过创建一个TrackerClient对象访问创建一个TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer连接对象
        return trackerClient.getConnection();
    }

    public static StorageClient getStorageClient(TrackerServer trackerServer) {
        return new StorageClient(trackerServer, null);
    }
}

