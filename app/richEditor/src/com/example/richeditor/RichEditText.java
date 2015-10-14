package com.example.richeditor;

import java.util.Arrays;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class RichEditText extends EditText {
	
	public static int COLOR_BLACK = 0x000000 | 0xFF000000, COLOR_BLUE = 0x3399db | 0xFF000000;
	
	/**
	 * font settings
	 */
	private boolean font_bold, font_big;
	private int font_color = COLOR_BLACK;
	private int last_start, last_count;
	private EditorListner editorListner;
	private int default_text_size = 20, big_text_size = 30;

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
				Log.e("onTextChanged", s.toString() + "  " + start + "   " + before + "   " + count);
				last_start = start;
				last_count = count;
				
				Editable editable = getEditableText();
				if (last_count > 0) {
					MetricAffectingSpan[] spans1 = null, spans2 = null;
//					if (last_count == 1) {
						//前面样式
						spans1 = editable.getSpans(last_start - 2, last_start - 1, MetricAffectingSpan.class);
						//后面样式
						if (editable.length() > last_start + last_count)
							spans2 = editable.getSpans(last_start + last_count, last_start + last_count + 1, MetricAffectingSpan.class);
						setFontStyle(editable, spans1, spans2);
//					}
				} else {
					if (editable.length() == 0) {
						MetricAffectingSpan[] spans = editable.getSpans(0, editable.length(), MetricAffectingSpan.class);
						for (MetricAffectingSpan span : spans)
							editable.removeSpan(span);
					} 
//					else {
//						MetricAffectingSpan[] spans = s.getSpans(last_start, last_start, MetricAffectingSpan.class);
//						for (MetricAffectingSpan span : spans)
//							s.removeSpan(span);
//					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Log.e("beforeTextChanged", s.toString() + "  " + start + "   " + after + "   " + count);
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
			boolean _big = false, _bold = false, _blue = false;
			for (int i = 0; i < spans1.length; i++) {
				MetricAffectingSpan span = spans1[i];
				if (span instanceof AbsoluteSizeSpan) {
					if (font_big)
						s.setSpan(new AbsoluteSizeSpan(big_text_size), last_start, last_start + last_count, 0);
					else
						s.setSpan(new AbsoluteSizeSpan(default_text_size), last_start, last_start + last_count, 0);
					_big = true;
				} else if (span instanceof StyleSpan) {
					_bold = true;
				} else if (span instanceof TextAppearanceSpan) {
					_blue = true;
				}
			}
			if (font_big && !_big)
				s.setSpan(new AbsoluteSizeSpan(big_text_size), last_start, last_start + last_count, 0);
			if (font_bold)// && !_bold)
				s.setSpan(new StyleSpan(Typeface.BOLD), last_start, last_start + last_count, 0);
			if (font_color == COLOR_BLUE)// && !_blue)
				s.setSpan(new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(font_color), null), last_start, last_start + last_count, 0);
		
		}
	}
	
	public String getHtmlContent() {
		Editable editable = getEditableText();
		StringBuilder buffer = new StringBuilder();
		MetricAffectingSpan[] last_spans = null;
		for (int i = 0; i < editable.length(); i++) {
			MetricAffectingSpan[] spans = editable.getSpans(i, i + 1, MetricAffectingSpan.class);
			if (spans != null && spans.length == 1 && spans[0] instanceof ImageSpan)
				buffer.append(getHtmlImg((ImageSpan)spans[0]));
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
	
	private String getHtmlImg(ImageSpan imgSpan) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("<img src=\"").append(imgSpan.getSource())
			.append("\" width=\"100%\" />");
		return buffer.toString();
	}
	
	private String getHtmlStart(MetricAffectingSpan[] spans) {
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
		builder.append(COLOR_BLACK != color && -1 != color ? "<font color=\"" + Integer.toHexString(color) + "\">" : "")
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
	
	private String getStyleStart() {
		if (isDefaultFont())
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(COLOR_BLACK == font_color ? "" : "<font color=\"" + font_color + "\">")
			.append(font_big ? "<big>" : "")
			.append(font_bold ? "<b>" : "");
		return builder.toString();
	}

	private String getStyleEnd() {
		if (isDefaultFont())
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(font_bold ? "</b>" : "")
			.append(font_big ? "</big>" : "")
			.append(COLOR_BLACK == font_color ? "" : "</font>");
		return builder.toString();
	}
	
	public void insertPic() {
		String url = "http://gb.cri.cn/mmsource/images/2015/10/14/be0c24c01f924b5784112a92446136fe.jpg";
		int nowLocation = getSelectionStart();  
        getText().insert(nowLocation, Html.fromHtml("<img src=\"" + url + "\" width=\"100%\"/>"));
	}
	
	private boolean isDefaultFont() {
		return !font_bold && !font_big && COLOR_BLACK == font_color;
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
	
	public interface EditorListner {
		public void afterEdit(String str);
	}
}
