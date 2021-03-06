package com.example.richeditor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.widget.EditText;

public class RichEditText extends EditText {
	
	public static int COLOR_BLACK = 0x000000 | 0xFF000000, COLOR_BLUE = 0x3399db | 0xFF000000;
	
	/**
	 * font settings
	 */
	private boolean font_bold, font_big;
	private int font_color = COLOR_BLACK;
	
	//font flag
	private boolean edting;
	private String last_style = "";
	private int last_start, last_count;
	private Spanned last_spanned;
	
	private EditorListner editorListner;
	
	private EditorStatus status = EditorStatus.none;
	
	private enum EditorStatus {
		none, edit, delete, insert
	}

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
				System.out.println(s.toString() + "  " + start + "   " + before + "   " + count);
//				if (status == EditorStatus.none) {
					last_start = start;
					last_count = count;
//				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
//				if (!isDefaultFont()) {
//					if (status == EditorStatus.none) {
//						status = EditorStatus.insert;
//						String style = getStyleStart();
////						if (!style.equals(last_style)) {
//							
//							last_spanned = Html.fromHtml(getStyleStart() + 
//									s.subSequence(last_start, last_start + last_count).toString());
//							s.replace(last_start, last_start + last_count, last_spanned, 0, last_spanned.length());
//							last_style = style;
				MetricAffectingSpan[] spans = null;
				if (last_start >= 1 || last_count > 1) {
					spans = s.getSpans(last_start + last_count - 2, last_start + last_count - 1, MetricAffectingSpan.class);
					System.out.println(spans.length);
				}
							setFontStyle(s, spans);
//						}
						
//						s.delete(last_start, last_start + last_count);
//					} else if (status == EditorStatus.delete) {
//						status = EditorStatus.insert;
//						s.insert(getSelectionStart(), last_spanned);
						if (editorListner != null)
							editorListner.afterEdit(s.subSequence(last_start, last_start + last_count).toString());
//					} else if (status == EditorStatus.insert) {
//						status = EditorStatus.none;
//					}
//				}
//				else
//					s.clearSpans();
//				if (!edting && !isDefaultFont()) {
//					edting = true;
//////					s.replace(last_start, last_start + last_count, getStyle() + s.subSequence(last_start, last_start + last_count) + "</p>");
//////					s.replace(last_start, last_start + last_count, 
//////							Html.fromHtml(getStyle() + s.subSequence(last_start, last_start + last_count) + "</p>"));
//////					s.insert(getSelectionStart(), Html.fromHtml(getStyle() + s.subSequence(last_start, last_start + last_count) + "</p>"));
//					
//					//s.delete(last_start, last_start + last_count);
//					
//					s.insert(getSelectionStart(), Html.fromHtml(getStyleStart() + 
//							s.subSequence(last_start, last_start + last_count).toString() + getStyleEnd()));
//					if (editorListner != null)
//						editorListner.afterEdit(s.subSequence(last_start, last_start + last_count).toString());
//				} 
//				else if (!isDefaultFont())
//					edting = false;
//				
//				if (last_editable == null)
//					last_editable = s;
//				else {
//					last_editable.append(s.toString());
//				}
			}
		});
	}
	
	private void ending() {
	}
	
	private void reset() {
		font_bold = false;
		font_big = false;
		font_color = COLOR_BLACK;
		edting = false;
	}
	
	private static RelativeSizeSpan FONT_BIG = new RelativeSizeSpan(1.25f), FONT_NORMAL = new RelativeSizeSpan(1.0f);
	private static StyleSpan bold = new StyleSpan(Typeface.BOLD), normal = new StyleSpan(Typeface.NORMAL);
	private static TextAppearanceSpan black = new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(COLOR_BLACK), null),
			blue = new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(COLOR_BLUE), null);
	
	private void setFontStyle(Editable s, MetricAffectingSpan[] spans) {
//		if (COLOR_BLACK != font_color)
//			s.setSpan(new TextAppearanceSpan(null, 0, 0, ColorStateList.valueOf(font_color), null), last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//			s.setSpan(font_color == COLOR_BLACK ? black : blue, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
////		s.setSpan(new BackgroundColorSpan(font_color), last_start, last_start + last_count, 0); //Spanned.SPAN_INCLUSIVE_INCLUSIVE);
////		if (font_big)
//			s.setSpan(font_big ? FONT_BIG : FONT_NORMAL, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
////		if (font_bold)
////			s.setSpan(new StyleSpan(font_bold ? Typeface.BOLD : Typeface.NORMAL), last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//			s.setSpan(font_bold ? bold : normal, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			
		
		if (spans == null || spans.length == 0) {
			if (font_color != COLOR_BLACK)
				s.setSpan(blue, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			if (font_big)
				s.setSpan(FONT_BIG, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			if (font_bold)
				s.setSpan(bold, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		} else {
			boolean _big = false, _bold = false, _blue = false;
			for (int i = 0; i < spans.length; i++) {
				MetricAffectingSpan span = spans[i];
				if (span instanceof RelativeSizeSpan) {
					float size = ((RelativeSizeSpan)span).getSizeChange();
					if (font_big && size != 1.25)
						s.setSpan(FONT_BIG, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
					else if (!font_big && size == 1.25)
						s.setSpan(FONT_NORMAL, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//						s.removeSpan(FONT_BIG);
					_big = true;
				} else if (span instanceof StyleSpan) {
//					int style = ((StyleSpan)span).getStyle();
//					Typeface.BOLD
//					if (!font_bold)
//						s.removeSpan(bold);
					_bold = true;
				} else if (span instanceof TextAppearanceSpan) {
//					((TextAppearanceSpan)span).getTextColor().getDefaultColor() == COLOR_BLUE
//					if (font_color != COLOR_BLUE)
//						s.removeSpan(blue);
					_blue = true;
				}
			}
			if (font_big && !_big)
				s.setSpan(FONT_BIG, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			if (font_bold && !_bold)
				s.setSpan(bold, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			if (font_color == COLOR_BLUE && !_blue)
				s.setSpan(blue, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		}
			
//		if (font_color == COLOR_BLACK)
//			s.removeSpan(blue);
//		else
//			s.setSpan(blue, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//		
//		if (font_big)
//			s.setSpan(FONT_BIG, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//		else
//			s.removeSpan(FONT_BIG);
//		
//		if (font_bold)
//			s.setSpan(bold, last_start, last_start + last_count, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//		else
//			s.removeSpan(bold);
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
		
//		getEditableText().insert(getSelectionStart(), Html.fromHtml(getStyleStart() + "this is a test" + getStyleEnd()));
		
//		moveCursorToVisibleOffset();
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
