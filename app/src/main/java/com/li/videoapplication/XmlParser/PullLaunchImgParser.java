package com.li.videoapplication.XmlParser;

import android.util.Xml;

import com.li.videoapplication.entity.LaunchImgEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/17 0017.
 */
public class PullLaunchImgParser implements LaunchImgParser {

    /**
     * InputStream转换为List<LaunchImgEntity>*
     */
    @Override
    public List<LaunchImgEntity> parse(InputStream is) throws Exception {
        List<LaunchImgEntity> launchImgs = null;
        LaunchImgEntity launchImg = null;

//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//      XmlPullParser parser = factory.newPullParser();

        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(is, "UTF-8");               //设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    launchImgs = new ArrayList<LaunchImgEntity>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("launchImg")) {
                        launchImg = new LaunchImgEntity();
                        launchImg.setLaunch_id(parser.getAttributeName(0));
                    } else if (parser.getName().equals("changetime")) {
                        eventType = parser.next();
                        launchImg.setChangetime(parser.getText());
                    } else if (parser.getName().equals("title")) {
                        eventType = parser.next();
                        launchImg.setTitle(parser.getText());
                    } else if (parser.getName().equals("flag")) {
                        eventType = parser.next();
                        launchImg.setFlag(parser.getText());
                    } else if (parser.getName().equals("starttime")) {
                        eventType = parser.next();
                        launchImg.setStarttime(parser.getText());
                    } else if (parser.getName().equals("endtime")) {
                        eventType = parser.next();
                        launchImg.setEndtime(parser.getText());
                    } else if (parser.getName().equals("alone_id")) {
                        eventType = parser.next();
                        launchImg.setAlone_id(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("launchImg")) {
                        launchImgs.add(launchImg);
                        launchImg = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return launchImgs;
    }

    /**
     * List<LaunchImgEntity>转换为String
     */
    @Override
    public String serialize(List<LaunchImgEntity> launchImgs) throws Exception {

        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);   //设置输出方向为writer
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "launchImgs");
        for (LaunchImgEntity launchImg : launchImgs) {
            serializer.startTag("", "launchImg");
            serializer.attribute("", "launch_id", launchImg.getLaunch_id());

            serializer.startTag("", "changetime");
            serializer.text(launchImg.getChangetime());
            serializer.endTag("", "changetime");

            serializer.startTag("", "title");
            serializer.text(launchImg.getTitle());
            serializer.endTag("", "title");

            serializer.startTag("", "flag");
            serializer.text(launchImg.getFlag());
            serializer.endTag("", "flag");

            serializer.startTag("", "starttime");
            serializer.text(launchImg.getStarttime());
            serializer.endTag("", "starttime");

            serializer.startTag("", "endtime");
            serializer.text(launchImg.getEndtime());
            serializer.endTag("", "endtime");

            serializer.startTag("", "alone_id");
            serializer.text(launchImg.getAlone_id());
            serializer.endTag("", "alone_id");

            serializer.endTag("", "launchImg");
        }
        serializer.endTag("", "launchImgs");
        serializer.endDocument();

        return writer.toString();
    }
}
