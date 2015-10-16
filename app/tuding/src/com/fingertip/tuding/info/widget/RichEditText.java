package com.fingertip.tuding.info.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.widget.EditText;

import com.fingertip.tuding.util.ImageCache;
import com.fingertip.tuding.util.http.common.UploadImgEntity;

public class RichEditText extends EditText {
	
//	黄色#ff9c00
//	蓝色#0070c1
//	绿色#00b150
//	紫色#531483
//	黑色#000000
//	红色#c10000
	public static int COLOR_BLACK = 0x000000 | 0xFF000000, 
			COLOR_YELLOW = 0xff9c00 | 0xFF000000,
			COLOR_BLUE = 0x0070c1 | 0xFF000000,
			COLOR_GREEN = 0x00b150 | 0xFF000000,
			COLOR_RED = 0xc10000 | 0xFF000000,
			COLOR_PURPLE = 0x531483 | 0xFF000000;
	
	public static SparseArray<String> COLOR_MAP = new SparseArray<String>();
	static {
		COLOR_MAP.put(COLOR_BLACK, "#000000");
		COLOR_MAP.put(COLOR_YELLOW, "#ff9c00");
		COLOR_MAP.put(COLOR_BLUE, "#0070c1");
		COLOR_MAP.put(COLOR_GREEN, "#00b150");
		COLOR_MAP.put(COLOR_RED, "#c10000");
		COLOR_MAP.put(COLOR_PURPLE, "#531483");
	}
	
	private static String IMG_PREFIX = "file:///sdcard";
	
	/**
	 * font settings
	 */
	private boolean font_bold, font_big;
	private int font_color = COLOR_BLACK;
	private int last_start, last_count;
	private EditorListner editorListner;
	private int default_text_size = 20, big_text_size = 30;
	private int pic_index = 0;
	private List<UploadImgEntity> images = new ArrayList<UploadImgEntity>();

	public RichEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RichEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RichEditText(Context context) {
		super(context);
		init();
	}

	private void init() {
		default_text_size = (int)this.getTextSize();
		big_text_size = (int) (default_text_size * 3 / 2);
		this.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				Log.e("onTextChanged", s.toString() + "  " + start + "   " + before + "   " + count);
				last_start = start;
				last_count = count;
				
				Editable editable = getEditableText();
				if (last_count > 0) {
					MetricAffectingSpan[] spans1 = null, spans2 = null;
					//前面样式
					spans1 = editable.getSpans(last_start - 2, last_start - 1, MetricAffectingSpan.class);
					//后面样式
					if (editable.length() > last_start + last_count)
						spans2 = editable.getSpans(last_start + last_count, last_start + last_count + 1, MetricAffectingSpan.class);
					setFontStyle(editable, spans1, spans2);
				} else {
					if (editable.length() == 0) {
						MetricAffectingSpan[] spans = editable.getSpans(0, editable.length(), MetricAffectingSpan.class);
						for (MetricAffectingSpan span : spans)
							editable.removeSpan(span);
					} 
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				Log.e("beforeTextChanged", s.toString() + "  " + start + "   " + after + "   " + count);
				//删除时删掉spans
				if (count == 1 && after == 0) {
					boolean delete = true;
					MetricAffectingSpan[] spans = getEditableText().getSpans(start, start + 1, MetricAffectingSpan.class);
					if (start > 0) {
						MetricAffectingSpan[] front_spans = getEditableText().getSpans(start - 1, start, MetricAffectingSpan.class);
						if (Arrays.equals(spans, front_spans))
							delete = false;
					}
					if (delete && start + 1 < s.length()) {
						MetricAffectingSpan[] end_spans = getEditableText().getSpans(start + 1, start + 2, MetricAffectingSpan.class);
						if (Arrays.equals(spans, end_spans))
							delete = false;
					}
					if (delete) {
						for (MetricAffectingSpan span : spans)
							getEditableText().removeSpan(span);
					}
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (editorListner != null)
					editorListner.afterEdit(s.subSequence(last_start, last_start + last_count).toString());
			}
		});
	}
	
	private void setFontStyle(Editable s, MetricAffectingSpan[] spans1, MetricAffectingSpan[] spans2) {
		if (spans1 == null || spans1.length == 0) {
			if (COLOR_BLACK != font_color)
				s.setSpan(new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(font_color), null), last_start, last_start + last_count, 0);
			if (font_big)
				s.setSpan(new AbsoluteSizeSpan(big_text_size), last_start, last_start + last_count, 0);
			if (font_bold)
				s.setSpan(new StyleSpan(Typeface.BOLD), last_start, last_start + last_count, 0);
		} else {
//			boolean _big = false;
//			for (int i = 0; i < spans1.length; i++) {
//				MetricAffectingSpan span = spans1[i];
//				if (span instanceof AbsoluteSizeSpan) {
//					int font_size = ((AbsoluteSizeSpan) span).getSize();
//					_big = font_size == big_text_size;
//				}
//			}
			if (font_big)
				s.setSpan(new AbsoluteSizeSpan(big_text_size), last_start, last_start + last_count, 0);
			else
				s.setSpan(new AbsoluteSizeSpan(default_text_size), last_start, last_start + last_count, 0);
			if (font_bold)
				s.setSpan(new StyleSpan(Typeface.BOLD), last_start, last_start + last_count, 0);
			if (font_color != COLOR_BLACK)
				s.setSpan(new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(font_color), null), last_start, last_start + last_count, 0);
		
		}
	}
	
//	public String getHtmlContent() {
//		return getHtmlContent(null);
//	}
		
	public String getHtmlContent(Map<String, String> img_map) {
		Editable editable = getEditableText();
		StringBuilder buffer = new StringBuilder();
		images.clear();
		MetricAffectingSpan[] last_spans = null;
		for (int i = 0; i < editable.length(); i++) {
			MetricAffectingSpan[] spans = editable.getSpans(i, i + 1, MetricAffectingSpan.class);
			ImageSpan image = getImageSpan(spans);
			if (image != null)
				buffer.append(getHtmlImg(image, img_map));
			else {
				String text = toHtml(editable.subSequence(i, i + 1).toString());
				if (!spansEquals(last_spans, spans)) {
					buffer.append(getHtmlEnd(last_spans))
						.append(getHtmlStart(spans))
						.append(text);
					last_spans = spans;
				} else
					buffer.append(text);
			}
		}
		buffer.append(getHtmlEnd(last_spans));
		return buffer.toString();
	}
	
	private ImageSpan getImageSpan(MetricAffectingSpan[] spans) {
		if (spans != null && spans.length > 0) {
			for (MetricAffectingSpan span : spans) {
				if (span instanceof ImageSpan)
					return (ImageSpan)span;
			}
		}
		return null; 
	}
	
	private String toHtml(String text) {
		if (text.equals("\n"))
			return "<br>";
		return text;
	}
	
	private boolean spansEquals(MetricAffectingSpan[] spans1, MetricAffectingSpan[] spans2) {
		int length1 = spans1 == null ? 0 : spans1.length;
		int length2 = spans2 == null ? 0 : spans2.length;
		if (length1 != length2)
			return false;
		return getHtmlStart(spans1).equals(getHtmlStart(spans2));
	}
	
	private static final int[] EMPTY = new int[0];
	
	private String getHtmlStart(MetricAffectingSpan[] spans) {
		if (spans == null || spans.length == 0)
			return "";
		boolean _big = false, _bold = false;
		int color = -1;
		for (MetricAffectingSpan span : spans) {
			if (span instanceof TextAppearanceSpan) {
				color = ((TextAppearanceSpan) span).getTextColor().getColorForState(EMPTY, -1);
			} else if (span instanceof AbsoluteSizeSpan) {
				int font_size = ((AbsoluteSizeSpan) span).getSize();
				_big = font_size == big_text_size;
			} else if (span instanceof StyleSpan)
				_bold = true;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(COLOR_BLACK != color && -1 != color ? "<font color=\"" + COLOR_MAP.get(color) + "\">" : "")
			.append(_big ? "<big>" : "")
			.append(_bold ? "<b>" : "");
		return builder.toString();
	}

	private String getHtmlEnd(MetricAffectingSpan[] spans) {
		if (spans == null || spans.length == 0)
			return "";
		boolean _big = false, _bold = false;
		int color = -1;
		for (MetricAffectingSpan span : spans) {
			if (span instanceof TextAppearanceSpan) {
				color = ((TextAppearanceSpan) span).getTextColor().getColorForState(EMPTY, -1);
				Log.e("getTextColor", Integer.toHexString(color));
			} else if (span instanceof AbsoluteSizeSpan) {
				int font_size = ((AbsoluteSizeSpan) span).getSize();
				_big = font_size == big_text_size;
			} else if (span instanceof StyleSpan)
				_bold = true;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(_bold ? "</b>" : "")
			.append(_big ? "</big>" : "")
			.append(COLOR_BLACK != color && -1 != color ? "</font>" : "");
		return builder.toString();
	}
	
	public void insertPic(String path) {
		UploadImgEntity img_entity = ImageCache.compressImageForPreview(path, pic_index++);
		path = ImageCache.getPreviewRelativDir() + img_entity.small_file.getName();
		int nowLocation = getSelectionStart();
        getText().insert(nowLocation, Html.fromHtml("<br><img src=\"" + IMG_PREFIX + path 
        		+ "\" width=\"100%\"/><br>", imageGetter, null));
	}
	
	private String getHtmlImg(ImageSpan imgSpan, Map<String, String> img_map) {
		String src = imgSpan.getSource();
		String path = src;
		if (path.startsWith(IMG_PREFIX))
			path = Environment.getExternalStorageDirectory().getAbsolutePath() + path.substring(IMG_PREFIX.length());
		UploadImgEntity uploadeEntity = new UploadImgEntity();
		uploadeEntity.small_file = new File(path);
		String big_file_path = ImageCache.getAnOtherUploadImgPath(uploadeEntity.small_file.getName(), false);
		uploadeEntity.big_file = new File(big_file_path != null ? big_file_path : path);
		images.add(uploadeEntity);
		StringBuilder buffer = new StringBuilder();
		src = img_map != null && img_map.containsKey(path) ? img_map.get(path) : src;
		buffer.append("<img src=\"").append(src).append("\" width=\"100%\" />");
		return buffer.toString();
	}
	
	public boolean isFont_bold() {
		return font_bold;
	}

	public void setFont_bold(boolean font_bold) {
		this.font_bold = font_bold;
	}

	public boolean isFont_big() {
		return font_big;
	}

	public void setFont_big(boolean font_big) {
		this.font_big = font_big;
	}

	public int getFont_color() {
		return font_color;
	}

	public void setFont_color(int font_color) {
		this.font_color = font_color;
	}

	public void setEditorListner(EditorListner editorListner) {
		this.editorListner = editorListner;
	}
	
	public List<UploadImgEntity> getImages() {
		return images;
	}

	public List<UploadImgEntity> getImages(Map<String, String> img_map) {
		if (img_map != null && !img_map.isEmpty()) {
			for (UploadImgEntity img : images) {
				String small_path = img.small_file.getAbsolutePath();
				String big_path = img.big_file.getAbsolutePath();
				img.small_url = img_map.get(small_path);
				img.big_url = img_map.get(big_path);
			} 
		}
		return images;
	}

	public interface EditorListner {
		public void afterEdit(String str);
	}
	
	private ImageGetter imageGetter = new ImageGetter() {
		
		@Override
		public Drawable getDrawable(String source) {
			int index = source.lastIndexOf(File.separator);
			source = index > 0 ? source.substring(index) : source;
			Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(ImageCache.getPreviewDir() + source));
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			return d;
		}
	};
}
