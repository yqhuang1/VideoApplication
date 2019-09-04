package com.li.videoapplication.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.li.videoapplication.R;

import java.util.List;

/**
 * Created by feimoyuangong on 2014/12/27.
 * 表情适配器
 */
public class FaceAdapter extends BaseAdapter {

    public List<Integer> list;
    public EditText editText;
    public Context context;
    public LayoutInflater inflater;
    public Bitmap bitmap;
    public ImageSpan imageSpan;
    public SpannableString spannableString;
    private String[] faceCnArray;

    public FaceAdapter(Context context, List<Integer> list, EditText editText) {

        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.editText = editText;
        list.add(R.drawable.face_del);
//        faceCnArray = context.getResources().getStringArray(R.array.faceCnArray);
        faceCnArray = context.getResources().getStringArray(R.array.expressionCnArray);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.face_item, null);
            holder.face = (ImageView) convertView.findViewById(R.id.face_item_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.face.setBackgroundResource(list.get(position));
        holder.face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position) == R.drawable.face_del) {
                    //删除表情
                    int selectionStart = editText.getSelectionStart();// 获取光标的位置
                    if (selectionStart > 0) {
                        String body = editText.getText().toString();
                        if (!TextUtils.isEmpty(body)) {
                            String tempStr = body.substring(0, selectionStart);
                            if (tempStr.lastIndexOf("]") == selectionStart - 1) {
                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的起始位置
                                int j = tempStr.lastIndexOf("]");// 获取最后一个表情的终止位置
                                if (i != -1 && j != -1) {
//                                CharSequence cs = tempStr
//                                        .subSequence(i, selectionStart-3);
//                                if (cs.equals("[face")) {// 判断是否是一个表情
//                                    editText.getEditableText().delete(i, selectionStart);
//                                    return;
//                                }
//                                CharSequence cs = tempStr
//                                        .subSequence(i, j);
//                                if (cs.equals("[face")) {// 判断是否是一个表情
                                    editText.getEditableText().delete(i, selectionStart);
                                    return;
//                                }
                                }
                            } else {
                                editText.getEditableText().delete(tempStr.length() - 1,
                                        selectionStart);
                            }
                        }
                    }
                } else {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), list.get(position));
                    bitmap = zoomImage(bitmap, bitmap.getWidth() / 2,
                            bitmap.getHeight() / 2);
                    imageSpan = new ImageSpan(context, bitmap);//用ImageSpan指定图片替代文字
                    // 其实写入EditView中的是这个字段“[face+'position']”，表情图片会替代这个字段显示
//                    if (position+1<10){
//                        spannableString=new SpannableString("[face0"+(position+1)+"]");
//                    }else {
//                        spannableString=new SpannableString("[face"+(position+1)+"]");
//                    }
                    String faceCnStr = "[" + faceCnArray[position] + "]";
                    spannableString = new SpannableString("[" + faceCnArray[position] + "]");
                    spannableString.setSpan(imageSpan, 0, faceCnStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //将表情追加到编辑框
//                    editText.append(spannableString);
                    int index = editText.getSelectionStart();//获取光标位置
                    /**
                     * getEditableText：获取EditText的文字，可编辑
                     * getText：获取EditText的文字，不可编辑
                     * */
                    Editable editable = editText.getEditableText();
                    if (index < 0 || index >= editable.length()) {
                        editable.append(spannableString);
                    } else {
                        editable.insert(index, spannableString);//光标所在位置插入文字
                    }
                }

            }
        });
        return convertView;
    }

    /**
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    class ViewHolder {
        ImageView face;
    }
}
