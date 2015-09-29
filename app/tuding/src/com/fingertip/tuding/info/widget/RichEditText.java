package com.fingertip.tuding.info.widget;

import java.util.Arrays;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
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
		this.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.e("onTextChanged", s.toString() + "  " + start + "   " + before + "   " + count);
				last_start = start;
				last_count = count;
				
				Editable editable = getEditableText();
				if (last_count > 0) {
					MetricAffectingSpan[] spans1 = null, spans2 = null;
					if (last_count == 1) {
						//前面样式
						spans1 = editable.getSpans(last_start - 2, last_start - 1, MetricAffectingSpan.class);
						//后面样式
						if (editable.length() > last_start + last_count)
							spans2 = editable.getSpans(last_start + last_count, last_start + last_count + 1, MetricAffectingSpan.class);
						setFontStyle(editable, spans1, spans2);
					}
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
	
	private void reset() {
		font_bold = false;
		font_big = false;
		font_color = COLOR_BLACK;
	}
	
	private void setFontStyle(Editable s, MetricAffectingSpan[] spans1, MetricAffectingSpan[] spans2) {
		if (spans1 == null || spans1.length == 0) {
			if (COLOR_BLACK != font_color)
				s.setSpan(new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(font_color), null), last_start, last_start + last_count, 0);
			if (font_big)
				s.setSpan(new AbsoluteSizeSpan(30), last_start, last_start + last_count, 0);
			if (font_bold)
				s.setSpan(new StyleSpan(Typeface.BOLD), last_start, last_start + last_count, 0);
		} else {
			boolean _big = false, _bold = false, _blue = false;
			for (int i = 0; i < spans1.length; i++) {
				MetricAffectingSpan span = spans1[i];
				if (span instanceof AbsoluteSizeSpan) {
					int size = ((AbsoluteSizeSpan)span).getSize();
					if (font_big)// && size != 30)
						s.setSpan(new AbsoluteSizeSpan(30), last_start, last_start + last_count, 0);
					else if (!font_big)// && size == 30)
						s.setSpan(new AbsoluteSizeSpan(20), last_start, last_start + last_count, 0);
//					else
//						s.setSpan(span, last_start, last_start + last_count, 0);
					_big = true;
				} else if (span instanceof StyleSpan) {
//					int style = ((StyleSpan)span).getStyle();
//					Typeface.BOLD
//					if (!font_bold)
//						s.removeSpan(bold);
//					if (font_bold)
//						s.setSpan(span, last_start, last_start + last_count, 0);
					_bold = true;
				} else if (span instanceof TextAppearanceSpan) {
//					((TextAppearanceSpan)span).getTextColor().getDefaultColor() == COLOR_BLUE
//					if (font_color == COLOR_BLUE)
//						s.setSpan(span, last_start, last_start + last_count, 0);
					_blue = true;
				}
			}
			if (font_big && !_big)
				s.setSpan(new AbsoluteSizeSpan(30), last_start, last_start + last_count, 0);
			if (font_bold)// && !_bold)
				s.setSpan(new StyleSpan(Typeface.BOLD), last_start, last_start + last_count, 0);
			if (font_color == COLOR_BLUE)// && !_blue)
				s.setSpan(new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(font_color), null), last_start, last_start + last_count, 0);
		
		}
	}
	
	public String getStyleStart() {
		if (isDefaultFont())
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(COLOR_BLACK == font_color ? "" : "<font color=\"" + font_color + "\">")
			.append(font_big ? "<big>" : "")
			.append(font_bold ? "<b>" : "");
		return builder.toString();
	}

	public String getStyleEnd() {
		if (isDefaultFont())
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(font_bold ? "</b>" : "")
			.append(font_big ? "</big>" : "")
			.append(COLOR_BLACK == font_color ? "" : "</font>");
		return builder.toString();
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
