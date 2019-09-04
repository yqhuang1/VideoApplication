package com.li.videoapplication.XmlParser;

import com.li.videoapplication.entity.LaunchImgEntity;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Administrator on 2015/9/17 0017.
 */
public interface LaunchImgParser {
    /**
     * 解析输入流 得到LaunchImg对象集合
     *
     * @param is
     * @return
     * @throws Exception
     */
    public List<LaunchImgEntity> parse(InputStream is) throws Exception;

    /**
     * 序列化LaunchImg对象集合 得到XML形式的字符串
     *
     * @param LaunchImgs
     * @return
     * @throws Exception
     */
    public String serialize(List<LaunchImgEntity> LaunchImgs) throws Exception;
}
