package com.li.videoapplication.utils;

import com.li.videoapplication.entity.VideoManagerEntity;
import java.util.ArrayList;
import java.util.List;

/** 视频管理选择工具类
 * Created by li on 2014/10/16.
 */
public class VideoManagerCheckUtils {

    /**
     * 判断是否进入编辑模式
     */
    public static boolean isEditor=false;

    /**
     *记录选中的 项目
     */
    public static List<VideoManagerEntity> selectList=new ArrayList<VideoManagerEntity>();

    /**
     * 添加选中的项目
     * @param videoManagerEntity
     */
    public static void addCollectProduct(VideoManagerEntity videoManagerEntity){
        selectList.add(videoManagerEntity);
    }

    /**
     * 删除选中了之后又取消的项目
     * @param videoManagerEntity
     */
    public static void removeCollectProduce(VideoManagerEntity videoManagerEntity){
        for (int i=0;i<selectList.size();i++){
            if (videoManagerEntity.getTitle().equals(selectList.get(i).getTitle())){
                selectList.remove(i);
            }
        }
    }

    /**
     * 清空保存所有选中的项目
     */
    public static void clearAllCollectProduce(){
        selectList.clear();
    }

    /**
     * 返回选中的所有项目
     * @return
     */
    public static List<VideoManagerEntity> getProductList(){
        return selectList;
    }

}
