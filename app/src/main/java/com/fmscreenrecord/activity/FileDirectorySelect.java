package com.fmscreenrecord.activity;

/**
 * 文件管理器
 * @author lin
 * Creat:2014-08
 * Refactor:2014-12
 */

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.fmscreenrecord.VideoList.ExternalImageLoader;
import com.fmscreenrecord.VideoList.VideoScanningThrread;
import com.fmscreenrecord.app.ExApplication;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.SharedPreferencesUtils;
import com.fmscreenrecord.utils.StoreDirUtil;
import com.fmscreenrecord.video.VideoInfo;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileDirectorySelect extends ListActivity implements
		View.OnClickListener {

	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/mnt/sdcard/SupperLulu"; // SD以上目录不放开给用户
	private View myView;
	private EditText myEditText;
	SharedPreferences sp;
	private String action;
	private Context mContext;

	// 二维list，分布存储机身视频文件和SDK
	List<List<VideoInfo>> childata = new ArrayList<List<VideoInfo>>();
	// 父类菜单
	private List<String> GroupData;
	// 父类菜单文件夹地址
	private List<String> GroupList = new ArrayList<String>();

	// 手机机身内存视频文件路径
	public static String Filepath;
	public static String FileSdPath = null;
	private boolean isLongClick = false;

	private LinearLayout btBack;

	ExternalImageLoader imageLoaderLoad;

	private File store;
	List<VideoInfo> list;

	// 右上角按钮
	private LinearLayout video_menu;
	// 填充listview的adapter
	private ExtentAdapter adapter;

	private ExpandableListView expandableListView;
	private boolean isDelSel = false;
	boolean selAll = false;

	private List<Boolean> delList = new ArrayList<Boolean>(); // 选择删除的列表

	// 文件迁移进度条
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(MResource.getIdByName(getApplication(), "layout",
				"fm_file_directory_list"));

		mContext = com.fmscreenrecord.activity.FileDirectorySelect.this;
		// 查找页面控件
		findViews();
		// 注册监听
		setOnClick();
		// 初始化页面数据
		initData();

		getFileDir(Filepath);

		creatProgressDialog();

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		sp = SharedPreferencesUtils.getMinJieKaiFaPreferences(this);
		// 获得机身内存视频文件路径
		Filepath = sp.getString("image_store_dir", StoreDirUtil
				.getDefault(this).toString());

		action = getIntent().getStringExtra("action");
		// 获得二级列表数据
		childata = upListAdapterData();

		// 添加一级列表菜单
		GroupData.add("内置SD卡目录");
		GroupData.add("外置SD卡目录");
		// 添加文件夹地址
		GroupList.add(Filepath);
		if (FileSdPath != null) {
			GroupList.add(FileSdPath);
		} else {

			GroupList.add("(无SD卡)");
		}

		adapter = new ExtentAdapter(this);
		expandableListView.setAdapter(adapter);
		expandableListView.setGroupIndicator(null);
	}

	/**
	 * 更新adapter所需要的list数组
	 */
	private List<List<VideoInfo>> upListAdapterData() {
		// 新增两个空的数组
		List<VideoInfo> list_0 = new ArrayList<VideoInfo>();
		List<VideoInfo> list_1 = new ArrayList<VideoInfo>();
		List<List<VideoInfo>> childata = new ArrayList<List<VideoInfo>>();
		childata.add(list_0);
		childata.add(list_1);
		// 将机身视频文件文件存入数组
		if (StoreDirUtil.getDefault(this) != null) {
			File file = new File(Filepath);
			VideoScanningThrread.getVideoFile(childata.get(0), file);

		}
		// 将SD卡视频文件存入数组
		if (StoreDirUtil.getSDDEfault(this) != null) {
			FileSdPath = StoreDirUtil.getSDDEfault(this).getPath();
			File file = new File(FileSdPath);
			VideoScanningThrread.getVideoFile(childata.get(1), file);

		}
		return childata;
	}

	/**
	 * 注册点击监听
	 */
	private void setOnClick() {
		btBack.setOnClickListener(this);

		video_menu.setOnClickListener(this);

	}

	/**
	 * 初始化控件
	 */
	private void findViews() {
		btBack = (LinearLayout) findViewById(MResource.getIdByName(
				getApplication(), "id", "file_dir_imageButton_back"));

		video_menu = (LinearLayout) findViewById(MResource.getIdByName(
				getApplication(), "id", "video_menu"));

		expandableListView = (ExpandableListView) findViewById(MResource
				.getIdByName(getApplication(), "id", "fm_viode_expandlist"));

		childata = new ArrayList<List<VideoInfo>>();

		GroupData = new ArrayList<String>();

	}

	/**
	 * 初始化进度条
	 */
	private void creatProgressDialog() {
		// 创建ProgressDialog对象
		progressDialog = new ProgressDialog(this);

		// 设置进度条风格，风格为长形
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		// 设置ProgressDialog 标题
		progressDialog.setTitle("提示");

		// 设置ProgressDialog 提示信息
		progressDialog.setMessage("视频文件正在迁移中，请稍候...");

		// // 设置ProgressDialog 进度条进度
		// progressDialog.setProgress(100);

		// 设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);

		// 设置ProgressDialog 是否可以按退回按键取消
		progressDialog.setCancelable(false);
	}

	// 取得文件架构
	private void getFileDir(String filePath) {
		// mPath.setText(filePath); /////////////显示位置
		Filepath = filePath;

		items = new ArrayList<String>();
		paths = new ArrayList<String>();

		File f = new File(filePath);
		File[] files = f.listFiles();

		if (!filePath.equals(rootPath)) {
			items.add("b2");
			paths.add(f.getParent());
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			items.add(file.getName());
			paths.add(file.getPath());
		}

		Collections.reverse(items);
		Collections.reverse(paths);
		for (int i = 0; i < items.size(); i++) {
			delList.add(false);
		}
	}

	// 处理文件的方法
	private void fileHandle(final File file) {
		OnClickListener listener1 = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					openFile(file);
				} else if (which == 1) {
					LayoutInflater factory = LayoutInflater
							.from(com.fmscreenrecord.activity.FileDirectorySelect.this);
					myView = factory.inflate(MResource.getIdByName(
							getApplication(), "layout",
							"fm_rename_alert_dialog"), null);
					myEditText = (EditText) myView.findViewById(MResource
							.getIdByName(getApplication(), "id", "mEdit"));

					myEditText.setText(file.getName());
					OnClickListener listener2 = new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String modName = myEditText.getText().toString();
							final String pFile = file.getParentFile().getPath()
									+ File.separator;
							final String newPath = pFile + modName;
							if (new File(newPath).exists()) {
								if (!modName.equals(file.getName())) {
									new AlertDialog.Builder(
											com.fmscreenrecord.activity.FileDirectorySelect.this)
											.setTitle("注意!")
											.setMessage("文件名已经存在，是否要覆盖?")
											.setPositiveButton(
													"确定",
													new OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															file.renameTo(new File(
																	newPath));
															getFileDir(pFile);

														}
													})
											.setNegativeButton(
													"取消",
													new OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
														}
													}).show();
								}
							} else {
								file.renameTo(new File(newPath));
								getFileDir(pFile);
							}
						}
					};

					AlertDialog renameDialog = new AlertDialog.Builder(
							com.fmscreenrecord.activity.FileDirectorySelect.this).create();
					renameDialog.setView(myView);

					renameDialog.setButton("确定", listener2);
					renameDialog.setButton2("取消",
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					renameDialog.show();
				} else {
					new AlertDialog.Builder(com.fmscreenrecord.activity.FileDirectorySelect.this)
							.setTitle("注意!")
							.setMessage("确定要删除吗?")
							.setPositiveButton("确定",
									new OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											file.delete();
											getFileDir(file.getParent());

										}
									})
							.setNegativeButton("取消",
									new OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).show();
				}
			}
		};

		String[] menu = { "打开文件", "更改文件名", "删除文件" };
		new AlertDialog.Builder(com.fmscreenrecord.activity.FileDirectorySelect.this).setTitle("选项")
				.setItems(menu, listener1)
				.setPositiveButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	// 长按处理文件
	private void fileHandle_forLongClick(final File file) {
		OnClickListener listener1 = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					LayoutInflater factory = LayoutInflater
							.from(com.fmscreenrecord.activity.FileDirectorySelect.this);
					myView = factory.inflate(MResource.getIdByName(
							getApplication(), "layout",
							"fm_rename_alert_dialog"), null);
					myEditText = (EditText) myView.findViewById(MResource
							.getIdByName(getApplication(), "id", "mEdit"));
					// 截取视频文件后缀名前的字符串
					final String[] fileName = file.getName().split("\\.mp4");

					myEditText.setText(fileName[0]);
					OnClickListener listener2 = new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String modName = myEditText.getText().toString();
							final String pFile = file.getParentFile().getPath()
									+ File.separator;
							// 去除掉编辑框中文件名中的后缀名,防止命名冲突
							String[] modFileName = modName.split("\\.");
							final String newPath = pFile + modFileName[0]
									+ ".mp4";
							if (new File(newPath).exists()) {
								if (!modFileName.equals(fileName[0])) {
									new AlertDialog.Builder(
											com.fmscreenrecord.activity.FileDirectorySelect.this)
											.setTitle("注意!")
											.setMessage("文件名已经存在，是否要覆盖?")
											.setPositiveButton(
													"确定",
													new OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															file.renameTo(new File(
																	newPath));
															getFileDir(pFile);

															// 刷新数据
															childata = upListAdapterData();

															// 更新adapter
															((BaseExpandableListAdapter) adapter)
																	.notifyDataSetChanged();
														}
													})
											.setNegativeButton(
													"取消",
													new OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
														}
													}).show();
								}
							} else {
								file.renameTo(new File(newPath));
								// getFileDir(pFile);
							}
							Toast.makeText(com.fmscreenrecord.activity.FileDirectorySelect.this, "修改成功！",
									Toast.LENGTH_SHORT).show();
							// 刷新数据
							childata = upListAdapterData();

							// 更新adapter
							((BaseExpandableListAdapter) adapter)
									.notifyDataSetChanged();
						}
					};
					AlertDialog renameDialog = new AlertDialog.Builder(
							com.fmscreenrecord.activity.FileDirectorySelect.this).create();
					renameDialog.setView(myView);
					renameDialog.setButton("确定", listener2);
					renameDialog.setButton2("取消",
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					renameDialog.show();
				} else {
					new AlertDialog.Builder(com.fmscreenrecord.activity.FileDirectorySelect.this)
							.setTitle("注意!")
							.setMessage("确定要删除吗?")
							.setPositiveButton("确定",
									new OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											file.delete();

											Toast.makeText(
													com.fmscreenrecord.activity.FileDirectorySelect.this,
													"删除成功！", Toast.LENGTH_SHORT)
													.show();
											// 刷新数据
											childata = upListAdapterData();

											// 更新adapter
											((BaseExpandableListAdapter) adapter)
													.notifyDataSetChanged();

										}
									})
							.setNegativeButton("取消",
									new OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).show();
				}
			}
		};

		String[] menu = { "重命名", "删除" };
		new AlertDialog.Builder(com.fmscreenrecord.activity.FileDirectorySelect.this).setTitle("选项")
				.setItems(menu, listener1)
				.setPositiveButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	// 手机打开文件的method
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);

		// 调用getMIMEType()来取得MimeType
		String type = getMIMEType(f);
		// 设定intent的file与MimeType
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	// 判断文件MimeType的method
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		// 取得扩展名
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		// 按扩展名的类型决定MimeType
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else {
			type = "*";
		}
		// 如果无法直接打开，就弹出软件列表给用户选择
		type += "/*";
		return type;
	}

	public class ExtentAdapter extends BaseExpandableListAdapter {
		private LayoutInflater mInflater;

		private Bitmap mIcon2;
		private Bitmap mIcon3;
		private Bitmap mIcon4;

		public ExtentAdapter(Context context) {
			mInflater = LayoutInflater.from(context);

			// mIcon1 = BitmapFactory.decodeResource(context.getResources(),
			// MResource.getIdByName(getApplication(),"drawable","back01"));
			mIcon2 = BitmapFactory.decodeResource(context.getResources(),
					MResource.getIdByName(getApplication(), "drawable",
							"triangle_right"));
			mIcon3 = BitmapFactory.decodeResource(context.getResources(),
					MResource.getIdByName(getApplication(), "drawable",
							"triangle_down"));
			mIcon4 = BitmapFactory.decodeResource(context.getResources(),
					MResource.getIdByName(getApplication(), "drawable", "doc"));
		}

		public int getGroupCount() {

			return GroupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {

			return childata.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {

			return GroupData.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {

			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {

			return childPosition;
		}

		@Override
		public boolean hasStableIds() {

			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			GroupViewHolder holder;

			if (convertView == null) {
				/* 使用定义的file_row作为Layout */
				convertView = mInflater.inflate(MResource.getIdByName(
						getApplication(), "layout", "fm_file_row_group"), null);
				/* 初始化holder的text与icon */
				holder = new GroupViewHolder();
				holder.text = (TextView) convertView.findViewById(MResource
						.getIdByName(getApplication(), "id", "fm_group_text"));
				holder.pathext = (TextView) convertView.findViewById(MResource
						.getIdByName(getApplication(), "id",
								"fm_group_path_text"));
				holder.icon = (ImageView) convertView.findViewById(MResource
						.getIdByName(getApplication(), "id", "fm_group_icon"));

				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			// 根据所在Group筛选机身或者SD卡文件视频
			String videoName = GroupData.get(groupPosition);
			holder.text.setText(videoName);
			String videoPath = GroupList.get(groupPosition);
			holder.pathext.setText(videoPath);

			// 展开关闭设置不同的icon
			if (isExpanded) {
				holder.icon.setImageBitmap(mIcon3);

			} else {
				holder.icon.setImageBitmap(mIcon2);

			}

			return convertView;

		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {
				/* 使用定义的file_row作为Layout */
				convertView = mInflater.inflate(MResource.getIdByName(
						getApplication(), "layout", "fm_file_row"), null);
				/* 初始化holder的text与icon */
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(MResource
						.getIdByName(getApplication(), "id", "text"));
				holder.icon = (ImageView) convertView.findViewById(MResource
						.getIdByName(getApplication(), "id", "icon"));

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 根据所在Group筛选机身或者SD卡文件视频
			String videName = childata.get(groupPosition).get(childPosition)
					.getDisplayName();

			holder.text.setText(videName);

			holder.icon.setImageBitmap(mIcon4);

			holder.text.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					File file = new File(childata.get(groupPosition)
							.get(childPosition).getPath());
					fileHandle_forLongClick(file);
					file = null;
					return false;
				}
			});
			// 单击打开视频文件
			holder.text
					.setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							File file = new File(childata.get(groupPosition)
									.get(childPosition).getPath());
							openFile(file);
							file = null;
						}

					});
			return convertView;

		}

		/* class ViewHolder */
		private class ViewHolder {
			TextView text;
			ImageView icon;

		}

		private class GroupViewHolder {
			TextView text;
			TextView pathext;
			ImageView icon;

		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {

			return true;
		}

	}

	@Override
	public void onClick(View v) {
		if (v == video_menu) {

			// 检测SD卡文件路径是否存在
			if (StoreDirUtil.getSDDEfault(this) != null
					&& StoreDirUtil.getSDDEfault(getApplicationContext()) != null) {
				// 获取机身内存文件夹路径
				store = StoreDirUtil.getDefault(getApplicationContext());
				list = new ArrayList<VideoInfo>();
				// 将文件夹中的MP4视频加入list
				new VideoScanningThrread(getApplication()).getVideoFile(list,
						store);
				if (list.size() > 0) {
					if (ExApplication.ExpandSdCardAndroidData == true) {
						AlertDialoshow();
					} else {
						ProgressBarAsyncTask asyntask = new ProgressBarAsyncTask();
						asyntask.execute();
						progressDialog.show();
					}

				} else {

					Toast.makeText(mContext, "录屏大师目录下没有视频文件哦，请录屏后再迁移",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(mContext, "检测不到源文件或者外置SD卡 ，请检查后再试试",
						Toast.LENGTH_LONG).show();
			}

		} else if (v == btBack) {

			finish();

		}
	}

	public class ProgressBarAsyncTask extends AsyncTask<Void, Integer, Integer> {

		/**
		 * 该方法在AsyncTask的execute执行后立即执行， 运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		 */
		protected void onPreExecute() {

		}

		/**
		 * 这里的Void参数对应AsyncTask中的第一个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */

		protected Integer doInBackground(Void... params) {

			String path_SDdir = StoreDirUtil.getSDDEfault(
					getApplicationContext()).toString();

			FileOutputStream fos = null;
			Movie countVideo;
			File file = null;
			// 单个视频文件
			File videoFile = null;
			// 所有视频文件总大小
			long videoSize = 0;
			// SD卡剩余空间
			long sdSize;
			try {

				// 获取将要移动的视频文件总大小
				for (int i = 0; i < list.size(); i++) {
					videoFile = new File(list.get(i).getPath());
					videoSize = videoSize + videoFile.length();
				}
				// 获得SD卡剩余空间大小
				sdSize = StoreDirUtil
						.getSDAvailableSize(com.fmscreenrecord.activity.FileDirectorySelect.this);

				if (sdSize > videoSize) {
					// 开始对视频文件进行迁移
					for (int i = 0; i < list.size(); i++) {

						// 获取文件夹中的视频路径
						countVideo = MovieCreator.build(list.get(i).getPath());

						Container out = new DefaultMp4Builder()
								.build(countVideo);
						fos = new FileOutputStream(new File(path_SDdir + "/"
								+ list.get(i).getDisplayName()));
						// 往SD卡相应路径写入视频文件
						out.writeContainer(fos.getChannel());

						// 根据视频文件数量设置进度条最大值
						progressDialog.setMax(list.size());

						// 删除内置SD卡视频文件
						file = new File(list.get(i).getPath());
						if (file.exists()) {

							file.delete();
						}
						publishProgress(i);

					}

					fos.close();
					return 1;
				} else {

					return 0;
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			return 0;

		}

		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		protected void onProgressUpdate(Integer... values) {
			int vlaue = values[0];
			progressDialog.setProgress(vlaue);

		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		protected void onPostExecute(Integer result) {
			progressDialog.cancel();

			if (result == 1) {
				childata = upListAdapterData();
				// 更新adapter
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();

				Toast.makeText(getApplication(), "视频文件已成功迁移至SD卡!", 1).show();
			} else if (result == 0) {
				Toast.makeText(com.fmscreenrecord.activity.FileDirectorySelect.this, "您的SD卡剩余空间不足，请清理后再试",
						Toast.LENGTH_LONG).show();
			}

		}

	}

	// 删除指定文件
	private void delFile(final File file) {

		new AlertDialog.Builder(com.fmscreenrecord.activity.FileDirectorySelect.this)
				.setTitle("注意!")
				.setMessage("确定要删除吗?")
				.setPositiveButton("确定", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (file.isDirectory()) {
							// 若该文件夹为当前选择的目录，将目录更为其父目录
							if (file.getPath().equals(
									sp.getString("image_store_dir", ""))) {
								sp.edit()
										.putString("image_store_dir",
												file.getParent()).commit();
							}
							file.delete();
							getFileDir(file.getParent());
						} else {
							file.delete();
							getFileDir(file.getParent());
						}

					}
				})
				.setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	public void AlertDialoshow() {

		new AlertDialog.Builder(com.fmscreenrecord.activity.FileDirectorySelect.this)
				.setTitle("注意!")
				.setMessage(
						"视频文件迁移到外置SD卡后，请不要轻易卸载或者清除录屏大师应用数据，否则会一并清除您的视频文件。卸载前请做好另外备份")
				.setPositiveButton("确定", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ProgressBarAsyncTask asyntask = new ProgressBarAsyncTask();
						asyntask.execute();
						progressDialog.show();

					}
				})
				.setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	@Override
	protected void onPause() {

		super.onPause();

	}
}