package com.li.videoapplication.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 首页底部标签切换动画类
 */
public class MyAnimation extends Animation {  
    
    private float mFromDegree;//旋转前的角度
    private float mToDegree;//旋转后的角度
    private Camera mCamera;  
    private int halfWidth;  
    private int halfHeight;  
  
    public MyAnimation(float fromDegree, float toDegree)  
    {  
        mFromDegree = fromDegree;  
        mToDegree = toDegree;  
        mCamera = new Camera();  
    }  
          
    public void initialize(int width, int height, int parentWidth,  
            int parentHeight) {  
        // TODO Auto-generated method stub  
        super.initialize(width, height, parentWidth, parentHeight);  
        halfWidth = width;
        halfHeight = height;
    }
  
    @Override  
    protected void applyTransformation(float interpolatedTime, Transformation t) {  
        Matrix matrix = t.getMatrix();

        //当前旋转的角度值
        float degree = mFromDegree + ( mToDegree - mFromDegree ) * interpolatedTime;

        //save()、restore()求解释
        mCamera.save();
          
        if ( degree >= 82.0f )
        {
            //第一次看的时候可先跳过这里，直接到后面的else子句。
            //这里不是使用mCamera.rotateY(degree);因为由于没有mCamera.translate(0,0,-halfWidth);画面要旋转多少度我们看上去就是转了多少度（有了那句代码，画面旋转82度时我们看上去已经转了90度）
            //如果使用rotateY(degree),那么实际的效果就是，degree从0度到82度时，我们看到画面从0度转到了90度，画面消失，然后degree 83度时，画面又是旋转了83度的样子，并且是绕着自己的中轴线转的83度（没有
            //mCamera.translate(0,0,-halfWidth);也就没有了x轴平移，画面将绕自己的中轴线转）
            mCamera.rotateX(90.0f);
        }
        else if ( degree <= -82.0f )
        {
            mCamera.rotateX(90.0f);
        }
        else
        {
            //这句代码的效果是画面固定左上角缩放：halfWidth值为正，画面缩小，值越大，画面越小；值为负，画面放大，halfWidth值越大（即-halfWidth值越小），画面越大。
            //这个值就如同照相机与画面之间距离的增量。
            mCamera.translate(0, 0,halfWidth/2);

            //值为正，画面右移等量px；值为负，画面左移等量px。究竟是照相机在动呢？照相机固定，画面在动？
//          mCamera.translate(halfWidth, 0, 0);
            //值为正，画面上移等量px;值为负，画面下移等量px
//          mCamera.translate(0, halfWidth, 0);

            //如果只有这一句变换而没有其它处理的话，画面绕着其左边界旋转。代码的本意是让画面绕Y轴旋转，由于默认画面左边界就在Y轴上，所以效果是画面绕着其左边界旋转。
            //想像一下，任何一个动画效果，都是由许许多多张静态的画面组成的，本函数的作用就是返回一个Matrix来将原来的一张画面处理成动画里的一帧画面。
            //任何一个画面，都是由无数个点组成的。Matrix就是让这一个一个的点发生位移，位移量与该点所处的坐标相关。所有发生位移后的点组成的新画面就是动画里的一帧。
            //打印矩阵值，从实质上看，这句代码一方面修改了scale_x值（上面那句translate的缩放是一个与halfWidth相关的定值，新的scale_x值是用上面那个定值乘以degree的余弦值。要实现旋转效果，x轴缩放应该是随着degree的变化而变化的）
            mCamera.rotateX(degree);

            //这句代码的本意是把画面放大。神奇的是，即是没有前面的mCamera.translate(0, 0,halfWidth);这句缩小代码，依然会有立方体旋转效果，只是画面变大了。
            //应该注意的是这里的变大不是把最初的原画面变大，也不是把旋转后的画面在三维空间里把宽高拉回到原来的大小；要知道，我们这里的3D是假3D。这句代码就是把旋转后的画面——我们看到的那个2D的侧面画面，
            //按比例放大了，因此他不是mCamera.translate(0, 0,halfWidth);的逆过程
            //同理而言，前面那句mCamera.rotateY(degree);其实也不是真的让画面在三维世界里转动，其是绕Y轴转动效果只是图像x轴缩放与错切相结合产生的人眼幻觉。
            //为什么要先缩小再旋转然后再放大呢？如果没有这两句缩放代码，画面始终是绕着自己的中轴线在转。如果打印每一句代码后矩阵的9个值，会发现，
            //mCamera.translate(0, 0, -halfWidth );不仅会产生缩放，还会产生x轴的平移。这一点平移是立方体效果的关键所在。
            //为什么前面那句mCamera.translate(0, 0,halfWidth);没有产生x轴的平移呢？也许前面有平移，只是平移量是0.在rotateY之前，矩阵左下角那个值是0.0；随着degree的不同，左下角的值也不
            //同，也许这个值与translate的x轴平移量也有关系。
            //打印矩阵9个值，除了x轴平移外，本句代码还修改了scale_x。mCamera.translate(0, 0,halfWidth);产生的scale_x是一个定值，该值小于1；而mCamera.rotateY(degree);产生的scale_x是从定值到0.0。
            //这意味着如果没有本句代码，我们将会看到画面一开始就瞬间缩小了（比如瞬间变为原来的一半），没有过渡。本句代码会修改scale_x，使其值从1.0到0.0，实现平滑的缩放。
            //这句代码还会引起当degree等于82度时画面就看上去已经旋转了90度，超过82度后，画面又从后面转回来，所以前面有if...else...代码来将82度后的旋转单独处理。其实代码也可以有另一种写法，见最后的注释1
            //如果把本句代码的第三个参数减小(绝对值变大)，比如 -halfWidth * 2，我们看到画面变大旋转，并且原来形成立方体效果的两个view之间的空白距离拉大，出现类似旋转木马的效果
            mCamera.translate(0, 0, -halfWidth/2);
        }

        //注意这句代码，这句代码是以set的方式直接用mCamera里的matrix值将参数matrix覆盖掉，所以如果除了mCamera变换外还有其它变换，请放在这句之后（比如后面的变换中心点调整）。
        mCamera.getMatrix(matrix);  
        mCamera.restore();

        //当只有这一句变换代码时，画面左移halfWidth,上移halfHeight，从而画面的中心与原来的左上角重合。而左上角，是默认的变换中心点。
        //每一种变换，无论是缩放还是旋转，都是相对于一个中心点而言的。图像上的每一点根据自身与中心点的距离来决定自己变换的量
        //这句代码可以让图像好像是以自己的中心为变换中心来变换一样（比如不是绕左边界转而是绕自己的中轴线转），其实是让自己的中心与变换中心重合了而已。
        //由于这句代码的存在，前面mCamera里的那些变换的变换中心都将不仅是原位置的左上角，也是画面本身的中心，这两个中心重合在一起了。
        matrix.preTranslate(-halfWidth, -halfHeight);

        //当只有这一句变换代码时，画面右移halfWidth,下移halfHeight.
        //这句话的存在是因为上一句代码把图像中心平移到左上角去了。要是没有这句，我们看到的画面就是立方体在原先位置的左上角转了。
        matrix.postTranslate(halfWidth, halfHeight);  
          
  
//        Log.v("XXXX","" + degree + " " + matrix.toString());
    }     
}  
  
/*注释1
    protected void applyTransformation(float interpolatedTime, Transformation t) { 
        Matrix matrix = t.getMatrix();   
         
        float degree = mFromDegree + ( mToDegree - mFromDegree ) * interpolatedTime; 
        mCamera.save(); 
        mCamera.translate(0, 0,halfWidth); 
         
        if ( degree >= 82.0f ) 
        { 
            mCamera.rotateY(90.0f); 
        } 
        else if ( degree <= -82.0f ) 
        { 
            mCamera.rotateY(90.0f); 
        } 
        else 
        {    
            mCamera.rotateY(degree); 
            mCamera.translate(0, 0, -halfWidth ); 
        } 
         
        mCamera.getMatrix(matrix); 
        mCamera.restore(); 
        matrix.preTranslate(-halfWidth, -halfHeight);    
        matrix.postTranslate(halfWidth, halfHeight); 
    } 
} 
*/  
